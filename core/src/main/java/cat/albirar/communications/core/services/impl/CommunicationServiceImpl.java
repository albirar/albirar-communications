/*
 * This file is part of "albirar-communications".
 * 
 * "albirar-communications" is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * "albirar-communications" is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with "albirar-communications" source code. If
 * not, see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *
 * Copyright (C) 2020 Octavi Fornés
 */
package cat.albirar.communications.core.services.impl;

import java.util.Optional;

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
import org.springframework.stereotype.Service;

import cat.albirar.communications.core.channels.models.ECommunicationChannelType;
import cat.albirar.communications.core.configuration.IPropertiesComm;
import cat.albirar.communications.core.messages.models.MessageBean;
import cat.albirar.communications.core.services.ICommunicationService;
import cat.albirar.communications.core.services.IReportRegister;
import cat.albirar.communications.core.services.ServiceException;
import cat.albirar.communications.core.status.models.MessageStatusBean;

/**
 * {@link ICommunicationService} implementation.
 * 
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Service
public class CommunicationServiceImpl implements ICommunicationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommunicationServiceImpl.class);

    @Autowired
    private RabbitOperations rabbitTemplate;
    
    @Autowired
    private MessageConverter messageConverter;

    @Autowired
    private NamingStrategy namingStrategy;
    
    @Autowired
    private IReportRegister reportRegister;

    private String exchangeName = IPropertiesComm.EXCHANGE_NAME;

    private String emailSendQueueName = IPropertiesComm.QUEUE_SEND_EMAIL;

    private String smsSendQueueName = IPropertiesComm.QUEUE_SEND_SMS;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<MessageStatusBean> popStatusMessage(String messageId) {
        return reportRegister.getAndDeleteReport(messageId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStatusMessage(String messageId) {
        return reportRegister.isReport(messageId);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String pushMessage(MessageBean message) {
        CorrelationData crlt;
        String rKey;
        MessageProperties mp;
        Message msg;

        LOGGER.debug("Pushing message {}...", message);
        crlt = createCorrelationData();
        if(message.getReceiver().getChannelBean().getChannelType() == ECommunicationChannelType.EMAIL) {
            rKey = emailSendQueueName;
        } else {
            rKey = smsSendQueueName;
        }
        try {
            mp = new MessageProperties();
            mp.setMessageId(crlt.getId());
            msg = messageConverter.toMessage(message, mp);
            rabbitTemplate.send(exchangeName, rKey, msg, crlt);
            LOGGER.debug("Message ({}) pushed onto {} with id {}", message, rKey, crlt.getId());
            return crlt.getId();
        } catch (MessageConversionException e) {
            String s;
            s = String.format("Error converting message %s to AMQP message with %s destination", message, rKey);
            LOGGER.error(s, e);
            throw new ServiceException(s, e);
        } catch (AmqpException e) {
            String s;
            s = String.format("Cannot push message %s onto %s", message, rKey);
            LOGGER.error(s, e);
            throw new ServiceException(s, e);
        }
    }
    /**
     * Create the {@link CorrelationData} for a message.
     * The {@link CorrelationData#getId()} is populated with a new value from {@link #namingStrategy} with a call to {@link NamingStrategy#generateName()}
     * @return The correlation data with {@link CorrelationData#getId()} populated
     */
    private CorrelationData createCorrelationData() {
        return new CorrelationData(namingStrategy.generateName());
    }
}
