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
package cat.albirar.communications.status.models;

import java.util.Optional;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import cat.albirar.communications.messages.models.MessageBean;
import cat.albirar.communications.status.EStatusMessage;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * The status of a message.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@SuperBuilder(toBuilder = true)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class MessageStatusBean extends MessageBean {
    private static final long serialVersionUID = -4642994972130453525L;
    @NotBlank
    @Setter(onParam_ = { @NotBlank })
    private String messageId;
    @NotNull
    @Setter(onParam_ = { @NotNull })
    private EStatusMessage status;
    @Default
    @NotNull
    @Setter(onParam_ = { @NotNull })
    private Optional<String> errorMessage = Optional.empty();
    
    public static final MessageStatusBeanBuilder<?, ?> copyBuilder(MessageBean message) {
        return (MessageStatusBeanBuilder<?, ?>) MessageStatusBean.builder()
                .address(message.getAddress())
                .addressFrom(message.getAddressFrom())
                .title(message.getTitle())
                .bodyType(message.getBodyType())
                .bodyCharSet(message.getBodyCharSet())
                .body(message.getBody())
                ;
    }
}
