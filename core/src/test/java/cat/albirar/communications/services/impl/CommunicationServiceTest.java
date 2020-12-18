/*
 * This file is part of "albirar-communications-core".
 * 
 * "albirar-communications-core" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * "albirar-communications-core" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with "albirar-communications-core" source code.  If not, see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *
 * Copyright (C) 2020 Octavi Fornés
 */
package cat.albirar.communications.services.impl;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StringUtils;

import cat.albirar.communications.configuration.AlbirarCommunicationsConfiguration;
import cat.albirar.communications.messages.models.MessageBean;
import cat.albirar.communications.providers.ProviderException;
import cat.albirar.communications.providers.email.EmailPushBean;
import cat.albirar.communications.providers.email.SmsPushBean;
import cat.albirar.communications.providers.mocks.IMockEmailServiceProvider;
import cat.albirar.communications.providers.mocks.IMockSmsServiceProvider;
import cat.albirar.communications.services.ICommunicationService;
import cat.albirar.communications.status.EStatusMessage;
import cat.albirar.communications.status.models.MessageStatusBean;
import cat.albirar.communications.test.AbstractCommunicationsTest;
import cat.albirar.communications.test.RabbitMqContainerExtension;
import cat.albirar.communications.test.configuration.CoreCommunicationsTestConfiguration;
import cat.albirar.communications.test.configuration.TestMessagesFactory;

