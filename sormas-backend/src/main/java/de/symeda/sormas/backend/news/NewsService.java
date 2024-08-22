package de.symeda.sormas.backend.news;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import org.jetbrains.annotations.Nullable;

import de.symeda.sormas.api.news.NewsCriteria;
import de.symeda.sormas.api.news.eios.NewsStatus;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;

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
			filter = CriteriaBuilderHelper.and(cb, filter, cb.greaterThanOrEqualTo(root.get(News.NEWS_DATE), criteria.getStartDate()));

		}
		if (criteria.getEndDate() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.lessThanOrEqualTo(root.get(News.NEWS_DATE), criteria.getEndDate()));

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

	public News getByEiosId(Long eiosId) {
		List<News> result = getByPredicate((cb, from, cq) -> cb.equal(from.get(News.EIOS_ID), eiosId));
		return result == null || result.isEmpty() ? null : result.get(0);
	}
}
