package com.blue.wappsender.daemon.jobs.sender.reder;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.jdbc.core.BeanPropertyRowMapper;


public class SenderReader implements ItemReader<WhatSappMessageDTO>{
	
	private static final String QUERY = "select * from messages";
	
	public static int count = 1;	
	private DataSource dataSource;
	
	
	private static final Logger log = LoggerFactory.getLogger(SenderReader.class);

	@Override
	public WhatSappMessageDTO read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {		
		
		log.info("Se comienza con la lectura de una tupla");
		
		 JdbcCursorItemReader<WhatSappMessageDTO> databaseReader = new JdbcCursorItemReader<>();		 
	        databaseReader.setDataSource(this.dataSource);
	        databaseReader.setSql(QUERY);
	        databaseReader.setRowMapper(new BeanPropertyRowMapper<>(WhatSappMessageDTO.class));	 
	        return databaseReader.read();				
	}

	
	/**
	 * Set a datasource
	 * @param dataSource
	 */
	public void setDataSouce(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
