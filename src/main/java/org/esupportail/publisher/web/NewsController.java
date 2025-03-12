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

import org.esupportail.publisher.service.NewsService;
import org.esupportail.publisher.service.PagingService;
import org.esupportail.publisher.service.ViewService;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.web.rest.vo.Actualite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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
public class NewsController {

    private static final Logger log = LoggerFactory.getLogger(NewsController.class);

    private final int PAGE_SIZE = 10;

    @Inject
    private NewsService newsService;

    @Inject
    private PagingService pagingService;

    @Inject
    private ViewService viewService;


    @GetMapping(value = "/myNews/{reader_id}")
    public ResponseEntity<Object> getUserNews(
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
            Actualite actualite = this.newsService.getNewsByUserOnContext(readerId, reading, request);
            return ResponseEntity.ok(this.pagingService.paginateActualite(actualite, pageIndex, PAGE_SIZE, source, rubriques));
        } catch (ObjectNotFoundException exception) {
            log.error("Exception raised on /myNews", exception);
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            log.error("Exception raised on /myNews", ex);
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    @RequestMapping(value = "/item/" + "{item_id}")
    public ResponseEntity<Object> getItemById(@PathVariable("item_id") Long itemId, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(this.viewService.itemView(itemId, request));
        } catch (Exception ex) {
            log.error("Exception raised on /item", ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @RequestMapping(value = "/readingInfos")
    public ResponseEntity<Object>  getNewsReadingInformations() {
        try {
            return ResponseEntity.ok(this.newsService.getAllReadingInfosForCurrentUser());
        } catch (Exception e) {
            log.error("Exception raised on /readingInfos", e);
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PatchMapping(value = "/setNewsReading/" + "{item_id}/" + "{isRead}")
    public ResponseEntity<Object> setReading(@PathVariable("item_id") Long itemId, @PathVariable("isRead") boolean isRead) {
        try {
            this.newsService.readingManagement(itemId, isRead);
            return ResponseEntity.ok("");
        } catch (Exception e) {
            log.error("Exception raised on /setNewsReading", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
