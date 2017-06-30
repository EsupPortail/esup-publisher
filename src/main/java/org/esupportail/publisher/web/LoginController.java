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
package org.esupportail.publisher.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.SecurityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
@Slf4j
public class LoginController {

    @RequestMapping("/login")
    public Object login(Model model, HttpServletRequest request) throws IOException {

        log.debug("=========> Login Call ");

        /*String then = request.getParameter("then");
        if (then != null) {
            log.debug("Going on redirect");
            // then = URLDecoder.decode(then, "UTF-8");
            String url = then;
            log.debug("url : {}", url);
            return "redirect:" + URI.create(url);
        }*/
        // retrieve the user
        CustomUserDetails user = SecurityUtils.getCurrentUserDetails();
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        log.debug("UserDetails {}", user);
        //log.debug("#########   RedirectURL is present ? {}", getRedirectUrl(request));
        String uri = request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        log.debug("#########   Requested url {}", uri);

        String jsUser = new ObjectMapper().writeValueAsString(user);
        String content, type;
        if (request.getParameter("postMessage") != null) {
            log.debug("Going on postMessage");
            type = "text/html";
            content = "Login success, please wait...\n<script>\n (window.opener ? (window.opener.postMessage ? window.opener : window.opener.document) : window.parent).postMessage('loggedUser=' + JSON.stringify("
                + jsUser + "), '*');\n</script>";
        } else if (request.getParameter("callback") != null) {
            log.debug("Going on callback");
            type = "application/x-javascript";
            content = request.getParameter("callback") + "(" + jsUser + ")";
        } else {
            log.debug("Going on else");
            type = "application/json";
            content = jsUser;
        }
        log.debug("content : {}", content);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.valueOf(type));
        return new ResponseEntity<String>(content, responseHeaders, HttpStatus.OK);
    }

    // @RequestMapping("/j_spring_cas_security_logout")
    // public String spring_logout() {
    // return "redirect:/logout";
    // }

    // @RequestMapping("/logout")
    // public String logout() {
    // return "redirect:/";
    // }

//    @RequestMapping(value = { "/", "" }, method = RequestMethod.GET)
//    public String index(Model model) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String user = auth.getName();
//        log.info("passing in /");
//        model.addAttribute("user", user);
//
//        // renders /WEB-INF/jsp/index.jsp
//        return "index";
//        // return "Hello World! (user: " + user + ")";
//    }
//
//    @RequestMapping(value = "/secure", method = RequestMethod.GET)
//    public String secure(Model model) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String user = auth.getName();
//        log.info("passing in /");
//        model.addAttribute("user", user);
//
//        // renders /WEB-INF/jsp/index.jsp
//        return "secure/index";
//        // return "Hello World! (user: " + user + ")";
//    }
//
//    @RequestMapping(value = "/filtered", method = RequestMethod.GET)
//    public String filtered(Model model) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String user = auth.getName();
//        log.info("passing in /");
//        model.addAttribute("user", user);
//
//        // renders /WEB-INF/jsp/index.jsp
//        return "secure/admin/index";
//        // return "Hello World! (user: " + user + ")";
//    }

//    protected String getRedirectUrl(HttpServletRequest request) {
//        HttpSession session = request.getSession(false);
//        if (session != null) {
//            SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, null);
//            if (savedRequest != null) {
//                return savedRequest.getRedirectUrl();
//            }
//        }
//
//		/* return a sane default in case data isn't there */
//        return request.getContextPath() + "/";
//    }
}
