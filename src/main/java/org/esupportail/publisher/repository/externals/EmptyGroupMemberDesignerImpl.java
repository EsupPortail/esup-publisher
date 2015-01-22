package org.esupportail.publisher.repository.externals;

import org.esupportail.publisher.domain.externals.IExternalGroup;

/**
 * Created by jgribonvald on 11/06/15.
 */
public class EmptyGroupMemberDesignerImpl implements IGroupMemberDesigner {

    @Override
    public IExternalGroup designe(IExternalGroup group, IExternalGroupDao externalGroupDao) {
        return group;
    }
}
