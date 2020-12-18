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
package cat.albirar.communications.services.impl;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

import cat.albirar.communications.services.IReportRegister;
import cat.albirar.communications.status.models.MessageStatusBean;

/**
 * {@link IReportRegister} implementation.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 2.0.0
 */
@Component
public class ReportRegister implements IReportRegister {
    // Collection -> messageId
    private ConcurrentMap<String, ConcurrentMap<String, MessageStatusBean>> reports;
    
    /**
     * Simple unique constructor. 
     */
    public ReportRegister() {
        reports = new ConcurrentHashMap<>();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<MessageStatusBean> getReport(String messageId) {
        synchronized(reports) {
            for(String collectionId : reports.keySet()) {
                if(reports.get(collectionId).containsKey(messageId)) {
                    return Optional.of(reports.get(collectionId).get(messageId));
                }
            }
            return Optional.empty();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<MessageStatusBean> getAndDeleteReport(String messageId) {
        synchronized(reports) {
            for(String collectionId : reports.keySet()) {
                if(reports.get(collectionId).containsKey(messageId)) {
                    return removeReport(collectionId, messageId);
                }
            }
            return Optional.empty();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReport(String messageId) {
        synchronized(reports) {
            for(String collectionId : reports.keySet()) {
                if(reports.get(collectionId).containsKey(messageId)) {
                    return true;
                }
            }
            return false;
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void putReport(String collectionId, String messageId, MessageStatusBean report) {
        ConcurrentMap<String, MessageStatusBean> collection;
        
        synchronized(reports) {
            if(!reports.containsKey(collectionId)) {
                reports.put(collectionId, new ConcurrentHashMap<>());
            }
            collection = reports.get(collectionId);
            collection.put(messageId, report);
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<MessageStatusBean> removeReport(String collectionId, String messageId) {
        synchronized(reports) {
            if(reports.containsKey(collectionId)) {
                return Optional.ofNullable(reports.get(collectionId).remove(messageId));
            }
            return Optional.empty();
        }
    }
}
