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
package cat.albirar.communications.providers.email;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import cat.albirar.communications.channels.models.ContactBean;
import cat.albirar.communications.providers.IServiceProvider;

/**
 * An email service provider.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
public interface IEmailProvider extends IServiceProvider {
    /**
     * Send an email message to a recipient.
     * <b>If no exception is raised, the message is considered to be send.</b>
     * @param messageId A unique id for this email operation
     * @param from The sender
     * @param recipient The recipient
     * @param subject The message subject
     * @param message The message body
     * @param mediaType The media type of body message
     * @param charset The charset of body message
     * @throws ProviderException If any problem sending the message was produced
     */
    public void sendEmail(@NotBlank String messageId, @NotNull @Valid ContactBean from, @NotNull @Valid ContactBean recipient, @NotBlank String subject, @NotBlank String message, @NotBlank String mediaType, @NotBlank String charset);

}
