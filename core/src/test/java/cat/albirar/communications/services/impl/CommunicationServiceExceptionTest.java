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
import static org.mockito.Mockito.doThrow;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import cat.albirar.communications.core.configuration.AlbirarCommunicationsConfiguration;
import cat.albirar.communications.core.messages.models.MessageBean;
import cat.albirar.communications.core.services.ICommunicationService;
import cat.albirar.communications.core.services.ServiceException;
import cat.albirar.communications.core.services.impl.CommunicationServiceImpl;
import cat.albirar.communications.core.status.models.MessageStatusBean;
import cat.albirar.communications.test.RabbitMqContainerExtension;
import cat.albirar.communications.test.configuration.CoreCommunicationsTestConfiguration;
import cat.albirar.communications.test.configuration.TestMessagesFactory;

/**
 * Test exceptions on {@link CommunicationServiceImpl} with mocked rabbitmq.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@ContextConfiguration(classes = {AlbirarCommunicationsConfiguration.class, CoreCommunicationsTestConfiguration.class})
@ExtendWith({SpringExtension.class, RabbitMqContainerExtension.class})
public class CommunicationServiceExceptionTest {
    @Autowired
    private ICommunicationService communicationService;

    @SpyBean
    private RabbitOperations mockedRabbitOperations;
    
    private MessageBean emailMessage;

    @BeforeEach
    public void init() {
        TestMessagesFactory tm;
        
        tm = new TestMessagesFactory();
        
        emailMessage = tm.getEmailMessage();
    }
    
    @Test
    public void testSendingRabbitMqException() {
        ServiceException ex;
        
        doThrow(AmqpException.class).when(mockedRabbitOperations).send(any(String.class), any(String.class), any(Message.class), any(CorrelationData.class));
        
        ex = assertThrows(ServiceException.class, () -> communicationService.pushMessage(emailMessage));
        assertNotNull(ex.getCause());
        assertEquals(AmqpException.class, ex.getCause().getClass());
    }
    
    @Test
    public void testReportRabbitMqException() {
        String msgId;
        
        doThrow(AmqpException.class).when(mockedRabbitOperations).convertAndSend(any(String.class), any(String.class), any(MessageStatusBean.class), any(CorrelationData.class));
        msgId = communicationService.pushMessage(emailMessage);
        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS);
        assertFalse(communicationService.isStatusMessage(msgId));
    }
}
