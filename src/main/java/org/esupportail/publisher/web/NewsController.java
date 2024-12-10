package org.esupportail.publisher.web;


import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import lombok.Data;

import org.esupportail.publisher.service.NewsService;
import org.esupportail.publisher.service.PagingService;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.util.JWTDecoder;
import org.esupportail.publisher.web.rest.vo.Actualite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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


    @GetMapping(value = "/home")
    public Object getGuestNews(@PathVariable("reader_id") Long readerId,
        @RequestParam(value = "pageIndex", required = true) String user) {

        return 100;
    }

    @GetMapping(value = "/myHome/{reader_id}")
    public Object getUserNews(@RequestHeader(name = "Authorization") String token,
        @PathVariable(value = "reader_id", required = true) Long readerId,
        @RequestParam(value = "pageIndex", defaultValue = "0", required = false) Integer pageIndex,
        @RequestParam(value = "source", required = false) String source,
        @RequestParam(value = "rubriques", required = false) List<Long> rubriques, HttpServletRequest request) {

        if (!(rubriques == null) && source == null){
            return ResponseEntity.badRequest().body("Veuillez renseigner une source avant de renseigner des rubriques.");
        }

        try {

            Map<String, Object> payload = JWTDecoder.getPayloadJWT(token.substring(7));
            Actualite actualite = this.newsService.getNewsByUserOnContext(payload, readerId, request);
            return this.pagingService.paginateActualite(actualite, pageIndex, PAGE_SIZE, source, rubriques);
        }
        catch (ObjectNotFoundException exception){
            return ResponseEntity.notFound().build();
        }
        catch (Exception ex) {
            return "error";
        }
    }


}
