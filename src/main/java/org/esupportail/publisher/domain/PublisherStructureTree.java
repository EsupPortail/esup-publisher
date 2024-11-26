package org.esupportail.publisher.domain;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.esupportail.publisher.web.rest.vo.Actualite;

import java.util.List;

@Data
@NoArgsConstructor
public class PublisherStructureTree {

    private Publisher publisher;

    private List<Actualite> actualites;

}
