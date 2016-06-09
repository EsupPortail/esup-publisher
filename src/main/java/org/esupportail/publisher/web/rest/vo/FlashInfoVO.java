package org.esupportail.publisher.web.rest.vo;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Data
@JsonTypeName(value = "flashInfo")
public class FlashInfoVO implements Serializable {

    private String mediaUrl;

    private String title;

    private String summary;

    private String link;

}
