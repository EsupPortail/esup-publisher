package org.esupportail.publisher.repository;

import org.esupportail.publisher.domain.enums.PermissionClass;

/**
 * Created by jgribonvald on 04/11/15.
 */
public interface IPermissionRepositorySelector {
    PermissionRepository getPermissionDao(final PermissionClass type);
}
