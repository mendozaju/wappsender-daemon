package com.blue.wappsender.daemon.jobs.sender.procesor;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.JdbcTemplate;

import com.blue.wappsender.daemon.core.SenderTables;
import com.blue.wappsender.daemon.jobs.sender.step.reader.deprecated.WhatSappMessageDTO;

/**
 * Procesamiento de los mensajes de whatsapp
 * @author jmendoza
 *
 */
public class SenderProcessor implements ItemProcessor<WhatSappMessageDTO, WhatSappMessageDTO>{
	
	private static final Logger log = LoggerFactory.getLogger(SenderProcessor.class);
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	public SenderProcessor(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public WhatSappMessageDTO process(WhatSappMessageDTO message) throws Exception {
		
		
		
		log.info("El thread :[{}] procesa el mensaje con id: [{}]",Thread.currentThread().getName(), message.getId());
		
		Integer resutl = jdbcTemplate.update("update " +SenderTables.CAMPAINGS.table()+" set status = 'COMPLETE' where id = ? ", message.getId());
		//log.info("Resultado ->" + resutl);
		
		//Thread.sleep(10000);
		
		return message;
	}

}
