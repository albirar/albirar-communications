/*
 * This file is part of "albirar-communications-provider-javamail".
 * 
 * "albirar-communications-provider-javamail" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * "albirar-communications-provider-javamail" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with "albirar-communications-provider-javamail" source code.  If not, see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *
 * Copyright (C) 2020 Octavi Fornés
 */
package cat.albirar.communications.providers.email.javamail;

import java.io.Serializable;

import org.springframework.boot.context.properties.ConfigurationProperties;

import cat.albirar.communications.providers.email.javamail.configuration.JavaMailSenderConfiguration;
import cat.albirar.communications.providers.email.javamail.impl.JavaMailSenderEmailProvider;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Properties for {@link JavaMailSenderEmailProvider}.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@ConfigurationProperties(prefix = JavaMailSenderConfiguration.JAVAMAIL_ROOT_PROPERTY_NAME)
public class JavaMailProperties implements Serializable {
    private static final long serialVersionUID = -2459571155938982292L;
    
    private String host;
    private int port;
    private String username;
    private String password;
}
