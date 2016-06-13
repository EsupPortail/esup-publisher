package org.esupportail.publisher.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mysema.query.types.Predicate;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.domain.util.ItemList;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.security.SecurityConstants;
import org.esupportail.publisher.service.ContentService;
import org.esupportail.publisher.service.FileService;
import org.esupportail.publisher.web.rest.dto.ActionDTO;
import org.esupportail.publisher.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * REST controller for managing Evaluator.
 */
@RestController
@RequestMapping("/api")
public class ItemResource {

    private final Logger log = LoggerFactory.getLogger(ItemResource.class);

    @Inject
    private ItemRepository<AbstractItem> itemRepository;

    @Inject
    private IPermissionService permissionService;

    @Inject
    private ContentService contentService;

    @Inject
    private FileService fileService;

    /**
     * POST  /items -> Create a new item.
     */
    @RequestMapping(value = "/items",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    @Timed
    public ResponseEntity<Void> create(@RequestBody AbstractItem item) throws URISyntaxException {
        log.debug("REST request to save AbstractItem : {}", item);
        if (item.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new items cannot already have an ID").build();
        }
        itemRepository.save(item);
        return ResponseEntity.created(new URI("/api/items/" + item.getId())).build();
    }

    /**
     * PUT  /items -> Updates an existing item.
     */
    @RequestMapping(value = "/items",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && @permissionService.canEditCtx(authentication, #item.contextKey)")
    @Timed
    public ResponseEntity<Void> update(@RequestBody AbstractItem item) throws URISyntaxException {
        log.debug("REST request to update Item : {}", item);

        PermissionType permType = permissionService.getRoleOfUserInContext(SecurityContextHolder.getContext().getAuthentication(), item.getContextKey());
        if (permType == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        if (item.getId() == null && PermissionType.ADMIN.equals(permType)) {
            return create(item);
        } else if (item.getId() == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (PermissionType.CONTRIBUTOR.equals(permType)) {
            item.setValidatedBy(null);
            item.setValidatedDate(null);
            item.setStatus(ItemStatus.PENDING);
        }
        itemRepository.save(item);
        return ResponseEntity.ok().build();
    }

    /**
     * PUT  /items/action -> Updates an existing item.
     */
    @RequestMapping(value = "/items/action",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && @permissionService.canEditCtx(authentication, #action.objectId, '" +  SecurityConstants.CTX_ITEM + "')")
    @Timed
    public ResponseEntity<Void> update(@RequestBody ActionDTO action) throws URISyntaxException {
        log.debug("REST request to update Item with action : {}", action);
        AbstractItem item = itemRepository.findOne(action.getObjectId());
        switch(action.getAttribute()) {
            case "enclosure" :
                item.setEnclosure(action.getValue());
                break;
            case "validate" :
                return contentService.setValidationItem(Boolean.parseBoolean(action.getValue()), item);
            default : return ResponseEntity.badRequest().build();
        }
        itemRepository.save(item);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /items -> get all the items.
     */
    @RequestMapping(value = "/items",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_USER)
    @Timed
    public ResponseEntity<ItemList> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                           @RequestParam(value = "per_page", required = false) Integer limit,
                                           @RequestParam(value = "displayOrder", required = false) DisplayOrderType displayOrder,
                                           @RequestParam(value = "owned", required = false) Boolean owned,
                                           @RequestParam(value = "item_status", required = false) Integer itemStatus)
        throws URISyntaxException {
        Sort sort = new QSort(ItemPredicates.orderByItemDefinition(DisplayOrderType.START_DATE));
        if (displayOrder != null) {
            sort = new QSort(ItemPredicates.orderByClassifDefinition(displayOrder));
        }
        Predicate filter = permissionService.filterAuthorizedAllOfContextType(SecurityContextHolder.getContext().getAuthentication(),
            ContextType.ITEM, PermissionType.LOOKOVER, ItemPredicates.OwnedItemsOfStatus(owned, itemStatus));
        log.debug("Filter applied to obtain all items : {}", filter.toString());
        Page<AbstractItem> page = itemRepository.findAll(filter, PaginationUtil.generatePageRequest(offset, limit, sort));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/items", offset, limit);
        return new ResponseEntity<ItemList>(new ItemList(page.getContent()), headers, HttpStatus.OK);
    }

    /**
     * GET  /items/:id -> get the "id" item.
     */
    @RequestMapping(value = "/items/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && hasPermission(#id,  '" + SecurityConstants.CTX_ITEM + "', '" + SecurityConstants.PERM_CONTRIBUTOR + "')")
    @Timed
    public ResponseEntity<AbstractItem> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Item : {}", id);
        AbstractItem item = itemRepository.findOne(id);
        if (item == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    /**
     * DELETE  /items/:id -> delete the "id" item.
     */
    @RequestMapping(value = "/items/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && @permissionService.canDeleteCtx(authentication, #id, '" +  SecurityConstants.CTX_ITEM + "')")
    //+ " && (hasPermission(#item, '" + SecurityConstants.PERM_EDITOR + "') || #item.createdBy.login.equals(principal.username))")
    //+ " && hasPermission(#id,  '" + SecurityConstants.CTX_ITEM + "', '" + SecurityConstants.PERM_EDITOR + "')")
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Item : {}", id);
        final AbstractItem item = itemRepository.findOne(id);
        fileService.deleteInternalResource(item.getEnclosure());
        itemRepository.delete(id);
    }
}
