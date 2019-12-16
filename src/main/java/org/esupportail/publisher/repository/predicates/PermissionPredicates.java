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
package org.esupportail.publisher.repository.predicates;

import java.util.Collection;

import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.QAbstractPermission;
import org.esupportail.publisher.domain.QPermissionOnClassificationWithSubjectList;
import org.esupportail.publisher.domain.QPermissionOnContext;
import org.esupportail.publisher.domain.QPermissionOnSubjects;
import org.esupportail.publisher.domain.QPermissionOnSubjectsWithClassificationList;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionClass;

import com.querydsl.core.types.Predicate;

/**
 * @author GIP RECIA - Julien Gribonvald 22 juil. 2014
 */
public final class PermissionPredicates {

	private static final QAbstractPermission qobj = QAbstractPermission.abstractPermission;
	private static final QPermissionOnContext qPermCtx = QPermissionOnContext.permissionOnContext;
	private static final QPermissionOnClassificationWithSubjectList qPermCtxWSL = QPermissionOnClassificationWithSubjectList.permissionOnClassificationWithSubjectList;
	private static final QPermissionOnSubjectsWithClassificationList qPermSubjCtx = QPermissionOnSubjectsWithClassificationList.permissionOnSubjectsWithClassificationList;
	private static final QPermissionOnSubjects qPermSubj = QPermissionOnSubjects.permissionOnSubjects;

	public static Predicate AbstractPermOnCtxType(ContextType keyType) {
		return qobj.context.keyType.eq(keyType);
	}

	public static Predicate AbstractPermOnCtx(ContextType keyType, Collection<Long> ids) {
		return qobj.context.keyId.in(ids).and(AbstractPermOnCtxType(keyType));
	}

	public static Predicate AbstractPermOnCtx(ContextType keyType, Long id) {
		return qobj.context.keyId.eq(id).and(AbstractPermOnCtxType(keyType));
	}

	public static Predicate AbstractPermOnCtx(Collection<ContextKey> keys) {
		return qobj.context.in(keys);
	}

    public static Predicate OnCtxType(ContextType keyType,
                                      PermissionClass type, boolean onAbstract) {
        if (!onAbstract) {
            switch (type) {
                case CONTEXT:
                    return qPermCtx.context.keyType.eq(keyType).and(qPermCtx._super.instanceOf(type.getType()));
                case CONTEXT_WITH_SUBJECTS:
                    return qPermCtxWSL.context.keyType.eq(keyType).and(qPermCtxWSL._super._super.instanceOf(type.getType()));
                case SUBJECT_WITH_CONTEXT:
                    return qPermSubjCtx.context.keyType.eq(keyType).and(qPermSubjCtx._super._super.instanceOf(type.getType()));
                case SUBJECT:
                    return qPermSubj.context.keyType.eq(keyType).and(qPermSubj._super.instanceOf(type.getType()));
                default:
                    throw new IllegalArgumentException("Enum property of PermissionClass not managed");
            }
        }

        return qobj.context.keyType.eq(keyType).and(qobj.instanceOf(type.getType()));
    }


	public static Predicate OnCtx(ContextType keyType, Collection<Long> ids,
                                  PermissionClass type, boolean onAbstract) {
        if (!onAbstract) {
            switch (type) {
                case CONTEXT:
                    return qPermCtx.context.keyId.in(ids).and(qPermCtx.context.keyType.eq(keyType)).and(qPermCtx._super.instanceOf(type.getType()));
                case CONTEXT_WITH_SUBJECTS:
                    return qPermCtxWSL.context.keyId.in(ids).and(qPermCtxWSL.context.keyType.eq(keyType)).and(qPermCtxWSL._super._super.instanceOf(type.getType()));
                case SUBJECT_WITH_CONTEXT:
                    return qPermSubjCtx.context.keyId.in(ids).and(qPermSubjCtx.context.keyType.eq(keyType)).and(qPermSubjCtx._super._super.instanceOf(type.getType()));
                case SUBJECT:
                    return qPermSubj.context.keyId.in(ids).and(qPermSubj.context.keyType.eq(keyType)).and(qPermSubj._super.instanceOf(type.getType()));
                default:
                    throw new IllegalArgumentException("Enum property of PermissionClass not managed");
            }
        }
		return qobj.context.keyId.in(ids).and(AbstractPermOnCtxType(keyType)).and(qobj.instanceOf(type.getType()));
	}

	public static Predicate OnCtx(ContextType keyType, Long id,
                                  PermissionClass type, boolean onAbstract) {
        if (!onAbstract) {
            switch (type) {
                case CONTEXT:
                    return qPermCtx.context.keyId.eq(id).and(qPermCtx.context.keyType.eq(keyType)).and(qPermCtx._super.instanceOf(type.getType()));
                case CONTEXT_WITH_SUBJECTS:
                    return qPermCtxWSL.context.keyId.eq(id).and(qPermCtxWSL.context.keyType.eq(keyType)).and(qPermCtxWSL._super._super.instanceOf(type.getType()));
                case SUBJECT_WITH_CONTEXT:
                    return qPermSubjCtx.context.keyId.eq(id).and(qPermSubjCtx.context.keyType.eq(keyType)).and(qPermSubjCtx._super._super.instanceOf(type.getType()));
                case SUBJECT:
                    return qPermSubj.context.keyId.eq(id).and(qPermSubj.context.keyType.eq(keyType)).and(qPermSubj._super.instanceOf(type.getType()));
                default:
                    throw new IllegalArgumentException("Enum property of PermissionClass not managed");
            }
        }
		return qobj.context.keyId.eq(id).and(AbstractPermOnCtxType(keyType)).and(qobj.instanceOf(type.getType()));
	}

	public static Predicate OnCtx(Collection<ContextKey> keys,
                                  PermissionClass type, boolean onAbstract) {
        if (!onAbstract) {
            switch (type) {
                case CONTEXT:
                    return qPermCtx.context.in(keys).and(qPermCtx._super.instanceOf(type.getType()));
                case CONTEXT_WITH_SUBJECTS:
                    return qPermCtxWSL.context.in(keys).and(qPermCtxWSL._super._super.instanceOf(type.getType()));
                case SUBJECT_WITH_CONTEXT:
                    return qPermSubjCtx.context.in(keys).and(qPermSubjCtx._super._super.instanceOf(type.getType()));
                case SUBJECT:
                    return qPermSubj.context.in(keys).and(qPermSubj._super.instanceOf(type.getType()));
                default:
                    throw new IllegalArgumentException("Enum property of PermissionClass not managed");
            }
        }
		return qobj.context.in(keys).and(qobj.instanceOf(type.getType()));
	}

    public static Predicate ofType(PermissionClass type, boolean onAbstract) {
        if (!onAbstract) {
            switch (type) {
                case CONTEXT:
                    return qPermCtx._super.instanceOf(type.getType());
                case CONTEXT_WITH_SUBJECTS:
                    return qPermCtxWSL._super._super.instanceOf(type.getType());
                case SUBJECT_WITH_CONTEXT:
                    return qPermSubjCtx._super._super.instanceOf(type.getType());
                case SUBJECT:
                    return qPermSubj._super.instanceOf(type.getType());
                default:
                    throw new IllegalArgumentException("Enum property of PermissionClass not managed");
            }
        }
        return qobj.instanceOf(type.getType());
    }

}
