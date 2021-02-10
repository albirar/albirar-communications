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
package cat.albirar.communications.core.processors.impl;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.NamingStrategy;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitOperations;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

import cat.albirar.communications.core.configuration.IPropertiesComm;
import cat.albirar.communications.core.messages.models.MessageBean;
import cat.albirar.communications.core.processors.IProcessor;
import cat.albirar.communications.core.services.ServiceException;
import cat.albirar.communications.core.status.models.MessageStatusBean;

/**
 * Common properties and behavior for all single message processors.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
public abstract class AbstractSenderProcessor implements IProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSenderProcessor.class);
    
    @Autowired
    protected MessageConverter converter;

    @Autowired
    protected RabbitOperations rabbitTemplate;
    
    @Autowired
    private NamingStrategy namingStrategy;
    
    protected String exchangeName = IPropertiesComm.EXCHANGE_NAME;
    
    private AtomicLong processedMessages = new AtomicLong(0);
    
    private AtomicLong managedMessages = new AtomicLong(0);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessage(Message message) {
        MessageBean msg;
        Object o;
        
        LOGGER.info("Processing single amqp message {}", message);
        managedMessages.incrementAndGet();
        try {
            o = converter.fromMessage(message);
            try {
                msg = (MessageBean) o;
                processMessage(message.getMessageProperties().getMessageId(), msg);
                processedMessages.incrementAndGet();
                LOGGER.info("End of processing single amqp message {}", message);
            } catch(ClassCastException e) {
                LOGGER.error("The single amqp message of class {} is not assignable to class {}", o.getClass(), MessageBean.class);
            } catch (ServiceException e) {
                // Another problem...
                LOGGER.error(String.format("On processing message %s", message), e);
            }
        } catch (MessageConversionException e) {
            LOGGER.error(String.format("Unknow error converting amqp message %s", message), e);
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public long getManagedMessages() {
        return managedMessages.get();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public long getProcessedMessages() {
        return processedMessages.get();
    }
    /**
     * Should to be implemented in order of processing the converted {@code message}.
     * @param message The message
     */
    protected abstract void processMessage(String messageId, MessageBean message);

    /**
     * Report the result of processing a message.
     * @param queueName The queue name to report to
     * @param message The message status
     */
    protected void reportMessage(String queueName, MessageStatusBean message) {
        CorrelationData crlt;
        Message msg;
        MessageProperties msgProps;

        LOGGER.debug("Report on {} the message with id {} ({})", queueName, message.getMessageId(), message);
        crlt = new CorrelationData(message.getMessageId());
        try {
            msgProps = new MessageProperties();
            msgProps.setCorrelationId(message.getMessageId());
            msgProps.setMessageId(namingStrategy.generateName());
            msg = converter.toMessage(message, msgProps);
            rabbitTemplate.send(exchangeName, queueName, msg);
            LOGGER.debug("Message {} reported on {} with id {}!", message, queueName, crlt.getId());
        } catch (MessageConversionException e) {
            String s;
            s = String.format("Error converting status message %s to AMQP message to exchange %s and queue %s", message, exchangeName, queueName);
            LOGGER.error(s, e);
            throw new ServiceException(s, e);
        } catch (AmqpException e) {
            String s;
            s = String.format("Error on send the message %s to exchange %s and queue %s", message, exchangeName, queueName);
            LOGGER.error(s, e);
            throw new ServiceException(s, e);
        }
    }
}
