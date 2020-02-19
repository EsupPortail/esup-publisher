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
package org.esupportail.publisher.security;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mysema.commons.lang.Pair;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.AbstractFeed;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.Category;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.ItemClassificationOrder;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.PermissionOnContext;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.repository.CategoryRepository;
import org.esupportail.publisher.repository.FeedRepository;
import org.esupportail.publisher.repository.IPermissionRepositorySelector;
import org.esupportail.publisher.repository.ItemClassificationOrderRepository;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.OrganizationRepository;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.repository.predicates.ClassificationPredicates;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.repository.predicates.PermissionPredicates;
import org.esupportail.publisher.repository.predicates.PublisherPredicates;
import org.esupportail.publisher.service.bean.OwnerContextKey;
import org.esupportail.publisher.service.bean.UserContextTree;
import org.esupportail.publisher.service.evaluators.IEvaluationFactory;
import org.esupportail.publisher.service.factories.PermissionDTOSelectorFactory;
import org.esupportail.publisher.web.rest.dto.PermOnCtxDTO;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Complex bean to obtains objects Organization, Publisher, Category, Internal/ExternalFeed, Item where the authenticated user has a permission.
 * We should have permission type of PermissionOnContext only for Organization and Publisher. Publisher will give the type of permission for after.
 * Warning permission of type OnSubjects can be on publisher, but they won't be applied
 */
