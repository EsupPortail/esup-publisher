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

    @NotNull
    @NonNull
    private String myNewsUri;

    public static final String ITEM_VIEW = "/view/item/";
    public static final String FILE_VIEW = "/view/file/";

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

    public String replaceBodyUrl(final String body, final HttpServletRequest request) {
        final String baseUrl = !request.getContextPath().isEmpty() ? request.getContextPath() + "/" : "/";
        if (body != null && !body.trim().isEmpty()) {
            String fileview = FILE_VIEW;
            fileview = fileview.substring(1);
            return body.replaceAll("src=\"files/", "src=\"" + baseUrl + "files/").replaceAll("href=\"" + fileview,
                "href=\"" + baseUrl + fileview).replaceAll("href=\"files/", "href=\"" + baseUrl + "files/");
        }
        return body;
    }

    public String replaceRelativeUrl(final String localUrl, final HttpServletRequest request) {
        final String baseUrl = !request.getContextPath().isEmpty() ? request.getContextPath() + "/" : "/";
        if (localUrl != null && !localUrl.trim().isEmpty() && !localUrl.matches("^https?://.*$")) {
            return baseUrl + localUrl;
        }
        return localUrl;
    }

    @Override
    public String toString() {
        return "ServiceUrlHelper{" +
            "contextPath='" + contextPath + '\'' +
            ", authorizedDomainNames='" + authorizedDomainNames.toString() + '\'' +
            ", protocol='" + protocol + '\'' +
            ", itemUri='" + itemUri + '\'' +
            ", myNewsUri='" + myNewsUri + '\'' +
            ", example of itemUrl generated ='" + getItemUrl() + '\'' +
            '}';
    }
}
