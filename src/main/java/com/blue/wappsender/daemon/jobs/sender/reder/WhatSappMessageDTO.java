package com.blue.wappsender.daemon.jobs.sender.reder;

/**
 * Clase que representa un mensaje de whatsapp de la bbdd
 * 
 * @author jmendoza
 *
 */
public class WhatSappMessageDTO {

	private int id;
	private String number;
	private String text;
	private int intent;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getIntent() {
		return intent;
	}

	public void setIntent(int intent) {
		this.intent = intent;
	}


}
