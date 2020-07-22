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
package cat.albirar.communications.sms.providers.clickandsend;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * The JSON message for send a SMS. 
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Data
@SuperBuilder(toBuilder = true)
public class ClickAndSendMessage implements Serializable {
    private static final long serialVersionUID = -1377886237555250668L;
    @NotBlank
    @Setter(onParam_ = { @NotBlank })
    private String to;
    @NotBlank
    @Setter(onParam_ = { @NotBlank })
    private String from;
    @NotBlank
    @Setter(onParam_ = { @NotBlank })
    private String body;
}