@Slf4j
@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class UserContextLoaderServiceImpl implements UserContextLoaderService {

	@Inject
	private IPermissionRepositorySelector permissionDao;

	@Inject
	private IEvaluationFactory evaluationFactory;

	@Inject
	private PermissionDTOSelectorFactory permissionDTOFactory;

	@Inject
	public UserContextTree userSessionTree;

	@Inject
	private OrganizationRepository organizationDao;
	@Inject
	private PublisherRepository publisherDao;
	@Inject
	private CategoryRepository categoryDao;
	@Inject
	private FeedRepository<AbstractFeed> feedDao;
	@Inject
	private ItemClassificationOrderRepository itemClassifDao;
	@Inject
	private ItemRepository<AbstractItem> itemDao;

	private static EnumSet<PermissionClass> contextPermsType = EnumSet
			.of(PermissionClass.CONTEXT, PermissionClass.CONTEXT_WITH_SUBJECTS);

	public void loadUserTree(Authentication authentication) {
		loadUserTree(((CustomUserDetails) authentication.getPrincipal()).getUser(),
				((CustomUserDetails) authentication.getPrincipal()).getAuthorities());
	}

	public synchronized void loadUserTree(final UserDTO user, final Collection<? extends GrantedAuthority> authorities) {
		// init userTree
        if (!userSessionTree.loadingCanBeDone()) {
            return;
        }
        if (userSessionTree.isTreeLoadInProgress()) {
            // we should wait that the loading is done
            long totalSleep = 0;
            boolean isInterrupted = false;
            log.debug("watching loading : {}", userSessionTree.toString());
            while (userSessionTree.isTreeLoadInProgress() && totalSleep < 10000) {
                try {
                    log.debug("waiting loading : {}", userSessionTree.toString());
                    Thread.sleep(300);
                    totalSleep += 300;
                } catch (InterruptedException e) {
                    isInterrupted = true;
                }
            }
            if (totalSleep > 10000) {
                isInterrupted = true;
            }

            if (isInterrupted) {
                throw new IllegalStateException("The tree loader was interrupted for the user " + user.toString());
            }
            return;
        }
        log.warn("========================= WARNING loadingUserTree ========================");
		userSessionTree.processingLoading();
		if (authorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN))) {
			userSessionTree.setSuperAdmin(true);
		} else if (authorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.USER))) {
			userSessionTree.setSuperAdmin(false);
			log.debug("Call loadUserTree for USER access !");
			// Load list of organizations

			@SuppressWarnings("unchecked")
			final List<PermissionOnContext> perms = Lists.newArrayList(permissionDao.getPermissionDao(
					PermissionClass.CONTEXT).findAll(
					PermissionPredicates.OnCtxType(ContextType.ORGANIZATION, PermissionClass.CONTEXT, false)));
			Map<ContextKey, PermissionOnContext> ctxRoles = Maps.newHashMap();
			// Evaluate perms on all Organizations, all users should have a role on the organization to access on it
			for (PermissionOnContext perm : perms) {
				if (evaluationFactory.from(perm.getEvaluator()).isApplicable(user)
						&& perm.getRole().getMask() >= PermissionType.LOOKOVER.getMask()) {
					if (log.isDebugEnabled()) {
						log.debug("TreeLoader should add {}", perm.getContext());
					}
					if (ctxRoles.containsKey(perm.getContext())) {
						PermissionOnContext role = ctxRoles.get(perm.getContext());
						if (role == null || perm.getRole().getMask() > role.getRole().getMask()) {
							ctxRoles.put(perm.getContext(), perm);
						}
					} else {
						ctxRoles.put(perm.getContext(), perm);
					}
				}
			}

			// now we can go on childs
			for (Map.Entry<ContextKey, PermissionOnContext> ctx : ctxRoles.entrySet()) {
				if (ctx.getValue() != null && PermissionType.MANAGER.getMask() <= ctx.getValue().getRole().getMask()) {
					final PermOnCtxDTO permDTO = (PermOnCtxDTO) permissionDTOFactory.from(ctx.getValue());
					userSessionTree.addCtx(ctx.getKey(), false, null, null, permDTO);
					loadAuthorizedOrganizationChilds(user, ctx.getKey(), false, new Pair<PermissionType, PermOnCtxDTO>(
							ctx.getValue().getRole(), permDTO));
				} else {
					final PermOnCtxDTO permDTO = (PermOnCtxDTO) permissionDTOFactory.from(ctx.getValue());
					loadAuthorizedOrganizationChilds(user, ctx.getKey(), true, new Pair<PermissionType, PermOnCtxDTO>(
							ctx.getValue().getRole(), permDTO));
				}
			}
		} else {
			userSessionTree.setSuperAdmin(false);
		}
		userSessionTree.notifyEndLoading();
		if (log.isDebugEnabled()) {
			log.debug("Tree loaded : {}", userSessionTree.toString());
		}
	}

	// add publishers
	private void loadAuthorizedOrganizationChilds(final UserDTO user, final ContextKey organizationCtx,
			final boolean checkPerms, final Pair<PermissionType, ? extends PermOnCtxDTO> parentPerm) {
		log.debug("Call loadAuthorizedOrganizationChilds {},{}", organizationCtx, checkPerms);
		Optional<Organization> optionalOrganization = organizationDao.findById(organizationCtx.getKeyId());
		Organization organization = optionalOrganization.orElse(null);
		//final PermissionType parentPerm = userSessionTree.getRoleFromContextTree(organizationCtx);
		// in this case only Perm could be LOOKOVER
		Assert.isTrue(parentPerm != null && parentPerm.getFirst() != null && parentPerm.getSecond() != null
				&& parentPerm.getFirst().getMask() >= PermissionType.LOOKOVER.getMask(),
				"This method is called whereas it's not allowed in these conditions");

        Assert.notNull(organization, "Error the linked organization with id " + organizationCtx.getKeyId() + " should exist !");
        final List<Publisher> publishers = Lists.newArrayList(publisherDao.findAll(PublisherPredicates
				.AllOfOrganization(organization)));
        Map<ContextKey, PermOnCtxDTO> ctxRoles = Maps.newHashMap();
        //Map<ContextKey, Publisher> ctxPubInfos = Maps.newHashMap();
        Map<PermissionClass, List<ContextKey>> pubsCtx = Maps.newHashMap();
        for (Publisher pub : publishers) {
            if (!checkPerms) {
                ctxRoles.put(pub.getContextKey(), parentPerm.getSecond());
            } else {
                PermOnCtxDTO perm = null;
                if (parentPerm.getFirst().getMask() > PermissionType.LOOKOVER.getMask()) {
                    // we keep at least all childs with parent perm
                    perm = parentPerm.getSecond();
                }
                if (pubsCtx.containsKey(pub.getPermissionType())) {
                    pubsCtx.get(pub.getPermissionType()).add(pub.getContextKey());
                } else {
                    pubsCtx.put(pub.getPermissionType(), Lists.newArrayList(pub.getContextKey()));
                }
                // we keep at least all childs with parent perm
                ctxRoles.put(pub.getContextKey(), perm);
            }

            //ctxPubInfos.put(pub.getContextKey(), pub);
            // managed type should be checked before to obtain permission of a type managed
            if (!contextPermsType.contains(pub.getPermissionType())) {
                log.error(String.format("Permission of type %s not yet managed in publisher %s",
                    pub.getPermissionType(), pub));
                throw new IllegalStateException(String.format("Permission of type %s not yet managed",
						pub.getPermissionType()));
            }
        }

		if (checkPerms) {
            List<? extends PermissionOnContext> perms = Lists.newArrayList();
            for (Map.Entry<PermissionClass, List<ContextKey>> ctx : pubsCtx.entrySet()) {
                log.debug("foreach : for permClass {} the publishers ctx are {}", ctx.getKey(), ctx.getValue());
                perms.addAll(Lists.newArrayList(permissionDao.getPermissionDao(ctx.getKey())
                    .findAll(PermissionPredicates.OnCtx(ctx.getValue(), ctx.getKey(), false))));
            }
			// we need to evaluate all permissions of a publisher to get the
			// greater possible permission before to continue
			// there can be no permission defined so we kept all ctx in ctxRoles in case with parent perm
			for (PermissionOnContext perm : perms) {
				if (evaluationFactory.from(perm.getEvaluator()).isApplicable(user)
						&& perm.getRole().getMask() > PermissionType.LOOKOVER.getMask()) {
					if (log.isDebugEnabled()) {
						log.debug("TreeLoader should add {}", perm.getContext());
					}
					if (ctxRoles.containsKey(perm.getContext())) {
                        PermOnCtxDTO role = ctxRoles.get(perm.getContext());
						if (role == null || perm.getRole().getMask() > role.getRole().getMask()) {
							ctxRoles.put(perm.getContext(), (PermOnCtxDTO)permissionDTOFactory.from(perm));
						}
					} else {
						// must not come here
						log.warn("ContextKey " + perm.getContext()
								+ " wasn't added to possible publishers where childs has a role");
						ctxRoles.put(perm.getContext(), (PermOnCtxDTO)permissionDTOFactory.from(perm));
					}
				} else if (!ctxRoles.containsKey(perm.getContext())) {
					// must not come here
					log.warn("ContextKey " + perm.getContext()
							+ " wasn't added to possible publishers where childs has a role");
					ctxRoles.put(perm.getContext(), null);
				}
			}
		}
		// now we can go on childs
		if (!ctxRoles.isEmpty()) {
			for (Map.Entry<ContextKey, PermOnCtxDTO> ctx : ctxRoles.entrySet()) {
                final PermOnCtxDTO perm = ctx.getValue();
                if (!checkPerms) {
                    userSessionTree.addCtx(ctx.getKey(), false, organizationCtx, null, null);
                    loadAuthorizedPublisherChilds(user, ctx.getKey(), false, parentPerm);
                } else if (perm != null
                    && PermissionType.MANAGER.getMask() <= perm.getRole().getMask()) {
                    userSessionTree.addCtx(organizationCtx, false, null, null, null, parentPerm.getFirst());// it doesn't add organisation if already loaded, when perm > LOOKOVER
                    userSessionTree.addCtx(ctx.getKey(), false, organizationCtx, null, perm);
                    loadAuthorizedPublisherChilds(user, ctx.getKey(), false, new Pair<>(perm.getRole(), perm));
                } else if (ctx.getValue() != null) {
                    // if != null perm is more than lookover
                    userSessionTree.addCtx(organizationCtx, false, null, null, null, parentPerm.getFirst());// it doesn't add organisation if already loaded, when perm > LOOKOVER
                    userSessionTree.addCtx(ctx.getKey(), false, organizationCtx, null, perm);
                    loadAuthorizedPublisherChilds(user, ctx.getKey(), true, new Pair<>(perm.getRole(), perm));
                } else {
                    // we need to find a child with perm > LOOKOVER
                    findAuthorizedPublisherChilds(user, ctx.getKey());
                }
			}
		}
		// case of no rights upper than lookover are given on publishers - eg a user should have a role of USER or UPPER on publishers
		/*else {

		    for (Map.Entry<ContextKey, Publisher> ctx : ctxPubInfos.entrySet()) {
		        findAuthorizedPublisherChilds(user, ctx.getKey());
		    }
		}*/
		// now we load Items saved without classification owned by the user
		loadOwnedItemsWithoutClassif(user, organization.getContextKey());
	}

	/**
	 * This method is called when a user have at least a perm > LOOKOVER
	 * @param user
	 * @param publisherCtx
	 * @param checkPerms
	 */
	private void loadAuthorizedPublisherChilds(final UserDTO user, final ContextKey publisherCtx,
			final boolean checkPerms, final Pair<PermissionType, ? extends PermOnCtxDTO> parentPerm) {
		log.debug("Call loadAuthorizedPublisherChilds {},{}", publisherCtx, checkPerms);
		Optional<Publisher> optionalPublisher = publisherDao.findById(publisherCtx.getKeyId());
		Publisher publisher = optionalPublisher.orElse(null);
		//final PermissionType parentPerm = userSessionTree.getRoleFromContextTree(publisherCtx);
		Assert.isTrue(parentPerm != null && parentPerm.getFirst() != null && parentPerm.getSecond() != null
				&& parentPerm.getFirst().getMask() > PermissionType.LOOKOVER.getMask(),
				"This method is called whereas it's not allowed in these conditions");
        Assert.notNull(publisher, "Error the linked publisher with id " + publisherCtx.getKeyId() + " should exist !");
		if (contextPermsType.contains(publisher.getPermissionType())) {

			final List<Category> categories = Lists.newArrayList(categoryDao.findAll(ClassificationPredicates
					.CategoryOfPublisher(publisher.getId())));
			Map<ContextKey, PermOnCtxDTO> ctxRoles = Maps.newHashMap();
			List<ContextKey> catsCtx = Lists.newArrayList();
			for (Category cat : categories) {
				if (!checkPerms) {
					ctxRoles.put(cat.getContextKey(), null);
				} else {
					catsCtx.add(cat.getContextKey());
					//if (parentPerm != null && parentPerm.getMask() > PermissionType.LOOKOVER.getMask()) {
					// we keep at least all childs with parent perm
					ctxRoles.put(cat.getContextKey(), parentPerm.getSecond());
					// }
				}
			}

			// shortcut to avoid to evaluate all perms, check if upper perm can be found on childs
			if (checkPerms) {
				final List<? extends PermissionOnContext> perms = Lists.newArrayList(permissionDao.getPermissionDao(
						publisher.getPermissionType()).findAll(
						PermissionPredicates.OnCtx(catsCtx, publisher.getPermissionType(), false)));

				// we need to evaluate all permissions of a publisher to get the
				// greater possible permission before to continue
				// there can be no permission defined so we kept all ctx in ctxRoles in case with parent perm
				for (PermissionOnContext perm : perms) {
					if (evaluationFactory.from(perm.getEvaluator()).isApplicable(user)
							&& perm.getRole().getMask() > PermissionType.LOOKOVER.getMask()) {
						if (log.isDebugEnabled()) {
							log.debug("TreeLoader should add {}", perm.getContext());
						}
						if (ctxRoles.containsKey(perm.getContext())) {
							PermOnCtxDTO role = (PermOnCtxDTO) ctxRoles.get(perm.getContext());
							if (role == null || perm.getRole().getMask() > role.getRole().getMask()) {
								ctxRoles.put(perm.getContext(), (PermOnCtxDTO) permissionDTOFactory.from(perm));

							}
						} else {
							// must not come here
							log.warn("ContextKey " + perm.getContext()
									+ " wasn't added to possible publishers where childs has a role");
							ctxRoles.put(perm.getContext(), (PermOnCtxDTO) permissionDTOFactory.from(perm));
						}
					} else if (!ctxRoles.containsKey(perm.getContext())) {
						// must not come here
						log.warn("ContextKey " + perm.getContext()
								+ " wasn't added to possible publishers where childs has a role");
						ctxRoles.put(perm.getContext(), parentPerm.getSecond());
					}
				}
			}
			/* equivalent à la suite
			    final boolean isLastNode = WritingMode.TARGETS_ON_ITEM.equals(publisher.getContext().getRedactor().getWritingMode());
			 */
			final boolean hasFeeds = publisher.getContext().getRedactor().getNbLevelsOfClassification() > 1;
			// now we can go on childs
			for (Map.Entry<ContextKey, PermOnCtxDTO> ctx : ctxRoles.entrySet()) {
				if (!checkPerms) {
					userSessionTree.addCtx(ctx.getKey(), !hasFeeds, publisherCtx, null, null);
					loadAuthorizedCategoryChilds(user, ctx.getKey(), false, publisher.getPermissionType(), parentPerm);
				} else if (!publisher.isHasSubPermsManagement()) {
					userSessionTree.addCtx(ctx.getKey(), !hasFeeds, publisherCtx, null, ctx.getValue());
					loadAuthorizedCategoryChilds(user, ctx.getKey(), false, publisher.getPermissionType(),
							new Pair<PermissionType, PermOnCtxDTO>(ctx.getValue().getRole(), ctx.getValue()));
				} else if (ctx.getValue() != null
						&& PermissionType.MANAGER.getMask() <= ctx.getValue().getRole().getMask()) {
					userSessionTree.addCtx(ctx.getKey(), !hasFeeds, publisherCtx, null, ctx.getValue());
					loadAuthorizedCategoryChilds(user, ctx.getKey(), false, publisher.getPermissionType(),
							new Pair<PermissionType, PermOnCtxDTO>(ctx.getValue().getRole(), ctx.getValue()));
				} else if (ctx.getValue() != null) {
					//perm parent is > LOOKOVER so load and not find
					userSessionTree.addCtx(ctx.getKey(), !hasFeeds, publisherCtx, null, ctx.getValue());
					loadAuthorizedCategoryChilds(user, ctx.getKey(), true, publisher.getPermissionType(),
							new Pair<PermissionType, PermOnCtxDTO>(ctx.getValue().getRole(), ctx.getValue()));
				} else {
					Assert.notNull(ctx.getValue(), "ContextKey " + ctx.getKey()
							+ " wasn't authorized and entering in wrong state !");
				}
			}
		} else {
			log.error(String.format("Permission of type %s not yet managed in publisher %s",
					publisher.getPermissionType(), publisher));
			throw new IllegalStateException(String.format("Permission of type %s not yet managed",
					publisher.getPermissionType()));
		}
	}

	/**
	 * This method is called when a user have at least a perm > LOOKOVER
	 * @param user
	 * @param categoryCtx
	 * @param checkPerms
	 * @param permClass
	 */
	private void loadAuthorizedCategoryChilds(final UserDTO user, final ContextKey categoryCtx,
			final boolean checkPerms, final PermissionClass permClass,
			final Pair<PermissionType, ? extends PermOnCtxDTO> parentPerm) {
		log.debug("Call loadAuthorizedCategoryChilds {},{}, {}", categoryCtx, checkPerms, permClass);
		Assert.isTrue(contextPermsType.contains(permClass),
				String.format("Permission of type %s not yet managed loadAuthorizedCategoryChilds", permClass));
		Optional<Category> optionalCategory = categoryDao.findById(categoryCtx.getKeyId());
		Category category = optionalCategory.orElse(null);
		//final PermissionType parentPerm = userSessionTree.getRoleFromContextTree(categoryCtx);

		Assert.isTrue(parentPerm != null && parentPerm.getFirst() != null && parentPerm.getSecond() != null
				&& parentPerm.getFirst().getMask() > PermissionType.LOOKOVER.getMask(),
				"This method is called whereas it's not allowed in these conditions");

        Assert.notNull(category, "Error the linked category with id " + categoryCtx.getKeyId() + " should exist !");
		final boolean hasFeeds = category.getPublisher().getContext().getRedactor().getNbLevelsOfClassification() > 1;

		if (hasFeeds) {
			final List<AbstractFeed> feeds = Lists.newArrayList(feedDao.findAll(ClassificationPredicates
					.AbstractFeedsOfCategory(category)));

			Map<ContextKey, PermOnCtxDTO> ctxRoles = Maps.newHashMap();
			List<ContextKey> feedsCtx = Lists.newArrayList();
			for (AbstractFeed feed : feeds) {
				if (!checkPerms) {
					ctxRoles.put(feed.getContextKey(), null);
				} else {
					feedsCtx.add(feed.getContextKey());
					//if (parentPerm != null && parentPerm.getMask() > PermissionType.LOOKOVER.getMask()) {
					ctxRoles.put(feed.getContextKey(), parentPerm.getSecond());
					// }
				}
			}

			// shortcut to avoid to evaluate all perms
			if (checkPerms) {
				final List<? extends PermissionOnContext> perms = Lists.newArrayList(permissionDao.getPermissionDao(
						permClass).findAll(PermissionPredicates.OnCtx(feedsCtx, permClass, false)));
				// we need to evaluate all permissions of a publisher to get the
				// greater possible permission before to continue
				for (PermissionOnContext perm : perms) {
					if (evaluationFactory.from(perm.getEvaluator()).isApplicable(user)
							&& perm.getRole().getMask() > PermissionType.LOOKOVER.getMask()) {
						if (log.isDebugEnabled()) {
							log.debug("TreeLoader should add {}", perm.getContext());
						}
						if (ctxRoles.containsKey(perm.getContext())) {
							PermOnCtxDTO role = ctxRoles.get(perm.getContext());
							if (role == null || perm.getRole().getMask() > role.getRole().getMask()) {
								ctxRoles.put(perm.getContext(), (PermOnCtxDTO) permissionDTOFactory.from(perm));
							}
						} else {
							// must not come here
							log.warn("ContextKey " + perm.getContext()
									+ " wasn't added to possible publishers where childs has a role");
							ctxRoles.put(perm.getContext(), (PermOnCtxDTO) permissionDTOFactory.from(perm));
						}
					} else if (!ctxRoles.containsKey(perm.getContext())) {
						// must not come here
						log.warn("ContextKey " + perm.getContext()
								+ " wasn't added to possible publishers where childs has a role");
						ctxRoles.put(perm.getContext(), parentPerm.getSecond());
					}
				}
			}

			// now we can go on childs
			for (Map.Entry<ContextKey, PermOnCtxDTO> ctx : ctxRoles.entrySet()) {
				if (!checkPerms) {
					userSessionTree.addCtx(ctx.getKey(), false, categoryCtx, null, null);
				} else {
					userSessionTree.addCtx(ctx.getKey(), false, categoryCtx, null, ctx.getValue());
				}
				loadAllItemsOf(feedDao.findById(ctx.getKey().getKeyId()).get());
			}
		} else {
			loadAllItemsOf(category);
		}
	}

	/**
	 * This method is called only if the user has no perm > LOOKOVER on parents
	 * @param user
	 * @param publisherCtx
	 */
	private void findAuthorizedPublisherChilds(final UserDTO user, final ContextKey publisherCtx) {
		log.debug("Call findAuthorizedPublisherChilds {}", publisherCtx);
		Optional<Publisher> optionalPublisher = publisherDao.findById(publisherCtx.getKeyId());
		Publisher publisher = optionalPublisher.orElse(null);
		// if no subcontext permission management it's useless to find possible rights.
        Assert.notNull(publisher, "Error the linked publisher with id " + publisherCtx.getKeyId() + " should exist !");
		if (!publisher.isHasSubPermsManagement())
			return;

		if (!contextPermsType.contains(publisher.getPermissionType())) {
			log.error(String.format(
					"With Permission of type %s we should not try to find a permission on Publisher childs.",
					publisher.getPermissionType()));
			throw new IllegalStateException(String.format("With Permission of type %s we should not try "
					+ "to find a permission on Publisher childs.", publisher.getPermissionType()));
		}
		final List<Category> categories = Lists.newArrayList(categoryDao.findAll(ClassificationPredicates
				.CategoryOfPublisher(publisher.getId())));
		Map<ContextKey, PermissionOnContext> ctxRoles = Maps.newHashMap();
		List<ContextKey> catsCtx = Lists.newArrayList();
		for (Category cat : categories) {
			catsCtx.add(cat.getContextKey());
			ctxRoles.put(cat.getContextKey(), null);
		}

		final List<? extends PermissionOnContext> perms = Lists.newArrayList(permissionDao.getPermissionDao(
				publisher.getPermissionType()).findAll(
				PermissionPredicates.OnCtx(catsCtx, publisher.getPermissionType(), false)));

		// we need to evaluate all permissions of a publisher to get the
		// greater possible permission before to continue
		for (PermissionOnContext perm : perms) {
			if (evaluationFactory.from(perm.getEvaluator()).isApplicable(user)
					&& perm.getRole().getMask() > PermissionType.LOOKOVER.getMask()) {
				if (log.isDebugEnabled()) {
					log.debug("TreeLoader should add {}", perm.getContext());
				}
				if (ctxRoles.containsKey(perm.getContext())) {
					PermissionOnContext role = ctxRoles.get(perm.getContext());
					if (role == null || perm.getRole().getMask() > role.getRole().getMask()) {
						ctxRoles.put(perm.getContext(), perm);
					}
				} else {
					// must not come here
					log.warn("ContextKey " + perm.getContext()
							+ " wasn't added to possible publishers where childs has a role");
					ctxRoles.put(perm.getContext(), perm);
				}
			} else if (!ctxRoles.containsKey(perm.getContext())) {
				// must not come here
				log.warn("ContextKey " + perm.getContext()
						+ " wasn't added to possible publishers where childs has a role");
				ctxRoles.put(perm.getContext(), null);
			}
		}

		/* equivalent à la suite
		        final boolean isLastNode = WritingMode.TARGETS_ON_ITEM.equals(publisher.getContext().getRedactor().getWritingMode());
		     */
		final boolean hasFeeds = publisher.getContext().getRedactor().getNbLevelsOfClassification() > 1;

		// now we can go on childs
		boolean parentsCtxLoaded = false;
		for (Map.Entry<ContextKey, PermissionOnContext> ctx : ctxRoles.entrySet()) {
			if (ctx.getValue() != null && PermissionType.LOOKOVER.getMask() < ctx.getValue().getRole().getMask()) {
				if (!parentsCtxLoaded) {
					userSessionTree.addCtx(publisher.getContext().getOrganization().getContextKey(), false, null, null,
							null, PermissionType.LOOKOVER);
					userSessionTree.addCtx(publisher.getContextKey(), false, publisher.getContext().getOrganization()
							.getContextKey(), null, null, PermissionType.LOOKOVER);
					parentsCtxLoaded = true;
				}
				if (PermissionType.MANAGER.getMask() <= ctx.getValue().getRole().getMask()) {
					final PermOnCtxDTO permDTO = (PermOnCtxDTO) permissionDTOFactory.from(ctx.getValue());
					userSessionTree.addCtx(ctx.getKey(), !hasFeeds, publisherCtx, null, permDTO);
					loadAuthorizedCategoryChilds(user, ctx.getKey(), false, publisher.getPermissionType(),
							new Pair<PermissionType, PermOnCtxDTO>(ctx.getValue().getRole(), permDTO));
				} else {
					final PermOnCtxDTO permDTO = (PermOnCtxDTO) permissionDTOFactory.from(ctx.getValue());
					userSessionTree.addCtx(ctx.getKey(), !hasFeeds, publisherCtx, null, permDTO);
					loadAuthorizedCategoryChilds(user, ctx.getKey(), true, publisher.getPermissionType(),
							new Pair<PermissionType, PermOnCtxDTO>(ctx.getValue().getRole(), permDTO));
				}
			} else {
				Map<ContextKey, ? extends PermissionOnContext> founds = findAuthorizedCategoryChilds(user,
						ctx.getKey(), publisher.getPermissionType());
				if (!founds.isEmpty()) {
					userSessionTree.addCtx(publisher.getContext().getOrganization().getContextKey(), false, null, null,
							null, PermissionType.LOOKOVER);
					userSessionTree.addCtx(publisher.getContextKey(), false, publisher.getContext().getOrganization()
							.getContextKey(), null, null, PermissionType.LOOKOVER);
					userSessionTree.addCtx(ctx.getKey(), !hasFeeds, publisher.getContextKey(), null, null,
							PermissionType.LOOKOVER);
					for (Map.Entry<ContextKey, ? extends PermissionOnContext> child : founds.entrySet()) {
						if (child.getKey().getKeyType().equals(ContextType.FEED)) {
							userSessionTree.addCtx(child.getKey(), true, ctx.getKey(), null,
									(PermOnCtxDTO) permissionDTOFactory.from(child.getValue()));
						}
						if (child.getKey().getKeyType().equals(ContextType.ITEM)) {
							userSessionTree.addCtx(child.getKey(), false, ctx.getKey(), null,
									(PermOnCtxDTO) permissionDTOFactory.from(child.getValue()));
						}
					}
				}
			}
		}
	}

	/**
	 * This method is called only if the user has no perm > LOOKOVER on parents
	 * @param user
	 * @param categoryCtx
	 * @param permClass
	 * @return
	 */
	private Map<ContextKey, ? extends PermissionOnContext> findAuthorizedCategoryChilds(final UserDTO user,
			final ContextKey categoryCtx, final PermissionClass permClass) {
		log.debug("Call findAuthorizedCategoryChilds {},{}", categoryCtx, permClass);
		Assert.isTrue(contextPermsType.contains(permClass),
				String.format("Permission of type %s not yet managed findAuthorizedCategoryChilds", permClass));
		Map<ContextKey, PermissionOnContext> found = Maps.newHashMap();

		Optional<Category> optionalCategory = categoryDao.findById(categoryCtx.getKeyId());
		Category category = optionalCategory.orElse(null);

        Assert.notNull(category, "Error the linked category with id " + categoryCtx.getKeyId() + " should exist !");
		final boolean hasFeeds = category.getPublisher().getContext().getRedactor().getNbLevelsOfClassification() > 1;

		if (hasFeeds) {
			final List<AbstractFeed> feeds = Lists.newArrayList(feedDao.findAll(ClassificationPredicates
					.AbstractFeedsOfCategory(category)));

			Map<ContextKey, PermissionOnContext> ctxRoles = Maps.newHashMap();
			List<ContextKey> feedsCtx = Lists.newArrayList();
			for (AbstractFeed feed : feeds) {
				feedsCtx.add(feed.getContextKey());
			}

			final List<? extends PermissionOnContext> perms = Lists.newArrayList(permissionDao.getPermissionDao(
					permClass).findAll(PermissionPredicates.OnCtx(feedsCtx, permClass, false)));
			// we need to evaluate all permissions of a publisher to get the
			// greater possible permission before to continue
			for (PermissionOnContext perm : perms) {
				// last chance to get permission
				if (evaluationFactory.from(perm.getEvaluator()).isApplicable(user)
						&& perm.getRole().getMask() > PermissionType.LOOKOVER.getMask()) {
					if (ctxRoles.containsKey(perm.getContext())) {
						PermissionOnContext role = ctxRoles.get(perm.getContext());
						if (role == null || perm.getRole().getMask() > role.getRole().getMask()) {
							ctxRoles.put(perm.getContext(), perm);
						}
					} else {
						ctxRoles.put(perm.getContext(), perm);
					}
				}
			}

			// now we can go on childs
			for (Map.Entry<ContextKey, ? extends PermissionOnContext> ctx : ctxRoles.entrySet()) {
				found.put(ctx.getKey(), ctx.getValue());
				for (OwnerContextKey itemCtx : getItemsCtxOf(feedDao.findById(ctx.getKey().getKeyId()).get())) {
					found.put(itemCtx, null);
				}
			}
		}
		//        else {
		//            loadAllItemsOf(category);
		//        }

		// else it's too let to have permissions
		return found;
	}

	private void loadAllItemsOf(final AbstractClassification classification) {
		log.debug("Call loadAllItemsOf {}", classification);
		final List<ItemClassificationOrder> items = Lists.newArrayList(itemClassifDao.findAll(ItemPredicates
				.itemsClassOfClassification(classification)));
		for (ItemClassificationOrder item : items) {
			userSessionTree.addCtx(new OwnerContextKey(
					item.getItemClassificationId().getAbstractItem().getContextKey(), item.getItemClassificationId()
							.getAbstractItem().getCreatedBy().getSubject()), false, classification.getContextKey(),
					null, null);
		}
	}

	private Set<OwnerContextKey> getItemsCtxOf(final AbstractClassification classification) {
		log.debug("Call getItemsCtxOf {}", classification);
		Set<OwnerContextKey> itemsCtx = Sets.newHashSet();
		final List<ItemClassificationOrder> items = Lists.newArrayList(itemClassifDao.findAll(ItemPredicates
				.itemsClassOfClassification(classification)));
		for (ItemClassificationOrder item : items) {
			itemsCtx.add(new OwnerContextKey(item.getItemClassificationId().getAbstractItem().getContextKey(), item
					.getItemClassificationId().getAbstractItem().getCreatedBy().getSubject()));
		}

		return itemsCtx;
	}

	private void loadOwnedItemsWithoutClassif(final UserDTO user, final ContextKey organization) {
		log.debug("Call loadOwnedItemsWithoutClassif {}", organization);
		log.debug("Call loadOwnedItemsWithoutClassif {}",
				ItemPredicates.itemsOwnedOfOrganizationWithoutClassif(user, organization.getKeyId()));

		final List<AbstractItem> items = Lists.newArrayList(itemDao.findAll(ItemPredicates
				.itemsOwnedOfOrganizationWithoutClassif(user, organization.getKeyId())));
		for (AbstractItem item : items) {
			userSessionTree.addCtx(new OwnerContextKey(item.getContextKey(), item.getCreatedBy().getSubject()), false,
					organization, null, null);
		}
	}
}
