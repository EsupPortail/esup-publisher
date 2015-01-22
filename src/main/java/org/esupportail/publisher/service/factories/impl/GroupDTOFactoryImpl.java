/**
 *
 */
package org.esupportail.publisher.service.factories.impl;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.externals.ExternalGroup;
import org.esupportail.publisher.domain.externals.IExternalGroup;
import org.esupportail.publisher.repository.externals.IExternalGroupDao;
import org.esupportail.publisher.service.factories.GroupDTOFactory;
import org.esupportail.publisher.web.rest.dto.GroupDTO;
import org.esupportail.publisher.web.rest.dto.SubjectDTO;
import org.esupportail.publisher.web.rest.dto.SubjectKeyDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

/**
 * @author GIP RECIA - Julien Gribonvald 25 juil. 2014
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class GroupDTOFactoryImpl implements GroupDTOFactory {

	@Inject
	@Getter
	private transient IExternalGroupDao extDao;

	public GroupDTOFactoryImpl() {
		super();
	}

	@Override
	public GroupDTO from(final String id) {
		log.debug("from login Id to DTO of {}", id);
		IExternalGroup extModel = getExtDao().getGroupById(id, true);
        if (extModel == null) {
            return new GroupDTO(id, null, false, null);
        }
		return from(extModel);
	}

    @Override
    public SubjectDTO liteFrom(@NotNull String id) {
        log.debug("from login Id to DTO of {}", id);
        IExternalGroup extModel = getExtDao().getGroupById(id, false);
        if (extModel == null) {
            return new SubjectDTO(new SubjectKeyDTO(id, SubjectType.GROUP), null, false);
        }
        return new SubjectDTO(new SubjectKeyDTO(extModel.getId(), SubjectType.GROUP), extModel.getDisplayName(),true);
    }

    @Override
	public GroupDTO from(final IExternalGroup extModel) {
		if (extModel != null) {
            boolean hasChilds;
            if (extModel instanceof ExternalGroup) {
                hasChilds = ((ExternalGroup) extModel).hasMembers();
            } else {
                hasChilds = (extModel.getGroupMembers() != null && !extModel.getGroupMembers().isEmpty())
                    || (extModel.getUserMembers() != null && !extModel.getUserMembers().isEmpty());
            }
			return new GroupDTO(extModel.getId(), extModel.getDisplayName(), hasChilds, extModel.getAttributes());
		}
		return null;
	}

    @Override
	public List<GroupDTO> asDTOList(final Collection<IExternalGroup> models) {
		final List<GroupDTO> tos = Lists.newArrayList();

		if ((models != null) && !models.isEmpty()) {
			for (IExternalGroup model : models) {
				tos.add(from(model));
			}
		}

		return tos;
	}

    @Override
    public List<SubjectDTO> asLiteDTOList(Collection<IExternalGroup> models) {
        final List<SubjectDTO> tos = Lists.newArrayList();

        if ((models != null) && !models.isEmpty()) {
            for (IExternalGroup model : models) {
                tos.add(new SubjectDTO(new SubjectKeyDTO(model.getId(), SubjectType.GROUP), model.getDisplayName(), true));
            }
        }

        return tos;
    }

	/*public Set<GroupDTO> asDTOSet(final Collection<IExternalGroup> models) {
		final Set<GroupDTO> dtos = Sets.newHashSet();
		for (IExternalGroup model : models) {
			dtos.add(from(model));
		}
		return dtos;
	}*/
}
