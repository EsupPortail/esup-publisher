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
package org.esupportail.publisher.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.ReadingIndicator;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.repository.ReaderRepository;
import org.esupportail.publisher.repository.ReadingIndicatorRepository;
import org.esupportail.publisher.repository.predicates.ReadingIndicatorPredicates;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.SecurityUtils;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.web.rest.dto.PublisherDTO;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.esupportail.publisher.web.rest.vo.ActualiteForRead;
import org.esupportail.publisher.web.rest.vo.ActualiteWithSource;
import org.esupportail.publisher.web.rest.vo.PublisherForRead;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
class NewsReaderServiceTest {

    @Spy
    @InjectMocks
    private NewsReaderService newsReaderService;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private ReadingIndicatorRepository readingIndicatorRepository;

    @Mock
    private ItemRepository<AbstractItem> itemRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private PublishersReadLoaderService publishersReadLoaderService;

    private static MockedStatic<SecurityUtils> securiyUtilsMock;

    @BeforeAll
    public static void initTest(){
        try {
            securiyUtilsMock = Mockito.mockStatic(SecurityUtils.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldThrowException_whenUserNotFound() {

        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());
        // Given
        when(SecurityUtils.getCurrentUserDetails()).thenReturn(null);

        // When & Then
        // TODO : Should getNewsByUserOnContext throw custom exception in case user is null ? Or is it the SecurityUtils that should throw it ?
        //assertThrows(ObjectNotFoundException.class, () ->
        //   newsReaderService.getNewsByUserOnContext(1L, null, request));
    }

    @Test
    void shouldReturnEmptyActualite_whenNoNewsFound() throws Exception {

        // Given
        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());
        Mockito.when(SecurityUtils.getCurrentUserDetails()).thenReturn(customUserDetails);
        when(publishersReadLoaderService.getUserPublishersToReadOfReader(any(), anyLong())).thenReturn(Collections.emptyList());

        // When
        ActualiteWithSource actualite = this.newsReaderService.getNewsByUserOnContext(1L, null, request);

        // Then
        assertNotNull(actualite);
        assertTrue(actualite.getItems().isEmpty());
        assertTrue(actualite.getRubriques().isEmpty());
        assertTrue(actualite.getSources().isEmpty());
    }

    @Test
    void shouldGeneratePublisherStructureTreesForValidPublishers() {
        // Arrange
        Long readerId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);

        Publisher publisher1 = new Publisher();
        Publisher publisher2 = new Publisher();

        CacheManager cacheManager = mock(CacheManager.class);

        when(publisherRepository.findAll(any(BooleanBuilder.class))).thenReturn(List.of(publisher1, publisher2));
        when(cacheManager.getCache(any())).thenReturn(null);

        // Act
        List<PublisherForRead> result = newsReaderService.getPublishersReadLoader().getPublisherStructureTreeOfReader(readerId);

        // TODO : assert
        assertNotNull(result);
    }

    @Test
    void shouldThrowNewsExceptionWhenNoPublishersFound() {
        // Arrange
        Long readerId = 1L;
        when(publisherRepository.findAll(any(BooleanBuilder.class), any(), any())).thenReturn(List.of()); // Aucun publisher

        // Act & Assert
        List<PublisherForRead> publisherForReadList = newsReaderService.getPublishersReadLoader().getPublisherStructureTreeOfReader(readerId);

        // TODO : it just returns an empty list if no publisher is found, no exception raised ?
        assertEquals(0, publisherForReadList.size());
    }

    @Test
    void shouldReturnReadingInfos_whenUserAndIndicatorsExist() {

        User user = new User("FR1", "alice");
        AbstractItem item1 = new News();
        item1.setId(1L);
        item1.setTitle("Item 1");
        AbstractItem item2 = new News();
        item2.setId(2L);
        item2.setTitle("Item 2");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());

        ReadingIndicator readingIndicator1 = new ReadingIndicator();
        ReadingIndicator readingIndicator2 = new ReadingIndicator();
        ReadingIndicator readingIndicator3 = new ReadingIndicator();

        readingIndicator1.setUser(customUserDetails.getInternalUser().getLogin());
        readingIndicator2.setUser(customUserDetails.getInternalUser().getLogin());
        readingIndicator3.setUser(new User("FR2", "bob").getLogin());

        readingIndicator1.setItem(item1);
        readingIndicator2.setItem(item2);
        readingIndicator3.setItem(item1);

        readingIndicator1.setRead(true);
        readingIndicator2.setRead(false);
        readingIndicator3.setRead(false);

        Mockito.when(SecurityUtils.getCurrentUserDetails()).thenReturn(customUserDetails);
        Mockito.when(this.readingIndicatorRepository.findAll(any(Predicate.class))).thenReturn(Arrays.asList(readingIndicator1, readingIndicator2));

        Map<String, Boolean> result = this.newsReaderService.getAllReadingInfosForCurrentUser();

        System.out.println(result);

