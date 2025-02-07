package org.esupportail.publisher.web.rest.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualiteExtended extends Actualite {

    private List<String> sources = new ArrayList<>();

}
