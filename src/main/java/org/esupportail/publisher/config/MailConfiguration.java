/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.publisher.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.CollectionUtils;

@Configuration
@AutoConfigureAfter({ESUPPublisherProperties.class, MailProperties.class})
public class MailConfiguration{

    private static final String DEFAULT_HOST = "127.0.0.1";

    private final Logger log = LoggerFactory.getLogger(MailConfiguration.class);

    @Bean
    public JavaMailSenderImpl javaMailSender(@NonNull MailProperties mailProperties) {
        log.debug("Configuring mail server");
        final String host = mailProperties.getHost();

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        if (host != null && !host.isEmpty()) {
            sender.setHost(host);
        } else {
            log.warn("Warning! Your SMTP server is not configured. We will try to use one on localhost.");
            log.debug("Did you configure your SMTP settings in your application.yml?");
            sender.setHost(DEFAULT_HOST);
        }
        sender.setPort(mailProperties.getPort());
        sender.setUsername(mailProperties.getUsername());
        sender.setPassword(mailProperties.getPassword());
        sender.setProtocol(mailProperties.getProtocol());
        sender.setDefaultEncoding(mailProperties.getDefaultEncoding().name());

        Properties sendProperties = new Properties();
        if (!CollectionUtils.isEmpty(mailProperties.getProperties())) {
            sendProperties.putAll(mailProperties.getProperties());
        }
        sender.setJavaMailProperties(sendProperties);
        return sender;
    }
}