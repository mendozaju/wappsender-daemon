package com.blue.wappsender.daemon.jobs.messages.step.reader;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.blue.wappsender.daemon.core.SenderTables;
import com.blue.wappsender.daemon.jobs.messages.step.MessageJobStatus;
import com.blue.wappsender.daemon.model.Campaign;

/**
 * Reader utilizado para obtener las campañas pendientes de procesar
 * 
 * @author jmendoza |
 */

public class StepMessagesReader implements ItemReader<Campaign> {
	private static final Logger LOG = LoggerFactory.getLogger(StepMessagesReader.class);

	private JdbcTemplate jdbcTemplate;

	public StepMessagesReader(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public Campaign read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

		List<Campaign> campaigns = new ArrayList<Campaign>();
		campaigns = getCampaingToProces();		
		LOG.info("ID:[{}]", (campaigns.size()>0) ? campaigns.get(0).getId(): null);

		if (campaigns.iterator().hasNext()) {
			Campaign aCampaign = campaigns.iterator().next();
			if (aCampaign.getId() != null) {
				this.setCampaignAsBuild(aCampaign);
				return aCampaign;
			}
		}
		LOG.info("No se identificarion mas campañas a procesar");
		return null;
	}

	/**
	 * Retorna una campaña a procesar para construir los mensajes
	 * @return
	 */
	private List<Campaign> getCampaingToProces() {
		List<Campaign> campaigns;
		String query = "SELECT * FROM campaings where status = ? ORDER BY id LIMIT 1 FOR UPDATE";
		Object[] params = { MessageJobStatus.PENDING.toString() };
		LOG.info("Query de lectura.[{}] - parametros:[{}]", query, params);

		campaigns = this.jdbcTemplate.query(query, params, new BeanPropertyRowMapper<Campaign>(Campaign.class));
		return campaigns;
	}

	/**
	 * Coloca la campaña en estado de construccion - (BUILDING)
	 * @param aCampaign
	 */
	private void setCampaignAsBuild(Campaign aCampaign) {
		String queryUpdate = String.format("UPDATE %s SET STATUS = ? where id = ?", SenderTables.CAMPAINGS.table());
		Object[] paramsUpdate = { MessageJobStatus.BUILDING.toString(), aCampaign.getId() };
		this.jdbcTemplate.update(queryUpdate, paramsUpdate);
	}

}
