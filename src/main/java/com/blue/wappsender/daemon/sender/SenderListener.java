package com.blue.wappsender.daemon.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class SenderListener extends JobExecutionListenerSupport {
	
	private static final Logger log = LoggerFactory.getLogger(SenderListener.class);
	
	@Override
	public void afterJob(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		super.afterJob(jobExecution);
		
		log.debug("Se escucho como termino el job");
	}

}
