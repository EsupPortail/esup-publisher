package org.esupportail.publisher.security;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.QUser;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.repository.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

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
    public User getCurrentAuditor() {
        User auditor = SecurityUtils.getCurrentUser();
        if (auditor == null || auditor.getCreatedDate() == null) {
            auditor = systemUser;
        }
        log.debug("Current auditor is >>> {}", auditor);
        return auditor;
    }


    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (this.systemUser == null) {
            log.debug(" >>> loading system user");
            systemUser = this.userRepository.findOne(QUser.user.login.like("SYSTEM"));
        }
    }
}
