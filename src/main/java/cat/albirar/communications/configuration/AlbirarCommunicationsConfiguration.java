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
package cat.albirar.communications.configuration;

import static cat.albirar.communications.configuration.PropertiesComm.VALUE_HOST_CONNECTION_PROPERTY;
import static cat.albirar.communications.configuration.PropertiesComm.VALUE_PASSWORD_CONNECTION_PROPERTY;
import static cat.albirar.communications.configuration.PropertiesComm.VALUE_PORT_CONNECTION_PROPERTY;
import static cat.albirar.communications.configuration.PropertiesComm.VALUE_USERNAME_CONNECTION_PROPERTY;
import static cat.albirar.communications.configuration.PropertiesComm.VALUE_VIRTUALHOST_CONNECTION_PROPERTY;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.NamingStrategy;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.UUIDNamingStrategy;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory.ConfirmType;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import cat.albirar.communications.processors.EmailSenderProcessor;
import cat.albirar.communications.processors.SmsSenderProcessor;
import cat.albirar.communications.providers.email.impl.PropertiesConfigurableEmailProvider;
import cat.albirar.communications.providers.sms.clickandsend.ClickAndSendSmsSenderProvider;
import cat.albirar.communications.services.impl.CommunicationServiceImpl;
import cat.albirar.communications.sms.providers.clickandsend.ClickAndSendProperties;

/**
 * Configuration class for this module.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Configuration
@ComponentScan(basePackageClasses = {EmailSenderProcessor.class, CommunicationServiceImpl.class, ClickAndSendSmsSenderProvider.class, ClickAndSendProperties.class, PropertiesConfigurableEmailProvider.class})
@PropertySource("classpath:/cat/albirar/communications/albirar-communications.properties")
public class AlbirarCommunicationsConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlbirarCommunicationsConfiguration.class);
    
    @Value(VALUE_HOST_CONNECTION_PROPERTY)
    private String host;
    @Value(VALUE_VIRTUALHOST_CONNECTION_PROPERTY)
    private String virtualHost;
    @Value(VALUE_PORT_CONNECTION_PROPERTY)
    private int port;
    @Value(VALUE_USERNAME_CONNECTION_PROPERTY)
    private String username;
    @Value(VALUE_PASSWORD_CONNECTION_PROPERTY)
    private String password;
    
    private String exchangeName = PropertiesComm.EXCHANGE_NAME;
    
    private String emailSendQueueName = PropertiesComm.QUEUE_SEND_EMAIL;
    private String emailReportQueueName = PropertiesComm.QUEUE_REPORT_EMAIL;
    private String smsSendQueueName = PropertiesComm.QUEUE_SEND_SMS;
    private String smsReportQueueName = PropertiesComm.QUEUE_REPORT_SMS;
    
    // Basic configuration...

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }
    @Bean
    public MethodValidationPostProcessor validationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
    
    // AMQP configuration
    @Bean
    public NamingStrategy namingStrategy() {
        return new UUIDNamingStrategy();
    }
    
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory cnxFc;

        LOGGER.debug("Creating connection factory with values: {}",
                new StringBuilder("host: '").append(host)
                .append("', port: ").append(port)
                .append(", virtualHost: '").append(virtualHost)
                .append("', username: '").append(username)
                .append("', password: '").append(password).append("'")
                .toString()
                );
        cnxFc = new CachingConnectionFactory(host, port);
        cnxFc.setVirtualHost(virtualHost);
        cnxFc.setUsername(username);
        cnxFc.setPassword(password);
        cnxFc.setPublisherConfirmType(ConfirmType.CORRELATED);
        return cnxFc;
    }
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin radm;
        
        radm = new RabbitAdmin(connectionFactory);
        return radm;
    }
    
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template;
        
        template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        // TODO Other configuration for template?
        return template;
    }

    @Bean
    public Queue emailSendQueue() {
        return new Queue(emailSendQueueName);
    }
    
    @Bean
    public Queue smsSendQueue() {
        return new Queue(smsSendQueueName);
    }

    @Bean
    public Queue emailReportQueue() {
        return new Queue(emailReportQueueName);
    }
    
    @Bean
    public Queue smsReportQueue() {
        return new Queue(smsReportQueueName);
    }
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding bindingEmailSend(@Qualifier("emailSendQueue") Queue emailSendQueue, TopicExchange exchange) {
        return BindingBuilder.bind(emailSendQueue).to(exchange).with(emailSendQueueName);
    }

    @Bean
    public Binding bindingEmailReport(@Qualifier("emailReportQueue") Queue emailReportQueue, TopicExchange exchange) {
        return BindingBuilder.bind(emailReportQueue).to(exchange).with(emailReportQueueName);
    }

    @Bean
    public Binding bindingSmsSend(@Qualifier("smsSendQueue") Queue smsSendQueue, TopicExchange exchange) {
        return BindingBuilder.bind(smsSendQueue).to(exchange).with(smsSendQueueName);
    }

    @Bean
    public Binding bindingReportSend(@Qualifier("smsReportQueue") Queue smsReportQueue, TopicExchange exchange) {
        return BindingBuilder.bind(smsReportQueue).to(exchange).with(smsReportQueueName);
    }
    
    @Bean
    public SimpleMessageListenerContainer listenerSendEmail(ConnectionFactory connectionFactory
            , EmailSenderProcessor emailSenderProcessor
            , @Qualifier("emailSendQueue") Queue emailSendQueue) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(emailSendQueue);
        container.setMessageListener(emailSenderProcessor);
        return container;
    }
    
    @Bean
    public SimpleMessageListenerContainer listenerSendSms(ConnectionFactory connectionFactory
            , SmsSenderProcessor smsSenderProcessor
            , @Qualifier("smsSendQueue") Queue smsSendQueue) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(smsSendQueue);
        container.setMessageListener(smsSenderProcessor);
        return container;
    }
}
