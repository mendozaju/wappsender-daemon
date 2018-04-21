package com.blue.wappsender.daemon.jobs.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

/**
 * Listener de finalizacion del job
 * @author jmendoza
 *
 */
public class JobFinalizedListener extends JobExecutionListenerSupport {
	
	private static final Logger log = LoggerFactory.getLogger(JobFinalizedListener.class);
	
	@Override
	public void afterJob(JobExecution jobExecution) {
		super.afterJob(jobExecution);		
		log.info("------ FINALIZA EL JOB [{}] CON STATUS: {} ------",jobExecution.getJobId(), jobExecution.getStatus());
	}

}
