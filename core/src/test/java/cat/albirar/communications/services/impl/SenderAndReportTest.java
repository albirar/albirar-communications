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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.eq;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitOperations;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import cat.albirar.communications.core.configuration.AlbirarCommunicationsConfiguration;
import cat.albirar.communications.core.configuration.IPropertiesComm;
import cat.albirar.communications.core.messages.models.MessageBean;
import cat.albirar.communications.core.processors.IProcessor;
import cat.albirar.communications.core.processors.impl.AbstractReportProcessor;
import cat.albirar.communications.core.processors.impl.AbstractSenderProcessor;
import cat.albirar.communications.core.processors.impl.EmailReportProcessor;
import cat.albirar.communications.core.processors.impl.EmailSenderProcessor;
import cat.albirar.communications.core.processors.impl.SmsReportProcessor;
import cat.albirar.communications.core.processors.impl.SmsSenderProcessor;
import cat.albirar.communications.core.services.ICommunicationService;
import cat.albirar.communications.core.services.ServiceException;
import cat.albirar.communications.core.status.models.MessageStatusBean;
import cat.albirar.communications.test.RabbitMqContainerExtension;
import cat.albirar.communications.test.configuration.CoreCommunicationsTestConfiguration;
import cat.albirar.communications.test.configuration.TestMessagesFactory;

