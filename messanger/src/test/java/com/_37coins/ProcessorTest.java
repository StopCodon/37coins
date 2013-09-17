package com._37coins;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.internet.AddressException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.restnucleus.dao.GenericRepository;
import org.restnucleus.dao.Model;

import com._37coins.parse.MessageParser;
import com._37coins.parse.RequestInterpreter;
import com._37coins.persistence.dto.Account;
import com._37coins.persistence.dto.Gateway;
import com._37coins.persistence.dto.MsgAddress;
import com._37coins.workflow.pojo.DataSet;
import com._37coins.workflow.pojo.DataSet.Action;
import com._37coins.workflow.pojo.MessageAddress;
import com._37coins.workflow.pojo.PaymentAddress;
import com._37coins.workflow.pojo.PaymentAddress.PaymentType;
import com._37coins.workflow.pojo.Withdrawal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.i18n.phonenumbers.NumberParseException;

public class ProcessorTest{
	static MessageParser ep =null;
	GenericRepository gr = null;
	static MessageAddress USER1;
	static final MessageAddress SENDER1= new MessageAddress().setAddress("testtest@37coins.com").setGateway("123");
	static final MessageAddress SENDER2= new MessageAddress().setAddress("test3@37coins.com").setGateway("123");
	static final BigDecimal FEE=new BigDecimal("0.0002").setScale(8,RoundingMode.UP);
	
	static{
		try {
			USER1 = MessageAddress.fromString("+821012345678", "+821099999999");
		} catch (AddressException | NumberParseException e) {
			e.printStackTrace();
		}
	}
	
	@Before
	public void before(){
		if (ep==null){
			ep =  new MessageParser();
			gr = new GenericRepository();
			List<Account> accounts = new ArrayList<>();
			accounts.add(new Account());
			accounts.add(new Account());
			List<Gateway> gws = new ArrayList<>();
			gws.add(new Gateway().setOwner(accounts.get(0)).setAddress("123").setFee(FEE));
			gws.add(new Gateway().setOwner(accounts.get(1)).setAddress("+821099999999").setCountryCode(82).setFee(FEE));
			List<MsgAddress> addrs = new ArrayList<>();
			addrs.add(new MsgAddress().setAddress(SENDER1.getAddress()).setOwner(accounts.get(0)).setGateway(gws.get(0)));
			addrs.add(new MsgAddress().setAddress("test2@37coins.com").setOwner(accounts.get(1)).setGateway(gws.get(0)));
			addrs.add(new MsgAddress().setAddress("01029382039").setOwner(accounts.get(1)).setGateway(gws.get(0)));
			Map<Class<? extends Model>, List<? extends Model>> data = new HashMap<Class<? extends Model>, List<? extends Model>>();
			data.put(Account.class, accounts);
			data.put(Gateway.class,gws);
			data.put(MsgAddress.class, addrs);
			persist(data);
		}
	}
	
