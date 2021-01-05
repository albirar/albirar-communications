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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StringUtils;

import cat.albirar.communications.configuration.AlbirarCommunicationsConfiguration;
import cat.albirar.communications.messages.models.MessageBean;
import cat.albirar.communications.services.ICommunicationService;
import cat.albirar.communications.status.EStatusMessage;
import cat.albirar.communications.status.models.MessageStatusBean;
import cat.albirar.communications.test.RabbitMqContainerExtension;
import cat.albirar.communications.test.configuration.TestMessagesFactory;

/**
 * Test for no provider extensions.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@ContextConfiguration(classes = {AlbirarCommunicationsConfiguration.class})
@ExtendWith({SpringExtension.class, RabbitMqContainerExtension.class})
public class NoProvidersTest {

    @Autowired
    private ICommunicationService communicationService;

    private MessageBean emailMessage;
    private MessageBean smsMessage;

    @SpyBean
    private RabbitOperations rabbitTemplate;

    @BeforeEach
    public void init() {
        TestMessagesFactory tm;
        
        tm = new TestMessagesFactory();
        
        emailMessage = tm.getEmailMessage();
        smsMessage = tm.getSmsMessage();
    }
    
    @Test
    public void testSendEmailWithoutProviderTest() {
        String messageId;
        Optional<MessageStatusBean> oSt;
        MessageStatusBean st;
        
        doCallRealMethod().when(rabbitTemplate).send(anyString(), anyString(), any(Message.class), any(CorrelationData.class));
        messageId = communicationService.pushMessage(emailMessage);
        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS).until(() -> communicationService.isStatusMessage(messageId));
        oSt = communicationService.popStatusMessage(messageId);
        assertNotNull(oSt);
        assertTrue(oSt.isPresent());
        st = oSt.get();
        assertEquals(EStatusMessage.ERROR, st.getStatus());
        assertNotNull(st.getErrorMessage());
        assertTrue(st.getErrorMessage().isPresent());
        assertTrue(StringUtils.hasText(st.getErrorMessage().get()));
    }
    
    @Test
    public void testSendSmsWithoutProviderTest() {
        String messageId;
        Optional<MessageStatusBean> oSt;
        MessageStatusBean st;

        doCallRealMethod().when(rabbitTemplate).send(anyString(), anyString(), any(Message.class), any(CorrelationData.class));
        messageId = communicationService.pushMessage(smsMessage);
        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(10, TimeUnit.SECONDS).until(() -> communicationService.isStatusMessage(messageId));
        oSt = communicationService.popStatusMessage(messageId);
        assertNotNull(oSt);
        assertTrue(oSt.isPresent());
        st = oSt.get();
        assertEquals(EStatusMessage.ERROR, st.getStatus());
        assertNotNull(st.getErrorMessage());
        assertTrue(st.getErrorMessage().isPresent());
        assertTrue(StringUtils.hasText(st.getErrorMessage().get()));
    }
}
