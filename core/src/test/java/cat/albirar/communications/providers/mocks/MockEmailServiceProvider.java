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
package cat.albirar.communications.providers.mocks;

import java.time.Instant;

import javax.validation.constraints.NotBlank;

import org.springframework.stereotype.Component;

import cat.albirar.communications.channels.models.ContactBean;
import cat.albirar.communications.providers.ProviderException;
import cat.albirar.communications.providers.email.EmailPushBean;
import cat.albirar.communications.providers.email.IEmailProvider;

/**
 * An implementation for {@link IEmailProvider} for test purposes.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Component
public class MockEmailServiceProvider extends AbstractMockServiceProvider<EmailPushBean> implements IMockEmailServiceProvider {
    /**
     * {@inheritDoc}
     */
    @Override
    public @NotBlank String getName() {
        return MockEmailServiceProvider.class.getSimpleName().toUpperCase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendEmail(String messageId, ContactBean from, ContactBean recipient, String subject, String message, String mediaType, String charset) {
        EmailPushBean bean;
        
        bean = EmailPushBean.builder()
                .messageId(messageId)
                .timestamp(Instant.now())
                .from(from)
                .recipient(recipient)
                .subject(subject)
                .message(message)
                .mediaType(mediaType)
                .charset(charset)
                .build()
                ;
        if(throwException) {
            synchronized(throwMessages) {
                throwMessages.put(messageId, bean);
            }
            throw new ProviderException("Test exception");
        }
        
        pushedMessages.put(messageId, bean);
    }

}