	private <E extends Model> void persist(
			Map<Class<? extends Model>, List<? extends Model>> data) {
		if (null != data) {
			for (Entry<Class<? extends Model>, List<? extends Model>> e : data
					.entrySet()) {
				try {
					if (null == e.getValue() || e.getValue().size() < 1) {
						System.out.println("no data found for: " + e.getKey().getSimpleName());
					} else {
						for (Model m : e.getValue()) {
							if (m.getId() == null)
								gr.add(m);
						}
						System.out.println(e.getKey().getSimpleName()
								+ " populated with " + e.getValue().size()
								+ " entities");
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	@Test
	public void testBalance() throws Exception {
		RequestInterpreter ri = new RequestInterpreter(ep) {
			@Override
			public void startWithdrawal(DataSet data, String workflowId) {Assert.assertFalse(true);}
			@Override
			public void startDeposit(DataSet data) {
				Assert.assertNotNull(data.getAccountId());
				DataSet expected = new DataSet()
					.setAction(Action.BALANCE)
					.setLocale(new Locale("en"))
					.setAccountId(data.getAccountId())
					.setTo(SENDER1);
				Assert.assertEquals(expected, data);
			}
			@Override
			public void respond(DataSet rsp) {Assert.assertFalse(true);}
		};
		ri.process(SENDER1, " balance");
	}
	
	@Test
	public void testCreate() throws Exception {
		RequestInterpreter ri = new RequestInterpreter(ep) {
			@Override
			public void startWithdrawal(DataSet data, String workflowId) {Assert.assertFalse(true);}
			@Override
			public void startDeposit(DataSet data) {
				Assert.assertNotNull(data.getAccountId());
				if (data.getAction()==Action.BALANCE){
					DataSet expected = new DataSet()
						.setAction(Action.BALANCE)
						.setAccountId(data.getAccountId())
						.setService("37coins")
						.setLocale(new Locale("en"))
						.setTo(SENDER2);
					Assert.assertEquals(expected, data);
				}else if (data.getAction()==Action.SIGNUP){
					DataSet expected = new DataSet()
						.setAction(Action.SIGNUP)
						.setAccountId(data.getAccountId())
						.setService("37coins")
						.setLocale(new Locale("en"))
						.setTo(SENDER2);
					Assert.assertEquals(expected, data);
				}else{
					Assert.assertFalse(true);
				}
			}
			@Override
			public void respond(DataSet rsp) {Assert.assertFalse(true);}
		};
		ri.process(SENDER2, "balance");
	}
	
	@Test
	public void testHelp() throws Exception {
		RequestInterpreter ri = new RequestInterpreter(ep) {
			@Override
			public void startWithdrawal(DataSet data, String workflowId) {Assert.assertFalse(true);}
			@Override
			public void startDeposit(DataSet data) {Assert.assertFalse(true);}
			@Override
			public void respond(DataSet rsp) {
				DataSet expected = new DataSet()
				.setAction(Action.HELP)
				.setAccountId(0L)
				.setLocale(new Locale("ko"))
				.setService("37coins")
				.setTo(SENDER1);
			Assert.assertEquals(expected, rsp);
			}
		};
		ri.process(SENDER1, "도움");
	}
	
	@Test
	public void testNonExistingCommand() throws Exception {
		RequestInterpreter ri = new RequestInterpreter(ep) {
			@Override
			public void startWithdrawal(DataSet data, String workflowId) {Assert.assertFalse(true);}
			@Override
			public void startDeposit(DataSet data) {
				DataSet expected = new DataSet()
					.setAction(Action.SIGNUP)
					.setAccountId(data.getAccountId())
					.setLocale(new Locale("en"))
					.setTo(SENDER2);
				Assert.assertEquals(expected, data);
			}
			@Override
			public void respond(DataSet rsp) {
				try {
					System.out.println(new ObjectMapper().writeValueAsString(rsp));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Assert.assertFalse(true);}
		};
		ri.process(SENDER2, "bla");
	}
	
	@Test
	public void testDeposit() throws Exception {
		RequestInterpreter ri = new RequestInterpreter(ep) {
			@Override
			public void startWithdrawal(DataSet data, String workflowId) {Assert.assertFalse(true);}
			@Override
			public void startDeposit(DataSet data) {
				DataSet expected = new DataSet()
					.setAction(Action.DEPOSIT_REQ)
					.setLocale(new Locale("en"))
					.setAccountId(0L)
					.setTo(SENDER1);
				Assert.assertEquals(expected, data);
			}
			@Override
			public void respond(DataSet rsp) {Assert.assertFalse(true);}
		};
		ri.process(SENDER1, "DEPOSIT ");
	}
	
	@Test
	public void testSend() throws Exception {
		RequestInterpreter ri = new RequestInterpreter(ep) {
			@Override
			public void startWithdrawal(DataSet data, String workflowId) {
				DataSet expected = new DataSet()
				.setAction(Action.WITHDRAWAL_REQ)
				.setLocale(new Locale("en"))
				.setService("37coins")
				.setAccountId(0L)
				.setTo(SENDER1)
				.setPayload(new Withdrawal()
					.setAmount(new BigDecimal("0.001"))
					.setMsgDest(new MessageAddress()
						.setAddress("test2@37coins.com"))
					.setPayDest(new PaymentAddress()
						.setAddress(((Withdrawal)data.getPayload()).getPayDest().getAddress())
						.setAddressType(PaymentType.ACCOUNT))
					.setFee(new BigDecimal("0.0002"))
					.setFeeAccount("0")
					.setComment("comment multiple wor"));//each comment truncated to 20 
				Assert.assertEquals(expected, data);
			}
			@Override
			public void startDeposit(DataSet data) {Assert.assertFalse(true);}
			@Override
			public void respond(DataSet rsp) {Assert.assertFalse("did not expect" + rsp,true);}
		};
		ri.process(SENDER1, " send    0.001  test2@37coins.com    ::comment   multiple words");
	}
	
	
	@Test
	public void testSendReverse() throws Exception {
		RequestInterpreter ri = new RequestInterpreter(ep) {
			@Override
			public void startWithdrawal(DataSet data, String workflowId) {
				DataSet expected = new DataSet()
				.setAction(Action.WITHDRAWAL_REQ)
				.setLocale(new Locale("en"))
				.setService("37coins")
				.setAccountId(0L)
				.setTo(SENDER1)
				.setPayload(new Withdrawal()
					.setAmount(new BigDecimal("0.1").setScale(8))
					.setMsgDest(new MessageAddress()
						.setAddress("test2@37coins.com"))
					.setPayDest(new PaymentAddress()
						.setAddress(((Withdrawal)data.getPayload()).getPayDest().getAddress())
						.setAddressType(PaymentType.ACCOUNT))
					.setFee(new BigDecimal("0.0002").setScale(8))
					.setFeeAccount("0"));
				Assert.assertEquals(expected, data);
			}
			@Override
			public void startDeposit(DataSet data) {Assert.assertFalse(true);}
			@Override
			public void respond(DataSet rsp) {Assert.assertFalse(true);}
		};
		ri.process(SENDER1, "send test2@37coins.com 0.1");
	}
	
	@Test
	public void testSendPhone() throws Exception {
		RequestInterpreter ri = new RequestInterpreter(ep) {
			@Override
			public void startWithdrawal(DataSet data, String workflowId) {
				DataSet expected = new DataSet()
					.setAction(Action.WITHDRAWAL_REQ)
					.setLocale(new Locale("en"))
					.setAccountId(data.getAccountId())
					.setTo(USER1)
					.setPayload(new Withdrawal()
						.setAmount(new BigDecimal("0.1").setScale(8))
						.setMsgDest(((Withdrawal)data.getPayload()).getMsgDest())
						.setPayDest(new PaymentAddress()
							.setAddress(((Withdrawal)data.getPayload()).getPayDest().getAddress())
							.setAddressType(PaymentType.ACCOUNT))
						.setFee(new BigDecimal("0.0002").setScale(8))
						.setFeeAccount("1"));
				Assert.assertEquals(expected, data);
			}
			@Override
			public void startDeposit(DataSet data) {
				MessageAddress ma=null;
				try {
					ma = MessageAddress.fromString("01029382039", "821099999999");
				} catch (AddressException | NumberParseException e) {
					e.printStackTrace();
				}
				DataSet expected = new DataSet()
					.setAction(Action.SIGNUP)
					.setAccountId(data.getAccountId())
					.setLocale(new Locale("en"))
					.setTo(ma.setGateway("+821099999999"));
				Assert.assertEquals(expected, data);
			}
			@Override
			public void respond(DataSet rsp) {Assert.assertFalse(true);}
		};
		ri.process(USER1, "send 0.1 01029382039");
	}
	
	@Test
	public void testSendNewPhone() throws Exception {
		RequestInterpreter ri = new RequestInterpreter(ep) {
			@Override
			public void startWithdrawal(DataSet data, String workflowId) {
				DataSet expected = new DataSet()
					.setAction(Action.WITHDRAWAL_REQ)
					.setLocale(new Locale("en"))
					.setAccountId(0L)
					.setTo(SENDER1)
					.setPayload(new Withdrawal()
						.setAmount(new BigDecimal("0.1").setScale(8))
						.setMsgDest(USER1.setGateway(null))
						.setPayDest(new PaymentAddress()
							.setAddress("2")
							.setAddressType(PaymentType.ACCOUNT))
						.setFee(new BigDecimal("0.0002").setScale(8))
						.setFeeAccount("0"));
				Assert.assertEquals(expected, data);
			}
			@Override
			public void startDeposit(DataSet data) {
				DataSet expected = new DataSet()
					.setAction(Action.SIGNUP)
					.setAccountId(data.getAccountId())
					.setLocale(new Locale("en"))
					.setTo(USER1.setGateway("+821099999999"));
				Assert.assertEquals(expected, data);
			}
			@Override
			public void respond(DataSet rsp) {Assert.assertFalse(true);}
		};
		ri.process(SENDER1, "send 0.1 "+USER1.getAddress());
	}
	
	@Test
	public void testSendAddressValid() throws Exception {
		RequestInterpreter ri = new RequestInterpreter(ep) {
			@Override
			public void startWithdrawal(DataSet data, String workflowId) {
				DataSet expected = new DataSet()
					.setAction(Action.WITHDRAWAL_REQ)
					.setLocale(new Locale("en"))
					.setAccountId(0L)
					.setService("37coins")
					.setTo(SENDER1)
					.setPayload(new Withdrawal()
						.setAmount(new BigDecimal("0.1").setScale(8))
						.setPayDest(new PaymentAddress()
							.setAddress("1BLyr8ydFDcbgU9TUPy7NiGSCbq89hBiUf")
							.setAddressType(PaymentType.BTC))
						.setFee(new BigDecimal("0.0002").setScale(8))
						.setFeeAccount("0"));
				Assert.assertEquals(expected, data);
				}
			@Override
			public void startDeposit(DataSet data) {Assert.assertFalse(true);}
			@Override
			public void respond(DataSet rsp) {Assert.assertFalse(true);}
		};
		ri.process(SENDER1, "send 0.1 1BLyr8ydFDcbgU9TUPy7NiGSCbq89hBiUf");
	}	
	
	@Test
	public void testSendAddressTestnet() throws Exception {
		RequestInterpreter ri = new RequestInterpreter(ep) {
			@Override
			public void startWithdrawal(DataSet data, String workflowId) {
				DataSet expected = new DataSet()
					.setAction(Action.WITHDRAWAL_REQ)
					.setLocale(new Locale("en"))
					.setAccountId(0L)
					.setTo(SENDER1)
					.setPayload(new Withdrawal()
						.setAmount(new BigDecimal("0.1").setScale(8))
						.setPayDest(new PaymentAddress()
							.setAddress("mhYxdhvp9kuLypKC3ux6oMPyKTfGm5GaVP")
							.setAddressType(PaymentType.BTC))
						.setFee(new BigDecimal("0.0002").setScale(8))
						.setFeeAccount("0"));
				Assert.assertEquals(expected, data);
			}
			@Override
			public void startDeposit(DataSet data) {Assert.assertFalse(true);}
			@Override
			public void respond(DataSet rsp) {Assert.assertFalse(true);}
		};
		ri.process(SENDER1, "send 0.1 mhYxdhvp9kuLypKC3ux6oMPyKTfGm5GaVP");
	}	
	
	@Test
	public void testSendAddressWrong() throws Exception {
		RequestInterpreter ri = new RequestInterpreter(ep) {
			@Override
			public void startWithdrawal(DataSet data, String workflowId) {Assert.assertFalse(true);}
			@Override
			public void startDeposit(DataSet data) {Assert.assertFalse(true);}
			@Override
			public void respond(DataSet rsp) {
				DataSet expected = new DataSet()
				.setAction(Action.FORMAT_ERROR)
				.setLocale(new Locale("en"))
				.setService("37coins")
				.setTo(SENDER1);
			Assert.assertEquals(expected, rsp);
			}
		};
		ri.process(SENDER1, "send 0.1 mhYxdhvp9kuLypKC3u123MPyKTfGm5GaVP");
	}
	
	@Test
	public void testTransactions() throws Exception {
		RequestInterpreter ri = new RequestInterpreter(ep) {
			@Override
			public void startWithdrawal(DataSet data, String workflowId) {Assert.assertFalse(true);}
			@Override
			public void startDeposit(DataSet data) {
				DataSet expected = new DataSet()
				.setAction(Action.TRANSACTION)
				.setAccountId(0L)
				.setLocale(new Locale("en"))
				.setTo(SENDER1);
				Assert.assertEquals(expected, data);}
			@Override
			public void respond(DataSet rsp) {Assert.assertFalse(true);}
		};
		ri.process(SENDER1, "txns");
	}
	
	@Test
	public void testRequest() throws Exception {
		RequestInterpreter ri = new RequestInterpreter(ep) {
			@Override
			public void startWithdrawal(DataSet data, String workflowId) {
				DataSet expected = new DataSet()
					.setAction(Action.WITHDRAWAL_REQ_OTHER)
					.setLocale(new Locale("en"))
					.setTo(new MessageAddress()
						.setAddress("test2@37coins.com")
						.setGateway("123"))
					.setPayload(new Withdrawal()
						.setAmount(new BigDecimal("0.1").setScale(8))
						.setFee(FEE)
						.setFeeAccount("0")
						.setMsgDest(SENDER1)
						.setPayDest(new PaymentAddress()
							.setAddress(((Withdrawal)data.getPayload()).getPayDest().getAddress())
							.setAddressType(PaymentType.ACCOUNT)));
				Assert.assertEquals(expected, data);
			}
			@Override
			public void startDeposit(DataSet data) {Assert.assertFalse(true);}
			@Override
			public void respond(DataSet rsp) {Assert.assertFalse(true);}
		};
		ri.process(SENDER1, "request test2@37coins.com 0.1");
	}
	
}
