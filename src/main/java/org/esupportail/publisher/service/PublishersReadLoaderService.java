package org.esupportail.publisher.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.Reader;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.repository.ReaderRepository;
import org.esupportail.publisher.repository.predicates.PublisherPredicates;
import org.esupportail.publisher.service.factories.PublisherDTOFactory;
import org.esupportail.publisher.service.factories.SubscriberDTOFactory;
import org.esupportail.publisher.web.rest.dto.PublisherDTO;
import org.esupportail.publisher.web.rest.dto.SubscriberDTO;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.esupportail.publisher.web.rest.vo.PublisherForRead;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
public class PublishersReadLoaderService {

    @Inject
    private PublisherRepository publisherRepository;
    @Inject
    private SubscriberService subscriberService;
    @Inject
    private ReaderRepository readerRepository;
    @Inject
    private PublisherDTOFactory publisherDTOFactory;
    @Inject
    private SubscriberDTOFactory subscriberDTOFactory;
    @Inject
    private ViewService viewService;

    private Map<Long, List<PublisherForRead>> publishersByReaders = new HashMap<Long, List<PublisherForRead>>();

    @PostConstruct
    public void load() {
        log.info("Load all used publishers");

        final List<Reader> readers = readerRepository.findAll();

        if (!readers.isEmpty()) {
            readers.forEach(reader -> {
                log.info("Will load publishers of reader {}", reader);
                if (reader.getId() != null) {
                    publishersByReaders.put(reader.getId(), getPublisherStructureTreeOfReader(reader.getId()));
                }
            });
        }
    }

    protected List<PublisherForRead> getPublisherStructureTreeOfReader(long readerId) {
        final BooleanBuilder builder = new BooleanBuilder(PublisherPredicates.AllOfUsedState(true)).and(PublisherPredicates.AllOfReader(readerId));
        final List<Publisher> publishers = Lists.newArrayList(
            publisherRepository.findAll(builder, PublisherPredicates.orderByOrganizations(), PublisherPredicates.orderByDisplayOrder()));

        List<PublisherForRead> publisherForReadList = new ArrayList<>();

        if (!publishers.isEmpty()) {
            publishers.forEach(publisher -> {
                //TODO Temporary Patch To Remove
                List<SubscriberDTO> subscribers = subscriberDTOFactory.asDTOList(subscriberService.getDefaultsSubscribersOfContext(publisher.getContextKey()));
                subscribers.stream()
                    .filter(subscriber -> Lists.newArrayList("ESCOUAICourant","ESCOSIRENCourant").contains(subscriber.getModelId().getSubjectKey().getKeyAttribute()))
                    .forEach(subscriber -> {
                        subscriber.getModelId().getSubjectKey().setKeyAttribute(subscriber.getModelId().getSubjectKey().getKeyAttribute().replace("ESCOUAICourant", "ESCOUAI").replace("ESCOSIRENCourant", "ESCOSIREN"));
                    });
                publisherForReadList.add(new PublisherForRead(publisherDTOFactory.from(publisher), subscribers));
            });
        }
        log.debug("Obtained publishers from readerId '{}' are : {}", readerId, publisherForReadList);
        return publisherForReadList;
    }

    public void reloadPublishersOfReader(long readerId) {
        log.info("ReloadPublishers of readerId {}", readerId);
        publishersByReaders.put(readerId, getPublisherStructureTreeOfReader(readerId));
    }

    public List<PublisherDTO> getUserPublishersToReadOfReader(@NotNull UserDTO userDto, long readerId) {
        List<PublisherDTO> publisherDTOList = new ArrayList<>();
        if (publishersByReaders.containsKey(readerId) && !publishersByReaders.get(readerId).isEmpty()) {
            publishersByReaders.get(readerId).forEach(publisherTree -> {
                if (viewService.evalSubscribing(userDto, publisherTree.getSubscribersDTO())) {
                    publisherDTOList.add(publisherTree.getPublisherDTO());
                }
            });

            return publisherDTOList;
        }
        log.error("ReaderId {} doesn't have publishers loaded", readerId);
        return publisherDTOList;
    }

}
