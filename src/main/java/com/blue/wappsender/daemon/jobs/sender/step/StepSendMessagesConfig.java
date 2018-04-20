package com.blue.wappsender.daemon.jobs.sender.step;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.blue.wappsender.daemon.jobs.sender.step.reader.SenderMessageReader;
import com.blue.wappsender.daemon.jobs.sender.step.writer.SenderMessageWriter;
import com.blue.wappsender.daemon.model.Message;

@Configuration
public class StepSendMessagesConfig {
	
	private static final int CONCURRENCY_LIMIT = 5;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	@Qualifier(value="dataSource")
	private DataSource dataSource;	

	@Bean(name="send-messages")
    public Step stepBuildMessages() {
        return stepBuilderFactory.get("step_send_messages")
            .<Message, Message> chunk(10)
            .reader(this.getReader())
            .writer(this.getWriter())
            .taskExecutor(taskExecutor())
            .build();
    }

	private TaskExecutor taskExecutor() {
		 SimpleAsyncTaskExecutor asyncTaskExecutor=new SimpleAsyncTaskExecutor("sended-step-");
		    asyncTaskExecutor.setConcurrencyLimit(CONCURRENCY_LIMIT);
		    return asyncTaskExecutor;
	}

	private ItemWriter<? super Message> getWriter() {
		return new SenderMessageWriter(dataSource);
	}

	private SenderMessageReader getReader() {
		return new SenderMessageReader(this.dataSource);
	}	

}
