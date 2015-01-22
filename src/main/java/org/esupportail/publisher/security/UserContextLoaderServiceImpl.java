package org.esupportail.publisher.security;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mysema.commons.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.repository.*;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

/**
 * Complex bean to obtains objects Organization, Publisher, Category, Internal/ExternalFeed, Item where the authenticated user has a permission.
 * We should have permission type of PermissionOnContext only for Organization and Publisher. Publisher will give the type of permission for after.
 * Warning permission of type OnSubjects can be on publisher, but they won't be applied
 */
@Slf4j
@Component
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

	private static EnumSet contextPermsType = EnumSet
			.of(PermissionClass.CONTEXT, PermissionClass.CONTEXT_WITH_SUBJECTS);

	public void loadUserTree(Authentication authentication) {
		loadUserTree(((CustomUserDetails) authentication.getPrincipal()).getUser(),
				((CustomUserDetails) authentication.getPrincipal()).getAuthorities());
	}

	public void loadUserTree(final UserDTO user, final Collection<? extends GrantedAuthority> authorities) {
		// init userTree
		userSessionTree.cleanup();
		if (authorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN))) {
			userSessionTree.setSuperAdmin(true);
		} else if (authorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.USER))) {
			userSessionTree.setSuperAdmin(false);
			log.debug("Call loadUserTree for USER access !");
			// Load list of organizations
			final List<PermissionOnContext> perms = Lists.newArrayList(permissionDao.getPermissionDao(PermissionClass.CONTEXT).findAll(PermissionPredicates
                .OnCtxType(ContextType.ORGANIZATION, PermissionClass.CONTEXT, false)));
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
					userSessionTree.addCtx(ctx.getKey(), false, null, null,
							(PermOnCtxDTO) permissionDTOFactory.from(ctx.getValue()));
					loadAuthorizedOrganizationChilds(user, ctx.getKey(), false);
				} else {
					loadAuthorizedOrganizationChilds(user, ctx.getKey(), true);
				}
			}
		} else {
			userSessionTree.setSuperAdmin(false);
		}
		userSessionTree.setUserTreeLoaded(true);
		if (log.isDebugEnabled()) {
			log.debug("Tree loaded : {}", userSessionTree.toString());
		}
	}

	// add publishers
	private void loadAuthorizedOrganizationChilds(final UserDTO user, final ContextKey organizationCtx,
			final boolean checkPerms) {
		log.debug("Call loadAuthorizedOrganizationChilds {},{}", organizationCtx, checkPerms);
		final Organization organization = organizationDao.findOne(organizationCtx.getKeyId());

		final List<Publisher> publishers = Lists.newArrayList(publisherDao.findAll(PublisherPredicates
            .AllOfOrganization(organization)));
		Map<ContextKey, PermissionOnContext> ctxRoles = Maps.newHashMap();
		Map<ContextKey, Publisher> ctxPubInfos = Maps.newHashMap();
		List<ContextKey> pubsCtx = Lists.newArrayList();
		for (Publisher pub : publishers) {
			if (!checkPerms) {
				ctxRoles.put(pub.getContextKey(), null);
			} else {
				pubsCtx.add(pub.getContextKey());
			}
			ctxPubInfos.put(pub.getContextKey(), pub);
		}

		if (checkPerms) {
			final List<PermissionOnContext> perms = Lists.newArrayList(permissionDao.getPermissionDao(PermissionClass.CONTEXT).findAll(PermissionPredicates
                .OnCtx(pubsCtx, PermissionClass.CONTEXT, false)));
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
						ctxRoles.put(perm.getContext(), perm);
					}
				} else {
					if (!ctxRoles.containsKey(perm.getContext())) {
						ctxRoles.put(perm.getContext(), null);
					}
				}
			}
		}
		// now we can go on childs
		if (!ctxRoles.isEmpty()) {
			for (Map.Entry<ContextKey, PermissionOnContext> ctx : ctxRoles.entrySet()) {
				if (!checkPerms
						|| (ctx.getValue() != null && PermissionType.MANAGER.getMask() <= ctx.getValue().getRole()
								.getMask())) {
					if (!checkPerms) {
						userSessionTree.addCtx(ctx.getKey(), false, organizationCtx, null, null);
					} else {
						userSessionTree.addCtx(ctx.getKey(), false, organizationCtx, null,
								(PermOnCtxDTO) permissionDTOFactory.from(ctx.getValue()));
					}
					loadAuthorizedPublisherChilds(user, ctx.getKey(), false);
				} else if (ctx.getValue() != null) {
					userSessionTree.addCtx(ctx.getKey(), false, organizationCtx, null,
							(PermOnCtxDTO) permissionDTOFactory.from(ctx.getValue()));
					loadAuthorizedPublisherChilds(user, ctx.getKey(), true);
				} else {
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

	private void loadAuthorizedPublisherChilds(final UserDTO user, final ContextKey publisherCtx,
			final boolean checkPerms) {
		log.debug("Call loadAuthorizedPublisherChilds {},{}", publisherCtx, checkPerms);
		final Publisher publisher = publisherDao.findOne(publisherCtx.getKeyId());

		if (contextPermsType.contains(publisher.getPermissionType())) {

			final List<Category> categories = Lists.newArrayList(categoryDao.findAll(ClassificationPredicates
                .CategoryOfPublisher(publisher.getId())));
			Map<ContextKey, PermissionOnContext> ctxRoles = Maps.newHashMap();
			List<ContextKey> catsCtx = Lists.newArrayList();
			for (Category cat : categories) {
				if (!checkPerms) {
					ctxRoles.put(cat.getContextKey(), null);
				} else {
					catsCtx.add(cat.getContextKey());
				}
			}

			// shortcut to avoid to evaluate all perms
			if (checkPerms) {
				final List<? extends PermissionOnContext> perms = Lists.newArrayList(permissionDao.getPermissionDao(publisher.getPermissionType())
                    .findAll(PermissionPredicates.OnCtx(catsCtx, publisher.getPermissionType(), false)));

				// we need to evaluate all permissions of a publisher to get the
				// greater possible permission before to continue
				for (PermissionOnContext perm : perms) {
					if (evaluationFactory.from(perm.getEvaluator()).isApplicable(user)
							&& perm.getRole().getMask() > PermissionType.LOOKOVER.getMask()) {
						if (log.isDebugEnabled()) {
							log.debug("TreeLoader should add {}", perm.getContext());
						}
						if (ctxRoles.containsKey(perm.getContext())) {
							PermissionOnContext role = (PermissionOnContext) ctxRoles.get(perm.getContext());
							if (role == null || perm.getRole().getMask() > role.getRole().getMask()) {
								ctxRoles.put(perm.getContext(), perm);
							}
						} else {
							ctxRoles.put(perm.getContext(), perm);
						}
					} else {
						if (!ctxRoles.containsKey(perm.getContext())) {
							ctxRoles.put(perm.getContext(), null);
						}
					}
				}
			}
            /* equivalent à la suite
                final boolean isLastNode = WritingMode.TARGETS_ON_ITEM.equals(publisher.getContext().getRedactor().getWritingMode());
             */
            final boolean hasFeeds = publisher.getContext().getRedactor().getNbLevelsOfClassification() > 1;
			// now we can go on childs
			for (Map.Entry<ContextKey, PermissionOnContext> ctx : ctxRoles.entrySet()) {
				if (!checkPerms
						|| (ctx.getValue() != null && PermissionType.MANAGER.getMask() <= ctx.getValue().getRole()
								.getMask())) {
					if (!checkPerms) {
						userSessionTree.addCtx(ctx.getKey(), !hasFeeds, publisherCtx, null, null);
					} else {
						userSessionTree.addCtx(ctx.getKey(), !hasFeeds, publisherCtx, null,
								(PermOnCtxDTO) permissionDTOFactory.from(ctx.getValue()));
					}
					loadAuthorizedCategoryChilds(user, ctx.getKey(), false, publisher.getPermissionType());
				} else {
					userSessionTree.addCtx(ctx.getKey(), !hasFeeds, publisherCtx, null,
							(PermOnCtxDTO) permissionDTOFactory.from(ctx.getValue()));
					loadAuthorizedCategoryChilds(user, ctx.getKey(), true, publisher.getPermissionType());
				}
			}
		} else {
			log.error(String.format("Permission of type %s not yet managed in publisher %s",
					publisher.getPermissionType(), publisher));
			throw new IllegalStateException(String.format("Permission of type %s not yet managed",
					publisher.getPermissionType()));
		}
	}

	private void loadAuthorizedCategoryChilds(final UserDTO user, final ContextKey categoryCtx,
			final boolean checkPerms, final PermissionClass permClass) {
		log.debug("Call loadAuthorizedCategoryChilds {},{}, {}", categoryCtx, checkPerms, permClass);
		Assert.isTrue(contextPermsType.contains(permClass),
				String.format("Permission of type %s not yet managed loadAuthorizedCategoryChilds", permClass));
		final Category category = categoryDao.findOne(categoryCtx.getKeyId());

		final boolean hasFeeds = category.getPublisher().getContext().getRedactor().getNbLevelsOfClassification() > 1;

		if (hasFeeds) {
			final List<AbstractFeed> feeds = Lists.newArrayList(feedDao.findAll(ClassificationPredicates
                .AbstractFeedsOfCategory(category)));

			Map<ContextKey, PermissionOnContext> ctxRoles = Maps.newHashMap();
			List<ContextKey> feedsCtx = Lists.newArrayList();
			for (AbstractFeed feed : feeds) {
				if (!checkPerms) {
					ctxRoles.put(feed.getContextKey(), null);
				} else {
					feedsCtx.add(feed.getContextKey());
				}
			}

			// shortcut to avoid to evaluate all perms
			if (checkPerms) {
				final List<? extends PermissionOnContext> perms = Lists.newArrayList(permissionDao.getPermissionDao(permClass)
                    .findAll(PermissionPredicates.OnCtx(feedsCtx, permClass, false)));
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
							ctxRoles.put(perm.getContext(), perm);
						}
					} else {
						if (!ctxRoles.containsKey(perm.getContext())) {
							ctxRoles.put(perm.getContext(), null);
						}
					}
				}
			}

			// now we can go on childs
			for (Map.Entry<ContextKey, PermissionOnContext> ctx : ctxRoles.entrySet()) {
				if (!checkPerms) {
					userSessionTree.addCtx(ctx.getKey(), !hasFeeds, categoryCtx, null, null);
				} else {
					userSessionTree.addCtx(ctx.getKey(), !hasFeeds, categoryCtx, null,
							(PermOnCtxDTO) permissionDTOFactory.from(ctx.getValue()));
				}
				loadAllItemsOf(feedDao.findOne(ctx.getKey().getKeyId()));
			}
		} else {
			loadAllItemsOf(category);
		}
	}

	private void findAuthorizedPublisherChilds(final UserDTO user, final ContextKey publisherCtx) {
		log.debug("Call findAuthorizedPublisherChilds {}", publisherCtx);
		final Publisher publisher = publisherDao.findOne(publisherCtx.getKeyId());

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
		}

		final List<? extends PermissionOnContext> perms = Lists.newArrayList(permissionDao.getPermissionDao(publisher.getPermissionType())
            .findAll(PermissionPredicates.OnCtx(catsCtx, publisher.getPermissionType(), false)));

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
					ctxRoles.put(perm.getContext(), perm);
				}
			} else {
				if (!ctxRoles.containsKey(perm.getContext())) {
					ctxRoles.put(perm.getContext(), null);
				}
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

					userSessionTree.addCtx(publisher.getContext().getOrganization().getContextKey(), false, null, null, null,
							PermissionType.LOOKOVER);
					userSessionTree.addCtx(publisher.getContextKey(), false,
							publisher.getContext().getOrganization().getContextKey(), null, null, PermissionType.LOOKOVER);
					parentsCtxLoaded = true;
				}
				if (PermissionType.MANAGER.getMask() <= ctx.getValue().getRole().getMask()) {
					userSessionTree.addCtx(ctx.getKey(), !hasFeeds, publisherCtx, null,
							(PermOnCtxDTO) permissionDTOFactory.from(ctx.getValue()));
					loadAuthorizedCategoryChilds(user, ctx.getKey(), false, publisher.getPermissionType());
				} else {
					userSessionTree.addCtx(ctx.getKey(), !hasFeeds, publisherCtx, null,
							(PermOnCtxDTO) permissionDTOFactory.from(ctx.getValue()));
					loadAuthorizedCategoryChilds(user, ctx.getKey(), true, publisher.getPermissionType());
				}
			} else {
				Map<ContextKey, PermissionOnContext> founds = findAuthorizedCategoryChilds(user, publisherCtx,
						publisher.getPermissionType());
				if (!founds.isEmpty()) {
					userSessionTree.addCtx(publisher.getContext().getOrganization().getContextKey(), false, null, null, null,
							PermissionType.LOOKOVER);
					userSessionTree.addCtx(publisher.getContextKey(), false,
							publisher.getContext().getOrganization().getContextKey(), null, null, PermissionType.LOOKOVER);
					for (Map.Entry<ContextKey, PermissionOnContext> child : founds.entrySet()) {
						if (child.getKey().getKeyType().equals(ContextType.FEED)) {
							userSessionTree.addCtx(child.getKey(), true, ctx.getKey(), null,
									(PermOnCtxDTO) permissionDTOFactory.from(child.getValue()));
						}
					}
					for (Map.Entry<ContextKey, PermissionOnContext> child : founds.entrySet()) {
						if (child.getKey().getKeyType().equals(ContextType.ITEM)) {
							userSessionTree.addCtx(child.getKey(), false, ctx.getKey(), null,
									(PermOnCtxDTO) permissionDTOFactory.from(child.getValue()));
						}
					}
				}
			}
		}
	}

	private Map<ContextKey, PermissionOnContext> findAuthorizedCategoryChilds(final UserDTO user,
			final ContextKey categoryCtx, final PermissionClass permClass) {
		log.debug("Call findAuthorizedCategoryChilds {},{}", categoryCtx, permClass);
		Assert.isTrue(contextPermsType.contains(permClass),
				String.format("Permission of type %s not yet managed findAuthorizedCategoryChilds", permClass));
		Map<ContextKey, PermissionOnContext> found = Maps.newHashMap();

		final Category category = categoryDao.findOne(categoryCtx.getKeyId());

		final boolean hasFeeds = category.getPublisher().getContext().getRedactor().getNbLevelsOfClassification() > 1;

		if (hasFeeds) {
			final List<AbstractFeed> feeds = Lists.newArrayList(feedDao.findAll(ClassificationPredicates
                .AbstractFeedsOfCategory(category)));

			Map<ContextKey, PermissionOnContext> ctxRoles = Maps.newHashMap();
			List<ContextKey> feedsCtx = Lists.newArrayList();
			for (AbstractFeed feed : feeds) {
				feedsCtx.add(feed.getContextKey());
			}

			final List<? extends PermissionOnContext> perms = Lists.newArrayList(permissionDao.getPermissionDao(permClass).findAll(PermissionPredicates
                .OnCtx(feedsCtx, permClass, false)));
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
			for (Map.Entry<ContextKey, PermissionOnContext> ctx : ctxRoles.entrySet()) {
				found.put(ctx.getKey(), ctx.getValue());
				for (OwnerContextKey itemCtx : getItemsCtxOf(feedDao.findOne(ctx.getKey().getKeyId()))) {
					found.put(itemCtx, null);
				}
			}
		} else {
			loadAllItemsOf(category);
		}

		// else it's too let to have permissions
		return found;
	}

	private void loadAllItemsOf(final AbstractClassification classification) {
		log.debug("Call loadAllItemsOf {}", classification);
		final List<ItemClassificationOrder> items = Lists.newArrayList(itemClassifDao.findAll(ItemPredicates
            .itemsClassOfClassification(classification)));
		for (ItemClassificationOrder item : items) {
			userSessionTree.addCtx(new OwnerContextKey(item.getItemClassificationId().getAbstractItem().getContextKey(), item.getItemClassificationId()
					.getAbstractItem().getCreatedBy().getSubject()), false, classification.getContextKey(), null, null);
		}
	}

	private Set<OwnerContextKey> getItemsCtxOf(final AbstractClassification classification) {
		log.debug("Call getItemsCtxOf {}", classification);
		Set<OwnerContextKey> itemsCtx = Sets.newHashSet();
		final List<ItemClassificationOrder> items = Lists.newArrayList(itemClassifDao.findAll(ItemPredicates
            .itemsClassOfClassification(classification)));
		for (ItemClassificationOrder item : items) {
			itemsCtx.add(new OwnerContextKey(item.getItemClassificationId().getAbstractItem().getContextKey(), item.getItemClassificationId()
					.getAbstractItem().getCreatedBy().getSubject()));
		}

		return itemsCtx;
	}

    private void loadOwnedItemsWithoutClassif(final UserDTO user, final ContextKey organization) {
        log.debug("Call loadOwnedItemsWithoutClassif {}", organization);
        log.debug("Call loadOwnedItemsWithoutClassif {}", ItemPredicates.itemsOwnedOfOrganizationWithoutClassif(user, organization.getKeyId()));

        final List<AbstractItem> items = Lists.newArrayList(itemDao.findAll(ItemPredicates.itemsOwnedOfOrganizationWithoutClassif(user, organization.getKeyId())));
        for (AbstractItem item : items) {
            userSessionTree.addCtx(new OwnerContextKey(item.getContextKey(), item.getCreatedBy().getSubject()), false, organization, null, null);
        }
    }
}
