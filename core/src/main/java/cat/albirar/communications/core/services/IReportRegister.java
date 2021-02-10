/*
 * This file is part of "albirar-communications-core".
 * 
 * "albirar-communications-core" is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * "albirar-communications-core" is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with "albirar-communications-core" source
 * code. If not, see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *
 * Copyright (C) 2020 Octavi Fornés
 */
package cat.albirar.communications.core.services;

import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import cat.albirar.communications.core.configuration.AlbirarCommunicationsConfiguration;
import cat.albirar.communications.core.configuration.IPropertiesComm;
import cat.albirar.communications.core.status.models.MessageStatusBean;

/**
 * The report register, that hold any report message.
 * The register hold a collection with {@link Map} for each report type (see {@link IPropertiesComm#QUEUE_REPORT_EMAIL} and {@link IPropertiesComm#QUEUE_REPORT_SMS}).
 * When a new message is reported, a collection should to be indicated.
 * When users ask for get report or check if a report exists, all collection are checked for the indicated id.
 * Warning about this, if {@link AlbirarCommunicationsConfiguration#namingStrategy()} is no unique over all collections, a
 * collision can be produced if two reports with same id is put on this register.
 * 
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Validated
public interface IReportRegister {

    /**
     * Get the report about the message with id {@code messageId}.
     * @param messageId The key
     * @return The message or {@link Optional#empty()} if no report for that messageId exists.
     */
    Optional<MessageStatusBean> getReport(@NotBlank String messageId);

    /**
     * Get and delete the report associated with the indicated {@code messageId}
     * @param messageId The message id
     * @return The associated report or {@link Optional#empty()} if no report exists associated with the indicated {@code messageId} 
     */
    Optional<MessageStatusBean> getAndDeleteReport(@NotBlank String messageId);

    /**
     * Check if report for {@code messageId} exists.
     * @param messageId
     * @return true if exists and false if not
     */
    boolean isReport(@NotBlank String messageId);

    /**
     * Put a new {@code report} for the {@code messageId} onto collection with {@code collectionId} id.
     * @param collectionId The collection id
     * @param messageId The message id
     * @param report The report
     */
    void putReport(@NotBlank String collectionId, @NotBlank String messageId, @NotNull @Valid MessageStatusBean report);

    /**
     * Remove, if exists, the report associated with the {@code messageId} from the collection with {@code collectionId} id.
     * @param collectionId The collection id
     * @param messageId The message id
     * @return The report or {@link Optional#empty()} if report was not found
     */
    Optional<MessageStatusBean> removeReport(@NotBlank String collectionId, @NotBlank String messageId);

}