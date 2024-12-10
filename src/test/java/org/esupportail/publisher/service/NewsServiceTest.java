package org.esupportail.publisher.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.PublisherStructureTree;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.repository.ReaderRepository;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.web.rest.vo.Actualite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.querydsl.core.BooleanBuilder;


@ExtendWith(SpringExtension.class)
class NewsServiceTest {

    @InjectMocks
    private NewsService service;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private TreeGenerationService treeGenerationService;

    @Mock
    private ReaderRepository readerRepository;



    @Test
    void shouldGeneratePublisherStructureTreesForValidPublishers() throws Exception {
        // Arrange
        Long readerId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);

        Publisher publisher1 = new Publisher();
        Publisher publisher2 = new Publisher();
        Actualite actualite = new Actualite();

        when(publisherRepository.findAll(any(BooleanBuilder.class))).thenReturn(List.of(publisher1, publisher2));
        when(treeGenerationService.getActualiteByPublisher(any(Publisher.class), eq(request))).thenReturn(actualite);

        // Act
        List<PublisherStructureTree> result = service.generateNewsTreeByReader(readerId, request);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // Deux publishers = deux arbres
        verify(treeGenerationService, times(2)).getActualiteByPublisher(any(Publisher.class), eq(request));
        verify(publisherRepository, times(1)).findAll(any(BooleanBuilder.class));
    }

    @Test
    void shouldThrowNewsExceptionWhenNoPublishersFound() {
        // Arrange
        Long readerId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(publisherRepository.findAll(any(BooleanBuilder.class))).thenReturn(List.of()); // Aucun publisher

        // Act & Assert
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            service.generateNewsTreeByReader(readerId, request);
        });

        assertNotNull(exception);
        verify(publisherRepository, times(1)).findAll(any(BooleanBuilder.class));
        verifyNoInteractions(treeGenerationService); // Aucun appel au service d'arbre
    }

}
