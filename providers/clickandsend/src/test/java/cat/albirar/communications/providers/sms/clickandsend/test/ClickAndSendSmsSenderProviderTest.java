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

import static cat.albirar.communications.channels.models.ECommunicationChannelType.EMAIL;
import static cat.albirar.communications.providers.sms.clickandsend.test.ClickAndSendTestConfiguration.SMS_TEXT_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.time.Duration;
import java.util.List;

import javax.validation.ValidationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.NamingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;

import cat.albirar.communications.channels.models.ContactBean;
import cat.albirar.communications.configuration.AlbirarCommunicationsConfiguration;
import cat.albirar.communications.providers.ProviderException;
import cat.albirar.communications.providers.sms.ISmsSenderProvider;
import cat.albirar.communications.providers.sms.clickandsend.ClickAndSendMessage;
import cat.albirar.communications.providers.sms.clickandsend.ClickAndSendProperties;
import cat.albirar.communications.providers.sms.clickandsend.ClickAndSendSmsSenderProvider;
import reactor.core.publisher.Mono;

/**
 * Test for {@link ClickAndSendSmsSenderProvider}.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@ContextConfiguration(classes = {ClickAndSendTestConfiguration.class, AlbirarCommunicationsConfiguration.class})
@ExtendWith({SpringExtension.class})
public class ClickAndSendSmsSenderProviderTest {
    @Autowired
    private ISmsSenderProvider smsSenderProvider;
    
    @Autowired
    private NamingStrategy namingStrategy;
    
    @Autowired
    @Qualifier("senderContact")
    private ContactBean senderContact;
    
    @Autowired
    @Qualifier("recipientContact")
    private ContactBean recipientContact;
    
    @Autowired
    private WebClient webClient;
    
    @Test
    public void testArgsValidation() {
        assertThrows(ValidationException.class, () -> smsSenderProvider.sendSms(null, null, null, null));
        assertThrows(ValidationException.class, () -> smsSenderProvider.sendSms("", null, null, null));
        assertThrows(ValidationException.class, () -> smsSenderProvider.sendSms("     ", null, null, null));
        assertThrows(ValidationException.class, () -> smsSenderProvider.sendSms(namingStrategy.generateName(), null, null, null));
        assertThrows(ValidationException.class, () -> smsSenderProvider.sendSms(namingStrategy.generateName(), null, null, ""));
        assertThrows(ValidationException.class, () -> smsSenderProvider.sendSms(namingStrategy.generateName(), null, null, "     "));
        assertThrows(ValidationException.class, () -> smsSenderProvider.sendSms(namingStrategy.generateName(), null, null, SMS_TEXT_MESSAGE));
        assertThrows(ValidationException.class, () -> smsSenderProvider.sendSms(namingStrategy.generateName(), senderContact, null, SMS_TEXT_MESSAGE));
        assertThrows(ValidationException.class, () -> smsSenderProvider.sendSms(namingStrategy.generateName(), null, recipientContact, SMS_TEXT_MESSAGE));
        assertThrows(IllegalArgumentException.class, () -> smsSenderProvider.sendSms(namingStrategy.generateName(), senderContact.toBuilder().channelBean(senderContact.getChannelBean().toBuilder().channelType(EMAIL).build()).build()
                , recipientContact, SMS_TEXT_MESSAGE));
        assertThrows(IllegalArgumentException.class, () -> smsSenderProvider.sendSms(namingStrategy.generateName(), senderContact, recipientContact.toBuilder().channelBean(recipientContact.getChannelBean().toBuilder().channelType(EMAIL).build()).build()
                , SMS_TEXT_MESSAGE));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testWebClientPostReturnsNull() {
        RequestBodyUriSpec rbus;
        RequestBodySpec rbs;
        RequestHeadersSpec<?> rhs;
        Mono<ClientResponse> mn;
        
        rbus = mock(RequestBodyUriSpec.class);
        rbs = mock(RequestBodySpec.class);
        rhs = mock(RequestHeadersSpec.class);
        mn = mock(Mono.class);
        
        doReturn(rbus).when(webClient).post();
        doReturn(rbs).when(rbus).uri(eq(ClickAndSendProperties.URI_SEND_SMS));
        doReturn(rbs).when(rbs).contentType(eq(MediaType.APPLICATION_JSON));
        doReturn(rhs).when(rbs).<List<ClickAndSendMessage>, Mono<List<ClickAndSendMessage>>>body(any(Mono.class), any(ParameterizedTypeReference.class));
        doReturn(mn).when(rhs).exchange();
        doReturn(null).when(mn).block(any(Duration.class));
        
        assertThrows(ProviderException.class, () -> smsSenderProvider.sendSms(namingStrategy.generateName(), senderContact, recipientContact, SMS_TEXT_MESSAGE));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testWebClientPostThrowsException() {
        RequestBodyUriSpec rbus;
        RequestBodySpec rbs;
        RequestHeadersSpec<?> rhs;
        Mono<ClientResponse> mn;
        
        rbus = mock(RequestBodyUriSpec.class);
        rbs = mock(RequestBodySpec.class);
        rhs = mock(RequestHeadersSpec.class);
        mn = mock(Mono.class);
        
        doReturn(rbus).when(webClient).post();
        doReturn(rbs).when(rbus).uri(eq(ClickAndSendProperties.URI_SEND_SMS));
        doReturn(rbs).when(rbs).contentType(eq(MediaType.APPLICATION_JSON));
        doReturn(rhs).when(rbs).<List<ClickAndSendMessage>, Mono<List<ClickAndSendMessage>>>body(any(Mono.class), any(ParameterizedTypeReference.class));
        doReturn(mn).when(rhs).exchange();
        doThrow(RuntimeException.class).when(mn).block(any(Duration.class));
        
        assertThrows(ProviderException.class, () -> smsSenderProvider.sendSms(namingStrategy.generateName(), senderContact, recipientContact, SMS_TEXT_MESSAGE));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testWebClientPostReturnHttpStatusNok() {
        RequestBodyUriSpec rbus;
        RequestBodySpec rbs;
        RequestHeadersSpec<?> rhs;
        Mono<ClientResponse> mn;
        ClientResponse crsp;
        
        rbus = mock(RequestBodyUriSpec.class);
        rbs = mock(RequestBodySpec.class);
        rhs = mock(RequestHeadersSpec.class);
        mn = mock(Mono.class);
        crsp = mock(ClientResponse.class);
        
        doReturn(rbus).when(webClient).post();
        doReturn(rbs).when(rbus).uri(eq(ClickAndSendProperties.URI_SEND_SMS));
        doReturn(rbs).when(rbs).contentType(eq(MediaType.APPLICATION_JSON));
        doReturn(rhs).when(rbs).<List<ClickAndSendMessage>, Mono<List<ClickAndSendMessage>>>body(any(Mono.class), any(ParameterizedTypeReference.class));
        doReturn(mn).when(rhs).exchange();
        doReturn(crsp).when(mn).block(any(Duration.class));
        doReturn(HttpStatus.BAD_REQUEST).when(crsp).statusCode();
        
        assertThrows(ProviderException.class, () -> smsSenderProvider.sendSms(namingStrategy.generateName(), senderContact, recipientContact, SMS_TEXT_MESSAGE));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testWebClientPostReturnHttpStatusOk() {
        RequestBodyUriSpec rbus;
        RequestBodySpec rbs;
        RequestHeadersSpec<?> rhs;
        Mono<ClientResponse> mn;
        ClientResponse crsp;
        
        rbus = mock(RequestBodyUriSpec.class);
        rbs = mock(RequestBodySpec.class);
        rhs = mock(RequestHeadersSpec.class);
        mn = mock(Mono.class);
        crsp = mock(ClientResponse.class);
        
        doReturn(rbus).when(webClient).post();
        doReturn(rbs).when(rbus).uri(eq(ClickAndSendProperties.URI_SEND_SMS));
        doReturn(rbs).when(rbs).contentType(eq(MediaType.APPLICATION_JSON));
        doReturn(rhs).when(rbs).<List<ClickAndSendMessage>, Mono<List<ClickAndSendMessage>>>body(any(Mono.class), any(ParameterizedTypeReference.class));
        doReturn(mn).when(rhs).exchange();
        doReturn(crsp).when(mn).block(any(Duration.class));
        doReturn(HttpStatus.OK).when(crsp).statusCode();
        
        smsSenderProvider.sendSms(namingStrategy.generateName(), senderContact, recipientContact, SMS_TEXT_MESSAGE);
    }
    
    @Test
    public void testName() {
        String name;
        
        name = smsSenderProvider.getName();
        assertTrue(StringUtils.hasText(name));
        assertEquals(ClickAndSendSmsSenderProvider.CLICK_AND_SEND_SMS_PROVIDER_NAME, name);
    }
}
