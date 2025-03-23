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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.UserDetailsService;
import org.esupportail.publisher.service.bean.PublisherStructureTree;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.esupportail.publisher.web.rest.vo.Actualite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.data.domain.Example;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
class NewsReaderServiceTest {

    @Spy
    @InjectMocks
    private NewsReaderService service;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private NewsTreeGenerationService treeGenerationService;

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private ReadingIndicatorRepository readingIndicatorRepository;

    @Mock
    private ItemRepository<AbstractItem> itemRepository;

    @Mock
    private HttpServletRequest request;

    @Test
    void shouldThrowException_whenUserNotFound() {

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(customUserDetails);
        // Given
        when(userDetailsService.loadUserByUsername(any())).thenReturn(null);

        // When & Then
        assertThrows(ObjectNotFoundException.class, () ->
            service.getNewsByUserOnContext(1L, null, request));
    }

    @Test
    void shouldReturnEmptyActualite_whenNoNewsFound() throws Exception {

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Given
        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());
        when(userDetailsService.loadUserByUsername(any())).thenReturn(customUserDetails);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(customUserDetails);

        doReturn(Collections.emptyList()).when(service).generateNewsTreeByReader(anyLong(), any());

        // When
        Actualite actualite = this.service.getNewsByUserOnContext(1L, null, request);

        // Then
        assertNotNull(actualite);
        assertTrue(actualite.getItems().isEmpty());
        assertTrue(actualite.getRubriques().isEmpty());
        assertTrue(actualite.getRubriques().isEmpty());
    }

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
        Set<PublisherStructureTree> result = service.generateNewsTreeByReader(readerId, request);

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

    @Test
    void shouldReturnReadingInfos_whenUserAndIndicatorsExist() {

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


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

        Mockito.when(this.userDetailsService.loadUserByUsername(ArgumentMatchers.any())).thenReturn(customUserDetails);
        Mockito.when(this.readingIndicatorRepository.findAllByUserId(ArgumentMatchers.any())).thenReturn(Arrays.asList(readingIndicator1, readingIndicator2));
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);

        Map<String, Boolean> result = this.service.getAllReadingInfosForCurrentUser();

        System.out.println(result);

        assertEquals(result.size(), 2);
        assertTrue(result.containsKey("1"));
        assertTrue(result.containsKey("2"));
        assertEquals(result.get("1"), true);
        assertEquals(result.get("2"), false);

    }

    @Test
    void shouldReturnEmptyMap_whenNoIndicatorsExist() {

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);



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

        Mockito.when(this.userDetailsService.loadUserByUsername(ArgumentMatchers.any())).thenReturn(customUserDetails);
        Mockito.when(this.readingIndicatorRepository.findAllByUserId(ArgumentMatchers.any())).thenReturn(Arrays.asList());
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);

        Map<String, Boolean> result = this.service.getAllReadingInfosForCurrentUser();

        System.out.println(result);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    void shouldCreateReadingIndicator_whenItemIsReadForTheFirstTime() throws ObjectNotFoundException {

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());

        AbstractItem item1 = new News();
        item1.setId(1L);
        item1.setTitle("Item 1");


        Mockito.when(this.userDetailsService.loadUserByUsername(ArgumentMatchers.any())).thenReturn(customUserDetails);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        Mockito.when(this.itemRepository.findOne((Predicate) any())).thenReturn(Optional.of(item1));
        Mockito.when(this.readingIndicatorRepository.existsByItemIdAndUserId(any(), any())).thenReturn(false);

        // Act
        this.service.readingManagement(item1.getId(), true);

        // Assert
        verify(readingIndicatorRepository, times(1)).save(any(ReadingIndicator.class));
    }

    @Test
    void shouldUpdateReadingIndicator_whenItemIsMarkedAsReadAgain() throws ObjectNotFoundException {

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Given
        Long itemId = 1L;
        boolean isRead = true;

        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());
        when(userDetailsService.loadUserByUsername(any())).thenReturn(customUserDetails);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(customUserDetails);

        AbstractItem item = mock(News.class); // Item factice
        when(itemRepository.findOne((Example<AbstractItem>) any())).thenReturn(Optional.of(item));
        when(readingIndicatorRepository.existsByItemIdAndUserId(itemId, customUserDetails.getUser().getLogin())).thenReturn(true);

        // Act
        service.readingManagement(itemId, isRead);

        // Assert
        verify(readingIndicatorRepository, times(1)).readingManagement(eq(itemId), eq(customUserDetails.getInternalUser().getLogin()), eq(true));
        verify(readingIndicatorRepository, times(1)).incrementReadingCounter(eq(itemId), eq(customUserDetails.getInternalUser().getLogin()));
    }

    @Test
    void shouldDeleteReadingIndicator_whenItemIsMarkedAsUnread() throws ObjectNotFoundException {

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Given
        Long itemId = 1L;
        boolean isRead = false;

        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());
        when(userDetailsService.loadUserByUsername(any())).thenReturn(customUserDetails);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(customUserDetails);

        when(readingIndicatorRepository.existsByItemIdAndUserId(itemId, customUserDetails.getUser().getLogin())).thenReturn(true);

        // Act
        service.readingManagement(itemId, isRead);

        // Assert
        verify(readingIndicatorRepository, times(1)).readingManagement(eq(itemId), eq(customUserDetails.getInternalUser().getLogin()), eq(false));
    }

    @Test
    void shouldThrowException_whenItemNotFoundForUnread() {

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Given
        Long itemId = 1L;
        boolean isRead = false;

        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());
        when(userDetailsService.loadUserByUsername(any())).thenReturn(customUserDetails);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(customUserDetails);

        when(readingIndicatorRepository.existsByItemIdAndUserId(itemId, customUserDetails.getUser().getLogin())).thenReturn(false);

        // Act & Assert
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            service.readingManagement(itemId, isRead);
        });

        assertNotNull(exception);
    }

    @Test
    void shouldThrowException_whenItemNotFoundForRead() {

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Given
        Long itemId = 1L;
        boolean isRead = true;

        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());
        when(userDetailsService.loadUserByUsername(any())).thenReturn(customUserDetails);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(customUserDetails);

        when(itemRepository.findOne((Example<AbstractItem>) any())).thenReturn(Optional.empty());

        // Act & Assert
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            service.readingManagement(itemId, isRead);
        });

        assertNotNull(exception);
    }

}