/**
 * Test for {@link ICommunicationService}.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@ContextConfiguration(classes = {AlbirarCommunicationsConfiguration.class, CoreCommunicationsTestConfiguration.class})
@ExtendWith({SpringExtension.class, RabbitMqContainerExtension.class})
public class CommunicationServiceTest extends AbstractCommunicationsTest {
    
    @Autowired
    private ICommunicationService communicationService;
    
    @Autowired
    private IMockEmailServiceProvider emailProvider;
    @Autowired
    private IMockSmsServiceProvider smsProvider;
    
    private MessageBean emailMessage;
    private MessageBean smsMessage;

    @BeforeEach
    public void init() {
        TestMessagesFactory tm;
        
        tm = new TestMessagesFactory();
        
        emailMessage = tm.getEmailMessage();
        smsMessage = tm.getSmsMessage();
    }
    
    @Test
    public void testPushEmailMessage() {
        Instant ts;
        String messageId;
        EmailPushBean msg;
        Optional<EmailPushBean> omsg;
        
        ts = Instant.now();
        messageId = communicationService.pushMessage(emailMessage);
        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS).until(() -> emailProvider.isMessageProcessed(messageId));
        
        omsg = emailProvider.getMessage(messageId);
        assertNotNull(omsg);
        assertTrue(omsg.isPresent());
        msg = omsg.get();
        assertEquals(emailMessage.getSender(), msg.getFrom());
        assertEquals(emailMessage.getReceiver(), msg.getRecipient());
        assertEquals(emailMessage.getTitle(), msg.getSubject());
        assertEquals(emailMessage.getBody(), msg.getMessage());
        assertEquals(emailMessage.getBodyCharSet().displayName(), msg.getCharset());
        assertEquals(emailMessage.getBodyType().getMediaType(), msg.getMediaType());
        assertTrue(ts.isBefore(msg.getTimestamp()));
    }
    
    @Test
    public void testPushSmsMessage() {
        Instant ts;
        String messageId;
        SmsPushBean msg;
        Optional<SmsPushBean> omsg;
        
        ts = Instant.now();
        messageId = communicationService.pushMessage(smsMessage);
        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS).until(() -> smsProvider.isMessageProcessed(messageId));
        
        omsg = smsProvider.getMessage(messageId);
        assertNotNull(omsg);
        assertTrue(omsg.isPresent());
        msg = omsg.get();
        assertEquals(smsMessage.getSender(), msg.getFrom());
        assertEquals(smsMessage.getReceiver(), msg.getRecipient());
        assertEquals(smsMessage.getBody(), msg.getMessage());
        assertTrue(ts.isBefore(msg.getTimestamp()));
    }
    
    @Test
    public void testIsReportMessage() {
        String messageIdSms, messageIdEmail;
        
        messageIdSms = communicationService.pushMessage(smsMessage);
        messageIdEmail = communicationService.pushMessage(emailMessage);
        
        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS).until(() -> smsProvider.isMessageProcessed(messageIdSms) && emailProvider.isMessageProcessed(messageIdEmail));

        assertTrue(communicationService.isStatusMessage(messageIdSms));
        assertTrue(communicationService.isStatusMessage(messageIdEmail));
    }
    
    @Test
    public void testPopReportMessages() {
        String messageIdSms, messageIdEmail;
        MessageStatusBean msg;
        Optional<MessageStatusBean> omsg;
        
        messageIdSms = communicationService.pushMessage(smsMessage);
        messageIdEmail = communicationService.pushMessage(emailMessage);
        
        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS).until(() -> smsProvider.isMessageProcessed(messageIdSms) && emailProvider.isMessageProcessed(messageIdEmail));

        omsg = communicationService.popStatusMessage(messageIdSms);
        assertTrue(omsg.isPresent());
        msg = omsg.get();
        assertEquals(smsMessage.getBody(), msg.getBody());
        assertEquals(smsMessage.getBodyCharSet(), msg.getBodyCharSet());
        assertEquals(smsMessage.getBodyType(), msg.getBodyType());
        assertEquals(smsMessage.getReceiver(), msg.getReceiver());
        assertEquals(smsMessage.getSender(), msg.getSender());
        assertEquals(smsMessage.getTitle(), msg.getTitle());
        assertEquals(EStatusMessage.SEND, msg.getStatus());
        assertNotNull(msg.getErrorMessage());
        assertFalse(msg.getErrorMessage().isPresent());
        
        omsg = communicationService.popStatusMessage(messageIdEmail);
        assertTrue(omsg.isPresent());
        msg = omsg.get();
        assertEquals(emailMessage.getBody(), msg.getBody());
        assertEquals(emailMessage.getBodyCharSet(), msg.getBodyCharSet());
        assertEquals(emailMessage.getBodyType(), msg.getBodyType());
        assertEquals(emailMessage.getReceiver(), msg.getReceiver());
        assertEquals(emailMessage.getSender(), msg.getSender());
        assertEquals(emailMessage.getTitle(), msg.getTitle());
        assertEquals(EStatusMessage.SEND, msg.getStatus());
        assertNotNull(msg.getErrorMessage());
        assertFalse(msg.getErrorMessage().isPresent());
    }

    @Test
    public void testValidationIsReportMessage() {
        assertThrows(ConstraintViolationException.class, () -> communicationService.isStatusMessage(null));
        assertThrows(ConstraintViolationException.class, () -> communicationService.isStatusMessage(""));
        assertThrows(ConstraintViolationException.class, () -> communicationService.isStatusMessage("   "));
    }
    
    @Test
    public void testThrowProviderException() {
        String messageIdSms, messageIdEmail;
        MessageStatusBean msg;
        Optional<MessageStatusBean> omsg;
        ProviderException t;

        emailProvider.setThrowException(true);
        smsProvider.setThrowException(true);
        
        messageIdEmail = communicationService.pushMessage(emailMessage);
        messageIdSms = communicationService.pushMessage(smsMessage);
        
        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS).until(() ->
                communicationService.isStatusMessage(messageIdEmail) 
                && communicationService.isStatusMessage(messageIdSms));

        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS).until(() -> 
                smsProvider.isThrowMessageProcessed(messageIdSms) 
                && emailProvider.isThrowMessageProcessed(messageIdEmail));

        emailProvider.setThrowException(false);
        smsProvider.setThrowException(false);
        
        omsg = communicationService.popStatusMessage(messageIdEmail);
        assertTrue(omsg.isPresent());
        msg = omsg.get();
        assertEquals(emailMessage.getBody(), msg.getBody());
        assertEquals(emailMessage.getBodyCharSet(), msg.getBodyCharSet());
        assertEquals(emailMessage.getBodyType(), msg.getBodyType());
        assertEquals(emailMessage.getReceiver(), msg.getReceiver());
        assertEquals(emailMessage.getSender(), msg.getSender());
        assertEquals(emailMessage.getTitle(), msg.getTitle());
        assertEquals(EStatusMessage.ERROR, msg.getStatus());
        assertNotNull(msg.getErrorMessage());
        assertTrue(msg.getErrorMessage().isPresent());
        t = msg.getErrorMessage().get();
        assertTrue(StringUtils.hasText(t.getMessage()));
        assertNotNull(t.getCause());
        
        omsg = communicationService.popStatusMessage(messageIdSms);
        assertTrue(omsg.isPresent());
        msg = omsg.get();
        assertEquals(smsMessage.getBody(), msg.getBody());
        assertEquals(smsMessage.getBodyCharSet(), msg.getBodyCharSet());
        assertEquals(smsMessage.getBodyType(), msg.getBodyType());
        assertEquals(smsMessage.getReceiver(), msg.getReceiver());
        assertEquals(smsMessage.getSender(), msg.getSender());
        assertEquals(smsMessage.getTitle(), msg.getTitle());
        assertEquals(EStatusMessage.ERROR, msg.getStatus());
        assertNotNull(msg.getErrorMessage());
        assertTrue(msg.getErrorMessage().isPresent());
        t = msg.getErrorMessage().get();
        assertTrue(StringUtils.hasText(t.getMessage()));
        assertNotNull(t.getCause());
    }
}
