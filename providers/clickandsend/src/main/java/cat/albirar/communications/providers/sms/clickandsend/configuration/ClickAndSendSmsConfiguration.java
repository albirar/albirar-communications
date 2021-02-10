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
 * Copyright (C) 2020 Octavi Fornés
 */
package cat.albirar.communications.providers.sms.clickandsend.configuration;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import ClickSend.ApiClient;
import ClickSend.Api.SmsApi;
import cat.albirar.communications.core.configuration.AlbirarCommunicationsConfiguration;
import cat.albirar.communications.providers.sms.clickandsend.impl.ClickAndSendSmsSenderProvider;
import cat.albirar.communications.providers.sms.clickandsend.models.ClickAndSendPropertiesBean;

/**
 * The configuration bean for Click&Send sms provider.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Configuration
@ImportAutoConfiguration(classes = AlbirarCommunicationsConfiguration.class)
@ComponentScan(basePackageClasses = {ClickAndSendSmsSenderProvider.class, ClickAndSendPropertiesBean.class})
public class ClickAndSendSmsConfiguration {

    /**
     * The web client to access the remote REST API of Click&Send.
     * @param props The properties
     * @return The configured webclient
     */
    @Bean
    @ConditionalOnMissingBean
    public ApiClient apiClient(ClickAndSendPropertiesBean props) {

        ApiClient api;
        api = new ApiClient();
        api.setUsername(props.getUsername());
        api.setPassword(props.getKey());
        
        return api;
    }
    /**
     * Specific client to access SMS operation on rest API.
     * @param apiClient The generic api client
     * @return The specialized api client for SMS calls.
     */
    @Bean
    @ConditionalOnMissingBean
    public SmsApi apiSms(ApiClient apiClient) {
        return new SmsApi(apiClient);
    }
}
