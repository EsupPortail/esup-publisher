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
package org.esupportail.publisher.service.exceptions;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Exception to throw when an object was not found in database.
 * @author GIP RECIA - Julien Gribonvald
 * 18 juil. 2014
 */
public class ObjectNotFoundException extends Exception implements Serializable {

    private static final long serialVersionUID = -4359471388801312813L;

    /**
     * Identifier used to find the object
     */
    private Serializable objectId;

    /**
     * Entity class
     */
    private Class<?> entity;

    /**
     * Constructs a new exception with the detail message generated from the specified parameters.
     * @param objectId Identifier that could not be found
     * @param name Entity name that did not contain the specified objectId
     */
    public ObjectNotFoundException(final Serializable objectId, final Class<?> classe) {
        super(message(objectId, classe.getSimpleName()));
        this.objectId = objectId;
        this.entity = classe;
    }

    /**
     * Constructs a new exception with the detail message generated from the specified parameters, and cause.
     *
     * @param objectId Identifier that could not be found
     * @param name Entity name that did not contain the specified objectId
     * @param cause the cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A null value is permitted, and
     *            indicates that the cause is nonexistent or unknown.)
     */
    public ObjectNotFoundException(final Serializable objectId,
            final Class<?> classe,
            final Throwable cause) {
        super(message(objectId, classe.getSimpleName()), cause);
        this.objectId = objectId;
        this.entity = classe;
    }

    /**
     * Generate a user-readable message string.
     * @param objectId Identifier that could not be found
     * @param name Entity name that did not contain the specified objectId
     * @return A user-readable object not found message string.
     */
    private static String message(final Serializable objectId, @NotNull final String name) {
        return "Unable to load {" + name + "} with ID {"
                + (objectId == null ? "null" : objectId.toString() + "}");
    }

    @SuppressWarnings("unused")
    private void setObjectId(final Serializable objectId) { // NOPMD for serialization
        this.objectId = objectId;
    }

    public Serializable getObjectId() {
        return objectId;
    }

    @SuppressWarnings("unused")
    private void setEntity(Class<?> classe) { // NOPMD for serialization
        this.entity = classe;
    }

    public Class<?> getEntty() {
        return entity;
    }
}
