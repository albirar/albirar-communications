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
import cat.albirar.communications.providers.email.SmsPushBean;
import cat.albirar.communications.providers.sms.ISmsSenderProvider;

/**
 * An implementation for {@link ISmsSenderProvider} for test purposes.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Component
public class MockSmsServiceProvider extends AbstractMockServiceProvider<SmsPushBean> implements IMockSmsServiceProvider {
    /**
     * {@inheritDoc}
     */
    @Override
    public @NotBlank String getName() {
        return MockSmsServiceProvider.class.getSimpleName().toUpperCase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendSms(String messageId, ContactBean from, ContactBean recipient, String message) {
        if(throwException) {
            synchronized(throwMessages) {
                throwMessages.put(messageId, SmsPushBean.builder()
                        .messageId(messageId)
                        .timestamp(Instant.now())
                        .from(from)
                        .recipient(recipient)
                        .message(message)
                        .build()
                        );
            }
            throw new ProviderException("Test exception");
        }
        
        synchronized(pushedMessages) {
            pushedMessages.put(messageId, SmsPushBean.builder()
                    .messageId(messageId)
                    .timestamp(Instant.now())
                    .from(from)
                    .recipient(recipient)
                    .message(message)
                    .build()
                    );
        }
    }

}
