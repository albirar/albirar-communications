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
package cat.albirar.communications.providers.sms.clickandsend.models;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import cat.albirar.communications.core.configuration.IPropertiesComm;
import lombok.Data;
import lombok.Setter;

/**
 * Properties for ClickAndSend configuration.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = ClickAndSendPropertiesBean.CS_ROOT_PROPERTIES)
public class ClickAndSendPropertiesBean implements Serializable {
    private static final long serialVersionUID = -4348696169294481077L;
    
    /** Root for all Click&Send configuration properties. */
    public static final String CS_ROOT_PROPERTIES = IPropertiesComm.ROOT_COMMUNICATIONS_PROPERTIES + ".sms.cs.api";
    /** Property name for API username for Click&Send authorization. */
    public static final String CS_PROP_USERNAME = CS_ROOT_PROPERTIES + ".username";
    /** Property name for API KEY for Click&Send authorization. */
    public static final String CS_PROP_KEY = CS_ROOT_PROPERTIES + ".key";
    /**
     * The API username for authorization.
     * @param username The API username, should to be not blank
     * @return The API username
     */
    @NotBlank
    @Setter(onParam_ = { @NotBlank })
    private String username = "TEST_USERNAME";
    /**
     * The API key for authorization.
     * @param username The API key, should to be not blank
     * @return The API key
     */
    @NotBlank
    @Setter(onParam_ = { @NotBlank })
    private String key = "TEST_KEY";
}
