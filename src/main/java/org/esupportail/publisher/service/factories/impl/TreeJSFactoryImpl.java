package org.esupportail.publisher.service.factories.impl;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.IContext;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.externals.IExternalGroup;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.service.factories.TreeJSDTOFactory;
import org.esupportail.publisher.web.rest.dto.SubjectDTO;
import org.esupportail.publisher.web.rest.dto.SubjectKeyDTO;
import org.esupportail.publisher.web.rest.dto.TreeJS;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly=true)
@Slf4j
public class TreeJSFactoryImpl implements TreeJSDTOFactory {

    @Inject
    private IPermissionService permissionService;

    @Override
    public TreeJS from(@NotNull IContext model, PermissionType minPerm) {
        TreeJS node = new TreeJS();
        node.setId(model.getContextKey().getKeyId() + ":" + model.getContextKey().getKeyType());
        node.setText(model.getDisplayName());
        node.setType(model.getContextKey().getKeyType().name());
        /*PermissionType perm = permissionService.getRoleOfUserInContext(SecurityContextHolder.getContext().getAuthentication(), model.getContextKey());
        if (perm == null) {
            log.warn("Perms on context {} are undefined !", model.getContextKey());
            return null;
        }
        node.setUserPerms(perm);*/
        if (permissionService.hasAuthorizedChilds(SecurityContextHolder.getContext().getAuthentication(), model.getContextKey())) {
            node.setChildren(true);
        } else {
            node.setChildren(false);
        }
        if ( ContextType.ITEM.equals(model.getContextKey().getKeyType())) {
            node.getLi_attr().setLeaf(true);
        }
        node.getLi_attr().setBase(model.getContextKey().getKeyId() + ":" + model.getContextKey().getKeyType());
        log.debug("{}", node);
        return node;
    }

    @Override
    public List<TreeJS> asDTOList(@NotNull List<? extends IContext> models, PermissionType minPerm) {
        List<TreeJS> nodes = new ArrayList<>();
        for (IContext ctx: models) {
            nodes.add(this.from(ctx, minPerm));
        }
        return nodes;
    }


    @Override
    public TreeJS from(@NotNull final IExternalGroup model){
        TreeJS node = new TreeJS();
        node.setId(model.getId());
        node.setText(model.getDisplayName());
        node.setChildren(!model.getGroupMembers().isEmpty());
        node.getLi_attr().setLeaf(model.getGroupMembers().isEmpty());
        node.getLi_attr().setBase(model.getId());
        node.getA_attr().setTitle(model.getId());
        node.getA_attr().setModel(new SubjectDTO(new SubjectKeyDTO(model.getId(), SubjectType.GROUP), model.getDisplayName(), true));
        log.debug("{}", node);
        return node;
    }

    /**
     * Copy the list of models to a new list of transfer objects.
     *
     * @param models
     *            Models to translate
     * @return List of TreeJS object
     */
    @Override
    public List<TreeJS> asDTOList(@NotNull final List<IExternalGroup> models){
        List<TreeJS> nodes = new ArrayList<>();
        for (IExternalGroup group: models) {
            nodes.add(this.from(group));
        }
        return nodes;
    }
}
