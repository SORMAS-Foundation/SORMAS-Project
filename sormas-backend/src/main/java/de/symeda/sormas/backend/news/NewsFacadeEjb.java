package de.symeda.sormas.backend.news;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.news.NewsCriteria;
import de.symeda.sormas.api.news.NewsDto;
import de.symeda.sormas.api.news.NewsFacade;
import de.symeda.sormas.api.news.NewsIndexDto;
import de.symeda.sormas.api.news.NewsReferenceDto;
import de.symeda.sormas.api.news.eios.NewsStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.AbstractBaseEjb;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "NewsFacade")
@RightsAllowed(UserRight._VIEW_NEWS)
public class NewsFacadeEjb extends AbstractBaseEjb<News, NewsDto, NewsIndexDto, NewsReferenceDto, NewsService, NewsCriteria> implements NewsFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;
	@EJB
	private EiosFacadeEjb.EiosFacadeEjbLocal eiosFacadeEjb;
	@EJB
	private UserService userService;

	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;
	@EJB
	private NewsService newsService;

	public NewsFacadeEjb() {

	}

	@Inject
	public NewsFacadeEjb(NewsService newsService) {
		super(News.class, NewsDto.class, newsService);
	}

	@Override
	public List<NewsIndexDto> getIndexList(NewsCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<NewsIndexDto> cq = cb.createQuery(NewsIndexDto.class);
		Root<News> root = cq.from(News.class);
		NewsJoin newsJoin = new NewsJoin(root);
		cq.multiselect(
			root.get(News.ID),
			root.get(News.UUID),
			root.get(News.TITLE),
			root.get(News.URL),
			root.get(News.DESCRIPTION),
			newsJoin.getRegion().get(Region.NAME),
			newsJoin.getDistrict().get(District.NAME),
			newsJoin.getCommunity().get(Community.NAME),
			root.get(News.NEWS_DATE),
			root.get(News.RISK_LEVEL),
			root.get(News.STATUS),
			root.get(News.DISEASE));
		List<Long> indexListIds = getIndexListIds(criteria, first, max, sortProperties);
		sortBy(sortProperties, new NewsQueryContext(cb, cq, root));
		cq.where(root.get(News.ID).in(indexListIds));
		return em.createQuery(cq).getResultList();
	}

	@Override
	public Page<NewsIndexDto> getNewsPage(NewsCriteria newsCriteria, int offset, int size, List<SortProperty> sortProperties) {
		List<NewsIndexDto> indexList = getIndexList(newsCriteria, offset, size, sortProperties);
		long count = count(newsCriteria);
		return new Page<>(indexList, offset, size, count);

	}

	private List<Long> getIndexListIds(NewsCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<News> root = cq.from(News.class);
		List<Selection<?>> selections = new ArrayList<>();
		selections.add(root.get(News.ID));
		cq.multiselect(selections);
		NewsQueryContext queryContext = new NewsQueryContext(cb, cq, root);
		Predicate filter = newsService.buildFilterFromCriteria(criteria, queryContext);
		if (filter != null) {
			cq.where(filter);
		}
		selections.addAll(sortBy(sortProperties, queryContext));
		return QueryHelper.getResultList(em, cq, first, max).stream().map(t -> t.get(0, Long.class)).collect(Collectors.toList());
	}

	private List<Selection<?>> sortBy(List<SortProperty> sortProperties, NewsQueryContext queryContext) {
		CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		CriteriaQuery<?> cq = queryContext.getQuery();
		List<Selection<?>> selections = new ArrayList<>();
		if (sortProperties != null && !sortProperties.isEmpty()) {
			Expression expression;
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				switch (sortProperty.propertyName) {
				case News.UUID:
				case News.NEWS_DATE:
				case News.TITLE:
				case News.RISK_LEVEL:
				case News.STATUS:
				case News.DESCRIPTION:
				case News.DISEASE:
					expression = queryContext.getRoot().get(sortProperty.propertyName);
					break;
				case News.REGION:
					expression = queryContext.getJoins().getRegion().get(Region.NAME);
					break;
				case News.DISTRICT:
					expression = queryContext.getJoins().getDistrict().get(District.NAME);
					break;
				case News.COMMUNITY:
					expression = queryContext.getJoins().getCommunity().get(Community.NAME);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
				selections.add(expression);
			}
			cq.orderBy(order);
		} else {
			Path<Object> newsDate = queryContext.getRoot().get(News.NEWS_DATE);
			cq.orderBy(cb.desc(newsDate));
			selections.add(newsDate);
		}
		return selections;
	}

	@Override
	public long count(NewsCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<News> root = cq.from(News.class);
		NewsQueryContext queryContext = new NewsQueryContext(cb, cq, root);
		Predicate filter = newsService.buildFilterFromCriteria(criteria, queryContext);
		if (filter != null) {
			cq.where(filter);
		}
		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public NewsDto getByUuid(String uuid) {
		News news = newsService.getByUuid(uuid);
		return toDto(news);
	}

	@Override
	public void validate(NewsDto dto) throws ValidationRuntimeException {

	}

	@Override
	public void markApprove(NewsReferenceDto newsRef) {
		changeStatus(newsRef, NewsStatus.APPROVED);
	}

	@Override
	public void markUnUseful(NewsReferenceDto newsRef) {
		changeStatus(newsRef, NewsStatus.UNUSEFUL);
	}

	@Override
	@RightsAllowed(UserRight._EDIT_NEWS)
	public NewsDto save(@Valid @NotNull NewsDto newsDto) {
		validate(newsDto);
		News news = newsService.getByUuid(newsDto.getUuid());
		news = fillOrBuildEntity(newsDto, news, true);
		newsService.ensurePersisted(news);
		News article = newsService.getByUuid(news.getUuid());
		return toDto(article);
	}

	private void changeStatus(NewsReferenceDto newsIndexDto, NewsStatus unUseful) {
		News news = newsService.getByUuid(newsIndexDto.getUuid());
		news.setStatus(unUseful);
		newsService.ensurePersisted(news);
	}

	public News fillOrBuildEntity(NewsDto source, News target, boolean checkChangeDate) {
		if (source == null) {
			return null;
		}
		target = DtoHelper.fillOrBuildEntity(source, target, News::new, checkChangeDate);
		target.setTitle(source.getTitle());
		target.setUrl(source.getLink());
		target.setDescription(source.getDescription());
		target.setEiosUrl(source.getEiosUrl());
		target.setNewsDate(source.getNewsDate());
		target.setRiskLevel(source.getRiskLevel());
		target.setStatus(source.getStatus());
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setCommunity(communityService.getByReferenceDto(source.getCommunity()));
		target.setEiosId(source.getEiosId());
		target.setDisease(source.getDisease());
		return target;
	}

	public NewsDto toDto(News source) {
		if (source == null) {
			return null;
		}
		NewsDto target = new NewsDto();
		DtoHelper.fillDto(target, source);
		target.setUuid(source.getUuid());
		target.setTitle(source.getTitle());
		target.setLink(source.getUrl());
		target.setDescription(source.getDescription());
		target.setEiosUrl(source.getEiosUrl());
		target.setNewsDate(source.getNewsDate());
		target.setRiskLevel(source.getRiskLevel());
		target.setStatus(source.getStatus());
		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setCommunity(CommunityFacadeEjb.toReferenceDto(source.getCommunity()));
		target.setEiosId(source.getEiosId());
		target.setDisease(source.getDisease());
		return target;

	}

	@Override
	protected NewsReferenceDto toRefDto(News news) {
		if (news != null)
			return new NewsReferenceDto(news.getUuid(), news.getTitle());
		return null;
	}

	@Override
	protected void pseudonymizeDto(News source, NewsDto dto, Pseudonymizer pseudonymizer, boolean inJurisdiction) {

	}

	@Override
	protected void restorePseudonymizedDto(NewsDto dto, NewsDto existingDto, News entity, Pseudonymizer pseudonymizer) {

	}

	@Override
	public EditPermissionType getEditPermissionType(String uuid) {
		return isEditAllowed(uuid) ? EditPermissionType.ALLOWED : EditPermissionType.REFUSED;
	}

	@Override
	public boolean isEditAllowed(String uuid) {
		return userService.getCurrentUser().hasUserRight(UserRight.EDIT_NEWS);
	}

	@Override
	public NewsReferenceDto getReferenceByUuid(String uuid) {
		return toRefDto(newsService.getByUuid(uuid));
	}

	public static NewsReferenceDto toReferenceDto(News news) {
		if (news == null) {
			return null;
		}
		return new NewsReferenceDto(news.getUuid(), news.getTitle());
	}

	@LocalBean
	@Stateless
	public static class NewsFacadeEjbLocal extends NewsFacadeEjb {

		public NewsFacadeEjbLocal() {
			super();
		}

		@Inject
		public NewsFacadeEjbLocal(NewsService newsService) {
			super(newsService);
		}
	}
}
