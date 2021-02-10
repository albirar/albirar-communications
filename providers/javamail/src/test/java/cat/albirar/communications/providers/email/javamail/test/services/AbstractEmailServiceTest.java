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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import cat.albirar.communications.core.channels.models.ContactBean;
import cat.albirar.communications.core.channels.models.ECommunicationChannelType;
import cat.albirar.communications.core.channels.models.LocalizableAttributesCommunicationChannelBean;
import cat.albirar.communications.core.configuration.AlbirarCommunicationsConfiguration;
import cat.albirar.communications.core.messages.models.MessageBean;
import cat.albirar.communications.core.services.ICommunicationService;
import cat.albirar.communications.providers.email.javamail.configuration.JavaMailSenderConfiguration;
import cat.albirar.communications.providers.email.javamail.test.containers.EmailTestContainerExtension;
import cat.albirar.communications.test.AbstractCommunicationsTest;
import cat.albirar.communications.test.RabbitMqContainerExtension;
import cat.albirar.template.engine.EContentType;

/**
 * Abstract for email TEST.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 2.0.0
 */
@ContextConfiguration(classes = {JavaMailSenderConfiguration.class, AlbirarCommunicationsConfiguration.class})
@ExtendWith({SpringExtension.class, RabbitMqContainerExtension.class})
public abstract class AbstractEmailServiceTest extends AbstractCommunicationsTest {
    protected static final String FROM = "test@host.com";
    protected static final String TITLE = "Test";
    protected static final String BODY_TEXT = "Hello!\n\nI'm really happy.\n\nGoodbye!";
    protected static final String BODY_HTML = "<html><head><title>Test Title</title></head>"
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
    protected ICommunicationService commService;
    protected static AtomicInteger seq = new AtomicInteger(0);

    /**
     * Extract content from {@code mimeMessage}.
     * @param mimeMessage The mimeMessage
     * @return The content (text plain or html) in text format
     */
    protected String extractContent(MimeMessage mimeMessage) throws Exception {
        Object contentObject;
        Multipart content;
        BodyPart bodyPart;
        StringBuffer retorn;
        
        contentObject = mimeMessage.getContent();
        retorn = new StringBuffer();
        if(Multipart.class.isAssignableFrom(contentObject.getClass())) {
            content = (Multipart) contentObject;
            for(int n=0; n < content.getCount(); n++) {
                bodyPart = content.getBodyPart(n);
                if(bodyPart.isMimeType(EContentType.TEXT_PLAIN.getMediaType())) {
                    retorn.append((String)bodyPart.getContent());
                } else {
                    if(bodyPart.isMimeType(EContentType.HTML.getMediaType())) {
                        retorn.append(Jsoup.parse((String) bodyPart.getContent()).text());
                    }
                }
            }
        } else {
            retorn.append((String)mimeMessage.getContent());
        }
        return retorn.toString();
    }
    protected String generateEmailTo() {
        return String.format("a%09d@test.com", seq.incrementAndGet());
    }
    
    protected MessageBean buildEmailMessage(String body, EContentType contentType) {
        return buildEmailMessage(generateEmailTo(), body, contentType);
    }
    
    protected MessageBean buildEmailMessage(String toAddress, String body, EContentType contentType) {
        return MessageBean.builder()
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
            .body(body)
            .bodyType(contentType)
            .build()
        ;
    }
    /**
     * Wait for next message on email server send to {@code toAddress}.
     * @return The message or {@link Optional#empty()} if no messages was found send to {@code addressTo}
     */
    protected Optional<MimeMessage> waitForNextMessage(String toAddress) throws Exception {
        List<MimeMessage> l;

        if(EmailTestContainerExtension.waitForIncomingEmail()) {
            l = EmailTestContainerExtension.getReceivedMessages(toAddress);
            // Wait for execution
            for(int n = 0; n < LOOP_FOR_TEST && l.isEmpty(); n++) {
                TimeUnit.MILLISECONDS.sleep(WAIT_FOR_WORK);
                l = EmailTestContainerExtension.getReceivedMessages(toAddress);
            }
            if(!l.isEmpty()) {
                return Optional.of(l.get(0));
            }
        }
        return Optional.empty();
    }
}
