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
package cat.albirar.communications.test.configuration;

import java.nio.charset.Charset;
import java.util.Locale;

import cat.albirar.communications.core.channels.models.ContactBean;
import cat.albirar.communications.core.channels.models.ECommunicationChannelType;
import cat.albirar.communications.core.channels.models.LocalizableAttributesCommunicationChannelBean;
import cat.albirar.communications.core.messages.models.MessageBean;
import cat.albirar.template.engine.EContentType;

/**
 * A message Factory for test purposes.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
public class TestMessagesFactory {
    private MessageBean emailMessage;
    private MessageBean smsMessage;
    
    public TestMessagesFactory() {
        emailMessage = MessageBean.builder()
                .body("Test EMAIL BODY")
                .bodyCharSet(Charset.defaultCharset())
                .bodyType(EContentType.TEXT_PLAIN)
                .sender(ContactBean.builder()
                        .displayName("Test email sender")
                        .preferredLocale(Locale.CANADA)
                        .channelBean(LocalizableAttributesCommunicationChannelBean.builder()
                                .channelType(ECommunicationChannelType.EMAIL)
                                .channelId("sender@server.ca")
                                .locale(Locale.CANADA)
                                .build())
                        .build())
                .receiver(ContactBean.builder()
                        .displayName("Test email receiver")
                        .preferredLocale(Locale.US)
                        .channelBean(LocalizableAttributesCommunicationChannelBean.builder()
                                .channelType(ECommunicationChannelType.EMAIL)
                                .channelId("receiver@server.us")
                                .locale(Locale.US)
                                .build())
                        .build())
                .title("Email test message")
                .build()
                ;
        smsMessage = MessageBean.builder()
                .body("Test SMS BODY")
                .bodyCharSet(Charset.forName("US-ASCII"))
                .bodyType(EContentType.TEXT_PLAIN)
                .sender(ContactBean.builder()
                        .displayName("Test sms sender")
                        .preferredLocale(Locale.US)
                        .channelBean(LocalizableAttributesCommunicationChannelBean.builder()
                                .channelType(ECommunicationChannelType.SMS)
                                .channelId("+34622152635")
                                .locale(Locale.US)
                                .build())
                        .build())
                .receiver(ContactBean.builder()
                        .displayName("Test sms receiver")
                        .preferredLocale(Locale.US)
                        .channelBean(LocalizableAttributesCommunicationChannelBean.builder()
                                .channelType(ECommunicationChannelType.SMS)
                                .channelId("+28356999256")
                                .locale(Locale.US)
                                .build())
                        .build())
                .title("SMS test message")
                .build()
                ;        
    }

    public MessageBean getEmailMessage() {
        return emailMessage;
    }

    public MessageBean getSmsMessage() {
        return smsMessage;
    }
    
}
