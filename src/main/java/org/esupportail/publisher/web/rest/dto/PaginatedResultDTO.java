package org.esupportail.publisher.web.rest.dto;

import org.esupportail.publisher.web.rest.vo.Actualite;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaginatedResultDTO {
    private Actualite actualite;      // Les éléments de la page actuelle
    private int pageIndex;      // Le numéro de la page actuelle (commence à 0)
    private int pageSize;       // Le nombre d'éléments par page
    private int totalItems;    // Nombre total d'éléments (avant pagination)
    private int totalPages;     // Nombre total de pages
}
