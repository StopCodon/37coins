package com._37coins.persistence.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Unique;
import javax.validation.constraints.NotNull;

import org.restnucleus.dao.Model;

@PersistenceCapable
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public class Gateway extends Model {
	private static final long serialVersionUID = -1031604697212697657L;
    public static String getHostName(String email){
        String hostName = email.substring(email.indexOf("@") + 1, email.length());
        return hostName;
    }
	
	@Persistent
	@Index
	@NotNull
	private String cn;

	@Persistent
	@NotNull
	@Unique
	@Index
	private String email;
	
    @Persistent
    @NotNull
    private String hostName;	
	
	@Persistent
	private Double fee;
	
	@Persistent
	@Index
	private Integer countryCode;
	
	@Persistent
	private String password;
	
	@Persistent
	@Index
	@Unique
	private String mobile;
	
	@Persistent
	private String apiToken;
	
	@Persistent
	private String apiSecret;
	
	@Persistent
	private Locale locale;
	
	@Persistent
	private String welcomeMsg;
	
	@Persistent
	private String signupCallback;

	public String getMobile() {
        return mobile;
    }

    public Gateway setMobile(String mobile) {
        this.countryCode = Integer.parseInt(mobile.substring(1, 3));
        this.mobile = mobile;
        return this;
    }

    public Locale getLocale() {
        return locale;
    }

    public Gateway setLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public String getEmail() {
		return email;
	}

	public String getHostName() {
        return hostName;
    }

    public Gateway setHostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public String getCn() {
        return cn;
    }

    public Gateway setCn(String cn) {
        this.cn = cn;
        return this;
    }

    public Gateway setEmail(String email) {
		this.email = email;
		this.hostName = getHostName(email);
		return this;
	}

	public Gateway setFee(Double fee) {
		this.fee = fee;
		return this;
	}

	public BigDecimal getFee() {
	    if (null!=fee){
	        return new BigDecimal(fee).setScale(8,RoundingMode.HALF_UP);
	    }else{
	        return null;
	    }
	}

	public Gateway setFee(BigDecimal fee) {
	    if (null!=fee)
	        this.fee = fee.doubleValue();
		return this;
	}

	public String getApiToken() {
        return apiToken;
    }

    public Gateway setApiToken(String apiToken) {
        this.apiToken = apiToken;
        return this;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public Gateway setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
        return this;
    }
	
	public Integer getCountryCode() {
		return countryCode;
	}

	public Gateway setCountryCode(Integer countryCode) {
		this.countryCode = countryCode;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public Gateway setPassword(String password) {
		this.password = password;
		return this;
	}

    public String getWelcomeMsg() {
        return welcomeMsg;
    }

    public Gateway setWelcomeMsg(String welcomeMsg) {
        this.welcomeMsg = welcomeMsg;
        return this;
    }

    public String getSignupCallback() {
        return signupCallback;
    }

    public Gateway setSignupCallback(String signupCallback) {
        this.signupCallback = signupCallback;
        return this;
    }

    @Override
	public void update(Model newInstance) {
		Gateway n = (Gateway) newInstance;
		if (null != n.getFee())this.setFee(n.getFee());
		if (null != n.getCountryCode())this.setCountryCode(n.getCountryCode());
	}

}
