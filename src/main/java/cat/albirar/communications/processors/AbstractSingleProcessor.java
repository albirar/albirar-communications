/*
 * This file is part of "albirar-communications".
 * 
 * "albirar-communications" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * "albirar-communications" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with "albirar-communications" source code.  If not, see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *
 * Copyright (C) 2020 Octavi Fornés
 */
package cat.albirar.communications.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.NamingStrategy;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

import cat.albirar.communications.configuration.PropertiesComm;
import cat.albirar.communications.messages.models.MessageBean;
import cat.albirar.communications.status.models.MessageStatusBean;

/**
 * Common properties and behavior for all single message processors.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
public abstract class AbstractSingleProcessor implements MessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSingleProcessor.class);
    
    @Autowired
    protected MessageConverter converter;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private NamingStrategy namingStrategy;
    
    private String exchangeName = PropertiesComm.EXCHANGE_NAME;

    private String emailReportQueueName = PropertiesComm.QUEUE_REPORT_EMAIL;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessage(Message message) {
        MessageBean msg;
        Object o;
        
        LOGGER.info("Processing single amqp message {}", message);

        try {
            o = converter.fromMessage(message);
            try {
                msg = (MessageBean) o;
                try {
                    processMessage(message.getMessageProperties().getMessageId(), msg);
                    LOGGER.info("End of processing single amqp message {}", message);
                } catch (Exception e) {
                    LOGGER.error(String.format("Unknow error %s processing amqp message %s", e.getMessage(), message), e);
                }
            } catch(ClassCastException e) {
                LOGGER.error("The single amqp message of class {} is not assignable to class {}", o.getClass(), MessageBean.class);
            }
        } catch (Exception e) {
            LOGGER.error(String.format("Unknow error %s converting amqp message %s", e.getMessage(), message), e);
        }
    }
    /**
     * Should to be implemented in order of processing the converted {@code message}.
     * @param message The message
     */
    protected abstract void processMessage(String messageId, MessageBean message);

    /**
     * Report the result of processing a message.
     * @param message The message status
     */
    protected void reportMessage(MessageStatusBean message) {
        CorrelationData crlt;

        LOGGER.debug("Report message with id {} ({})", message.getMessageId(), message);
        crlt = new CorrelationData();
        crlt.setId(namingStrategy.generateName());
        rabbitTemplate.convertAndSend(exchangeName, emailReportQueueName, message, crlt);
        LOGGER.debug("Message {} reported with id {}!", message, crlt.getId());
    }}
