package com._37coins.web;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class Charge {
	
	private BigDecimal amount;
	
	private String source;
	
	private String token;

	public BigDecimal getAmount() {
		return amount;
	}

	public Charge setAmount(BigDecimal amount) {
		this.amount = amount;
		return this;
	}

	public String getSource() {
		return source;
	}

	public Charge setSource(String source) {
		this.source = source;
		return this;
	}

	public String getToken() {
		return token;
	}

	public Charge setToken(String token) {
		this.token = token;
		return this;
	}

}
