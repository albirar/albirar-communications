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
package cat.albirar.communications.test;

import java.util.concurrent.Semaphore;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.RabbitMQContainer;

import cat.albirar.communications.configuration.PropertiesComm;

/**
 * A {@link Extension} for containerizing a RabbitMQ server for test purposes.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
public class RabbitMqContainerExtension implements BeforeAllCallback, AfterAllCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqContainerExtension.class);
    
    private static RabbitMQContainer container = null;
    private Semaphore semaphore = new Semaphore(1, true);
    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        try {
            semaphore.acquire();
            LOGGER.info("Start RabbitMQ test container albirar extension...");
            if(container == null) {
                container = new RabbitMQContainer();
            } else {
                if(container.isRunning()) {
                    container.stop();
                }
            }
            container.start();
            LOGGER.info("RabbitMQ test container albirar extension started");

            System.setProperty(PropertiesComm.HOST_CONNECTION_PROPERTY_NAME, container.getHost());
            System.setProperty(PropertiesComm.PORT_CONNECTION_PROPERTY_NAME, container.getAmqpPort().toString());
            System.setProperty(PropertiesComm.USERNAME_CONNECTION_PROPERTY_NAME, container.getAdminUsername());
            System.setProperty(PropertiesComm.PASSWORD_CONNECTION_PROPERTY_NAME, container.getAdminPassword());
            
        } finally {
            semaphore.release();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        LOGGER.info("End RabbitMQ test container albirar extension...");
        try {
            semaphore.acquire();
            if(container != null && container.isRunning()) {
                LOGGER.info("Stopping RabbitMQ test container albirar extension...");
                try {
                    container.stop();
                    LOGGER.info("RabbitMQ test container albirar extension stopped");
                } finally {
                    container = null;
                }
            }
        } finally {
            semaphore.release();
        }
    }
}
