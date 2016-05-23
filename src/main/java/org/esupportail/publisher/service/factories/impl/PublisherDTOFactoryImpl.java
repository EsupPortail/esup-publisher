package org.esupportail.publisher.service.factories.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.service.factories.OrganizationDTOFactory;
import org.esupportail.publisher.service.factories.PublisherDTOFactory;
import org.esupportail.publisher.service.factories.ReaderDTOFactory;
import org.esupportail.publisher.service.factories.RedactorDTOFactory;
import org.esupportail.publisher.web.rest.dto.PublisherDTO;
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
public class PublisherDTOFactoryImpl extends AbstractDTOFactoryImpl<PublisherDTO, Publisher, Long> implements PublisherDTOFactory {

    @Inject
    @Getter
    private transient PublisherRepository dao;

    @Inject
    private transient ReaderDTOFactory readerFactory;
    @Inject
    private transient RedactorDTOFactory redactorFactory;
    @Inject
    private transient OrganizationDTOFactory organizationDTOFactory;

    public PublisherDTOFactoryImpl() {
        super(PublisherDTO.class, Publisher.class);
    }

    @Override
    public PublisherDTO from(final Publisher model) {
        log.debug("Model to DTO of {}", model);
        if (model != null) {
            return new PublisherDTO(model.getId(), organizationDTOFactory.from(model.getContext().getOrganization()),readerFactory.from(model.getContext().getReader()),
                redactorFactory.from(model.getContext().getRedactor()), model.isUsed(), model.getDisplayOrder(), model.getPermissionType(), model.getDefaultDisplayOrder()
                , model.isHasSubPermsManagement());
        }
        return null;
    }

    /*@Override
    public Publisher from(final PublisherDTO dtObject) throws ObjectNotFoundException {
        Publisher model = super.from(dtObject);
        model.setContext(new OrganizationReaderRedactorKey(organizationDTOFactory.from(dtObject.getOrganization().getModelId()),
            readerFactory.from(dtObject.getReader().getModelId()), redactorFactory.from(dtObject.getRedactor().getModelId())));
        model.setUsed(dtObject.isUsed());
        model.setDisplayOrder(dtObject.getDisplayOrder());
        model.setPermissionType(dtObject.getPermissionType());
        model.setDefaultDisplayOrder(dtObject.getDefaultDisplayOrder());
        return model;
    }*/

//    @Override
//    public List<PublisherDTO> asDTOList(Collection<Publisher> models) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public Set<PublisherDTO> asDTOSet(Collection<Publisher> models) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public Set<Publisher> asSet(Collection<PublisherDTO> tObjects)
//            throws ObjectNotFoundException {
//        // TODO Auto-generated method stub
//        return null;
//    }

}
