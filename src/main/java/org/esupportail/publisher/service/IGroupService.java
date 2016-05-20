package org.esupportail.publisher.service;

import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.web.rest.dto.TreeJS;
import org.esupportail.publisher.web.rest.dto.UserDTO;

import java.util.List;

/**
 * Created by jgribonvald on 10/06/15.
 */
public interface IGroupService {
    List<TreeJS> getRootNodes(ContextKey contextKey, List<ContextKey> subContextKeys);

    List<TreeJS> getGroupMembers(String id);

    List<UserDTO> getUserMembers(String id);
}
