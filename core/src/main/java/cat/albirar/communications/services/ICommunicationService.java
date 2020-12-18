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
package cat.albirar.communications.services;

import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import cat.albirar.communications.messages.models.MessageBean;
import cat.albirar.communications.status.models.MessageStatusBean;

/**
 * The contract for communications services.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Validated
public interface ICommunicationService {
    /**
     * Get information about {@link MessageStatusBean status} of message with {@code messageId} and delete it.
     * @param messageId The message id returned by {@link #pushMessage(MessageBean)} or by {@link #sendMessage(MessageBean)}
     * @return The message status or {@link Optional#empty()} if no message record exists with the indicated {@code messageId}
     * @throws ServiceException On service exceptions errors
     */
    public Optional<MessageStatusBean> popStatusMessage(@NotBlank String messageId);
    /**
     * Check for existence of status message for a message with {@code messageId}.
     * @param messageId The message id of the associated message
     * @return true if status message exists for associated message with {@code messageId} and false if not
     */
    public boolean isStatusMessage(@NotBlank String messageId);
    /**
     * Push a message to dispatch asynchronously.
     * @param message The message to send
     * @return The identification of pushed message
     * @throws ServiceException On service exceptions errors
     */
    public String pushMessage(@NotNull @Valid MessageBean message);
}
