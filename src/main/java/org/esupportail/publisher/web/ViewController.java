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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.Application;
import org.esupportail.publisher.config.SecurityConfiguration;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.Attachment;
import org.esupportail.publisher.domain.Flash;
import org.esupportail.publisher.domain.LinkedFileItem;
import org.esupportail.publisher.domain.Media;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.domain.Resource;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.externals.ExternalUserHelper;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.LinkedFileItemRepository;
import org.esupportail.publisher.repository.SubscriberRepository;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.security.SecurityUtils;
import org.esupportail.publisher.service.FileService;
import org.esupportail.publisher.service.ViewService;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by jgribonvald on 22/02/17.
 */
@Controller
@Slf4j
public class ViewController {

	@Inject
	//private ItemClassificationOrderRepository itemClassificationOrderRepository;
	private ItemRepository<AbstractItem> itemRepository;

	@Inject
	private SubscriberRepository subscriberRepository;

	@Inject
	private ExternalUserHelper externalUserHelper;

	@Inject
	private FileService fileService;

	@Inject
	private LinkedFileItemRepository linkedFileItemRepository;

	@Inject
	private IPermissionService permissionService;

    @Inject
    private ViewService viewService;

	private static final String REDIRECT_PARAM = "local-back-to";

	public static final String ITEM_VIEW = "/view/item/";
	public static final String FILE_VIEW = "/view/file/";

	@RequestMapping(value = SecurityConfiguration.PROTECTED_PATH, produces = MediaType.TEXT_HTML_VALUE)
	public String itemView(HttpServletRequest request) {
		try {
			final String redirect = "redirect:" + getSecurePathRedirect(request);
			log.debug("Redirecting to {}", redirect);
			return redirect;
		} catch (AccessDeniedException ade) {
			return "403";
		}
	}

	@RequestMapping(value = ITEM_VIEW + "{item_id}", produces = MediaType.TEXT_HTML_VALUE)
	@Transactional(readOnly = true)
	public String itemView(Map<String, Object> model, @PathVariable("item_id") Long itemId, HttpServletRequest request) {
		log.debug("Request to render in viewer, item with id {}", itemId);
		if (itemId == null)
			throw new IllegalArgumentException("No item identifier was provided to the request!");
		Optional<AbstractItem> optionnalItem = itemRepository.findOne(ItemPredicates.ItemWithStatus(itemId, ItemStatus.PUBLISHED, null));

		AbstractItem item = optionnalItem.orElse(null);
		if (item == null) {
			return "objectNotFound";
		}
		log.debug("Item found {}", item);


		try {
			if (!viewService.isSubscriber(item)) {
				return "403";
			}
		} catch (AccessDeniedException ade) {
			log.trace("Redirect to establish authentication !");
			return "redirect:" + SecurityConfiguration.PROTECTED_PATH + "?" + REDIRECT_PARAM + "=" + ITEM_VIEW + itemId;
		}

		final String baseUrl = !request.getContextPath().isEmpty() ? request.getContextPath() + "/" : "/";

		// all items has an enclosure but optional
		item.setEnclosure(replaceRelativeUrl(item.getEnclosure(), baseUrl));
		// looking to replace img src with path of object with body attribute of for specific property
		if (item instanceof News) {
			((News) item).setBody(replaceBodyUrl(((News) item).getBody(), baseUrl));
		} else if (item instanceof Flash) {
			((Flash) item).setBody(replaceBodyUrl(((Flash) item).getBody(), baseUrl));
		} else if (item instanceof Resource) {
			((Resource) item).setRessourceUrl(replaceRelativeUrl(((Resource) item).getRessourceUrl(), baseUrl));
		} else if (!(item instanceof Media) && !(item instanceof Attachment)) {
			log.error("Warning a new type of Item wasn't managed at this place, the item is :", item);
			throw new IllegalStateException("Warning missing type management of :" + item.toString());
		}
		model.put("item", item);

		Set<LinkedFileItem> attachments = Sets.newHashSet(linkedFileItemRepository.findByAbstractItemIdAndInBody(
				item.getId(), false));
		model.put("attachments", attachments);

		return "item";
	}

