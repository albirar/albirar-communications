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
package cat.albirar.communications.test.services;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.validation.ValidationException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import cat.albirar.communications.channels.models.ContactBean;
import cat.albirar.communications.channels.models.ECommunicationChannelType;
import cat.albirar.communications.channels.models.LocalizableAttributesCommunicationChannelBean;
import cat.albirar.communications.messages.models.MessageBean;
import cat.albirar.communications.services.ICommunicationService;
import cat.albirar.communications.test.AbstractCommunicationsTest;
import cat.albirar.communications.test.EmailTestContainerExtension;
import cat.albirar.template.engine.EContentType;

/**
 * Test for {@link ICommunicationService}.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@ExtendWith(EmailTestContainerExtension.class)
public class CommunicationServiceTest extends AbstractCommunicationsTest {
    private static final String FROM = "test@host.com";
    private static final String TITLE = "Test";
    private static final String BODY_TEXT = "Hello!\n\nI'm really happy.\n\nGoodbye!";
    private static final String BODY_HTML = "<html><head><title>Test Title</title></head>"
            + "<body>"
            + "<h1>Hi!</h1>"
            + "<p>I&#39;m very happy to meet <strong>you</strong>!</p>"
            + "<p>This is a link to test:</p>"
            + "<p><a href='https://duckduckgo.com' target='_blank' class='url'>https://duckduckgo.com</a></p>"
            + "<p>And this is a unordered list:</p>"
            + "<ul>"
            + "<li>Item 1</li>"
            + "<li>Item 2</li>"
            + "<li>Item 3</li>"
            + "</ul>"
            + "<p>&nbsp;</p>"
            + "<p>Greetings!</p>"
            + "</body>"
            + "</html>";
    
    @Autowired
    private ICommunicationService commService;
    
    private static AtomicInteger seq = new AtomicInteger(0);
    
    private String generateEmailTo() {
        return String.format("a%09d@test.com", seq.incrementAndGet());
    }

    @Test
    public void testValidations() {
        Assertions.assertThrows(ValidationException.class, () -> commService.pushMessage(null));
        Assertions.assertThrows(ValidationException.class, () -> commService.pushMessage(MessageBean.builder().build()));
    }
    
    @Test
    public void testPushOneSingleText() throws Exception {
        String id;
        Optional<MimeMessage> msg;
        MimeMessage message;
        String toAddress;
        
        toAddress = generateEmailTo();
        id = commService.pushMessage(MessageBean.builder()
                .receiver(ContactBean.builder()
                        .channelBean(LocalizableAttributesCommunicationChannelBean.builder()
                            .channelType(ECommunicationChannelType.EMAIL)
                            .channelId(toAddress)
                            .build())
                        .displayName(toAddress)
                        .build())
                .sender(ContactBean.builder()
                        .channelBean(LocalizableAttributesCommunicationChannelBean.builder()
                                .channelId(FROM)
                                .channelType(ECommunicationChannelType.EMAIL)
                                .build()
                                )
                        .displayName(FROM)
                        .build())
                .title(TITLE)
                .body(BODY_TEXT)
                .build()
                );
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
    public void testPushOneSingleHtml() throws Exception {
        String id;
        Optional<MimeMessage> msg;
        MimeMessage message;
        Document document, sample;
        MessageBean sendMessage;
        String toAddress;
        
        toAddress = generateEmailTo();
        
        sendMessage = MessageBean.builder()
                .receiver(ContactBean.builder()
                        .channelBean(LocalizableAttributesCommunicationChannelBean.builder()
                            .channelType(ECommunicationChannelType.EMAIL)
                            .channelId(toAddress)
                            .build())
                        .displayName(toAddress)
                        .build())
                .sender(ContactBean.builder()
                        .channelBean(LocalizableAttributesCommunicationChannelBean.builder()
                                .channelId(FROM)
                                .channelType(ECommunicationChannelType.EMAIL)
                                .build()
                                )
                        .displayName(FROM)
                        .build())
                .title(TITLE)
                .body(BODY_HTML)
                .bodyType(EContentType.HTML)
                .build()
                ;
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
