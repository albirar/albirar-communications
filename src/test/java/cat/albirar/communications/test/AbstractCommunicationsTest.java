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
package cat.albirar.communications.test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import cat.albirar.communications.configuration.AlbirarCommunicationsConfiguration;
import cat.albirar.communications.test.configuration.DefaultTestConfiguration;
import cat.albirar.template.engine.EContentType;

/**
 * A convenient abstract class to derive to any test.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@ContextConfiguration(classes = {AlbirarCommunicationsConfiguration.class, DefaultTestConfiguration.class})
@ExtendWith({SpringExtension.class, RabbitMqContainerExtension.class})
public abstract class AbstractCommunicationsTest {
    protected static long WAIT_FOR_WORK = 1000;
    protected static int LOOP_FOR_TEST = 5;

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
    /**
     * Normalize text for change CR+NL by NL and removes last empty line.
     * In some cases, the original text from email messages change new-line by carriage-return+new-line, and add a extra empty line at end.
     * @param origin The original email text
     * @return The text, normalized and without the last empty line.
     */
    protected String normalizeCarriageReturnAndEmptyLastLine(String origin) {
        String s = origin.replaceAll("\\r", "");
        return s.substring(0, s.lastIndexOf('\n'));
    }
    /**
     * Normalize text for change CR+NL by NL.
     * In some cases, the original text from email messages change new-line by carriage-return+new-line.
     * @param origin The original email text
     * @return The text, normalized
     */
    protected String normalizeCarriageReturn(String origin) {
        return origin.replaceAll("\\r", "");
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
