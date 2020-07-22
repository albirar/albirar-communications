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
package cat.albirar.communications.providers.email.impl;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import cat.albirar.communications.channels.models.ContactBean;
import cat.albirar.communications.configuration.PropertiesComm;
import cat.albirar.communications.providers.ProviderException;
import cat.albirar.communications.providers.email.IEmailProvider;

/**
 * A properties configurable email provider.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Component
public class PropertiesConfigurableEmailProvider implements IEmailProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesConfigurableEmailProvider.class);
    /**
     * Name of this provider.
     */
    public static final String NAME = "emailPropertiesConfigurable";
    
    private JavaMailSender emailSender;
    
    @Autowired
    private ConfigurableEnvironment env;
    
    @PostConstruct
    public void init() {
        JavaMailSenderImpl jm;
        List<EnumerablePropertySource<?>> propSrc;
        Properties props;
        String host;
        int port;
        String username;
        String password;
        
        props = new Properties();
        propSrc = env.getPropertySources().stream()
                .filter(ps -> EnumerablePropertySource.class.isAssignableFrom(ps.getClass()))
                .map(ps -> (EnumerablePropertySource<?>) ps)
                .collect(Collectors.toList())
                ;
        for(EnumerablePropertySource<?> eps : propSrc) {
            Arrays.stream(eps.getPropertyNames())
            .filter(name -> name.startsWith(PropertiesComm.JAVAMAIL_PROPERTIES_SUFIX_ROOT_PROPERTY_NAME))
            .forEach(name -> props.put(name.substring(PropertiesComm.JAVAMAIL_PROPERTIES_SUFIX_ROOT_PROPERTY_NAME.length()), env.getProperty(name)))
            ;
        }

        host = env.getRequiredProperty(PropertiesComm.JAVAMAIL_HOST_PROPERTY_NAME, String.class);
        port = env.getRequiredProperty(PropertiesComm.JAVAMAIL_PORT_PROPERTY_NAME, Integer.class);
        username = env.getRequiredProperty(PropertiesComm.JAVAMAIL_USERNAME_PROPERTY_NAME, String.class);
        password = env.getRequiredProperty(PropertiesComm.JAVAMAIL_PASSWORD_PROPERTY_NAME, String.class);
        
        LOGGER.debug("Creating javamail sender with values: {}",
                new StringBuilder("host: '").append(host)
                .append("', port: ").append(port)
                .append("', username: '").append(username)
                .append("', password: '").append(password).append("'")
                .append(", props: ").append(props)
                .toString()
                );
        jm = new JavaMailSenderImpl();
        jm.setJavaMailProperties(props);
        jm.setHost(host);
        jm.setPort(port);
        jm.setUsername(username);
        jm.setPassword(password);
        emailSender = jm;
    }
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
    public void sendEmail(ContactBean from, ContactBean recipient, String subject, String message, String mediaType, String charset) {
        MimeMessage mmsg;

        mmsg = emailSender.createMimeMessage();
        try {
            mmsg.setSubject(subject);
            mmsg.setRecipient(RecipientType.TO, new InternetAddress(recipient.getChannelBean().getChannelId(), recipient.getDisplayName()));
            mmsg.setFrom(new InternetAddress(from.getChannelBean().getChannelId(), from.getDisplayName()));
            mmsg.setContent(message, new StringBuffer(mediaType)
                    .append("; charset=").append(charset).toString());
            emailSender.send(mmsg);
        }
        catch(MessagingException | UnsupportedEncodingException e) {
            String errMsg;
            
            errMsg = String.format("On preparing or sending the mail message for %s (%s)", message, e.getMessage());
            LOGGER.error(errMsg, e);
            throw new ProviderException(errMsg, e);
        }
    }

}
