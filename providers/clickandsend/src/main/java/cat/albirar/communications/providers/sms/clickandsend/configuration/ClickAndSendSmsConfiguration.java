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

import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import cat.albirar.communications.providers.sms.clickandsend.ClickAndSendSmsSenderProvider;
import cat.albirar.communications.providers.sms.clickandsend.models.ClickAndSendPropertiesBean;

/**
 * The configuration bean for Click&Send sms provider.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Configuration
@ComponentScan(basePackageClasses = ClickAndSendSmsSenderProvider.class)
public class ClickAndSendSmsConfiguration {

    /**
     * The web client to access the remote REST API of Click&Send.
     * @param props The properties
     * @return The configured webclient
     */
    @Bean
    public WebClient webClient(ClickAndSendPropertiesBean props) {

        String stb;
        
        stb = new StringBuilder("Basic ").append(props.getUsername())
                .append(":").append(props.getKey())
                .toString()
                ;
        stb = Base64.getEncoder().encodeToString(stb.getBytes());
        return WebClient.builder().baseUrl(ClickAndSendPropertiesBean.URL_BASE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, stb)
                .build();
    }
}