        assertEquals(result.size(), 2);
        assertTrue(result.containsKey("1"));
        assertTrue(result.containsKey("2"));
        assertEquals(result.get("1"), true);
        assertEquals(result.get("2"), false);

    }

    @Test
    void shouldReturnEmptyMap_whenNoIndicatorsExist() {

        AbstractItem item1 = new News();
        item1.setId(1L);
        item1.setTitle("Item 1");
        AbstractItem item2 = new News();
        item2.setId(2L);
        item2.setTitle("Item 2");

        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());

        ReadingIndicator readingIndicator1 = new ReadingIndicator();
        ReadingIndicator readingIndicator2 = new ReadingIndicator();
        ReadingIndicator readingIndicator3 = new ReadingIndicator();

        readingIndicator1.setUser(customUserDetails.getInternalUser().getLogin());
        readingIndicator2.setUser(customUserDetails.getInternalUser().getLogin());
        readingIndicator3.setUser(new User("FR2", "bob").getLogin());

        readingIndicator1.setItem(item1);
        readingIndicator2.setItem(item2);
        readingIndicator3.setItem(item1);

        readingIndicator1.setRead(true);
        readingIndicator2.setRead(false);
        readingIndicator3.setRead(false);

        Mockito.when(SecurityUtils.getCurrentUserDetails()).thenReturn(customUserDetails);
        Mockito.when(this.readingIndicatorRepository.findAll(any(Predicate.class))).thenReturn(Arrays.asList());

        Map<String, Boolean> result = this.newsReaderService.getAllReadingInfosForCurrentUser();

        System.out.println(result);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    void shouldCreateReadingIndicator_whenItemIsReadForTheFirstTime() throws ObjectNotFoundException {

        try {
            //Mockito.mockStatic(SecurityUtils.class);
            User user = new User("FR1", "alice");
            CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
                Arrays.asList());

            AbstractItem item1 = new News();
            item1.setId(1L);
            item1.setTitle("Item 1");

            Mockito.when(SecurityUtils.getCurrentUserDetails()).thenReturn(customUserDetails);
            Mockito.when(this.itemRepository.findOne((Predicate) any())).thenReturn(Optional.of(item1));
            Mockito.when(this.readingIndicatorRepository.exists(any(Predicate.class))).thenReturn(false);

            // Act
            this.newsReaderService.readingManagement(item1.getId(), true);

            // Assert
            verify(readingIndicatorRepository, times(1)).save(any(ReadingIndicator.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void shouldUpdateReadingIndicator_whenItemIsMarkedAsReadAgain() throws ObjectNotFoundException {

        // Given
        Long itemId = 1L;
        boolean isRead = true;

        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());
        when(SecurityUtils.getCurrentUserDetails()).thenReturn(customUserDetails);

        AbstractItem item = mock(News.class); // Item factice
        when(itemRepository.findOne((Example<AbstractItem>) any())).thenReturn(Optional.of(item));
        when(readingIndicatorRepository.exists(ReadingIndicatorPredicates.readingIndicationOfItemAndUser(itemId, customUserDetails.getUser()))).thenReturn(true);

        // Act
        newsReaderService.readingManagement(itemId, isRead);

        // Assert
        //verify(readingIndicatorRepository, times(1)).readingManagement(eq(itemId), eq(customUserDetails.getInternalUser().getLogin()), eq(true));
        //verify(readingIndicatorRepository, times(1)).incrementReadingCounter(eq(itemId), eq(customUserDetails.getInternalUser().getLogin()));
    }

    @Test
    void shouldDeleteReadingIndicator_whenItemIsMarkedAsUnread() throws ObjectNotFoundException {

        // Given
        Long itemId = 1L;
        boolean isRead = false;
        ReadingIndicator readingIndicator = mock(ReadingIndicator.class);

        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());
        when(SecurityUtils.getCurrentUserDetails()).thenReturn(customUserDetails);
        when(readingIndicatorRepository.findOne(any(Predicate.class))).thenReturn(Optional.ofNullable(readingIndicator));

        // Act
        newsReaderService.readingManagement(itemId, isRead);

        // Assert
        verify(readingIndicator).setRead(eq(false));
    }

    @Test
    void shouldThrowException_whenItemNotFoundForUnread() {

        // Given
        Long itemId = 1L;
        boolean isRead = false;

        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());
        when(SecurityUtils.getCurrentUserDetails()).thenReturn(customUserDetails);

        when(readingIndicatorRepository.exists(ReadingIndicatorPredicates.readingIndicationOfItemAndUser(itemId, customUserDetails.getUser()))).thenReturn(false);

        // Act & Assert
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            newsReaderService.readingManagement(itemId, isRead);
        });

        assertNotNull(exception);
    }

    @Test
    void shouldThrowException_whenItemNotFoundForRead() {

        // Given
        Long itemId = 1L;
        boolean isRead = true;

        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());
        when(SecurityUtils.getCurrentUserDetails()).thenReturn(customUserDetails);
        when(itemRepository.findOne((Example<AbstractItem>) any())).thenReturn(Optional.empty());

        // Act & Assert
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            newsReaderService.readingManagement(itemId, isRead);
        });

        assertNotNull(exception);
    }

    @AfterAll
    static void tearDown() {
        if (securiyUtilsMock != null) {
            securiyUtilsMock.close();
            securiyUtilsMock = null;
        }
    }

}
