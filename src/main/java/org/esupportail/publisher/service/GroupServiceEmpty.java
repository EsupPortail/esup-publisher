package org.esupportail.publisher.service;

import com.google.common.collect.Lists;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.web.rest.dto.TreeJS;
import org.esupportail.publisher.web.rest.dto.UserDTO;

import java.util.List;

/**
 * Created by jgribonvald on 11/06/15.
 */
public class GroupServiceEmpty implements IGroupService {
    @Override
    public List<TreeJS> getRootNodes(ContextKey contextKey, List<ContextKey> subContextKeys) {
        return Lists.newArrayList();
    }

    @Override
    public List<TreeJS> getGroupMembers(String id) {
        return Lists.newArrayList();
    }

    @Override
    public List<UserDTO> getUserMembers(String id) {
        return Lists.newArrayList();
    }
}
