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

import java.util.Optional;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

import cat.albirar.communications.core.providers.ProviderException;
import cat.albirar.communications.providers.email.RootPushBean;

/**
 * A common behavior for any test class for mock service provider.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Validated
public interface IMockServiceProvider<T extends RootPushBean> {
    /**
     * Test if any message with the indicated {@code messageId} was processed.
     * @param messageId The messageId
     * @return true if processed and false if not
     */
    public boolean isMessageProcessed(@NotBlank String messageId);
    /**
     * Test if any message with the indicated {@code messageId} was processed when {@link #isThrowException()} is on.
     * @param messageId The messageId
     * @return true if processed and false if not
     */
    public boolean isThrowMessageProcessed(@NotBlank String messageId);
    /**
     * Return the pushed information associated with {@code messageId}.
     * @param messageId The message id
     * @return The pushed information or {@link Optional#empty()} if no message with {@code messageId} was processed
     */
    public Optional<T> getMessage(@NotBlank String messageId);
    /**
     * Should to throw the {@link ProviderException} when processing message.
     * @return true if throw and false if not
     */
    public boolean isThrowException();
    /**
     * Should to throw the {@link ProviderException} when processing message.
     * @param throwException true if throw and false if not
     */
    public void setThrowException(boolean throwException);
}
