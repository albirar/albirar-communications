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

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * A {@link Locale localized} communication channel with some variables.
 * Enable the use of a specific language and region for a destination.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@SuperBuilder(toBuilder = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class LocalizableAttributesCommunicationChannelBean extends CommunicationChannelBean {
    private static final long serialVersionUID = 4450726832946360111L;
    
    /**
     * The locale for this communication channel.
     */
    @NotNull
    @Setter(onParam_ = { @NotNull })
    @Default
    private Locale locale = Locale.getDefault();
    
    @NotNull
    @Setter(onParam_ = { @NotNull })
    @Default
    private Map<String, Object> attributes = new TreeMap<>();
    
    public static LocalizableAttributesCommunicationChannelBeanBuilder<?,?> builderCopy(CommunicationChannelBean parent) {
        return (LocalizableAttributesCommunicationChannelBeanBuilder<?, ?>) LocalizableAttributesCommunicationChannelBean.builder()
            .channelType(parent.getChannelType())
            .channelId(parent.getChannelId())
            ;
    }
}
