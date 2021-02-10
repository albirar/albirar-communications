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
package cat.albirar.communications.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import cat.albirar.communications.core.channels.models.ContactBean;
import cat.albirar.communications.core.channels.models.ECommunicationChannelType;
import cat.albirar.communications.core.channels.models.LocalizableAttributesCommunicationChannelBean;
import cat.albirar.communications.core.configuration.AlbirarCommunicationsConfiguration;
import cat.albirar.communications.core.services.IReportRegister;
import cat.albirar.communications.core.services.impl.ReportRegister;
import cat.albirar.communications.core.status.EStatusMessage;
import cat.albirar.communications.core.status.models.MessageStatusBean;
import cat.albirar.template.engine.EContentType;

/**
 * Test for {@link ReportRegister}.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@ContextConfiguration(classes = {AlbirarCommunicationsConfiguration.class})
@ExtendWith({SpringExtension.class})
@Execution(ExecutionMode.CONCURRENT)
public class ReportRegisterTest {
    
    @Autowired
    private IReportRegister reportRegister;
    
    private static final String ID_COLLECTION = "collection";
    
    private SecureRandom secureRandom =  new SecureRandom();
    /** A model without messageId and StatusMessage. */
    private MessageStatusBean model = MessageStatusBean.builder()
            .body("TEST-BODY")
            .bodyCharSet(Charset.defaultCharset())
            .bodyType(EContentType.TEXT_PLAIN)
            .receiver(ContactBean.builder()
                    .channelBean(LocalizableAttributesCommunicationChannelBean.builder()
                            .channelId("receiver@server.com")
                            .channelType(ECommunicationChannelType.EMAIL)
                            .locale(Locale.getDefault())
                            .build()
                    )
                    .displayName("TEST-RECEIVER")
                    .preferredLocale(Locale.getDefault())
                    .build()
                    )
            .sender(ContactBean.builder()
                    .channelBean(LocalizableAttributesCommunicationChannelBean.builder()
                            .channelId("sender@server.com")
                            .channelType(ECommunicationChannelType.EMAIL)
                            .locale(Locale.getDefault())
                            .build()
                    )
                    .displayName("TEST-SENDER")
                    .preferredLocale(Locale.getDefault())
                    .build()
                    )
            .title("TEST-TITLE")
            .build()
            ;
    /** Generate a random string id. */
    private String generateRandomId() {
        synchronized(secureRandom) {
            return secureRandom.ints(10, 48, 122)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        }
    }
    
    @Test
    public void testValidations() {
        String msgId;
        MessageStatusBean ms;
        
        assertThrows(ConstraintViolationException.class, () -> reportRegister.getAndDeleteReport(null));
        assertThrows(ConstraintViolationException.class, () -> reportRegister.getAndDeleteReport(""));
        assertThrows(ConstraintViolationException.class, () -> reportRegister.getAndDeleteReport("  "));
        
        assertThrows(ConstraintViolationException.class, () -> reportRegister.getReport(null));
        assertThrows(ConstraintViolationException.class, () -> reportRegister.getReport(""));
        assertThrows(ConstraintViolationException.class, () -> reportRegister.getReport("  "));
        
        assertThrows(ConstraintViolationException.class, () -> reportRegister.isReport(null));
        assertThrows(ConstraintViolationException.class, () -> reportRegister.isReport(""));
        assertThrows(ConstraintViolationException.class, () -> reportRegister.isReport("  "));
        
        assertThrows(ConstraintViolationException.class, () -> reportRegister.putReport(null, null, null));
        assertThrows(ConstraintViolationException.class, () -> reportRegister.putReport("", null, null));
        assertThrows(ConstraintViolationException.class, () -> reportRegister.putReport("  ", null, null));
        
        assertThrows(ConstraintViolationException.class, () -> reportRegister.putReport(ID_COLLECTION, null, null));
        assertThrows(ConstraintViolationException.class, () -> reportRegister.putReport(ID_COLLECTION, "", null));
        assertThrows(ConstraintViolationException.class, () -> reportRegister.putReport(ID_COLLECTION, "  ", null));
        
        msgId = generateRandomId();

        assertThrows(ConstraintViolationException.class, () -> reportRegister.putReport(ID_COLLECTION, msgId, null));
        
        ms = MessageStatusBean.builder()
                .build()
                ;
        
        assertThrows(ConstraintViolationException.class, () -> reportRegister.putReport(ID_COLLECTION, msgId, ms));
        
        assertThrows(ConstraintViolationException.class, () -> reportRegister.removeReport(null, null));
        assertThrows(ConstraintViolationException.class, () -> reportRegister.removeReport("", null));
        assertThrows(ConstraintViolationException.class, () -> reportRegister.removeReport("  ", null));
        
        assertThrows(ConstraintViolationException.class, () -> reportRegister.removeReport(ID_COLLECTION, null));
        assertThrows(ConstraintViolationException.class, () -> reportRegister.removeReport(ID_COLLECTION, ""));
        assertThrows(ConstraintViolationException.class, () -> reportRegister.removeReport(ID_COLLECTION, "  "));
    }

    @RepeatedTest(value = 100)
    public void testGetAndDeleteReportSend() {
        MessageStatusBean m, m1;
        Optional<MessageStatusBean> om;
        
        om = reportRegister.getAndDeleteReport("XXX");
        assertFalse(om.isPresent());
        
        m = model.toBuilder()
                .messageId(generateRandomId())
                .status(EStatusMessage.SEND)
                .build()
                ;
        reportRegister.putReport(ID_COLLECTION, m.getMessageId(), m);
        om = reportRegister.getAndDeleteReport(m.getMessageId());
        assertTrue(om.isPresent());
        m1 = om.get();
        assertSame(m, m1);
        assertFalse(reportRegister.isReport(m.getMessageId()));
    }
    
    @RepeatedTest(value = 100)
    public void testPutAndRemoveReport() {
        MessageStatusBean m, m1;
        Optional<MessageStatusBean> om;
        
        om = reportRegister.removeReport(ID_COLLECTION, "XXX");
        assertFalse(om.isPresent());
        
        m = model.toBuilder()
                .messageId(generateRandomId())
                .status(EStatusMessage.SEND)
                .build()
                ;
        reportRegister.putReport(ID_COLLECTION, m.getMessageId(), m);
        om = reportRegister.removeReport(ID_COLLECTION, "XXX");
        assertFalse(om.isPresent());
        
        om = reportRegister.removeReport(ID_COLLECTION, m.getMessageId());
        assertTrue(om.isPresent());
        m1 = om.get();
        assertEquals(m, m1);
        assertFalse(reportRegister.isReport(m.getMessageId()));
    }
    
    @RepeatedTest(value = 100)
    public void testIsReport() {
        MessageStatusBean m;
        
        assertFalse(reportRegister.isReport(generateRandomId()));
        m = model.toBuilder()
                .messageId(generateRandomId())
                .status(EStatusMessage.SEND)
                .build()
                ;
        reportRegister.putReport(ID_COLLECTION, m.getMessageId(), m);
        assertTrue(reportRegister.isReport(m.getMessageId()));
        assertFalse(reportRegister.isReport(generateRandomId()));
    }
    
    @RepeatedTest(value = 100)
    public void testGetReport() {
        MessageStatusBean m;
        Optional<MessageStatusBean> om;
        
        om = reportRegister.getReport("XXX");
        assertFalse(om.isPresent());
        
        m = model.toBuilder()
                .messageId(generateRandomId())
                .status(EStatusMessage.SEND)
                .build()
                ;
        reportRegister.putReport(ID_COLLECTION, m.getMessageId(), m);
        om = reportRegister.getReport(m.getMessageId());
        assertTrue(om.isPresent());
        assertSame(m, om.get());
    }
}
