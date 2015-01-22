package org.esupportail.publisher.service;

import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.web.rest.dto.TreeJS;
import org.esupportail.publisher.web.rest.dto.UserDTO;

import java.util.List;

/**
 * Created by jgribonvald on 11/06/15.
 */
public class GroupServiceEmpty implements IGroupService {
    @Override
    public List<TreeJS> getRootNodes(ContextKey contextKey) {
        return null;
    }

    @Override
    public List<TreeJS> getGroupMembers(String id) {
        return null;
    }

    @Override
    public List<UserDTO> getUserMembers(String id) {
        return null;
    }
}
