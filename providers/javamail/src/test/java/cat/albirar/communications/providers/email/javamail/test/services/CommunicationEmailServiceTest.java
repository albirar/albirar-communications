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

import java.util.Optional;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.validation.ValidationException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.util.StringUtils;

import cat.albirar.communications.messages.models.MessageBean;
import cat.albirar.communications.providers.email.javamail.test.containers.EmailTestContainerExtension;
import cat.albirar.communications.services.ICommunicationService;
import cat.albirar.template.engine.EContentType;

/**
 * Test for {@link ICommunicationService}.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@ExtendWith(EmailTestContainerExtension.class)
public class CommunicationEmailServiceTest extends AbstractEmailServiceTest {
    @Test
    public void testValidations() {
        Assertions.assertThrows(ValidationException.class, () -> commService.pushMessage(null));
        Assertions.assertThrows(ValidationException.class, () -> commService.pushMessage(MessageBean.builder().build()));
    }
    
    @Test
    public void testPushOneSingleEmailText() throws Exception {
        String id;
        Optional<MimeMessage> msg;
        MimeMessage message;
        String toAddress;
        
        toAddress = generateEmailTo();
        id = commService.pushMessage(buildEmailMessage(toAddress, BODY_TEXT, EContentType.TEXT_PLAIN));
        Assertions.assertTrue(StringUtils.hasText(id));
        msg = waitForNextMessage(toAddress);
        Assertions.assertTrue(msg.isPresent());
        message = msg.get();
        Assertions.assertEquals(new InternetAddress(toAddress), message.getRecipients(RecipientType.TO)[0]);
        Assertions.assertEquals(new InternetAddress(FROM), message.getFrom()[0]);
        Assertions.assertEquals(TITLE, message.getSubject());
        Assertions.assertEquals(BODY_TEXT, normalizeCarriageReturnAndEmptyLastLine(extractContent(message)));
    }
    
    @Test
    public void testPushOneSingleEmailHtml() throws Exception {
        String id;
        Optional<MimeMessage> msg;
        MimeMessage message;
        Document document, sample;
        MessageBean sendMessage;
        String toAddress;
        
        toAddress = generateEmailTo();
        
        sendMessage = buildEmailMessage(toAddress, BODY_HTML, EContentType.HTML);
        id = commService.pushMessage(sendMessage);
        Assertions.assertTrue(StringUtils.hasText(id));
        msg = waitForNextMessage(toAddress);
        Assertions.assertTrue(msg.isPresent());
        message = msg.get();
        Assertions.assertEquals(message.getContentType(), String.format("%s; charset=%s", EContentType.HTML.getMediaType(), sendMessage.getBodyCharSet()));
        Assertions.assertEquals(new InternetAddress(toAddress), message.getRecipients(RecipientType.TO)[0]);
        Assertions.assertEquals(new InternetAddress(FROM), message.getFrom()[0]);
        Assertions.assertEquals(TITLE, message.getSubject());
        document = Jsoup.parse(normalizeCarriageReturn(extractContent(message)));
        sample = Jsoup.parse(BODY_HTML);
        
        Assertions.assertTrue(document.hasSameValue(sample));
    }
}
