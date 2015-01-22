package org.esupportail.publisher.security;

import org.esupportail.publisher.domain.ContextKey;

import javax.validation.constraints.NotNull;

/**
 * Created by jgribonvald on 09/11/15.
 */
public interface IPermissionManagingContextCreation {

    void addContext(@NotNull final ContextKey contextKey);
}
