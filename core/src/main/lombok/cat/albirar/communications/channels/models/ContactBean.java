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
package cat.albirar.communications.channels.models;

import java.io.Serializable;
import java.util.Locale;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder.Default;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Data contact of a sender or receiver in SMS or EMAIL.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@SuperBuilder(toBuilder = true)
@Data
@JsonInclude(Include.NON_EMPTY)
public class ContactBean implements Serializable {
    private static final long serialVersionUID = 7887468884518241617L;

    @NotBlank
    @Setter(onParam_ = { @NotBlank })
    private String displayName;
    
    @NotNull
    @Setter(onParam_ = { @NotNull })
    @Default
    private Locale preferredLocale = Locale.getDefault();
    
    @NotNull
    @Valid
    @Setter(onParam_ = { @NotNull, @Valid })
    @Default
    private LocalizableAttributesCommunicationChannelBean channelBean = LocalizableAttributesCommunicationChannelBean.builder().build();
    
    public ContactBean() {
        ContactBean cb = ContactBean.builder().build();
        displayName = cb.getDisplayName();
        preferredLocale = cb.getPreferredLocale();
        channelBean = cb.getChannelBean();
    }
}
