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

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import cat.albirar.communications.core.configuration.AlbirarCommunicationsConfiguration;

/**
 * A convenient abstract class to derive to any test.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@ContextConfiguration(classes = {AlbirarCommunicationsConfiguration.class})
@ExtendWith({SpringExtension.class, RabbitMqContainerExtension.class})
public abstract class AbstractCommunicationsTest {
    protected static long WAIT_FOR_WORK = 1000;
    protected static int LOOP_FOR_TEST = 5;

    /**
     * Normalize text for change CR+NL by NL and removes last empty line.
     * In some cases, the original text from email messages change new-line by carriage-return+new-line, and add a extra empty line at end.
     * @param origin The original email text
     * @return The text, normalized and without the last empty line.
     */
    protected String normalizeCarriageReturnAndEmptyLastLine(String origin) {
        String s = origin.replaceAll("\\r", "");
        return s.substring(0, s.lastIndexOf('\n'));
    }
    /**
     * Normalize text for change CR+NL by NL.
     * In some cases, the original text from email messages change new-line by carriage-return+new-line.
     * @param origin The original email text
     * @return The text, normalized
     */
    protected String normalizeCarriageReturn(String origin) {
        return origin.replaceAll("\\r", "");
    }
}
