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
package org.esupportail.publisher.web.rest;

import javax.servlet.http.HttpServletRequest;

import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

	private final Logger log = LoggerFactory.getLogger(AccountResource.class);

	//
	// @Inject
	// private UserRepository userRepository;

//	@Inject
//	private UserService userService;

	// @Inject
	// private PersistentTokenRepository persistentTokenRepository;

	// @Inject
	// private MailService mailService;

	/**
	 * POST /register -> register the user.
	 */
//    @RequestMapping(value = "/register",
//            method = RequestMethod.POST,
//            produces = MediaType.TEXT_PLAIN_VALUE)
//    public ResponseEntity<?> registerAccount(@Valid @RequestBody UserDTO userDTO, HttpServletRequest request) {
//        User user = userRepository.findOneByLogin(userDTO.getLogin());
//        if (user != null) {
//            return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("login already in use");
//        } else {
//            if (userRepository.findOneByEmail(userDTO.getEmail()) != null) {
//                return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("e-mail address already in use");
//            }
//            user = userService.createUserInformation(userDTO.getLogin(), userDTO.getPassword(),
//            userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail().toLowerCase(),
//            userDTO.getLangKey());
//            String baseUrl = request.getScheme() + // "http"
//            "://" +                            // "://"
//            request.getServerName() +          // "myHost"
//            ":" +                              // ":"
//            request.getServerPort();           // "80"
//
//            mailService.sendActivationEmail(user, baseUrl);
//            return new ResponseEntity<>(HttpStatus.CREATED);
//        }
//    }
	/**
	 * GET /activate -> activate the registered user.
	 */
	// @RequestMapping(value = "/activate", method = RequestMethod.GET,
	// produces = MediaType.APPLICATION_JSON_VALUE)
	// public ResponseEntity<String> activateAccount(
	// @RequestParam(value = "key") String key) {
	// User user = userService.activateRegistration(key);
	// if (user == null) {
	// return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	// }
	// return new ResponseEntity<String>(HttpStatus.OK);
	// }

	/**
	 * GET /authenticate -> check if the user is authenticated, and return its login.
	 */
    @RequestMapping(value = "/authenticate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public String isAuthenticated(HttpServletRequest request) {
		log.debug("REST request to check if the current user is authenticated");
		return request.getRemoteUser();
	}

	/**
	 * GET /account -> get the current user.
	 */
    @RequestMapping(value = "/account",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CustomUserDetails> getAccount() {
        CustomUserDetails user = SecurityUtils.getCurrentUserDetails();
		if (user == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// List<String> roles = new ArrayList<>();
		// for (GrantedAuthority authority : user.getAuthorities()) {
		// roles.add(authority.getAuthority());
		// }
		// return new ResponseEntity<>(
         //    new UserDTO(
         //        user.getLogin(),
         //        null,
        //         user.getFirstName(),
        //         user.getLastName(),
        //         user.getEmail(),
        //         user.getLangKey(),
        //         roles),
        //     HttpStatus.OK);
		log.debug("REST request to get UserDetail account {}", user);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	/**
	 * POST /account -> update the current user information.
	 */
		// 	@RequestMapping(value = "/account",
  		//           method = RequestMethod.POST,
  		//           produces = MediaType.APPLICATION_JSON_VALUE)
		//     public ResponseEntity<String> saveAccount(@RequestBody UserDTO userDTO) {
 		//        User userHavingThisLogin = userRepository.findOneByLogin(userDTO.getLogin());
 		//        if (userHavingThisLogin != null && !userHavingThisLogin.getLogin().equals(SecurityUtils.getCurrentLogin())) {
 		//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		//         }
		//         userService.updateUserInformation(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
		//         return new ResponseEntity<>(HttpStatus.OK);
		//     }
	/**
	 * POST /change_password -> changes the current user's password
	 */
	// @RequestMapping(value = "/account/change_password", method =
	// RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	// public ResponseEntity<?> changePassword(@RequestBody String password) {
	// if (StringUtils.isEmpty(password)) {
	// return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	// }
	// userService.changePassword(password);
	// return new ResponseEntity<>(HttpStatus.OK);
	// }

	/**
	 * GET /account/sessions -> get the current open sessions.
	 */
//    @RequestMapping(value = "/account/sessions", method =
//        RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<PersistentToken>> getCurrentSessions() {
//        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentLogin());
//        if (user == null) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity<>(persistentTokenRepository.findByUser(user),
//            HttpStatus.OK);
//    }

	/**
     * DELETE  /account/sessions?series={series} -> invalidate an existing session.
     *
     * - You can only delete your own sessions, not any other user's session
     * - If you delete one of your existing sessions, and that you are currently logged in on that session, you will
     *   still be able to use that session, until you quit your browser: it does not work in real time (there is
     *   no API for that), it only removes the "remember me" cookie
     * - This is also true if you invalidate your current session: you will still be able to use it until you close
     *   your browser or that the session times out. But automatic login (the "remember me" cookie) will not work
     *   anymore.
     *   There is an API to invalidate the current session, but there is no API to check which session uses which
     *   cookie.
     */
//    @RequestMapping(value = "/account/sessions/{series}", method =
//        RequestMethod.DELETE)
//    public void invalidateSession(@PathVariable String series)
//        throws UnsupportedEncodingException {
//        String decodedSeries = URLDecoder.decode(series, "UTF-8");
//        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentLogin());
//        List<PersistentToken> persistentTokens = persistentTokenRepository
//            .findByUser(user);
//        for (PersistentToken persistentToken : persistentTokens) {
//            if (StringUtils.equals(persistentToken.getSeries(), decodedSeries)) {
//                persistentTokenRepository.delete(decodedSeries);
//            }
//        }
//    }
}
