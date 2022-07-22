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
package org.esupportail.publisher.security;

import java.util.Optional;

import javax.inject.Inject;

import org.esupportail.publisher.domain.QUser;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.repository.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of AuditorAware based on Spring Security.
 */
@Component//(value = "SpringSecurityAuditorAware")
@Slf4j
public class SpringSecurityAuditorAware implements AuditorAware<User>, ApplicationListener<ContextRefreshedEvent> {

    private User systemUser;

    @Inject
    private UserRepository userRepository;

    @Override
    public Optional<User> getCurrentAuditor() {
        User auditor = SecurityUtils.getCurrentUser();
        if (auditor == null || auditor.getCreatedDate() == null) {
            auditor = systemUser;
        }
        log.debug("Current auditor is >>> {}", auditor);
        return  Optional.of(auditor);
    }


    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (this.systemUser == null) {
            log.debug(" >>> loading system user");
            Optional<User> optionalUser = this.userRepository.findOne(QUser.user.login.like("SYSTEM"));
            systemUser = optionalUser.orElse(null);
        }
    }
}