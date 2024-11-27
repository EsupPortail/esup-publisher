package org.esupportail.publisher.domain;


import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esupportail.publisher.web.rest.vo.Actualite;

@Data
@NoArgsConstructor
public class PublisherStructureTree {

    private Publisher publisher;

    private List<Actualite> actualites;

}
