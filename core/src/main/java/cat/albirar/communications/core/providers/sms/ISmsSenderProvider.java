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
package cat.albirar.communications.core.providers.sms;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import cat.albirar.communications.core.channels.models.ContactBean;
import cat.albirar.communications.core.channels.models.ECommunicationChannelType;
import cat.albirar.communications.core.providers.IServiceProvider;
import cat.albirar.communications.core.providers.ProviderException;

/**
 * The contract for SMS sender provider.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Validated
public interface ISmsSenderProvider extends IServiceProvider {
    /**
     * Send a SMS message to a recipient number.
     * <b>If no exception is raised, the message is considered send.</b>
     * @param messageId A unique id for this sms operation
     * @param from The sender number
     * @param recipient The recipient number
     * @param message The message
     * @return true if sms was send and false if not
     * @throws ProviderException If any problem sending the message was produced
     * @throws IllegalArgumentException If {@code from} or {@code recipient} or both, are not of {@link ECommunicationChannelType#SMS} type
     */
    public void sendSms(@NotBlank String messageId, @NotNull @Valid ContactBean from, @NotNull @Valid ContactBean recipient, @NotBlank String message);
}
