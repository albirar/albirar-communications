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
package cat.albirar.communications.services.impl;

import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.NamingStrategy;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cat.albirar.communications.configuration.PropertiesComm;
import cat.albirar.communications.messages.models.MessageBean;
import cat.albirar.communications.services.ICommunicationService;
import cat.albirar.communications.status.models.MessageStatusBean;

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
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private NamingStrategy namingStrategy;

    private String exchangeName = PropertiesComm.EXCHANGE_NAME;

    private String emailSendQueueName = PropertiesComm.QUEUE_SEND_EMAIL;

    private String emailReportQueueName = PropertiesComm.QUEUE_REPORT_EMAIL;

    private String smsSendQueueName = PropertiesComm.QUEUE_SEND_SMS;

    private String smsReportQueueName = PropertiesComm.QUEUE_REPORT_SMS;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<MessageStatusBean> statusMessage(@NotBlank String messageId) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String pushMessage(@NotNull @Valid MessageBean message) {
        CorrelationData crlt;

        LOGGER.debug("Pushing message {}...", message);
        crlt = createCorrelationData();
        rabbitTemplate.convertAndSend(exchangeName, emailSendQueueName, message, crlt);
        LOGGER.debug("Message ({}) pushed with id {}", message, crlt.getId());
        return crlt.getId();
    }

    /**
     * Create the {@link CorrelationData} for a message.
     * @return The correlation data
     */
    private CorrelationData createCorrelationData() {
        CorrelationData crlt;

        crlt = new CorrelationData();
        crlt.setId(namingStrategy.generateName());
        return crlt;
    }
}
