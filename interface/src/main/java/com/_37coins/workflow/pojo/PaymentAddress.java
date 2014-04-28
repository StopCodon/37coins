package com._37coins.workflow.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PaymentAddress {
	
	public enum PaymentType {
		BTC, 
		ACCOUNT;
	}
	
	private String address;
	
	private PaymentType addressType;
	
	private String displayName;

	public String getAddress() {
		return address;
	}

	public PaymentAddress setAddress(String address) {
		this.address = address;
		return this;
	}

	public PaymentType getAddressType() {
		return addressType;
	}

	public PaymentAddress setAddressType(PaymentType addressType) {
		this.addressType = addressType;
		return this;
	}

	public String getDisplayName() {
		return displayName;
	}

	public PaymentAddress setDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

}