	@RequestMapping(value = FILE_VIEW + "**", method = RequestMethod.GET)
	public void downloadFile(final HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException, IOException {
		final String query = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		log.debug("Entering getting file with query {}", query);
		String filePath = query;
		if (filePath == null || filePath.isEmpty())
			throw new FileNotFoundException(filePath);
		if (filePath.startsWith("/"))
			filePath = filePath.substring(1);
		log.debug("Looking for file in database {}", filePath);
		List<LinkedFileItem> itemsFiles = Lists.newArrayList(linkedFileItemRepository.findByUri(filePath));
		if (itemsFiles.isEmpty()) {
			log.warn("Try to download a file that doesn't exist into the fileSystem", filePath);
			throw new FileNotFoundException(filePath);
		}
		boolean canView = false;
		String filename = null;
		for (LinkedFileItem lfiles : itemsFiles) {
			Optional<AbstractItem> optionalItem = itemRepository.findOne(ItemPredicates.ItemWithStatus(lfiles.getItemId(),
					null, null));
			AbstractItem item = optionalItem.orElse(null);
			// a user can see only when published expect if he can Edit the content
			if (item != null && item.getStatus().equals(ItemStatus.PUBLISHED)) {
				try {
					if (viewService.isSubscriber(item)) {
						canView = true;
						filename = lfiles.getFilename();
						break;
					}
				} catch (AccessDeniedException ade) {
					log.trace("Redirect to establish authentication !");
					response.sendRedirect(request.getContextPath() + SecurityConfiguration.PROTECTED_PATH + "?"
							+ REDIRECT_PARAM + "=" + query);
					return;
				}
			} else if (item != null) {
				if (SecurityUtils.getCurrentUserDetails() == null) {
					log.trace("Redirect to establish authentication !");
					response.sendRedirect(request.getContextPath() + SecurityConfiguration.PROTECTED_PATH + "?"
							+ REDIRECT_PARAM + "=" + query);
					return;
				}

				if ( permissionService.canEditCtx(SecurityContextHolder.getContext().getAuthentication(), item.getContextKey())) {
					canView = true;
					filename = lfiles.getFilename();
					break;
				}
			}
		}

		if (!canView)
			throw new AccessDeniedException("Impossible to get file '" + filePath + "'");

		if (query.startsWith(FILE_VIEW)) {
			filePath = query.substring(FILE_VIEW.length());
		}

		Path file = Paths.get(fileService.getProtectedFileUploadHelper().getUploadDirectoryPath(), filePath);
		if (filename == null || filename.isEmpty())
			filename = file.getFileName().toString();
		log.debug("Retrieving file {} in path {}, contentType {}", filename, file, Files.probeContentType(file));
		if (Files.exists(file) && !Files.isDirectory(file)) {
			response.setContentType(Files.probeContentType(file));
			response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
			response.addHeader("Content-Disposition", "attachement; filename=\"" + filename + "\"");
			response.setContentLengthLong(Files.size(file));

			// copy it to response's OutputStream
			try {
				Files.copy(file, response.getOutputStream());
				response.flushBuffer();
			} catch (FileNotFoundException fnfe) {
				log.warn("Try to download a file that doesn't exist into the fileSystem", fnfe);
				throw new FileNotFoundException(filePath);
			} catch (IOException ex) {
				log.info("Error writing file to output stream. Filename was '{}'", filePath, ex);
				throw ex;
			}
		} else
			throw new FileNotFoundException(filePath);
	}

	@ModelAttribute("version")
	public String getVersion() throws IOException {
		final Properties properties = new Properties();
		properties.load(Application.class.getResourceAsStream("/version.properties"));
		return properties.getProperty("version");
	}

	private String replaceBodyUrl(final String body, final String baseUrl) {
		if (body != null && !body.trim().isEmpty()) {
			String fileview = FILE_VIEW;
			if (fileview.startsWith("/"))
				fileview = fileview.substring(1);
			return body.replaceAll("src=\"files/", "src=\"" + baseUrl + "files/").replaceAll("href=\"" + fileview,
					"href=\"" + baseUrl + fileview);
		}
		return body;
	}

	private String replaceRelativeUrl(final String localUrl, final String baseUrl) {
		if (localUrl != null && !localUrl.trim().isEmpty() && !localUrl.matches("^https?://.*$")) {
			return baseUrl + localUrl;
		}
		return localUrl;
	}

	private String getSecurePathRedirect(final HttpServletRequest request) throws AccessDeniedException {
		String path = request.getQueryString();
		final String param = REDIRECT_PARAM + "=";
		if (path.startsWith(param)) {
			path = request.getQueryString().substring(param.length());
			if (path.startsWith(ITEM_VIEW) || path.startsWith(FILE_VIEW)) {
				return path;
			}
		}
		log.warn("The security was tested with a wrong request query '{}'", request.getQueryString());
		throw new AccessDeniedException("The security was tested with a wrong request query '"
				+ request.getQueryString());
	}

	@ExceptionHandler(FileNotFoundException.class)
	public ModelAndView handleAllExceptionFNFE(Exception ex) {
		return new ModelAndView("fileNotFound");
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ModelAndView handleAllExceptionADE(Exception ex) {
		return new ModelAndView("403");
	}
}
