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
package cat.albirar.communications.providers.email.javamail.configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import cat.albirar.communications.configuration.AlbirarCommunicationsConfiguration;
import cat.albirar.communications.configuration.IPropertiesComm;
import cat.albirar.communications.providers.email.javamail.impl.JavaMailSenderEmailProvider;

/**
 * An email provider that uses a {@link JavaMailSender}.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 2.0.0
 */
@Configuration
@ImportAutoConfiguration(classes = AlbirarCommunicationsConfiguration.class)
@ComponentScan(basePackageClasses = JavaMailSenderEmailProvider.class)
public class JavaMailSenderConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaMailSenderConfiguration.class);

    /**
     * Root for all property names for configuring email sender.
     */
    public static final String JAVAMAIL_ROOT_PROPERTY_NAME = IPropertiesComm.ROOT_COMMUNICATIONS_PROPERTIES + ".mail.javamail";
    /**
     * The property name for email host server.
     */
    public static final String JAVAMAIL_HOST_PROPERTY_NAME = JAVAMAIL_ROOT_PROPERTY_NAME + ".host";
    
    /**
     * The property name for email port server.
     */
    public static final String JAVAMAIL_PORT_PROPERTY_NAME = JAVAMAIL_ROOT_PROPERTY_NAME + ".port";
    /**
     * The property name for username on mail server.
     */
    public static final String JAVAMAIL_USERNAME_PROPERTY_NAME = JAVAMAIL_ROOT_PROPERTY_NAME + ".username";
    /**
     * The property name for password on mail server.
     */
    public static final String JAVAMAIL_PASSWORD_PROPERTY_NAME = JAVAMAIL_ROOT_PROPERTY_NAME + ".password";
    
    /**
     * The root for all {@code mail.*} properties, like {@code mail.smtp.starttls.enable}.
     */
    public static final String JAVAMAIL_PROPERTIES_ROOT_PROPERTY_NAME = JAVAMAIL_ROOT_PROPERTY_NAME + ".properties";
    /**
     * The root <strong>suffixed</strong> for all {@code mail.*} properties, like {@code mail.smtp.starttls.enable}.
     */
    public static final String JAVAMAIL_PROPERTIES_SUFIX_ROOT_PROPERTY_NAME = JAVAMAIL_PROPERTIES_ROOT_PROPERTY_NAME + ".";

    /**
     * The {@link Value} expression for {@link #JAVAMAIL_HOST_PROPERTY_NAME}.
     */
    public static final String VALUE_JAVAMAIL_HOST = "#{systemProperties['" + JAVAMAIL_HOST_PROPERTY_NAME + "']}";
    /**
     * The {@link Value} expression for {@link #JAVAMAIL_PORT_PROPERTY_NAME}.
     */
    public static final String VALUE_JAVAMAIL_PORT = "#{systemProperties['" + JAVAMAIL_PORT_PROPERTY_NAME + "']}";
    /**
     * The {@link Value} expression for {@link #JAVAMAIL_USERNAME_PROPERTY_NAME}.
     */
    public static final String VALUE_JAVAMAIL_USERNAME = "#{systemProperties['" + JAVAMAIL_USERNAME_PROPERTY_NAME + "']}";
    /**
     * The {@link Value} expression for {@link #JAVAMAIL_PASSWORD_PROPERTY_NAME}.
     */
    public static final String VALUE_JAVAMAIL_PASSWORD = "#{systemProperties['" + JAVAMAIL_PASSWORD_PROPERTY_NAME + "']}";

    @Bean
    @ConditionalOnMissingBean
    public JavaMailSender javaMailSender(ConfigurableEnvironment env) {
        JavaMailSenderImpl jm;
        List<EnumerablePropertySource<?>> propSrc;
        Properties props;
        String host;
        int port;
        String username;
        String password;
        
        props = new Properties();
        propSrc = env.getPropertySources().stream()
                .filter(ps -> EnumerablePropertySource.class.isAssignableFrom(ps.getClass()))
                .map(ps -> (EnumerablePropertySource<?>) ps)
                .collect(Collectors.toList())
                ;
        for(EnumerablePropertySource<?> eps : propSrc) {
            Arrays.stream(eps.getPropertyNames())
            .filter(name -> name.startsWith(JAVAMAIL_PROPERTIES_SUFIX_ROOT_PROPERTY_NAME))
            .forEach(name -> props.put(name.substring(JAVAMAIL_PROPERTIES_SUFIX_ROOT_PROPERTY_NAME.length()), env.getProperty(name)))
            ;
        }

        host = env.getRequiredProperty(JAVAMAIL_HOST_PROPERTY_NAME, String.class);
        port = env.getRequiredProperty(JAVAMAIL_PORT_PROPERTY_NAME, Integer.class);
        username = env.getRequiredProperty(JAVAMAIL_USERNAME_PROPERTY_NAME, String.class);
        password = env.getRequiredProperty(JAVAMAIL_PASSWORD_PROPERTY_NAME, String.class);
        
        LOGGER.debug("Creating javamail sender with values: {}",
                new StringBuilder("host: '").append(host)
                .append("', port: ").append(port)
                .append("', username: '").append(username)
                .append("', password: '").append(password).append("'")
                .append(", props: ").append(props)
                .toString()
                );
        jm = new JavaMailSenderImpl();
        jm.setJavaMailProperties(props);
        jm.setHost(host);
        jm.setPort(port);
        jm.setUsername(username);
        jm.setPassword(password);
        return jm;
    }
}
