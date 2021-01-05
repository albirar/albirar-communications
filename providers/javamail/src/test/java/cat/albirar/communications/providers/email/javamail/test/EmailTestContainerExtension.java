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
package cat.albirar.communications.providers.email.javamail.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Semaphore;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

import cat.albirar.communications.providers.email.javamail.configuration.JavaMailSenderConfiguration;

/**
 * A specific extension to start a mail server to test.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
public class EmailTestContainerExtension implements BeforeAllCallback, AfterAllCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailTestContainerExtension.class);

    private static final String NAME = "test";
    
    private static GreenMail emailServer = null;

    private static Semaphore semaphore = new Semaphore(1, true);
    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        Properties ps;
        try {
            EmailTestContainerExtension.semaphore.acquire();
            
            LOGGER.debug("Starting GreenMail test server albirar extension...");
            
            if(emailServer != null) {
                if(emailServer.getSmtp().isRunning()) {
                    emailServer.stop();
                }
            } else {
                emailServer = new GreenMail(ServerSetupTest.SMTP);
                
            }
            emailServer.setUser(NAME, NAME);
            emailServer.start();
        } finally {
            EmailTestContainerExtension.semaphore.release();
        }
        
        ps = emailServer.getSmtp().getServerSetup().configureJavaMailSessionProperties(null, true);
        
        // Set the basic properties of "albirar.communications"...
        System.setProperty(JavaMailSenderConfiguration.JAVAMAIL_HOST_PROPERTY_NAME, "localhost");
        System.setProperty(JavaMailSenderConfiguration.JAVAMAIL_PORT_PROPERTY_NAME, Integer.valueOf(ServerSetupTest.SMTP.getPort()).toString());
        System.setProperty(JavaMailSenderConfiguration.JAVAMAIL_USERNAME_PROPERTY_NAME, NAME);
        System.setProperty(JavaMailSenderConfiguration.JAVAMAIL_PASSWORD_PROPERTY_NAME, NAME);
        // Now the 'mail.*' javamail properties
        System.setProperty(JavaMailSenderConfiguration.JAVAMAIL_PROPERTIES_SUFIX_ROOT_PROPERTY_NAME + "mail.transport.protocol", "smtp");
        ps.entrySet().forEach(entry -> System.setProperty(JavaMailSenderConfiguration.JAVAMAIL_PROPERTIES_SUFIX_ROOT_PROPERTY_NAME + entry.getKey().toString(), entry.getValue().toString()));
        
        // Ok, ready!
        LOGGER.info("GreenMail test server albirar extension STARTED!");
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        LOGGER.info("End GreenMail test server albirar extension...");
        try {
            EmailTestContainerExtension.semaphore.acquire();
            if(emailServer != null && emailServer.getSmtp().isRunning()) {
                LOGGER.info("Stopping GreenMail test server albirar extension...");
                try {
                    emailServer.stop();
                    LOGGER.info("GreenMail test server albirar extension stopped");
                } finally {
                    emailServer = null;
                }
            }
        } finally {
            EmailTestContainerExtension.semaphore.release();
        }
    }
    /**
     * Wait for new incoming email.
     * @return true if new emails was arribed and false if not
     */
    public static final boolean waitForIncomingEmail() {
        if(emailServer != null && emailServer.getSmtp().isRunning()) {
            try {
                semaphore.acquire();
            }
            catch(InterruptedException e) {
                semaphore.release();
                return false;
            }
            try {
                int current;
                current = emailServer.getReceivedMessages().length;
                emailServer.waitForIncomingEmail(current + 1);
                return emailServer.getReceivedMessages().length > current;
            } finally {
                semaphore.release();
            }
        }
        return false;
    }
    /**
     * Return the received messages addressed to {@code addressTo}.
     * @return A list with the received messages, or empty if no messages to {@code addressTo} was found
     */
    public static final List<MimeMessage> getReceivedMessages(String addressTo) throws Exception {
        MimeMessage[] msgs = null;
        List<MimeMessage> retorn;
        Address adr;

        retorn = new ArrayList<>();
        semaphore.acquire();
        try {
            if(emailServer != null) {
                msgs = emailServer.getReceivedMessages();
            }
        } finally {
            semaphore.release();
        }
        if(ObjectUtils.isEmpty(msgs)) {
            return retorn;
        }
        // Filter by addressTo
        adr = new InternetAddress(addressTo);
        for(MimeMessage m : msgs) {
            for(Address a : m.getRecipients(RecipientType.TO)) {
                if(a.equals(adr)) {
                    retorn.add(m);
                    break;
                }
            }
        }
        return retorn;
    }
}
