package com.blue.wappsender.daemon.jobs.sender;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.jta.SpringJtaSynchronizationAdapter;

import com.blue.wappsender.daemon.core.SynchronizedItemReaderAdapter;
import com.blue.wappsender.daemon.jobs.sender.listener.SenderListener;
import com.blue.wappsender.daemon.jobs.sender.procesor.SenderProcessor;
import com.blue.wappsender.daemon.jobs.sender.reder.ColumnRangePartitioner;
import com.blue.wappsender.daemon.jobs.sender.reder.SenderReader;
import com.blue.wappsender.daemon.jobs.sender.reder.WhatSappMessageDTO;
import com.blue.wappsender.daemon.jobs.sender.writer.SenderWriter;

@Configuration
@EnableBatchProcessing
public class SenderJobConfig {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	@Qualifier("dataSource")
	public DataSource dataSource; //TODO: Cambiar el nombre
	
	@Autowired
	SenderReader reader;	

	@Autowired
	ItemWriter<WhatSappMessageDTO> writer;
	



	@Bean
	@StepScope
	public SenderReader reader(@Value("#{stepExecutionContext}") HashMap<String, String> context) {
		//return new SynchronizedItemReaderAdapter(new SenderReader(dataSource, context));		
		return new SenderReader(this.dataSource, context);
	}

	@Bean
	@StepScope
	public SenderWriter writer() {
		return new SenderWriter();
	}
	
	@Bean
	public TaskExecutor taskExecutor(){
	    SimpleAsyncTaskExecutor asyncTaskExecutor=new SimpleAsyncTaskExecutor("spring_batch");
	    asyncTaskExecutor.setConcurrencyLimit(50);
	    return asyncTaskExecutor;
	}

	@Bean
	public Job sendMesagges(@Qualifier("sender-listener")SenderListener listener) {
		return jobBuilderFactory.get("adads_" + System.currentTimeMillis())
				.start(step1())
				//.incrementer(new RunIdIncrementer())
				//.listener(listener)
				.start(partitionStep())
				.build();
	}
	
	@Bean(name="sender-listener")
	public SenderListener getSenderListener() {
		return new SenderListener();
	}
	
	/*
	@StepScope
	@Bean
    public ItemReader<WhatSappMessageDTO> bddReader(DataSource dataSource) {
        
		
		JdbcCursorItemReader<WhatSappMessageDTO> databaseReader = new JdbcCursorItemReader<>();

        databaseReader.setDataSource(dataSource);
        databaseReader.setRowMapper(new BeanPropertyRowMapper<>(WhatSappMessageDTO.class));
        databaseReader.setSql("SELECT * FROM blue_wapp.messages");
        databaseReader.setVerifyCursorPosition(Boolean.TRUE);

        return databaseReader;
    }
    */
	
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
	
	
	public SenderProcessor processor() {
		SenderProcessor procesor = new SenderProcessor(dataSource);
		return procesor;
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").
				<WhatSappMessageDTO, WhatSappMessageDTO>chunk(10)
				.reader(this.reader)
				.processor(processor())
				.writer(writer())
				.taskExecutor(taskExecutor())
				.build();
	}
	

}
