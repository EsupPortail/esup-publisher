package org.esupportail.publisher.service.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Data
@AllArgsConstructor
public class ServiceUrlHelper {

    private String contextPath;

    private String domainNameUniversal;

    private String protocol = "https://";

    private String itemUri;

    private String getItemUrl() {
        return protocol + domainNameUniversal + contextPath + itemUri + "ID";
    }

    public String getRootAppUrl() {
        return protocol + domainNameUniversal + contextPath + "/";
    }

    @Override
    public String toString() {
        return "ServiceUrlHelper{" +
            "contextPath='" + contextPath + '\'' +
            ", domainNameUniversal='" + domainNameUniversal + '\'' +
            ", protocol='" + protocol + '\'' +
            ", itemUri='" + itemUri + '\'' +
            ", example of itemUrl generated ='" + getItemUrl() + '\'' +
            '}';
    }
}
