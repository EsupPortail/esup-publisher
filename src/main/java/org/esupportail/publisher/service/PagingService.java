package org.esupportail.publisher.service;


import lombok.Data;
import org.esupportail.publisher.web.rest.vo.Actualite;
import org.esupportail.publisher.web.rest.vo.ItemVO;
import org.esupportail.publisher.web.rest.vo.RubriqueVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Service
public class PagingService {



    public Actualite paginateActualite(Actualite actualite, int page, int size) {
        // Étape 1 : Pagination des items
        int totalItems = actualite.getItems().size();
        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);

        actualite.setItems(actualite.getItems().subList(start, end));









//        List<ItemVO> paginatedItems = actualite.getItems().subList(start, end);
//
//        // Étape 2 : Filtrer les rubriques associées aux items paginés
//        Set<Long> rubriqueIds = paginatedItems.stream()
//            .flatMap(item -> item.getRubriques().stream()) // Supposons une méthode getRubriqueIds() sur ItemVO
//            .collect(Collectors.toSet());
//
//        List<RubriqueVO> filteredRubriques = actualite.getRubriques().stream()
//            .filter(rubrique -> rubriqueIds.contains(rubrique.getUuid()))
//            .collect(Collectors.toList());
//
//        // Étape 3 : Construire le résultat
//        Map<String, Object> result = new HashMap<>();
//        result.put("items", new PageImpl<>(paginatedItems, PageRequest.of(page, size), totalItems));
//        result.put("rubriques", filteredRubriques);

        return actualite;
    }


}
