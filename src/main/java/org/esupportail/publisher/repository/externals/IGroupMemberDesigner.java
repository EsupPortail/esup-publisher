package org.esupportail.publisher.repository.externals;

import org.esupportail.publisher.domain.externals.IExternalGroup;

/**
 * Created by jgribonvald on 11/06/15.
 */
public interface IGroupMemberDesigner {

   IExternalGroup designe(IExternalGroup group, IExternalGroupDao externalGroupDao);
}
