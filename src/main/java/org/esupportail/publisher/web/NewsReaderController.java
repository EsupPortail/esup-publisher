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
package org.esupportail.publisher.web;


import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.esupportail.publisher.config.ESUPPublisherProperties;
import org.esupportail.publisher.security.SecurityConstants;
import org.esupportail.publisher.service.NewsReaderService;
import org.esupportail.publisher.service.PagingService;
import org.esupportail.publisher.service.ViewService;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.web.rest.vo.Actualite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

@Data
@RestController
@RequestMapping("/news")
public class NewsReaderController {

    private static final Logger log = LoggerFactory.getLogger(NewsReaderController.class);

    private final int PAGE_SIZE = 10;

    @Inject
    private NewsReaderService newsReaderService;

    @Inject
    private PagingService pagingService;

    @Inject
    private ViewService viewService;

    @Inject
    private ESUPPublisherProperties esupPublisherProperties;

    @GetMapping(value = "/myNews/{reader_id}")
    public ResponseEntity<Object> getNewsOfUser(
            @PathVariable(value = "reader_id", required = true) Long readerId,
            @RequestParam(value = "pageIndex", defaultValue = "0", required = false) Integer pageIndex,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "rubriques", required = false) List<Long> rubriques,
            @RequestParam(value = "lecture", required = false) Boolean reading,
            HttpServletRequest request) {

        if (!(rubriques == null) && source == null) {
            return ResponseEntity.badRequest().body(
                    "Veuillez renseigner une source avant de renseigner des rubriques.");
        }

        try {
            Actualite actualite = this.newsReaderService.getNewsByUserOnContext(readerId, reading, request);
            return ResponseEntity.ok(this.pagingService.paginateActualite(actualite, pageIndex, PAGE_SIZE, source, rubriques));
        } catch (ObjectNotFoundException exception) {
            log.error("Exception raised on /myNews", exception);
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            log.error("Exception raised on /myNews", ex);
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    @GetMapping(value = "/myNews/{reader_id}/overview")
    public ResponseEntity<Object> getNewsOfUserOverview(
        @PathVariable(value = "reader_id", required = true) Long readerId,
        @RequestParam(value = "pageIndex", defaultValue = "0", required = false) Integer pageIndex,
        @RequestParam(value = "source", required = false) String source,
        @RequestParam(value = "rubriques", required = false) List<Long> rubriques,
        @RequestParam(value = "lecture", required = false) Boolean reading,
        HttpServletRequest request) {

        if (!(rubriques == null) && source == null) {
            return ResponseEntity.badRequest().body(
                "Veuillez renseigner une source avant de renseigner des rubriques.");
        }
        try {
            Actualite actualite = this.newsReaderService.getNewsByUserOnContext(readerId, reading, request);
            return ResponseEntity.ok(actualite.getItems().subList(0, Math.min(actualite.getItems().size(), esupPublisherProperties.getNews().getOverviewSize())));
        } catch (ObjectNotFoundException exception) {
            log.error("Exception raised on /myNews overview", exception);
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            log.error("Exception raised on /myNews overview", ex);
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    @GetMapping(value = "/item/{item_id}")
    public ResponseEntity<Object> getItemById(@PathVariable("item_id") Long itemId, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(this.viewService.itemView(itemId, request));
        } catch (Exception ex) {
            log.error("Exception raised on /item", ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping(value = "/readingInfos")
    public ResponseEntity<Object> getNewsReadingUserInformations() {
        try {
            return ResponseEntity.ok(this.newsReaderService.getAllReadingInfosForCurrentUser());
        } catch (Exception e) {
            log.error("Exception raised on /readingInfos", e);
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PatchMapping(value = "/setNewsReading/{item_id}/{isRead}")
    public ResponseEntity<Object> setUserReadingState(@PathVariable("item_id") Long itemId, @PathVariable("isRead") boolean isRead) {
        try {
            this.newsReaderService.readingManagement(itemId, isRead);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Exception raised on /setNewsReading", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(value = "/reloadPublishersOf/{reader_id}")
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    public ResponseEntity<Void> reloadPublishersOfReader(@PathVariable(value = "reader_id", required = true) Long readerId) {
        this.newsReaderService.reloadPublishersForReader(readerId);
        return ResponseEntity.ok().build();
    }

}
