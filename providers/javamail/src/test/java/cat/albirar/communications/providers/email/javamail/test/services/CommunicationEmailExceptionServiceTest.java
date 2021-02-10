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
package cat.albirar.communications.providers.email.javamail.test.services;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;

import cat.albirar.communications.core.services.ICommunicationService;
import cat.albirar.communications.core.status.EStatusMessage;
import cat.albirar.communications.core.status.models.MessageStatusBean;
import cat.albirar.template.engine.EContentType;

/**
 * Test email server exceptions.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 2.0.0
 */
@ContextConfiguration(classes = {MockTestConfiguration.class})
public class CommunicationEmailExceptionServiceTest extends AbstractEmailServiceTest {

    @Autowired
    private JavaMailSender javaMailSender;
    
    @Autowired
    private ICommunicationService commService;
    
    @Test
    public void testMessagingException() throws Exception {
        MimeMessage mm;
        String messageId;
        Optional<MessageStatusBean> oSt;
        MessageStatusBean st;
        
        mm = Mockito.mock(MimeMessage.class);
        
        when(javaMailSender.createMimeMessage()).thenReturn(mm);
        doThrow(MessagingException.class)
            .when(mm)
            .setSubject(any(String.class));
        
        messageId = commService.pushMessage(buildEmailMessage(BODY_HTML, EContentType.HTML));
        oSt = Optional.empty();
        await().atLeast(10, TimeUnit.MILLISECONDS).and().atMost(5, TimeUnit.DAYS).until(() -> commService.isStatusMessage(messageId));
        oSt = commService.popStatusMessage(messageId);
        Assert.assertTrue(oSt.isPresent());
        st = oSt.get();
        Assert.assertEquals(st.getStatus(), EStatusMessage.ERROR);
    }
}
