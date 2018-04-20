package com.blue.wappsender.daemon.jobs.sender;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.blue.wappsender.daemon.jobs.messages.step.listener.SenderListener;

/**
 * Configuracion del JOB para el envio de los mensajes
 * @author jmendoza
 *
 */
@Configuration
@EnableBatchProcessing
public class JobSenderConfig {
	
	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	@Qualifier(value="send-messages")
	Step sendMessageStep;
	
	
	@Bean(name = "send-messages-job")
	public Job sendMesagges() {
		return jobBuilderFactory.get("whappi_job_build_mesages")
				.start(sendMessageStep)
				.incrementer(new RunIdIncrementer())
				.listener(this.getSenderListener())
				.build();
	}

	public SenderListener getSenderListener() {
		return new SenderListener();
	}
}
