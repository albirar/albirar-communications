/*
 * This file is part of "albirar-communications-provider-clickandsend".
 * 
 * "albirar-communications-provider-clickandsend" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * "albirar-communications-provider-clickandsend" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with "albirar-communications-provider-clickandsend" source code.  If not, see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *
 * Copyright (C) 2021 Octavi Fornés
 */
package cat.albirar.communications.providers.sms.clickandsend.test;

import java.util.Locale;

import org.mockito.Mockito;
import org.springframework.amqp.core.NamingStrategy;
import org.springframework.amqp.core.UUIDNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

import cat.albirar.communications.channels.models.ContactBean;
import cat.albirar.communications.channels.models.ECommunicationChannelType;
import cat.albirar.communications.channels.models.LocalizableAttributesCommunicationChannelBean;
import cat.albirar.communications.providers.sms.clickandsend.configuration.ClickAndSendSmsConfiguration;

/**
 * Configuration for test purposes.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Configuration
@Import(ClickAndSendSmsConfiguration.class)
public class ClickAndSendTestConfiguration {
    public static final String ID_SENDER = "666899799";
    public static final String ID_RECIPIENT = "697225224";
    public static final String SMS_TEXT_MESSAGE = "A test message\nLike any other";
    
    @Bean("senderContact")
    public ContactBean senderContact() {
        return ContactBean.builder()
                .channelBean(LocalizableAttributesCommunicationChannelBean.builder()
                        .channelId(ID_SENDER)
                        .channelType(ECommunicationChannelType.SMS)
                        .locale(Locale.getDefault())
                        .build())
                .displayName(ID_SENDER)
                .preferredLocale(Locale.getDefault())
                .build()
                ;
    }

    @Bean("recipientContact")
    public ContactBean recipientContact() {
        return ContactBean.builder()
                .channelBean(LocalizableAttributesCommunicationChannelBean.builder()
                        .channelId(ID_RECIPIENT)
                        .channelType(ECommunicationChannelType.SMS)
                        .locale(Locale.getDefault())
                        .build())
                .displayName(ID_RECIPIENT)
                .preferredLocale(Locale.getDefault())
                .build()
                ;
    }
    
    @Bean
    @Primary
    public NamingStrategy namingStrategy() {
        return new UUIDNamingStrategy();
    }
    
    @Bean
    @Primary
    public WebClient webClient() {
        return Mockito.mock(WebClient.class);
    }
}
