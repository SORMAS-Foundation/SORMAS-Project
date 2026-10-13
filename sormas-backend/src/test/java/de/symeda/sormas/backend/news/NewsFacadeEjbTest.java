package de.symeda.sormas.backend.news;

import java.util.Date;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.event.RiskLevel;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.news.NewsCriteria;
import de.symeda.sormas.api.news.NewsDto;
import de.symeda.sormas.api.news.NewsFacade;
import de.symeda.sormas.api.news.NewsIndexDto;
import de.symeda.sormas.api.news.eios.NewsStatus;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

class NewsFacadeEjbTest extends AbstractBeanTest {

	private UserDto nationalUser;

	@Override
	public void init() {
		super.init();
		nationalUser = creator.createNationalUser();
	}

	@Test
	void testCountWithUserLevelFilter() {
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.LAB_USER));
		TestDataCreator.RDCF rdcf1 = creator.createRDCF("Region1", "District1", "Community1", "Facility1");
		UserDto user1 = creator.createUser(rdcf1, creator.getUserRoleReference(DefaultUserRole.COMMUNITY_OFFICER));
		loginWith(nationalUser);
		NewsCriteria criteria = new NewsCriteria();
		criteria.setOnlyInMyJurisdiction(true);
		NewsFacade newsFacade = getNewsFacade();
		newsFacade.save(createNews(null, null, null));
		newsFacade.save(createNews(rdcf1.region, rdcf1.district, null));
		long count = newsFacade.count(criteria);
		Assertions.assertEquals(2, count);

		loginWith(user);
		count = newsFacade.count(criteria);
		Assertions.assertEquals(0, count);

		loginWith(user1);
		count = newsFacade.count(criteria);
		Assertions.assertEquals(1, count);

		criteria.setOnlyInMyJurisdiction(false);
		count = newsFacade.count(criteria);
		Assertions.assertEquals(2, count);

		NewsDto newsDto = createNews(null, null, null);
		newsDto.setStatus(NewsStatus.PENDING);
		newsFacade.save(newsDto);

		count = newsFacade.count(criteria);
		Assertions.assertEquals(2, count);

		loginWith(nationalUser);
		count = newsFacade.count(criteria);
		Assertions.assertEquals(3, count);
	}

	@Test
	void testGetNewsSorted() {
		loginWith(nationalUser);
		List<SortProperty> sortProperties = List.of(
			new SortProperty(NewsIndexDto.TITLE, true),
			new SortProperty(NewsIndexDto.DESCRIPTION, false),
			new SortProperty(NewsIndexDto.NEWS_DATE, true),
			new SortProperty(NewsIndexDto.RISK_LEVEL, false),
			new SortProperty(NewsIndexDto.STATUS, true),
			new SortProperty(NewsIndexDto.REGION, true),
			new SortProperty(NewsIndexDto.DISTRICT, false),
			new SortProperty(NewsIndexDto.COMMUNITY, true));
		NewsFacade newsFacade = getNewsFacade();
		newsFacade.save(createNews(null, null, null));
		newsFacade.save(createNews(null, null, null));
		newsFacade.save(createNews(null, null, null));
		newsFacade.save(createNews(null, null, null));
		newsFacade.save(createNews(null, null, null));
		newsFacade.save(createNews(null, null, null));
		NewsCriteria criteria = new NewsCriteria();
		List<NewsIndexDto> newsIndexList = newsFacade.getIndexList(criteria, 0, 100, sortProperties);
		Assertions.assertEquals(6, newsIndexList.size());
	}

	@Test
	void testCriteriaFilter() {
		loginWith(nationalUser);
		NewsFacade newsFacade = getNewsFacade();
		newsFacade.save(createNews(null, null, null));
		newsFacade.save(createNews(null, null, null));
		newsFacade.save(createNews(null, null, null));

		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		newsFacade.save(createNews(rdcf.region, rdcf.district, rdcf.community));

		NewsCriteria criteria = new NewsCriteria();
		criteria.setRegion(rdcf.region);
		List<NewsIndexDto> newsIndexList = newsFacade.getIndexList(criteria, 0, 100, null);
		Assertions.assertEquals(1, newsIndexList.size());

	}

	@Test
	void testGetIndexListWithoutResults() {
		loginWith(nationalUser);
		NewsFacade newsFacade = getNewsFacade();
		Assertions.assertTrue(newsFacade.getIndexList(new NewsCriteria(), 0, 100, null).isEmpty());

		newsFacade.save(createNews(null, null, null));
		// a page beyond the last result must not run an "IN ()" query
		Assertions.assertTrue(newsFacade.getIndexList(new NewsCriteria(), 100, 100, null).isEmpty());
	}

	@Test
	void testSaveRejectsNonHttpLink() {
		loginWith(nationalUser);
		NewsDto news = createNews(null, null, null);
		news.setLink("javascript:alert(document.cookie)");
		Assertions.assertThrows(ValidationRuntimeException.class, () -> getNewsFacade().save(news));

		news.setLink("https://example.com/article");
		Assertions.assertEquals("https://example.com/article", getNewsFacade().save(news).getLink());
	}

	@Test
	void testEndDateFilterIncludesTheWholeDay() {
		loginWith(nationalUser);
		NewsDto news = createNews(null, null, null);
		news.setNewsDate(DateHelper.addSeconds(DateHelper.getStartOfDay(new Date()), 16 * 3600));
		getNewsFacade().save(news);

		NewsCriteria criteria = new NewsCriteria();
		// the UI passes the selected day at midnight
		criteria.setStartDate(DateHelper.getStartOfDay(new Date()));
		criteria.setEndDate(DateHelper.getStartOfDay(new Date()));
		Assertions.assertEquals(1, getNewsFacade().count(criteria));
	}

	@Test
	void testIndexListContainsJurisdictionUuids() {
		loginWith(nationalUser);
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		getNewsFacade().save(createNews(rdcf.region, rdcf.district, rdcf.community));

		NewsIndexDto news = getNewsFacade().getIndexList(new NewsCriteria(), 0, 100, null).get(0);
		Assertions.assertEquals(rdcf.region.getUuid(), news.getRegionUuid());
		Assertions.assertEquals(rdcf.district.getUuid(), news.getDistrictUuid());
		Assertions.assertEquals(rdcf.community.getUuid(), news.getCommunityUuid());
		Assertions.assertEquals("Region", news.getRegion());
	}

	@Test
	void testNewNewsIsPending() {
		Assertions.assertEquals(NewsStatus.PENDING, NewsDto.build().getStatus());
	}

	@NotNull
	private static NewsDto createNews(RegionReferenceDto regionRef, DistrictReferenceDto districtRef, CommunityReferenceDto communityReferenceDto) {
		NewsDto newsDto = new NewsDto();
		newsDto.setUuid(DataHelper.createUuid());
		newsDto.setRegion(regionRef);
		newsDto.setDistrict(districtRef);
		newsDto.setCommunity(communityReferenceDto);
		newsDto.setTitle("title");
		newsDto.setDescription("Description");
		newsDto.setLink("http://link.com");
		newsDto.setRiskLevel(RiskLevel.HIGH);
		newsDto.setStatus(NewsStatus.APPROVED);
		newsDto.setNewsDate(new Date());
		return newsDto;
	}

}
