/*
 * This file is part of "albirar-communications".
 * 
 * "albirar-communications" is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * "albirar-communications" is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with "albirar-communications" source code. If
 * not, see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *
 * Copyright (C) 2020 Octavi Fornés
 */
package cat.albirar.communications.providers.sms.clickandsend.impl;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import ClickSend.ApiException;
import ClickSend.ApiResponse;
import ClickSend.Api.SmsApi;
import ClickSend.Model.SmsMessage;
import ClickSend.Model.SmsMessageCollection;
import cat.albirar.communications.channels.models.ContactBean;
import cat.albirar.communications.channels.models.ECommunicationChannelType;
import cat.albirar.communications.providers.ProviderException;
import cat.albirar.communications.providers.sms.ISmsSenderProvider;

/**
 * Click & Send SMS service provider.
 * 
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 * @see <a href="https://clicksend.com/">https://clicksend.com/</a>
 */
@Component(value = "clickAndSendSmsProvider")
public class ClickAndSendSmsSenderProvider implements ISmsSenderProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClickAndSendSmsSenderProvider.class);

    public static final String CLICK_AND_SEND_SMS_PROVIDER_NAME = "clickAndSendSmsProvider";

    @Autowired
    private SmsApi smsApi;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return CLICK_AND_SEND_SMS_PROVIDER_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendSms(String messageId, ContactBean from, ContactBean recipient, String message) {
        SmsMessage msg;
        List<SmsMessage> body;
        String s;
        ApiResponse<String> apiResponse;
        SmsMessageCollection smsMessages;

        Assert.isTrue(from.getChannelBean().getChannelType() == ECommunicationChannelType.SMS, "'from' channel should be of SMS type");
        Assert.isTrue(recipient.getChannelBean().getChannelType() == ECommunicationChannelType.SMS, "'recipient' channel should be of SMS type");
        LOGGER.debug("Preparing to send a SMS with message id {} ({})", messageId, message);
        msg = new SmsMessage().source(from.getChannelBean().getChannelId()).to(recipient.getChannelBean().getChannelId()).body(message);
        body = Arrays.asList(msg);
        smsMessages = new SmsMessageCollection();
        smsMessages.messages(body);

        LOGGER.debug("Sending SMS with id {} ({})", messageId, msg);
        try {
            apiResponse = smsApi.smsSendPostWithHttpInfo(smsMessages);
            if(apiResponse.getStatusCode() != HttpURLConnection.HTTP_OK) {
                s = String.format("SMS message with id %s cannot be send: %s", messageId, apiResponse);
                LOGGER.error(s);
                throw new ProviderException(s);
            }
        }
        catch(ApiException e) {
            s = String.format("Exception on sending message with id %s (%s)", messageId, msg);
            LOGGER.error(s, e);
            throw new ProviderException(s, e);
        }

        LOGGER.info("SMS message with id {} send OK!", messageId);
    }
}
