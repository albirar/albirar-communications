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
package cat.albirar.communications.providers.sms.clickandsend;

import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import cat.albirar.communications.channels.models.ContactBean;
import cat.albirar.communications.providers.ProviderException;
import cat.albirar.communications.providers.sms.ISmsSenderProvider;
import cat.albirar.communications.sms.providers.clickandsend.ClickAndSendMessage;
import cat.albirar.communications.sms.providers.clickandsend.ClickAndSendProperties;
import reactor.core.publisher.Mono;

/**
 * Click & Send SMS service provider. 
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 * @see <a href="https://clicksend.com/">https://clicksend.com/</a>
 */
@Component
public class ClickAndSendSmsSenderProvider implements ISmsSenderProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClickAndSendSmsSenderProvider.class);
    
    public static final String CLICK_AND_SEND_SMS_PROVIDER_NAME = "ClickAndSend";
    
    private WebClient webClient;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return CLICK_AND_SEND_SMS_PROVIDER_NAME;
    }
    
    @Autowired
    private ClickAndSendProperties props;
    
    @PostConstruct
    public void init() {
        String stb;
        
        stb = new StringBuilder("Basic ").append(props.getUsername())
                .append(":").append(props.getKey())
                .toString()
                ;
        stb = Base64.getEncoder().encodeToString(stb.getBytes());
        webClient = WebClient.builder().baseUrl(ClickAndSendProperties.URL_BASE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, stb)
                .build();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void sendSms(ContactBean from, ContactBean recipient, String message) {
        ClickAndSendMessage msg;
        List<ClickAndSendMessage> body;
        ClientResponse crsp;
        String s;

        LOGGER.debug("Preparing to send a SMS...");
        msg = ClickAndSendMessage.builder()
                .to(recipient.getChannelBean().getChannelId())
                .from(from.getDisplayName())
                .body(message)
                .build()
                ;
        body = Arrays.asList(msg);
        
        LOGGER.debug("Send SMS {}", msg);
        crsp = webClient.post()
            .uri(ClickAndSendProperties.URI_SEND_SMS)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(body), new ParameterizedTypeReference<List<ClickAndSendMessage>>() {})
            .exchange()
            .block(Duration.ofMillis(props.getTimeout()))
            ;
        
        if(crsp == null) {
            LOGGER.error("Timeout on wait for response on sending SMS!");
            throw new ProviderException("Timeout on wait for response on sending SMS!");
        }
        if(crsp.statusCode() != HttpStatus.OK) {
            s = String.format("On sending SMS message %s, Statuscode: %s", msg, crsp.statusCode());
            LOGGER.error(s);
            throw new ProviderException(s);
        }
        LOGGER.debug("SMS message {} send OK!", msg);
    }
}
