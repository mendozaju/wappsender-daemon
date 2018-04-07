package com.blue.wappsender.daemon.jobs.sender.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class Scheduler {
	
	private static final Logger log = LoggerFactory.getLogger(Scheduler.class);
	
	@Autowired
	private JobLauncher luncher;
	
	@Autowired
	private Job job;
	
	@Scheduled(cron = "* */30 * * * *")
	public void myScheduler() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		
		log.info("Se ejecuta el JOB para envios de mensajes");
		
		JobParameters parameter = new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters();
		JobExecution execution = luncher.run(job, parameter);
		
		log.info("Finaliza el JOB para envios de mensajes");
		
	}

}
