package org.esupportail.publisher.web.rest.vo;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.esupportail.publisher.domain.util.Views;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Data
@JsonTypeName(value = "flashInfo")
public class FlashInfoVO implements Serializable {

    @JsonView(Views.Flash.class)
    private String mediaUrl;

    @JsonView(Views.Flash.class)
    private String title;

    @JsonView(Views.Flash.class)
    private String summary;

    @JsonView(Views.Flash.class)
    private String link;

    @JsonView(Views.Flash.class)
    private List<RubriqueVO> rubriques = new ArrayList<>();

}
