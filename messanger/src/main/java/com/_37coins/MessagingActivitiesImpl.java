package com._37coins;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.InitialLdapContext;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.ldap.JndiLdapContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._37coins.activities.MessagingActivities;
import com._37coins.envaya.QueueClient;
import com._37coins.persistence.dto.MsgAddress;
import com._37coins.persistence.dto.Transaction;
import com._37coins.sendMail.MailTransporter;
import com._37coins.workflow.pojo.DataSet;
import com._37coins.workflow.pojo.DataSet.Action;
import com._37coins.workflow.pojo.MessageAddress;
import com._37coins.workflow.pojo.MessageAddress.MsgType;
import com._37coins.workflow.pojo.PaymentAddress;
import com._37coins.workflow.pojo.Withdrawal;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.flow.ActivityExecutionContext;
import com.amazonaws.services.simpleworkflow.flow.ActivityExecutionContextProvider;
import com.amazonaws.services.simpleworkflow.flow.ActivityExecutionContextProviderImpl;
import com.amazonaws.services.simpleworkflow.flow.ManualActivityCompletionClient;
import com.amazonaws.services.simpleworkflow.flow.ManualActivityCompletionClientFactory;
import com.amazonaws.services.simpleworkflow.flow.ManualActivityCompletionClientFactoryImpl;
import com.amazonaws.services.simpleworkflow.flow.annotations.ManualActivityCompletion;
import com.google.inject.Inject;
import com.plivo.helper.api.client.RestAPI;
import com.plivo.helper.api.response.call.Call;
import com.plivo.helper.exception.PlivoException;

public class MessagingActivitiesImpl implements MessagingActivities {
	public static Logger log = LoggerFactory.getLogger(MessagingActivitiesImpl.class);
	ActivityExecutionContextProvider contextProvider = new ActivityExecutionContextProviderImpl();
	
	@Inject
	MailTransporter mt;
	
	@Inject
	QueueClient qc;
	
	@Inject
	JndiLdapContextFactory jlc;
	
	@Inject
	AmazonSimpleWorkflow swfService;
	
	@Inject
	Cache cache;
	
	@Inject
	MessageFactory mf;

	@Override
	public void sendMessage(DataSet rsp) {
		try {
			if (rsp.getTo().getAddressType() == MsgType.EMAIL){
				mt.sendMessage(rsp);
			}else{
				String runId = contextProvider.getActivityExecutionContext().getWorkflowExecution().getRunId();
				String taskId = contextProvider.getActivityExecutionContext().getTask().getActivityId();
				qc.send(rsp,MessagingServletConfig.queueUri, rsp.getTo().getGateway(),"amq.direct",runId+"::"+taskId);
			}
		} catch (Exception e) {
			return;
		}
	}
	
	@Override
	public void putCache(DataSet rsp) {
		cache.put(new Element("balance"+rsp.getCn(), ((Withdrawal)rsp.getPayload()).getBalance()));
	}
	
	@Override
	public void putAddressCache(DataSet rsp) {
		cache.put(new Element("address"+rsp.getCn(), ((PaymentAddress)rsp.getPayload()).getAddress()));
	}

