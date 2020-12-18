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
package cat.albirar.communications.processors.impl;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

import cat.albirar.communications.processors.IProcessor;
import cat.albirar.communications.services.IReportRegister;
import cat.albirar.communications.status.models.MessageStatusBean;

/**
 * Report processor.
 * Receive report messages from any provider and put then onto the "report" map.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 2.0.0
 */
public abstract class AbstractReportProcessor implements IProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractReportProcessor.class);
    
    @Autowired
    protected MessageConverter converter;

    @Autowired
    protected IReportRegister reportRegister;
    
    private AtomicLong processedMessages = new AtomicLong(0);
    
    private AtomicLong managedMessages = new AtomicLong(0);

    /**
     * {@inheritDoc}
     */
    @Override
    public long getManagedMessages() {
        return managedMessages.get();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public long getProcessedMessages() {
        return processedMessages.get();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessage(Message message) {
        MessageStatusBean r;
        Object o;
        
        LOGGER.debug("Processing report {}", message);
        managedMessages.incrementAndGet();
        try {
            o = converter.fromMessage(message);
            try {
                r = (MessageStatusBean) o;
                processReport(r);
                processedMessages.incrementAndGet();
            } catch(ClassCastException e) {
                LOGGER.error("The single amqp status message of class {} is not assignable to class {}", o.getClass(), MessageStatusBean.class);
            }
        } catch (MessageConversionException e) {
            LOGGER.error(String.format("Unknow error %s converting amqp status message %s", e.getMessage(), message), e);
        }
    }
    
    protected abstract void processReport(MessageStatusBean report);
}
