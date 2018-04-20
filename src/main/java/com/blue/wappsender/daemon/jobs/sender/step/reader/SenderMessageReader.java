package com.blue.wappsender.daemon.jobs.sender.step.reader;

import java.time.LocalDateTime;
import java.util.Iterator;
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
import com.blue.wappsender.daemon.model.Message;

/**
 * Clase encargada de la lectura de los mensajes a ser enviados via WHATSAPP
 * 
 * @author jmendoza
 *
 */
public class SenderMessageReader implements ItemReader<Message> {
	private static final Logger LOG = LoggerFactory.getLogger(SenderMessageReader.class);

	private JdbcTemplate jdbcTemplate;

	public SenderMessageReader(DataSource datasource) {
		this.jdbcTemplate = new JdbcTemplate(datasource);
	}

	@Override
	public Message read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

		LOG.info("Se comienza con la lectura de mensajes a enviar");
		String query = String.format("SELECT * FROM %s WHERE status = ? AND send_date <= ? LIMIT 1 FOR UPDATE",
				SenderTables.MESSAGES.table());
		Object[] parameters = { MessageJobStatus.PENDING_TO_SEND.toString(), LocalDateTime.now() };
		LOG.info("Query para obtencion de mensaje:[{}] - parametros:[{}]",query, parameters);
		List<Message> messageToSend = this.jdbcTemplate.query(query, parameters,
				new BeanPropertyRowMapper<Message>(Message.class));

		Iterator<Message> it = messageToSend.iterator();
		if (it.hasNext()) {
			Message message = it.next();
			if (message.getNumber() != null) {
				LOG.info("Mensaje de la campaña:[{}] para el numero:[{}]", message.getCampaignId(),message.getNumber());
				this.setMessageInProcessToSend(message);				
				return message;
			}
		}
		LOG.info("No se identificaron mas mensajes a enviar");
		return null;
	}

	/**
	 * Marca el mensaje como un mensaje en proceso de envio.
	 * @param message
	 */
	private void setMessageInProcessToSend(Message message) {
		String query = String.format("UPDATE %s SET status = ?, status_date = ? WHERE id = ?", SenderTables.MESSAGES.table());
		Object[] parameters = { MessageJobStatus.PROGRESS_TO_SEND.toString(), LocalDateTime.now(), message.getId()};
		LOG.info("Query de actualizacion:[{}] - parametros:[{}]",query,parameters);
		int result = this.jdbcTemplate.update(query,parameters);
		LOG.info("Se actualizo :[{}] mensaje a in progress to send:[{}]",result, MessageJobStatus.PROGRESS_TO_SEND.toString());
		
	}

}
