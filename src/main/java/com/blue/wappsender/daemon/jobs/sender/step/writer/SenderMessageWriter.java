package com.blue.wappsender.daemon.jobs.sender.step.writer;

import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.blue.wappsender.daemon.core.SenderTables;
import com.blue.wappsender.daemon.jobs.messages.step.MessageJobStatus;
import com.blue.wappsender.daemon.model.Message;

public class SenderMessageWriter implements ItemWriter<Message> {

	private static final Logger LOG = LoggerFactory.getLogger(Message.class);
	
	private JdbcTemplate jdbcTemplate;
	
	public SenderMessageWriter(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void write(List<? extends Message> messages) throws Exception {
		LOG.info("Se comienza con el envio de mensajes");
		
		Iterator<? extends Message> it = messages.iterator();
		while(it.hasNext()) {
			Message aMessage = it.next();
			LOG.info("Se realiza en envio para el numero:[{}] - con el texto:[{}]", aMessage.getNumber(), aMessage.getMessage());
			this.sendMessage(aMessage);
			this.setMessageAsSended(aMessage);
			
		}
	}

	/**
	 * Realiza en envio del mensaje
	 * @param aMessage
	 */
	private void sendMessage(Message aMessage) {
		// TODO tengo que ver de obtener las credeciales de envio para el cliente y crear el cliente pare realizar el envio.
		
	}

	/**
	 * Marca el mensaje como enviado
	 * @param aMessage
	 */
	private void setMessageAsSended(Message aMessage) {
		String query = String.format("UPDATE %s SET status = ? WHERE id = ?", SenderTables.MESSAGES.table());
		Object[] parameters = {MessageJobStatus.SENDED.toString(), aMessage.getId()};
		LOG.info("Query de actualizacion [{}] parametros:[{}]",query,parameters);
		int result = this.jdbcTemplate.update(query,parameters);
		LOG.info("Se actualizaron [{}] registros.");
		
	}

}
