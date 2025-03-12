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
