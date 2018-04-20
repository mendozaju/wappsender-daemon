package com.blue.wappsender.daemon.jobs.messages.step.writer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.blue.wappsender.daemon.core.SenderTables;
import com.blue.wappsender.daemon.jobs.messages.step.MessageJobStatus;
import com.blue.wappsender.daemon.jobs.sender.SenderStatus;
import com.blue.wappsender.daemon.model.Campaign;
import com.blue.wappsender.daemon.model.Message;

/**
 * Writer del step para el armado de los mensajes
 * @author jmendoza
 *
 */
public class StepMessageWriter implements ItemWriter<Campaign>{
	
	private static final Logger LOG = LoggerFactory.getLogger(StepMessageWriter.class);
	
	private JdbcTemplate jdbcTemplate;
	
	public StepMessageWriter(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void write(List<? extends Campaign> campaigns) throws Exception {
		List<Message> messages = new ArrayList<Message>();		
		messages = this.getMessagesToPerist(campaigns);
		
		Iterator<Message> it = messages.iterator();
		while(it.hasNext()) {
			this.createMessageToSend(it.next());
		}
		
		this.setCampaignAsProceced(campaigns.get(0));
		
		
	}
	
	/**
	 * Metodo que marca la campaña con los mensajes ya preparados.
	 * @param campaign
	 */
	private void setCampaignAsProceced(Campaign campaign) {
		String query =  String.format("UPDATE %s SET status = ? where id = ?", SenderTables.CAMPAINGS.table());
		Object[] params = {SenderStatus.BUILT.toString(),campaign.getId()};
		LOG.info("Query de creacion de mensaje:[{}] - Parametros:[{}]",query,params);		
		int result = this.jdbcTemplate.update(query,params);
		LOG.info("Registros actualizados:[{}] - Se actualizo la campaña :[{}] con el estado:[{}]",result, campaign.getId(),SenderStatus.BUILT);
	}

	/**
	 * Crea el mensaje en la tabla de mensajes con estado pendiente para luego ser enviado.
	 * @param message
	 */
	private void createMessageToSend(Message message) {
		String query =  String.format("INSERT INTO %s (phone, status, status_date, campaign_id, send_date, message) VALUES (?,?,?,?,?,?)",SenderTables.MESSAGES.table());
		Object[] params = {message.getNumber(),MessageJobStatus.PENDING_TO_SEND.toString(),LocalDateTime.now(),message.getCampaignId(), message.getActivationDate(), message.getMessage()};
		LOG.info("Query de creacion de mensaje:[{}] - Parametros:[{}]",query,params);		
		int result = this.jdbcTemplate.update(query,params);
		LOG.info("Registros actualizados:[{}] - Se crea el mensaje de la campaña:[{}] para el numero:[{}]",result,message.getCampaignId(), message.getNumber());		
	}

	/**
	 * Retorna la lista de mensajes que se van a persistir para comenzar a procesar.
	 * @return
	 */
	private List<Message> getMessagesToPerist(List<? extends Campaign> campaigns){		
		LOG.info("Se comienzan a obtener los mensajes a persistir para las campañas");
		List<Message> totalMessages = new ArrayList<Message>();
		
		Iterator<? extends Campaign> it = campaigns.iterator();
		while(it.hasNext()) {
			Campaign aCampaign = it.next();			
			List<Message> messgesOfCampaign = this.getMessagesOfCampaign(aCampaign);
			this.fillMessagesInformation(aCampaign, messgesOfCampaign);					
			LOG.info("Se agregan:[{}] mensajes", messgesOfCampaign.size());
			totalMessages.addAll(messgesOfCampaign);			
		}		
		
		LOG.info("Cantidad de mensajes obtenidos:[{}]", totalMessages.size());
		return totalMessages;		
	}

	/**
	 * Completa la informacion de los mensajes
	 * @param aCampaign
	 * @param messgesOfCampaign
	 */
	private void fillMessagesInformation(Campaign aCampaign, List<Message> messgesOfCampaign) {
		Iterator<Message> messagesIt = messgesOfCampaign.iterator();
		while(messagesIt.hasNext()) {
			Message aMessage = messagesIt.next();
			aMessage.setActivationDate(aCampaign.getActivationDate());
			aMessage.setMessage(aCampaign.getText());
		}
	}

	/**
	 * Obtiene los mensajes de las campañas
	 * @param aCampaign
	 * @return
	 */
	private List<Message> getMessagesOfCampaign(Campaign aCampaign) {
		String query =  String.format("SELECT * FROM %s where campaign_id = ?", SenderTables.DESTINTATIONS.table());
		Object[] params = {aCampaign.getId()};
		LOG.info("Query de obtencion:[{}] - parametros:[{}]", query, params);		
		
		List<Message> result = this.jdbcTemplate.query(query, params,  new BeanPropertyRowMapper<Message>(Message.class));
		return result;
	}

}