/**
 * Test for {@link AbstractSenderProcessor} and {@link AbstractReportProcessor} components.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@ContextConfiguration(classes = {AlbirarCommunicationsConfiguration.class, CoreCommunicationsTestConfiguration.class})
@ExtendWith({SpringExtension.class, RabbitMqContainerExtension.class, MockitoExtension.class})
public class SenderAndReportTest {
    @Autowired
    private ICommunicationService communicationService;

    @SpyBean
    private MessageConverter messageConverter;

    @SpyBean
    private RabbitOperations rabbitTemplate;
    
    @Autowired
    private EmailReportProcessor emailReportProcessor;
    
    @Autowired
    private SmsReportProcessor smsReportProcessor;
    
    @Autowired
    private EmailSenderProcessor emailSenderProcessor;
    
    @Autowired
    private SmsSenderProcessor smsSenderProcessor;
    
    private MessageBean emailMessage;
    
    private MessageBean smsMessage;
    
    @BeforeEach
    public void init() {
        TestMessagesFactory tm;
        
        tm = new TestMessagesFactory();
        
        emailMessage = tm.getEmailMessage();
        smsMessage = tm.getSmsMessage();
    }
    
    /**
     * Test for {@link IProcessor#getManagedMessages()} and {@link IProcessor#getProcessedMessages()} on {@link #emailSenderProcessor} and {@link #emailReportProcessor}.
     */
    @Test
    public void testEmailSenderManagedProcessedMessages() {
        long lSm, lSp, lRm, lRp;
        String id;

        lSm = emailSenderProcessor.getManagedMessages();
        lSp = emailSenderProcessor.getProcessedMessages();
        lRm = emailReportProcessor.getManagedMessages();
        lRp = emailReportProcessor.getProcessedMessages();
        
        id = communicationService.pushMessage(emailMessage);

        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS).until(() -> communicationService.isStatusMessage(id));
        Assertions.assertTrue(emailSenderProcessor.getManagedMessages() > lSm);
        Assertions.assertTrue(emailSenderProcessor.getProcessedMessages() > lSp);
        Assertions.assertTrue(emailReportProcessor.getManagedMessages() > lRm);
        Assertions.assertTrue(emailReportProcessor.getProcessedMessages() > lRp);
    }

    /**
     * Test for email a {@link MessageConversionException} on converting a {@link MessageBean} to {@link Message} at {@link ICommunicationService#pushMessage(MessageBean)} call.
     */
    @Test
    public void testEmailPushMessageConverterException() {
        ServiceException ex;
        
        doThrow(MessageConversionException.class).when(messageConverter).toMessage(any(MessageBean.class), any(MessageProperties.class));
        
        ex = assertThrows(ServiceException.class, () -> communicationService.pushMessage(emailMessage));
        
        assertNotNull(ex.getCause());
        assertEquals(MessageConversionException.class, ex.getCause().getClass());
    }

    /**
     * Test for email a {@link MessageConversionException} on converting a {@link Message} to {@link MessageBean} at {@link AbstractSenderProcessor#onMessage(Message)} call.
     */
    @Test
    public void testEmailSenderOnMessageMessageConverterException() throws Exception {
        String id;
        long l;

        doCallRealMethod().when(messageConverter).toMessage(any(MessageBean.class), any(MessageProperties.class));
        doThrow(MessageConversionException.class).when(messageConverter).fromMessage(any(Message.class));
        l = emailSenderProcessor.getManagedMessages();
        id = communicationService.pushMessage(emailMessage);
        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS).until(() -> emailSenderProcessor.getManagedMessages() > l);
        assertFalse(communicationService.isStatusMessage(id));
    }

    /**
     * Test for email a {@link ClassCastException} on converting a {@link Message} to {@link MessageBean} at {@link AbstractSenderProcessor#onMessage(Message)} call.
     */
    @Test
    public void testEmailSenderOnMessageClassCastException() throws Exception {
        String id;
        long l;

        doCallRealMethod().when(messageConverter).toMessage(any(MessageBean.class), any(MessageProperties.class));
        doReturn("Test").when(messageConverter).fromMessage(any(Message.class));
        l = emailSenderProcessor.getManagedMessages();
        id = communicationService.pushMessage(emailMessage);
        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS).until(() -> emailSenderProcessor.getManagedMessages() > l);
        assertFalse(communicationService.isStatusMessage(id));
    }

    /**
     * Test for sms a {@link AmqpException} on sending a {@link MessageStatusBean} at {@link AbstractSenderProcessor#reportMessage(String, MessageStatusBean)} call.
     */
    @Test
    public void testEmailPushReportAmqpException() throws Exception {
        String id;
        long l;
    
        doCallRealMethod().when(messageConverter).toMessage(Mockito.any(MessageBean.class), Mockito.any(MessageProperties.class));
        doCallRealMethod().when(messageConverter).fromMessage(any(Message.class));
        doThrow(AmqpException.class).when(rabbitTemplate).send(eq(IPropertiesComm.EXCHANGE_NAME), eq(IPropertiesComm.QUEUE_REPORT_EMAIL), any(Message.class));
        
        l = emailSenderProcessor.getManagedMessages();
        id = communicationService.pushMessage(emailMessage);
        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS).until(() -> emailSenderProcessor.getManagedMessages() > l);
        
        assertFalse(communicationService.isStatusMessage(id));
    }

    /**
     * Test for email a {@link MessageConversionException} on converting a {@link MessageStatusBean} to {@link Message} at {@link AbstractSenderProcessor#reportMessage(String, MessageStatusBean)} call.
     */
    @Test
    public void testEmailPushReportMessageConverterException() {
        String id;
        long l;
        
        doCallRealMethod().when(messageConverter).toMessage(eq(emailMessage), any(MessageProperties.class));
        doCallRealMethod().when(messageConverter).fromMessage(any(Message.class));
        doThrow(MessageConversionException.class).when(messageConverter).toMessage(any(MessageStatusBean.class), any(MessageProperties.class));
        
        l = emailSenderProcessor.getManagedMessages();
        
        id = communicationService.pushMessage(emailMessage);
        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS).until(() -> emailSenderProcessor.getManagedMessages() > l);
        assertFalse(communicationService.isStatusMessage(id));
        
    }

    /**
     * Test for email a {@link MessageConversionException} on converting a {@link Message} to {@link MessageStatusBean} at {@link AbstractReportProcessor#onMessage(Message)} call.
     */
    @Test
    public void testEmailReportOnMessageMessageConverterException() throws Exception {
        String id;
        long l;

        doCallRealMethod().when(messageConverter).toMessage(any(MessageBean.class), any(MessageProperties.class));
        doCallRealMethod().doThrow(MessageConversionException.class).when(messageConverter).fromMessage(any(Message.class));
        
        l = emailReportProcessor.getManagedMessages();
        
        id = communicationService.pushMessage(emailMessage);
        
        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS).until(() -> emailReportProcessor.getManagedMessages() > l);
        
        assertFalse(communicationService.isStatusMessage(id));
        
    }

    /**
     * Test for email a {@link ClassCastException} on converting a {@link Message} to {@link MessageStatusBean} at {@link AbstractReportProcessor#onMessage(Message)} call.
     */
    @Test
    public void testEmailReportOnMessageClassCastException() throws Exception {
        String id;
        long l;

        doCallRealMethod().when(messageConverter).toMessage(any(MessageBean.class), any(MessageProperties.class));
        doCallRealMethod().doReturn("Test").when(messageConverter).fromMessage(any(Message.class));
        
        l = emailReportProcessor.getManagedMessages();
        
        id = communicationService.pushMessage(emailMessage);
        
        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS).until(() -> emailReportProcessor.getManagedMessages() > l);
        
        assertFalse(communicationService.isStatusMessage(id));
    }


    /**
     * Test for sms a {@link MessageConversionException} on converting a {@link MessageBean} to {@link Message} at {@link ICommunicationService#pushMessage(MessageBean)} call.
     */
    @Test
    public void testSmsPushMessageConverterException() {
        ServiceException ex;
        
        doThrow(MessageConversionException.class).when(messageConverter).toMessage(any(MessageBean.class), any(MessageProperties.class));
        
        ex = assertThrows(ServiceException.class, () -> communicationService.pushMessage(smsMessage));
        
        assertNotNull(ex.getCause());
        assertEquals(MessageConversionException.class, ex.getCause().getClass());
        
    }

    /**
     * Test for sms a {@link MessageConversionException} on converting a {@link Message} to {@link MessageBean} at {@link AbstractSenderProcessor#onMessage(Message)} call.
     */
    @Test
    public void testSmsSenderOnMessageMessageConverterException() throws Exception {
        String id;
        long l;

        doCallRealMethod().when(messageConverter).toMessage(any(MessageBean.class), any(MessageProperties.class));
        doThrow(MessageConversionException.class).when(messageConverter).fromMessage(any(Message.class));
        l = smsSenderProcessor.getManagedMessages();
        id = communicationService.pushMessage(smsMessage);
        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS).until(() -> smsSenderProcessor.getManagedMessages() > l);
        assertFalse(communicationService.isStatusMessage(id));
        
    }

    /**
     * Test for sms a {@link ClassCastException} on converting a {@link Message} to {@link MessageBean} at {@link AbstractSenderProcessor#onMessage(Message)} call.
     */
    @Test
    public void testSmsSenderOnMessageClassCastException() throws Exception {
        String id;
        long l;

        doCallRealMethod().when(messageConverter).toMessage(any(MessageBean.class), any(MessageProperties.class));
        doReturn("Test").when(messageConverter).fromMessage(any(Message.class));
        l = smsSenderProcessor.getManagedMessages();
        id = communicationService.pushMessage(smsMessage);
        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS).until(() -> smsSenderProcessor.getManagedMessages() > l);
        assertFalse(communicationService.isStatusMessage(id));
        
    }

    /**
     * Test for sms a {@link AmqpException} on sending a {@link MessageStatusBean} at {@link AbstractSenderProcessor#reportMessage(String, MessageStatusBean)} call.
     */
    @Test
    public void testSmsPushReportAmqpException() throws Exception {
        String id;
        long l;
    
        doCallRealMethod().when(messageConverter).toMessage(any(MessageBean.class), any(MessageProperties.class));
        doCallRealMethod().when(messageConverter).fromMessage(any(Message.class));
        doCallRealMethod().when(messageConverter).fromMessage(any(Message.class));
        doThrow(AmqpException.class).when(rabbitTemplate).send(eq(IPropertiesComm.EXCHANGE_NAME), eq(IPropertiesComm.QUEUE_REPORT_SMS), any(Message.class));
        
        l = smsSenderProcessor.getManagedMessages();
        id = communicationService.pushMessage(smsMessage);
        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS).until(() -> smsSenderProcessor.getManagedMessages() > l);
        
        assertFalse(communicationService.isStatusMessage(id));
    }

    /**
     * Test for sms a {@link MessageConversionException} on converting a {@link MessageStatusBean} to {@link Message} at {@link AbstractSenderProcessor#reportMessage(String, MessageStatusBean)} call.
     */
    @Test
    public void testSmsPushReportMessageConverterException() {
        String id;
        long l;
        
        doCallRealMethod().when(messageConverter).toMessage(eq(smsMessage), any(MessageProperties.class));
        doCallRealMethod().when(messageConverter).fromMessage(any(Message.class));
        doThrow(MessageConversionException.class).when(messageConverter).toMessage(any(MessageStatusBean.class), any(MessageProperties.class));
        
        l = smsSenderProcessor.getManagedMessages();
        
        id = communicationService.pushMessage(smsMessage);
        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS).until(() -> smsSenderProcessor.getManagedMessages() > l);
        assertFalse(communicationService.isStatusMessage(id));
        
    }

    /**
     * Test for sms a {@link MessageConversionException} on converting a {@link Message} to {@link MessageStatusBean} at {@link AbstractReportProcessor#onMessage(Message)} call.
     */
    @Test
    public void testSmsReportOnMessageReportMessageConverterException() throws Exception {
        String id;
        long l;

        doCallRealMethod().when(messageConverter).toMessage(any(MessageBean.class), any(MessageProperties.class));
        doCallRealMethod().doThrow(MessageConversionException.class).when(messageConverter).fromMessage(any(Message.class));
        
        l = smsReportProcessor.getManagedMessages();
        
        id = communicationService.pushMessage(smsMessage);
        
        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS).until(() -> smsReportProcessor.getManagedMessages() > l);
        
        assertFalse(communicationService.isStatusMessage(id));
        
    }

    /**
     * Test for sms a {@link ClassCastException} on converting a {@link Message} to {@link MessageStatusBean} at {@link AbstractReportProcessor#onMessage(Message)} call.
     */
    @Test
    public void testSmsReportOnMessageReportClassCastException() throws Exception {
        String id;
        long l;

        doCallRealMethod().when(messageConverter).toMessage(any(MessageBean.class), any(MessageProperties.class));
        doCallRealMethod().doReturn("Test").when(messageConverter).fromMessage(any(Message.class));
        
        l = smsReportProcessor.getManagedMessages();
        
        id = communicationService.pushMessage(smsMessage);
        
        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS).until(() -> smsReportProcessor.getManagedMessages() > l);
        
        assertFalse(communicationService.isStatusMessage(id));
    }
}
