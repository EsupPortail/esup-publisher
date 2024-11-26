package org.esupportail.publisher.web;


import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.repository.*;
import org.esupportail.publisher.repository.externals.ldap.LdapUserDaoImpl;
import org.esupportail.publisher.service.NewsService;
import org.esupportail.publisher.service.PagingService;
import org.esupportail.publisher.service.SubscriberService;
import org.esupportail.publisher.service.TreeGenerationService;
import org.esupportail.publisher.service.factories.ItemVOFactory;
import org.esupportail.publisher.service.factories.RubriqueVOFactory;
import org.esupportail.publisher.service.util.JWTDecoder;
import org.esupportail.publisher.web.rest.vo.Actualite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@RestController
@RequestMapping("/news")
public class NewsController {

    private static final Logger log = LoggerFactory.getLogger(NewsController.class);

    private final int PAGE_SIZE = 100;

    @Inject
    private NewsService newsService;

    @Inject
    private PagingService pagingService;



    @GetMapping(value = "/home")
    public Object getAnonymousNews(@PathVariable("reader_id") Long readerId) throws JsonProcessingException {

        return 100;
    }

    @GetMapping(value = "/myHome/{reader_id}")
    public Object getUserNews(@RequestHeader(name = "Authorization") String token,
                              @PathVariable("reader_id") Long readerId,
                              @RequestParam(value = "pageIndex", defaultValue = "0", required = false) Integer pageIndex,
                              @RequestParam(value = "source", required = false) String source,
                              @RequestParam(value = "rubriques", required = false) List<Long> rubriques,

                              HttpServletRequest request) {

        try {
            Map<String, Object> payload = JWTDecoder.getPayloadJWT(token.substring(7));
            Actualite actualite = this.newsService.getNewsByUserOnContext(payload, readerId, request);
            return this.pagingService.paginateActualite(actualite, pageIndex, PAGE_SIZE, source, rubriques);
        } catch (Exception ex) {
            return "error";
        }
    }


}
