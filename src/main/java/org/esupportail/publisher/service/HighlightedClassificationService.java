package org.esupportail.publisher.service;

import javax.annotation.PostConstruct;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.service.bean.HighlightedClassification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Created by jgribonvald on 12/04/17.
 */
@Data
@Service
@Slf4j
public class HighlightedClassificationService {

    @Value("${app.service.highlightClassification.name}")
    private String name;

    @Value("${app.service.highlightClassification.description}")
    private String description;

    @Value("${app.service.highlightClassification.color}")
    private String color;

    private HighlightedClassification classification;

    @PostConstruct
    public void init() {
        Assert.notNull(name);
        Assert.notNull(description);
        Assert.notNull(color);
        classification = new HighlightedClassification(name, description, color);
        log.debug("Contructed {}", classification);

        Assert.notNull(classification);
    }

}
