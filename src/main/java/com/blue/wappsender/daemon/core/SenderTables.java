package com.blue.wappsender.daemon.core;

public enum SenderTables {
	
	CAMPAINGS("campaings"),
	DESTINTATIONS("campaing_destinations"),
	MESSAGES("messages");
	
	private String table;
	
	private SenderTables(String table) {
		this.table = table;
	}
	
	public String table() {
		return  this.table;
	}

}
