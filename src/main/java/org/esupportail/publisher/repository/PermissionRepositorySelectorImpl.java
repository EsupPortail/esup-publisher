package org.esupportail.publisher.repository;

import org.esupportail.publisher.domain.AbstractPermission;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by jgribonvald on 04/11/15.
 */
@Component
public class PermissionRepositorySelectorImpl implements IPermissionRepositorySelector {

    @Inject
    @Named("permissionRepository")
    private PermissionRepository<AbstractPermission> permissionDao;

    @Inject
    private PermissionOnContextRepository permissionOnCtxDao;

    @Inject
    private PermOnClassifWithSubjectsRepository permissionOnCtxWSubjDao;

    @Inject
    private PermissionOnSubjectsRepository permissionOnSubj;

    @Inject
    private PermOnSubjectsWithClassifsRepository permissionOnSubjWClassifsDao;


    public PermissionRepositorySelectorImpl() {
    }

    @Override
    public PermissionRepository getPermissionDao(final PermissionClass type) {
        if (type == null) return permissionDao;
        switch (type) {
            case CONTEXT: return permissionOnCtxDao;
            case CONTEXT_WITH_SUBJECTS: return permissionOnCtxWSubjDao;
            case SUBJECT: return permissionOnSubj;
            case SUBJECT_WITH_CONTEXT: return permissionOnSubjWClassifsDao;
        }
        return permissionDao;
    }
}
