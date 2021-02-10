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
 * Copyright (C) 2020 Octavi Fornés
 */
package cat.albirar.communications.providers.mocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotBlank;

import cat.albirar.communications.providers.email.RootPushBean;

/**
 * A convenient abstract class for implement mock service providers. 
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
public abstract class AbstractMockServiceProvider<T extends RootPushBean> implements IMockServiceProvider<T> {
    protected Map<String, T> pushedMessages;
    protected boolean throwException = false;
    protected Map<String, T> throwMessages;
    
    @PostConstruct
    public void init() {
        pushedMessages = new HashMap<>();
        throwMessages = new HashMap<>();
        throwException = false;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isThrowException() {
        return throwException;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void setThrowException(boolean throwException) {
        this.throwException = throwException;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMessageProcessed(String messageId) {
        synchronized(pushedMessages) {
            return pushedMessages.containsKey(messageId);
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isThrowMessageProcessed(@NotBlank String messageId) {
        synchronized(throwMessages) {
            return throwMessages.containsKey(messageId);
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<T> getMessage(String messageId) {
        synchronized(pushedMessages) {
            return Optional.ofNullable(pushedMessages.get(messageId));
        }
    }
}
