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
 * Copyright (C) 2021 Octavi Fornés
 */
package cat.albirar.communications.providers.email.javamail.test.app.configuration;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import cat.albirar.communications.providers.email.javamail.configuration.AutoconfigureJavamailEmailProvider;

/**
 * Configuration for autoconfiguration test.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 2.0.2
 */
@SpringBootConfiguration
@AutoconfigureJavamailEmailProvider
public class AutoConfigurationTestConfiguration {
    static JavaMailSender sender;
    
    @Bean
    @Primary
    public JavaMailSender javaMailSenderTest() {
        sender = new JavaMailSenderImpl();
        return sender;
    }

}
