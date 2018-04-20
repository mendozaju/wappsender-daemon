package com.blue.wappsender.daemon.model;

import java.time.LocalDateTime;

/**
 * Clase que representa las campañas.
 * @author jmendoza
 *
 */
public class Campaign {

	private String id;
	private String text;
	private String description;
	private LocalDateTime activationDate;
	private String status; // TODO: Ver de usar un enum
	private String userId;

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getStatus() {
		return status;
	}

	public String getUserId() {
		return userId;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setText(String text) {
		this.text = text;
	}


	public void setStatus(String status) {
		this.status = status;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public LocalDateTime getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(LocalDateTime activationDate) {
		this.activationDate = activationDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
