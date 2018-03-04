package com.blue.wappsender.daemon.jobs.sender.reder;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

public class CustomPartitioner implements Partitioner{
	
	private static final Logger log = LoggerFactory.getLogger(CustomPartitioner.class);
	
	
	private final int COUNT = 100;
	private final int RANGE = 10;
	
	private int from;
	private int to;	
	

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		
		Map<String, ExecutionContext> result
        = new HashMap<String, ExecutionContext>();
		
		
		for (int i = 1; i <= COUNT; i++) {
			
			ExecutionContext value = new ExecutionContext();

			log.info("\nStarting : Thread" + i);
			log.info("fromId : " + from);
			log.info("toId : " + to);

			value.putInt("fromId", from);
			value.putInt("toId", to);

			// give each thread a name, thread 1,2,3
			value.putString("name", "Thread" + i);

			result.put("partition" + i, value);

			from = to + 1;
			to += RANGE;

		}

		return result;
	}
	
	

}
