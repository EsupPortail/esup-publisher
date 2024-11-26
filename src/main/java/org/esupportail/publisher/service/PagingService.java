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


    public Actualite paginateActualite(Actualite actualite, int page, int size, String source, List<Long> rubriques) {

        if (source != null) {
            actualite.setItems(actualite.getItems().stream().filter(itemVO -> itemVO.getSource().equals(source)).collect(Collectors.toList()));
            if (rubriques != null) {
                actualite.setItems(actualite.getItems().stream().filter(itemVO -> itemVO.getRubriques().stream().anyMatch(rubriques::contains)).collect(Collectors.toList()));
            }
        }
        int totalItems = actualite.getItems().size();
        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);
        actualite.setItems(actualite.getItems().subList(start, end));

        return actualite;
    }


}
