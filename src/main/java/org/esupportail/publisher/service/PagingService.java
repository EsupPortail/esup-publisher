package org.esupportail.publisher.service;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Data;

import org.esupportail.publisher.web.rest.dto.PaginatedResultDTO;
import org.esupportail.publisher.web.rest.vo.Actualite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

@Data
@Service
public class PagingService {


    public PaginatedResultDTO paginateActualite(Actualite actualite, int pageIndex, int pageSize, String source,
        List<Long> rubriques) {

        if (source != null) {
            actualite.setItems(
                actualite.getItems().stream().filter(itemVO -> itemVO.getSource().equals(source)).collect(
                    Collectors.toList()));
            if (rubriques != null) {
                actualite.setItems(actualite.getItems().stream().filter(
                    itemVO -> itemVO.getRubriques().stream().anyMatch(rubriques::contains)).collect(
                    Collectors.toList()));
            }

            //
            // Étape 1 : Extraire les UUID des rubriques des items
            Set<Long> itemRubriqueUuids = actualite.getItems().stream().flatMap(
                    item -> item.getRubriques().stream()) // Obtenir tous les UUID des rubriques
                .collect(Collectors.toSet());

            // Étape 2 : Filtrer les rubriques
            actualite.setRubriques(actualite.getRubriques().stream().filter(
                rubrique -> itemRubriqueUuids.contains(Long.parseLong(rubrique.getUuid()))).collect(
                Collectors.toList()));
        }

        int totalItems = actualite.getItems().size();
        int start = Math.min(pageIndex * pageSize, totalItems);
        int end = Math.min(start + pageSize, totalItems);
        actualite.setItems(actualite.getItems().subList(start, end));

        return new PaginatedResultDTO(actualite, pageIndex, pageSize, totalItems,
            (int) Math.ceil((double) totalItems / pageSize));
    }

}
