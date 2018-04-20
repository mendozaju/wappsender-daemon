package com.blue.wappsender.daemon.jobs.messages.step;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.blue.wappsender.daemon.jobs.messages.step.reader.StepMessagesReader;
import com.blue.wappsender.daemon.jobs.messages.step.writer.StepMessageWriter;
import com.blue.wappsender.daemon.model.Campaign;

/**
 * Configuracion del step encargado de generar la tabla con los mensajes a enviar.
 * @author jmendoza
 *
 */
@Configuration
public class StepMessageBuildConfig {
	
	private final int CONCURRENCY_LIMIT = 5;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	@Qualifier(value="dataSource")
	private DataSource dataSource;	

	@Bean(name="build-messages")
    public Step stepBuildMessages() {
        return stepBuilderFactory.get("step_build_messages")
            .<Campaign, Campaign> chunk(2)
            .reader(this.getReader())
            .writer(this.getWriter())
            .taskExecutor(taskExecutor())
            .build();
    }	
	
	private StepMessageWriter getWriter() {
		return new StepMessageWriter(this.dataSource);
	}

	private StepMessagesReader getReader() {
		return new StepMessagesReader(this.dataSource);
	}
	
	
	public TaskExecutor taskExecutor(){
	    SimpleAsyncTaskExecutor asyncTaskExecutor=new SimpleAsyncTaskExecutor("message-step-");
	    asyncTaskExecutor.setConcurrencyLimit(CONCURRENCY_LIMIT);
	    return asyncTaskExecutor;
	    
	    /* TODO: Analizar para usar mas concurrencia
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setMaxPoolSize(executorsPoolSize);
	    executor.setCorePoolSize(executorsPoolSize);
	    return executor;
	    */
	}
	
}
