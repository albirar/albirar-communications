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
package cat.albirar.communications.providers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import cat.albirar.communications.core.providers.IServiceProvider;

/**
 * An abstract test for {@link IServiceProvider} implementations.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@ExtendWith({SpringExtension.class})
public abstract class AbstractServiceProviderBasicTest {
    /**
     * Test for the only one method on {@link IServiceProvider}.
     */
    @Test
    public void testName() {
        IServiceProvider provider;
        
        provider = getProvider();
        Assertions.assertNotNull(provider);
        Assertions.assertNotNull(provider.getName());
        Assertions.assertFalse(provider.getName().trim().isEmpty());
        Assertions.assertEquals(getProviderName(), provider.getName());
    }
    /**
     * Should to be implemented and return the service provider to test.
     * @return The service provider to test
     */
    protected abstract IServiceProvider getProvider();
    /**
     * Should to be implemented and return the name of the service provider to test.
     * @return The name of the service provider
     */
    protected abstract String getProviderName();
}
