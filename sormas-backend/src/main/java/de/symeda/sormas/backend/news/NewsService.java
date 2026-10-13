package de.symeda.sormas.backend.news;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import org.jetbrains.annotations.Nullable;

import de.symeda.sormas.api.news.NewsCriteria;
import de.symeda.sormas.api.news.eios.ArticleDto;
import de.symeda.sormas.api.news.eios.NewsStatus;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless
@LocalBean
public class NewsService extends AdoServiceWithUserFilterAndJurisdiction<News> {

	public NewsService() {
		super(News.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, News> from) {
		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return null;
		}
		Predicate filter = null;
		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		if (jurisdictionLevel != null) {
			if (jurisdictionLevel == JurisdictionLevel.NATION) {
				filter = cb.conjunction();
			} else {
				if (currentUser.getRegion() != null) {
					filter = cb.equal(from.get(News.REGION), currentUser.getRegion());
				}
				if (currentUser.getDistrict() != null) {
					filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(News.DISTRICT), currentUser.getDistrict()));
				}
			}

		}
		return filter;
	}

	@Nullable
	public Predicate buildFilterFromCriteria(NewsCriteria criteria, NewsQueryContext queryContext) {
		Predicate filter = null;
		From<?, News> root = queryContext.getRoot();
		CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		CriteriaQuery<?> cq = queryContext.getQuery();
		NewsJoin newsJoin = queryContext.getJoins();
		if (criteria.getRegion() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(newsJoin.getRegion().get(Region.UUID), criteria.getRegion().getUuid()));
		}
		if (criteria.getDistrict() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(newsJoin.getDistrict().get(District.UUID), criteria.getDistrict().getUuid()));
		}
		if (criteria.getCommunity() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(newsJoin.getCommunity().get(Community.UUID), criteria.getCommunity().getUuid()));
		}

		if (criteria.getRiskLevel() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(root.get(News.RISK_LEVEL), criteria.getRiskLevel()));
		}
		if (criteria.getStartDate() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.greaterThanOrEqualTo(root.get(News.NEWS_DATE), DateHelper.getStartOfDay(criteria.getStartDate())));

		}
		if (criteria.getEndDate() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.lessThanOrEqualTo(root.get(News.NEWS_DATE), DateHelper.getEndOfDay(criteria.getEndDate())));

		}
		if (!getCurrentUser().hasUserRight(UserRight.EDIT_NEWS)) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(root.get(News.STATUS), NewsStatus.APPROVED));
		} else if (criteria.getStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(root.get(News.STATUS), criteria.getStatus()));
		}
		if (Boolean.TRUE.equals(criteria.getOnlyInMyJurisdiction())) {
			Predicate userFilter = createUserFilter(cb, cq, root);
			if (userFilter != null) {
				filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
			}
		}
		if (!DataHelper.isNullOrEmpty(criteria.getNewsLike())) {
			Predicate likeFilter = cb.or(
				CriteriaBuilderHelper.ilike(cb, root.get(News.TITLE), criteria.getNewsLike()),
				CriteriaBuilderHelper.ilike(cb, root.get(News.DESCRIPTION), criteria.getNewsLike()));
			filter = CriteriaBuilderHelper.and(cb, filter, likeFilter);
		}
		return filter;
	}

	public News getByEiosId(String eiosId) {
		List<News> result = getByPredicate((cb, from, cq) -> cb.equal(from.get(News.EIOS_ID), eiosId));
		return result == null || result.isEmpty() ? null : result.get(0);
	}

	public News getByEiosUrl(String eiosUrl) {
		List<News> result = getByPredicate((cb, from, cq) -> cb.equal(from.get(News.EIOS_URL), eiosUrl));
		return result == null || result.isEmpty() ? null : result.get(0);
	}

	/**
	 * Creates or updates the news of an EIOS article in its own transaction, so a failing article does not roll back the other
	 * articles of the sync run.
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void saveEiosArticleInNewTransaction(ArticleDto article) {
		// EIOS ids are not stable across API versions, the article URL is - match on it first
		News news = null;
		if (article.getEiosUrl() != null) {
			news = getByEiosUrl(article.getEiosUrl().trim());
		}
		if (news == null && article.getEiosId() != null) {
			news = getByEiosId(article.getEiosId());
		}
		ensurePersisted(fillOrBuildEntity(article, news));
	}

	private News fillOrBuildEntity(ArticleDto source, News target) {
		boolean isNew = target == null;
		target = DtoHelper.fillOrBuildEntity(source, target, News::build, false);
		target.setTitle(source.getTitle());
		target.setUrl(toSafeLink(source.getLink(), source.getEiosId()));
		target.setDescription(source.getDescription());
		target.setEiosUrl(source.getEiosUrl() != null ? source.getEiosUrl().trim() : null);
		if (source.getProcessedOnDate() != null) {
			target.setNewsDate(Date.from(source.getProcessedOnDate().toInstant()));
		}
		// status and risk level are maintained by SORMAS users - a re-fetch from EIOS must not overwrite their review
		if (isNew) {
			target.setRiskLevel(source.getRiskLevel());
			target.setStatus(NewsStatus.PENDING);
		}
		target.setEiosId(source.getEiosId());
		return target;
	}

	private String toSafeLink(String link, String eiosId) {
		if (link == null) {
			return null;
		}
		if (!DataHelper.isHttpUrl(link)) {
			logger.warn("Ignoring non http(s) link of EIOS article {}", eiosId);
			return null;
		}
		return link.trim();
	}
}
