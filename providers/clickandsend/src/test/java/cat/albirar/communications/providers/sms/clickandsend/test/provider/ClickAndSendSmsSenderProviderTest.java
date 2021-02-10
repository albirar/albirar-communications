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
package cat.albirar.communications.providers.sms.clickandsend.test.provider;

import static cat.albirar.communications.core.channels.models.ECommunicationChannelType.EMAIL;
import static cat.albirar.communications.providers.sms.clickandsend.test.provider.ClickAndSendTestConfiguration.SMS_TEXT_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.net.HttpURLConnection;
import java.util.Collections;

import javax.validation.ValidationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.NamingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StringUtils;

import ClickSend.ApiException;
import ClickSend.ApiResponse;
import ClickSend.Api.SmsApi;
import ClickSend.Model.SmsMessageCollection;
import cat.albirar.communications.core.channels.models.ContactBean;
import cat.albirar.communications.core.providers.ProviderException;
import cat.albirar.communications.core.providers.sms.ISmsSenderProvider;
import cat.albirar.communications.providers.sms.clickandsend.impl.ClickAndSendSmsSenderProvider;

/**
 * Test for {@link ClickAndSendSmsSenderProvider}.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@ContextConfiguration(classes = {ClickAndSendTestConfiguration.class})
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
    private SmsApi smsApi;
    
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
    
    @Test
    public void testWebClientPostThrowsException() throws Exception {
        
        doThrow(ApiException.class).when(smsApi).smsSendPostWithHttpInfo(any(SmsMessageCollection.class));
        assertThrows(ProviderException.class, () -> smsSenderProvider.sendSms(namingStrategy.generateName(), senderContact, recipientContact, SMS_TEXT_MESSAGE));
    }
    
    @Test
    public void testWebClientPostReturnHttpStatusNok() throws Exception {
        ApiResponse<String> response;
        
        response = new ApiResponse<String>(HttpURLConnection.HTTP_BAD_METHOD, Collections.emptyMap(), "ERROR");
        doReturn(response).when(smsApi).smsSendPostWithHttpInfo(any(SmsMessageCollection.class));
        assertThrows(ProviderException.class, () -> smsSenderProvider.sendSms(namingStrategy.generateName(), senderContact, recipientContact, SMS_TEXT_MESSAGE));
    }
    
    @Test
    public void testWebClientPostReturnHttpStatusOk() throws Exception {
        ApiResponse<String> response;
        
        response = new ApiResponse<String>(HttpURLConnection.HTTP_OK, Collections.emptyMap(), "SMS OK!");
        doReturn(response).when(smsApi).smsSendPostWithHttpInfo(any(SmsMessageCollection.class));
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
