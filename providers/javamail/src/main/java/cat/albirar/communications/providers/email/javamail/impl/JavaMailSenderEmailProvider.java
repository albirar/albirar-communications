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
package cat.albirar.communications.providers.email.javamail.impl;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import cat.albirar.communications.core.channels.models.ContactBean;
import cat.albirar.communications.core.providers.ProviderException;
import cat.albirar.communications.core.providers.email.IEmailProvider;

/**
 * An email provider that uses a {@link JavaMailSender}.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Component("javaMailSenderEmailProvider")
public class JavaMailSenderEmailProvider implements IEmailProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaMailSenderEmailProvider.class);
    /**
     * Name of this provider.
     */
    public static final String NAME = "javaMailSenderEmailProvider";
    
    @Autowired
    private JavaMailSender javaMailSender;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendEmail(String messageId, ContactBean from, ContactBean recipient, String subject, String message, String mediaType, String charset) {
        MimeMessage mmsg;

        mmsg = javaMailSender.createMimeMessage();
        try {
            mmsg.setSubject(subject);
            mmsg.setRecipient(RecipientType.TO, new InternetAddress(recipient.getChannelBean().getChannelId(), recipient.getDisplayName()));
            mmsg.setFrom(new InternetAddress(from.getChannelBean().getChannelId(), from.getDisplayName()));
            mmsg.setContent(message, new StringBuffer(mediaType)
                    .append("; charset=").append(charset).toString());
            javaMailSender.send(mmsg);
        }
        catch(MessagingException | UnsupportedEncodingException e) {
            String errMsg;
            
            errMsg = String.format("On preparing or sending the mail message for %s (%s)", message, e.getMessage());
            LOGGER.error(errMsg, e);
            throw new ProviderException(errMsg, e);
        }
    }

}
