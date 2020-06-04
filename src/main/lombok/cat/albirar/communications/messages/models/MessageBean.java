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
package cat.albirar.communications.messages.models;

import java.io.Serializable;
import java.nio.charset.Charset;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import cat.albirar.communications.models.CommunicationChannelBean;
import cat.albirar.template.engine.EContentType;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * A message bean with address (to or from), title (in SMS does'nt apply) and body.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@SuperBuilder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class MessageBean implements Serializable {
    private static final long serialVersionUID = 3661502290127929947L;
    
    @NotNull
    @Valid
    @Setter(onParam_ = { @NotNull, @Valid })
    private CommunicationChannelBean address;
    @NotBlank
    @Valid
    @Setter(onParam_ = { @NotBlank, @Valid })
    private String addressFrom;
    
    @NotBlank
    @Setter(onParam_ = { @NotBlank })
    private String title;
    
    @NotNull
    @Setter(onParam_ = { @NotNull })
    @Default
    private EContentType bodyType = EContentType.HTML;
    
    @NotNull
    @Setter(onParam_ = { @NotNull })
    @Default
    private Charset bodyCharSet = Charset.forName("UTF-8");
    
    @NotBlank
    @Setter(onParam_ = { @NotBlank })
    private String body;
}
