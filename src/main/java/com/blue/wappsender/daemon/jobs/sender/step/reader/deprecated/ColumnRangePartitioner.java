package com.blue.wappsender.daemon.jobs.sender.step.reader.deprecated;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import com.blue.wappsender.daemon.core.SenderTables;
import com.blue.wappsender.daemon.jobs.sender.SenderStatus;
import com.blue.wappsender.daemon.jobs.sender.procesor.SenderProcessor;
/**
 * @author Michael Minella
 */
public class ColumnRangePartitioner implements Partitioner {
	
	private static final Logger log = LoggerFactory.getLogger(ColumnRangePartitioner.class);

	private JdbcOperations jdbcTemplate;

	private String table;

	private String column;

	/**
	 * The name of the SQL table the data are in.
	 *
	 * @param table the name of the table
	 */
	public void setTable(String table) {
		this.table = table;
	}

	/**
	 * The name of the column to partition.
	 *
	 * @param column the column name.
	 */
	public void setColumn(String column) {
		this.column = column;
	}

	/**
	 * The data source for connecting to the database.
	 *
	 * @param dataSource a {@link DataSource}
	 */
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/**
	 * Partition a database table assuming that the data in the column specified
	 * are uniformly distributed. The execution context values will have keys
	 * <code>minValue</code> and <code>maxValue</code> specifying the range of
	 * values to consider in each partition.
	 *
	 * @see Partitioner#partition(int)
	 */
	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		log.info("Ejecutando particion... GRID SIZE:[{}]",gridSize);
		
		int min = jdbcTemplate.queryForObject("SELECT MIN(" + column + ") from " + SenderTables.CAMPAINGS.table() + " where status <> \"" + SenderStatus.COMPLETE + "\"", Integer.class);
		int max = jdbcTemplate.queryForObject("SELECT MAX(" + column + ") from " + SenderTables.CAMPAINGS.table() + " where status <> \"" + SenderStatus.COMPLETE + "\"", Integer.class);
		//int min = jdbcTemplate.queryForObject("SELECT MIN(" + column + ") from " + SenderTables.ORIGIN.table(), Integer.class);
		//int max = jdbcTemplate.queryForObject("SELECT MAX(" + column + ") from " + SenderTables.ORIGIN.table(), Integer.class);
		
		log.info("Valor minimo:[{}], Valor maximo:[{}]",min,max);
		
		/*
		int targetSize = (max - min) / gridSize;

		Map<String, ExecutionContext> result = new HashMap<String, ExecutionContext>();
		int number = 0;
		int start = min;
		int end = start + targetSize - 1;

		while (start <= max) {
			ExecutionContext value = new ExecutionContext();
			result.put("partition" + number, value);

			if (end >= max) {
				end = max;
			}
			value.putInt("minValue", start);
			value.putInt("maxValue", end);
			start += targetSize;
			end += targetSize;
			number++;
		}
		*/
		
		Map<String, ExecutionContext> result = new HashMap<String, ExecutionContext>();
		
		if ( (max - min) < gridSize) {
			result = this.getDefaultPartition(gridSize,min,max);
		}else {			
			result = this.getCalculatedPartitions(gridSize, min, max);			
		}
		log.info("Se entrega el resultado de la particion:[{}]",result);
		return result;
	}

	/**
	 * Calcula el tamaño de las particiones
	 * @param gridSize
	 * @param min
	 * @param max
	 * @return
	 */
	private Map<String, ExecutionContext> getCalculatedPartitions(int gridSize, int min, int max) {
		Map<String, ExecutionContext> result = new HashMap<String, ExecutionContext>();
		
		int partitionSize = (max - min) / gridSize;
		int start = min;
		int end = start + partitionSize;
		
		for (int partition = 1; partition <= gridSize; partition++) {
			ExecutionContext value = new ExecutionContext();
			value.putInt("minValue", start);
			value.putInt("maxValue", end);
			value.put("APPLY", true);
			
			result.put("partition_" + partition, value);
			
			start = start +  partitionSize + 1;
			
			if(end + partitionSize + 1 < max) {
				end =  end + partitionSize + 1;
			}else {
				end =  end + partitionSize;
			}			
					 		
		}
		return result;
	}

	/**
	 * Retorna el mapa de reustados con todos los ids en la primera particion
	 * @param gridSize
	 * @param min
	 * @param max
	 * @return 
	 */
	private Map<String, ExecutionContext> getDefaultPartition(int gridSize, int min, int max) {
		Map<String, ExecutionContext> result = new HashMap<String, ExecutionContext>();		
		ExecutionContext firstPartition = new ExecutionContext();
		firstPartition.putInt("minValue", min);
		firstPartition.putInt("maxValue", max);
		firstPartition.put("APPLY", true);
		result.put("partition_1",firstPartition);

		for (int partition = 2; partition < gridSize; partition++) {
			ExecutionContext value = new ExecutionContext();
			value.putInt("minValue", 0);
			value.putInt("maxValue", 0);
			value.put("APPLY", false);
			result.put("partition_" + partition, value);
		}
		
		return result;
		
	}}