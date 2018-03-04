package com.blue.wappsender.daemon.sender;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.blue.wappsender.daemon.jobs.sender.reder.ColumnRangePartitioner;
import com.blue.wappsender.daemon.jobs.sender.reder.CustomPartitioner;
import com.blue.wappsender.daemon.jobs.sender.reder.SenderReader;
import com.blue.wappsender.daemon.jobs.sender.reder.WhatSappMessageDTO;
import com.blue.wappsender.daemon.model.WhatsappMessage;

@Configuration
@EnableBatchProcessing
public class SenderConfig {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	@Qualifier("dataSource")
	public DataSource dataSource;
	

	@Bean
	public SenderProcessor processor() {
		SenderProcessor procesor = new SenderProcessor(dataSource);
		return procesor;
	}

	@Bean
	@Scope("prototype")
	public SenderReader reader() {
		return new SenderReader();
	}

	@Bean
	public SenderWriter writer() {
		return new SenderWriter();
	}
	
	@Bean
	public TaskExecutor taskExecutor(){
	    SimpleAsyncTaskExecutor asyncTaskExecutor=new SimpleAsyncTaskExecutor("spring_batch");
	    asyncTaskExecutor.setConcurrencyLimit(1);
	    return asyncTaskExecutor;
	}

	@Bean
	public Job sendMesagges(SenderListener listener) {
		return jobBuilderFactory.get("adasdads")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(step1())
				.end()
				.build();
	}
	
	@Bean
    public ItemReader<WhatSappMessageDTO> bddReader(DataSource dataSource) {
        JdbcCursorItemReader<WhatSappMessageDTO> databaseReader = new JdbcCursorItemReader<>();

        databaseReader.setDataSource(dataSource);
        databaseReader.setRowMapper(new BeanPropertyRowMapper<>(WhatSappMessageDTO.class));
        databaseReader.setSql("SELECT * FROM blue_wapp.messages");
        databaseReader.setVerifyCursorPosition(Boolean.TRUE);

        return databaseReader;
    }
	
	public ColumnRangePartitioner custonPartitioner() {
		ColumnRangePartitioner partitioner = new ColumnRangePartitioner();
		partitioner.setDataSource(dataSource);
		partitioner.setTable("messages");
		partitioner.setColumn("id");		
		return partitioner;
	}
	
	public Step partitionStep() {
		return stepBuilderFactory.get("partitionStep")
			      .partitioner("slaveStep", custonPartitioner())
			      .step(step1())
			      .taskExecutor(taskExecutor())
			      .build();
	}
	

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").
				<WhatSappMessageDTO, WhatSappMessageDTO>chunk(1)
				.reader(bddReader(dataSource))
				.processor(processor())
				.writer(writer())
				.taskExecutor(taskExecutor())
				.build();
	}
	

}
