package org.esupportail.publisher.service.bean;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Data
@AllArgsConstructor
public class ServiceUrlHelper {

    private final Logger log = LoggerFactory.getLogger(ServiceUrlHelper.class);

    @NotNull
    @NonNull
    private String contextPath;

    @NotNull
    @NonNull
    private List<String> authorizedDomainNames;

    @NotNull
    @NonNull
    private String protocol = "https://";

    @NotNull
    @NonNull
    private String itemUri;

    /** Used for conf display only */
    private String getItemUrl() {
        return protocol + authorizedDomainNames.get(0) + contextPath + itemUri + "ID";
    }

    public String getRootAppUrl(final HttpServletRequest request) {
        final String contextPath = !request.getContextPath().isEmpty() ? request.getContextPath() + "/" : "/";
        return getRootDomainUrl(request) + contextPath;
    }

    public String getRootDomainUrl(final HttpServletRequest request) {
        final String url = request.getRequestURL().toString();
        final String uri = request.getRequestURI();
        return url.substring(0, url.length() - uri.length());
    }

    @Override
    public String toString() {
        return "ServiceUrlHelper{" +
            "contextPath='" + contextPath + '\'' +
            ", authorizedDomainNames='" + authorizedDomainNames.toString() + '\'' +
            ", protocol='" + protocol + '\'' +
            ", itemUri='" + itemUri + '\'' +
            ", example of itemUrl generated ='" + getItemUrl() + '\'' +
            '}';
    }
}
