package org.esupportail.publisher.service.factories.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.repository.OrganizationRepository;
import org.esupportail.publisher.service.factories.OrganizationDTOFactory;
import org.esupportail.publisher.web.rest.dto.OrganizationDTO;
import org.esupportail.publisher.web.rest.dto.SubjectDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 *
 * @author GIP RECIA - Julien Gribonvald
 * 13 oct. 2014
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class OrganizationDTOFactoryImpl extends AuditableDTOFactoryImpl<OrganizationDTO, Organization>
    implements OrganizationDTOFactory {

    @Inject
    @Getter
    private OrganizationRepository dao;

    public OrganizationDTOFactoryImpl() {
        super(OrganizationDTO.class, Organization.class);
    }

    @Override
    public OrganizationDTO from(final Organization model) {
        log.debug("Model to DTO of {}", model);
        if (model != null) {
            SubjectDTO updatedBy = null;
            if (model.getLastModifiedBy() != null ) {
                updatedBy = getSubjectFactory().from(model.getLastModifiedBy());
            }
            return new OrganizationDTO(model.getId(), model.getCreatedDate(), model.getLastModifiedDate(),
                    getSubjectFactory().from(model.getCreatedBy()), updatedBy,
                    model.getName(), model.getDisplayName(),
                    model.getDescription(), model.getDisplayOrder(),model.isAllowNotifications());
        }
        return null;
    }

    /*@Override
    public Organization from(final OrganizationDTO dtObject) throws ObjectNotFoundException {
        Organization model = super.from(dtObject);
        model.setName(dtObject.getName());
        model.setDisplayName(dtObject.getDisplayName());
        model.setDescription(dtObject.getDescription());
        model.setAllowNotifications(dtObject.isAllowNotifications());

        return model;
    }*/

//  @Override
//  public List<InternalFeedDTO> asDTOList(Collection<InternalFeed> models) {
//  	// TODO Auto-generated method stub
//  	return super.asDTOList(models);
//  }
//
//  @Override
//  public Set<InternalFeedDTO> asDTOSet(Collection<InternalFeed> models) {
//  	// TODO Auto-generated method stub
//  	return super.asDTOSet(models);
//  }
//
//  @Override
//  public Set<InternalFeed> asSet(Collection<InternalFeedDTO> dtObjects)
//  		throws ObjectNotFoundException {
//  	// TODO Auto-generated method stub
//  	return super.asSet(dtObjects);
//  }

}
