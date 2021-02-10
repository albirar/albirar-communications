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
package cat.albirar.communications.providers.email.javamail.test.services;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import cat.albirar.communications.core.configuration.AlbirarCommunicationsConfiguration;
import cat.albirar.communications.core.providers.IServiceProvider;
import cat.albirar.communications.providers.AbstractServiceProviderBasicTest;
import cat.albirar.communications.providers.email.javamail.configuration.JavaMailSenderConfiguration;
import cat.albirar.communications.providers.email.javamail.impl.JavaMailSenderEmailProvider;
import cat.albirar.communications.test.RabbitMqContainerExtension;

/**
 * Basic test for {@link JavaMailSenderEmailProvider}.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@ContextConfiguration(classes = {JavaMailSenderConfiguration.class, AlbirarCommunicationsConfiguration.class, MockTestConfiguration.class})
@ExtendWith({SpringExtension.class, RabbitMqContainerExtension.class})
public class EmailServiceProviderBasicTest extends AbstractServiceProviderBasicTest {

    @Autowired
    private JavaMailSenderEmailProvider serviceProvider;
    /**
     * {@inheritDoc}
     */
    @Override
    protected IServiceProvider getProvider() {
        return serviceProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getProviderName() {
        return JavaMailSenderEmailProvider.NAME;
    }

}
