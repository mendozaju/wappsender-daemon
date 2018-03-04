package com.blue.wappsender.daemon.sender;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import com.blue.wappsender.daemon.jobs.sender.reder.WhatSappMessageDTO;

public class SenderWriter implements ItemWriter<WhatSappMessageDTO>{
	
	private static final Logger log = LoggerFactory.getLogger(SenderWriter.class);

	@Override
	public void write(List<? extends WhatSappMessageDTO> arg0) throws Exception {
		
		log.info("Se escribe el valor del WS como enviado en algun lado");
		
	}

}
