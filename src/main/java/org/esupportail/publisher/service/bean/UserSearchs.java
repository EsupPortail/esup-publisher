/**
 *
 */
package org.esupportail.publisher.service.bean;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.SubjectKey;
import org.esupportail.publisher.domain.externals.IExternalGroup;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author GIP RECIA - Julien Gribonvald
 *
 *         Should be used as session bean
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
public class UserSearchs {

    private List<IExternalUser> lastSearchOnUsers;
    private List<UserDTO> lastConvertedSearchOnUsers;
    private List<SubjectKey> lastUserKeySearchOnUsers;

    private List<IExternalGroup> lastSearchOnGroups;
    //private List<GroupDTO> lastConvertedSearchOnGroups;
    private List<SubjectKey> lastGroupKeySearchOnUsers;

}
