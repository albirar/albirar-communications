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
package cat.albirar.communications.configuration;

import org.springframework.beans.factory.annotation.Value;

/**
 * The configuration property names, {@link Value} expressions and default values for this library.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
public interface PropertiesComm {
    /**
     * Root for all property names for configuring this module.
     */
    public static final String ROOT_COMMUNICATIONS_PROPERTIES = "albirar.communications";
    /**
     * Root for all property names for connection configuration.
     */
    public static final String CONNECTION_ROOT_PROPERTIES = ROOT_COMMUNICATIONS_PROPERTIES + ".connection";
    /**
     * The property name for AMQP broker host.
     */
    public static final String HOST_CONNECTION_PROPERTY_NAME = CONNECTION_ROOT_PROPERTIES + ".host";
    /**
     * The property name for AMQP broker virtualhost.
     */
    public static final String VIRTUALHOST_CONNECTION_PROPERTY_NAME = CONNECTION_ROOT_PROPERTIES + ".virtualhost";
    /**
     * The property name for AMQP broker port.
     */
    public static final String PORT_CONNECTION_PROPERTY_NAME = CONNECTION_ROOT_PROPERTIES + ".port";
    /**
     * The property name for AMQP broker username.
     */
    public static final String USERNAME_CONNECTION_PROPERTY_NAME = CONNECTION_ROOT_PROPERTIES + ".username";
    /**
     * The property name for AMQP broker password.
     */
    public static final String PASSWORD_CONNECTION_PROPERTY_NAME = CONNECTION_ROOT_PROPERTIES + ".password";
    /**
     * Default rabitmq host value.
     */
    public static final String DEFAULT_HOST = "localhost";
    /**
     * Default rabitmq virtualhost value.
     */
    public static final String DEFAULT_VIRTUALHOST = "/";
    /**
     * Default rabitmq port value.
     */
    public static final String DEFAULT_PORT = "5672";
    /**
     * Default rabitmq username value.
     */
    public static final String DEFAULT_USERNAME = "guest";
    /**
     * Default rabitmq password value.
     */
    public static final String DEFAULT_PASSWORD = "guest";
    /**
     * The {@link Value} expression for {@link #HOST_CONNECTION_PROPERTY_NAME} with default value.
     */
    public static final String VALUE_HOST_CONNECTION_PROPERTY = "#{systemProperties['" + HOST_CONNECTION_PROPERTY_NAME + "'] ?:'" + DEFAULT_HOST + "'}";
    /**
     * The {@link Value} expression for {@link #VIRTUALHOST_CONNECTION_PROPERTY_NAME} with default value.
     */
    public static final String VALUE_VIRTUALHOST_CONNECTION_PROPERTY = "#{systemProperties['" + VIRTUALHOST_CONNECTION_PROPERTY_NAME + "'] ?:'" + DEFAULT_VIRTUALHOST + "'}";
    /**
     * The {@link Value} expression for {@link #PORT_CONNECTION_PROPERTY_NAME} with default value.
     */
    public static final String VALUE_PORT_CONNECTION_PROPERTY = "#{systemProperties['" + PORT_CONNECTION_PROPERTY_NAME + "'] ?: '" + DEFAULT_PORT + "'}";
    /**
     * The {@link Value} expression for {@link #USERNAME_CONNECTION_PROPERTY_NAME} with default value.
     */
    public static final String VALUE_USERNAME_CONNECTION_PROPERTY = "#{systemProperties['" + USERNAME_CONNECTION_PROPERTY_NAME + "'] ?:'" + DEFAULT_USERNAME + "'}";
    /**
     * The {@link Value} expression for {@link #PASSWORD_CONNECTION_PROPERTY_NAME} with default value.
     */
    public static final String VALUE_PASSWORD_CONNECTION_PROPERTY = "#{systemProperties['" + PASSWORD_CONNECTION_PROPERTY_NAME + "'] ?:'" + DEFAULT_PASSWORD + "'}";
    
    /**
     * Root for all property names for configuring email sender.
     */
    public static final String JAVAMAIL_ROOT_PROPERTY_NAME = ROOT_COMMUNICATIONS_PROPERTIES + ".mail";
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

    /**
     * The default exchange name.
     */
    public static final String EXCHANGE_NAME = "albirar-communications";
    /**
     * Default value for email send queue name.
     */
    public static final String QUEUE_SEND_EMAIL = "emailSendQueue";
    /**
     * Default value for email reports queue name.
     */
    public static final String QUEUE_REPORT_EMAIL = "emailReportQueue";
    /**
     * Default value for sms send queue name.
     */
    public static final String QUEUE_SEND_SMS = "smsSendQueue";
    /**
     * Default value for sms report queue name.
     */
    public static final String QUEUE_REPORT_SMS = "smsSendQueue";
    
    
}
