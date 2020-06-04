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

import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import cat.albirar.communications.configuration.PropertiesComm;
import cat.albirar.communications.messages.models.MessageBean;
import cat.albirar.communications.status.EStatusMessage;
import cat.albirar.communications.status.models.MessageStatusBean;

/**
 * The email sender processor.
 * Receive messages and then, send it through email server connection.
 * The result will be put on {@link PropertiesComm#EMAIL_ROUTE_REPORT_PROPERTY_NAME} routing.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Component
public class EmailSenderProcessor extends AbstractSingleProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailSenderProcessor.class);
    
    @Autowired
    private JavaMailSender emailSender;
    /**
     * {@inheritDoc}
     */
    @Override
    protected void processMessage(String messageId, MessageBean message) {
        MimeMessage mmsg;
        MessageStatusBean r;
        
        LOGGER.debug("Sending email for message {}", message);
        mmsg = emailSender.createMimeMessage();
        try {
            mmsg.setSubject(message.getTitle());
            mmsg.setRecipient(RecipientType.TO, new InternetAddress(message.getAddress().getChannelId()));
            mmsg.setFrom(message.getAddressFrom());
            mmsg.setContent(message.getBody(), new StringBuffer(message.getBodyType().getMediaType())
                    .append("; charset=").append(message.getBodyCharSet()).toString());
            emailSender.send(mmsg);
            r = MessageStatusBean.copyBuilder(message)
                    .status(EStatusMessage.SEND)
                    .messageId(messageId)
                    .build()
                    ;
            reportMessage(r);
        }
        catch(MessagingException e) {
            String errMsg;
            
            errMsg = String.format("On preparing or sending the mail message for %s (%s)", message, e.getMessage());
            LOGGER.error(errMsg, e);
            r = MessageStatusBean.copyBuilder(message)
                    .status(EStatusMessage.ERROR)
                    .errorMessage(Optional.of(errMsg))
                    .build()
                    ;
            reportMessage(r);
        }
    }
}
