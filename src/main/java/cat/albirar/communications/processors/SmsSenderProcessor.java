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
package cat.albirar.communications.processors;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cat.albirar.communications.messages.models.MessageBean;
import cat.albirar.communications.providers.ProviderException;
import cat.albirar.communications.providers.sms.ISmsSenderProvider;
import cat.albirar.communications.status.EStatusMessage;
import cat.albirar.communications.status.models.MessageStatusBean;

/**
 * The processor for sending sms messages.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Component
public class SmsSenderProcessor extends AbstractSingleProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmsSenderProcessor.class);
    
    @Autowired
    private @Size(min = 1) List<ISmsSenderProvider> smsProviders;
    /**
     * {@inheritDoc}
     */
    @Override
    protected void processMessage(String messageId, MessageBean message) {
        MessageStatusBean r;
        LOGGER.info("Sending SMS for message {}", message);
        
        try {
            // TODO Until adapted, use the first one
            smsProviders.get(0).sendSms(message.getSender(), message.getReceiver(), message.getBody());
            r = MessageStatusBean.copyBuilder(message)
                    .status(EStatusMessage.SEND)
                    .messageId(messageId)
                    .build()
                    ;
            reportMessage(r);
        } catch(ProviderException e) {
            String errMsg;
            
            errMsg = String.format("On preparing or sending the SMS message for %s (%s)", message, e.getMessage());
            LOGGER.error(errMsg, e);
            r = MessageStatusBean.copyBuilder(message)
                    .status(EStatusMessage.ERROR)
                    .errorMessage(Optional.of(errMsg))
                    .build()
                    ;
            reportMessage(r);
        }
    }
}