	@Override
	@ManualActivityCompletion
	public Action sendConfirmation(DataSet rsp, String workflowId) {
		ActivityExecutionContext executionContext = contextProvider.getActivityExecutionContext();
		String taskToken = executionContext.getTaskToken();
		try{
			Element e = cache.get(workflowId);
			Transaction tt = (Transaction)e.getObjectValue();
			tt.setTaskToken(taskToken);
			String confLink = MessagingServletConfig.basePath + "/rest/withdrawal/approve?key="+URLEncoder.encode(tt.getKey(),"UTF-8");
			Withdrawal w = (Withdrawal)rsp.getPayload();
			w.setConfKey(tt.getKey());
			w.setConfLink(confLink);
			sendMessage(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	@ManualActivityCompletion
	public Action phoneConfirmation(DataSet rsp, String workflowId) {
		ActivityExecutionContext executionContext = contextProvider.getActivityExecutionContext();
		String taskToken = executionContext.getTaskToken();
		try{
			Element e = cache.get(workflowId);
			Transaction tt = (Transaction)e.getObjectValue();
			tt.setTaskToken(taskToken);
			
			InitialLdapContext ctx = null;
			AuthenticationToken at = new UsernamePasswordToken(MessagingServletConfig.ldapUser, MessagingServletConfig.ldapPw);
			ctx = (InitialLdapContext)jlc.getLdapContext(at.getPrincipal(),at.getCredentials());
			Attributes atts = ctx.getAttributes("cn="+rsp.getCn()+",ou=accounts,"+MessagingServletConfig.ldapBaseDn,new String[]{"pwdAccountLockedTime", "cn"});
			boolean pwLocked = (atts.get("pwdAccountLockedTime")!=null)?true:false;
			
			if (pwLocked){
				RestAPI restAPI = new RestAPI(MessagingServletConfig.plivoKey, MessagingServletConfig.plivoSecret, "v1");
	
				LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
			    params.put("from", rsp.getTo().getGateway());
			    params.put("to", rsp.getTo().getAddress());
			    params.put("answer_url", MessagingServletConfig.basePath + "/plivo/answer/"+rsp.getCn()+"/"+workflowId+"/"+mf.getLocale(rsp).toString());
			    params.put("time_limit", "55");
			    params.put("ring_timeout", "10");
			    params.put("machine_detection", "hangup");
			    params.put("hangup_url", MessagingServletConfig.basePath + "/plivo/hangup/"+workflowId);
			    params.put("caller_name", "37 Coins");
			    Call response = restAPI.makeCall(params);
			    if (response.serverCode != 200 && response.serverCode != 201 && response.serverCode !=204){
			    	throw new PlivoException(response.message);
			    }
			}else{
				
			}
		    return null;
		} catch (PlivoException | NamingException | MalformedURLException e) {
	        ManualActivityCompletionClientFactory manualCompletionClientFactory = new ManualActivityCompletionClientFactoryImpl(swfService);
	        ManualActivityCompletionClient manualCompletionClient = manualCompletionClientFactory.getClient(taskToken);
	        manualCompletionClient.complete(Action.TX_CANCELED);
	        e.printStackTrace();
	        return null;
		}
	}
	


	@Override
	public DataSet readMessageAddress(DataSet data) {
		InitialLdapContext ctx = null;
		AuthenticationToken at = new UsernamePasswordToken(MessagingServletConfig.ldapUser, MessagingServletConfig.ldapPw);
		try {
			ctx = (InitialLdapContext)jlc.getLdapContext(at.getPrincipal(),at.getCredentials());
		} catch (IllegalStateException | NamingException e) {
			e.printStackTrace();
		}
		try{
			Attributes atts = ctx.getAttributes("cn="+data.getCn()+",ou=accounts,"+MessagingServletConfig.ldapBaseDn,new String[]{"mobile", "manager","preferredLanguage"});
			String locale = (atts.get("preferredLanguage")!=null)?(String)atts.get("preferredLanguage").get():null;
			String gwDn = (atts.get("manager")!=null)?(String)atts.get("manager").get():null;
			String mobile = (atts.get("mobile")!=null)?(String)atts.get("mobile").get():null;
			MessageAddress to =  new MessageAddress()
				.setAddress(mobile)
				.setAddressType(MsgType.SMS)
				.setGateway(gwDn.substring(3, gwDn.indexOf(",")));
			return data.setTo(to)
				.setLocaleString(locale)
				.setService("37coins");
		}catch(NamingException e){
			return null;
		}
	}

	public MsgAddress pickMsgAddress(Set<MsgAddress> list){
		//TODO: get a strategy here
		return list.iterator().next();
	}


}
