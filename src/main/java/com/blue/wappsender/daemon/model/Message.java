package com.blue.wappsender.daemon.model;

import java.time.LocalDateTime;

/**
 * Clase que representa un mensjae.
 * @author jmendoza
 *
 */
public class Message {
	private String id;
	private String number;
	private String campaignId;
	private LocalDateTime activationDate;
	private String message;
	
	public String getPhone() {
		return this.number;
	}
	
	public void setPhone(String phone) {
		this.number = phone;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public LocalDateTime getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(LocalDateTime activationDate) {
		this.activationDate = activationDate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
