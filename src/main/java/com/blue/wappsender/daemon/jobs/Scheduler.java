package com.blue.wappsender.daemon.jobs;

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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
//@EnableScheduling
public class Scheduler {

	private static final Logger log = LoggerFactory.getLogger(Scheduler.class);
	private final String CRON = "0 0/2 * * * *";

	@Autowired
	private JobLauncher luncher;

	@Autowired
	@Qualifier(value = "build-messages-job")
	private Job jobBuildMessage;

	@Autowired
	@Qualifier(value = "send-messages-job")
	private Job jobSender;

	@Scheduled(cron = CRON)
	public void myScheduler() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		/*
		log.info("Se ejecuta el JOB para la consutruccion de mensajes a enviar - Sheduler expresion:[{}]", CRON);
		JobParameters parameter = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		JobExecution execution = luncher.run(jobBuildMessage, parameter);
		log.info("Finaliza el JOB de construccion de mensajes - Status:[{}]", execution.getStatus().toString());
		 */
		
		log.info("Se ejecuta el JOB para el envio de mensajes - Sheduler expresion:[{}]", CRON);
		JobParameters parameterSend = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		JobExecution executionSend = luncher.run(jobSender, parameterSend);
		log.info("Finaliza el JOB de construccion de mensajes - Status:[{}]", executionSend.getStatus().toString());

	}

}
