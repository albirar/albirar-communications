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

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import cat.albirar.communications.core.configuration.IPropertiesComm;
import cat.albirar.communications.core.messages.models.MessageBean;
import cat.albirar.communications.core.providers.ProviderException;
import cat.albirar.communications.core.providers.email.IEmailProvider;
import cat.albirar.communications.core.status.EStatusMessage;
import cat.albirar.communications.core.status.models.MessageStatusBean;

/**
 * The email sender processor.
 * Receive messages and then, send it through email server connection.
 * The result will be put on {@link IPropertiesComm#QUEUE_REPORT_EMAIL} routing.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Component
public class EmailSenderProcessor extends AbstractSenderProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailSenderProcessor.class);
    
    @Autowired(required = false)
    private List<IEmailProvider> emailProviders;
    /**
     * {@inheritDoc}
     */
    @Override
    protected void processMessage(String messageId, MessageBean message) {
        MessageStatusBean r;
        
        LOGGER.debug("Sending email for message {}", message);
        try {
            if(!CollectionUtils.isEmpty(emailProviders)) {
                // TODO Until adapted, use the first one
                emailProviders.get(0).sendEmail(messageId, message.getSender(), message.getReceiver(), message.getTitle(), message.getBody(), message.getBodyType().getMediaType(), message.getBodyCharSet().name());
                r = MessageStatusBean.copyBuilder(message)
                        .status(EStatusMessage.SEND)
                        .messageId(messageId)
                        .build()
                        ;
                reportMessage(IPropertiesComm.QUEUE_REPORT_EMAIL, r);
            } else {
                LOGGER.error("No email provider was configured!");
                r = MessageStatusBean.copyBuilder(message)
                        .status(EStatusMessage.ERROR)
                        .messageId(messageId)
                        .errorMessage(Optional.of("No email provider was configured!"))
                        .build()
                        ;
                reportMessage(IPropertiesComm.QUEUE_REPORT_EMAIL, r);
            }
        } catch(ProviderException e) {
            String errMsg;
            
            errMsg = String.format("On preparing or sending the mail message for %s (%s)", message, e.getMessage());
            LOGGER.error(errMsg, e);
            r = MessageStatusBean.copyBuilder(message)
                    .status(EStatusMessage.ERROR)
                    .messageId(messageId)
                    .errorMessage(Optional.of(errMsg))
                    .build()
                    ;
            reportMessage(IPropertiesComm.QUEUE_REPORT_EMAIL, r);
        }
    }
}
