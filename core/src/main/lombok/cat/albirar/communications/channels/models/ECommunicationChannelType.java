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

import javax.validation.constraints.NotNull;

/**
 * Types of channel communication.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
public enum ECommunicationChannelType {
    /** Email channel type. */
    EMAIL,
    /** SMS channel type. */
    SMS
    ;
    /**
     * Test if the indicated {@code channelType} allow HTML on body or not.
     * @param channelType The channel type
     * @return true if channel allow html on body and false if not
     */
    public static final boolean isHtmlBodyChannel(@NotNull ECommunicationChannelType channelType) {
        return channelType == ECommunicationChannelType.EMAIL;
    }
    /**
     * Test if this channel allow HTML on body or not.
     * @return true if this channel allow html on body and false if not
     */
    public boolean isHtmlBodyChannel() {
        return this == ECommunicationChannelType.EMAIL;
    }
}
