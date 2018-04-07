package com.blue.wappsender.daemon.jobs.sender.reder;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;


public class SenderReader implements ItemReader<WhatSappMessageDTO>{
	
	private static final String QUERY = "select * from messages";
	
	public static int count = 1;	
	private DataSource dataSource;	
	private  HashMap<String, String>  context;
	
	
	private static final Logger log = LoggerFactory.getLogger(SenderReader.class);
	
	public SenderReader(DataSource datasource , HashMap<String, String> context ) {
		this.dataSource = datasource;
		this.context = context;
	}

	@Override
	public WhatSappMessageDTO read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

		log.info("Se comienza con la lectura de una tupla. Leyendo en el thread[{}]",Thread.currentThread().getName());
		
		
		String from  = this.context.get("minValue"); //TODO: Sacarlo a un enum
		String to = this.context.get("maxValue");
		log.info("Valores a procesar - from:[{}] to:[{}]",from, to);

		final SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
		queryProvider.setDataSource(dataSource);
		queryProvider.setSelectClause("select *");
		queryProvider.setFromClause("from messages");
		queryProvider.setWhereClause("where id >= " + from + " and id <= " + to + " and thread = \"NOT\"");
		queryProvider.setSortKey("id");
		
		
		JdbcPagingItemReader<WhatSappMessageDTO> databaseReader = new JdbcPagingItemReader<WhatSappMessageDTO>();
		databaseReader.setDataSource(dataSource);
		databaseReader.setPageSize(10);
		databaseReader.setRowMapper(new BeanPropertyRowMapper<>(WhatSappMessageDTO.class));
		databaseReader.setQueryProvider(queryProvider.getObject());
		
		databaseReader.afterPropertiesSet();
		databaseReader.setSaveState(true);
		
		//JdbcCursorItemReader<WhatSappMessageDTO> databaseReader = new JdbcCursorItemReader<>();
		//databaseReader.setDataSource(this.dataSource);
		//databaseReader.setSql(QUERY);
		//databaseReader.setRowMapper(new BeanPropertyRowMapper<>(WhatSappMessageDTO.class));
		
		//WhatSappMessageDTO result = databaseReader.read();
		//WhatSappMessageDTO result = new WhatSappMessageDTO();
		
		
        //databaseReader.setPageSize(1);
        //databaseReader.setRowMapper(new BeanPropertyRowMapper<>(StudentDTO.class));

        //PagingQueryProvider queryProvider = createQueryProvider();
        //databaseReader.setQueryProvider(queryProvider);
		
		log.info("Se entrega la informacion a procesar por el thread:[{}]",Thread.currentThread().getName());
        return databaseReader.read();

		
		
		//return result;
	}

	
	/**
	 * Set a datasource
	 * @param dataSource
	 */
	public void setDataSouce(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
