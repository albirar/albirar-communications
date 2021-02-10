/*
 * This file is part of "albirar-communications-provider-clickandsend".
 * 
 * "albirar-communications-provider-clickandsend" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * "albirar-communications-provider-clickandsend" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with "albirar-communications-provider-clickandsend" source code.  If not, see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *
 * Copyright (C) 2021 Octavi Fornés
 */
package cat.albirar.communications.providers.sms.clickandsend.test.app.configuration;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import ClickSend.ApiClient;
import cat.albirar.communications.providers.sms.clickandsend.configuration.AutoconfigureClickAndSendSmsProvider;
import cat.albirar.communications.providers.sms.clickandsend.models.ClickAndSendPropertiesBean;

/**
 * Configuration for autoconfiguration test.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 2.0.2
 */
@SpringBootConfiguration
@AutoconfigureClickAndSendSmsProvider
public class AutoConfigurationTestConfiguration {
    static ApiClient apiClient;
    
    @Bean
    public ApiClient apiClient(ClickAndSendPropertiesBean props) {
        apiClient = new ApiClient();
        apiClient.setUsername(props.getUsername());
        apiClient.setPassword(props.getKey());
        
        return apiClient;
    }
}
