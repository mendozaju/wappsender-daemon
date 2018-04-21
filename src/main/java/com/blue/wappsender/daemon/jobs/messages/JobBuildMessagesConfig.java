package com.blue.wappsender.daemon.jobs.messages;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.blue.wappsender.daemon.jobs.common.listener.JobFinalizedListener;

@Configuration
//@EnableBatchProcessing
@EnableScheduling
public class JobBuildMessagesConfig {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	@Qualifier(value="build-messages")
	private Step messageBuildStep;

	
	@Bean(name = "build-messages-job")
	public Job sendMesagges() {
		return jobBuilderFactory.get("whappi_job_build_mesages")
				.start(messageBuildStep)
				.incrementer(new RunIdIncrementer())
				.listener(this.getSenderListener())
				.build();
	}
	
	public JobFinalizedListener getSenderListener() {
		return new JobFinalizedListener();
	}
	
	/*
	@Bean
	@StepScope
	public SenderWriter writer() {
		return new SenderWriter();
	}
	*/


	/*
	@Bean
	@StepScope
	public SenderReader reader(@Value("#{stepExecutionContext}") HashMap<String, String> context) {
		//return new SynchronizedItemReaderAdapter(new SenderReader(dataSource, context));		
		return new SenderReader(this.dataSource, context);
	}
	*/

	
		
	/*
	@Bean
	public TaskExecutor taskExecutor(){
	    SimpleAsyncTaskExecutor asyncTaskExecutor=new SimpleAsyncTaskExecutor("spring_batch");
	    asyncTaskExecutor.setConcurrencyLimit(6);
	    return asyncTaskExecutor;
	}
	*/


	
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
	
	/*
	public ColumnRangePartitioner custonPartitioner() {
		ColumnRangePartitioner partitioner = new ColumnRangePartitioner();
		partitioner.setDataSource(dataSource);
		partitioner.setTable("messages");
		partitioner.setColumn("id");	
		return partitioner;
	}
	*/
	
	/*
	
	public Step partitionStep() {
		return stepBuilderFactory.get("partitionStep")
			      .partitioner("slaveStep", custonPartitioner())
			      .step(step1())
			      .taskExecutor(taskExecutor())
			      .build();
	}
	*/
	
	/*
	public SenderProcessor processor() {
		SenderProcessor procesor = new SenderProcessor(dataSource);
		return procesor;
	}
	*/

	
	/*
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").
				<WhatSappMessageDTO, WhatSappMessageDTO>chunk(6)
				.reader(this.reader)
				.processor(processor())
				.writer(writer())
				.taskExecutor(taskExecutor())
				.build();
	}
	*/
	

}
