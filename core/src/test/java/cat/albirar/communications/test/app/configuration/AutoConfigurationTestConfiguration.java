/*
 * This file is part of "albirar-communications-core".
 * 
 * "albirar-communications-core" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * "albirar-communications-core" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with "albirar-communications-core" source code.  If not, see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *
 * Copyright (C) 2021 Octavi Fornés
 */
package cat.albirar.communications.test.app.configuration;

import org.springframework.amqp.core.AbstractExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import cat.albirar.communications.core.configuration.IPropertiesComm;

/**
 * The app configuration to test autoconfiguration.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 2.0.2
 */
public class AutoConfigurationTestConfiguration {
    static TopicExchange exchange;
    
    @Bean
    @Qualifier("exchange")
    @Primary
    public AbstractExchange exchangeTest() {
        exchange = new TopicExchange(IPropertiesComm.EXCHANGE_NAME);
        return exchange;
    }
}
