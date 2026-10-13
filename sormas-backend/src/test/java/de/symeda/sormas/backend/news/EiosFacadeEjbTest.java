package de.symeda.sormas.backend.news;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static de.symeda.sormas.backend.common.ConfigFacadeEjb.EIOS_CLIENT_ID;
import static de.symeda.sormas.backend.common.ConfigFacadeEjb.EIOS_CLIENT_SECRET;
import static de.symeda.sormas.backend.common.ConfigFacadeEjb.EIOS_ODI_URL;
import static de.symeda.sormas.backend.common.ConfigFacadeEjb.EIOS_SCOPE;
import static de.symeda.sormas.backend.common.ConfigFacadeEjb.EIOS_URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.news.NewsCriteria;
import de.symeda.sormas.api.news.NewsIndexDto;
import de.symeda.sormas.api.news.NewsReferenceDto;
import de.symeda.sormas.api.news.eios.ArticleDto;
import de.symeda.sormas.api.news.eios.EiosArticleCriteria;
import de.symeda.sormas.api.news.eios.EiosArticlesResponse;
import de.symeda.sormas.api.news.eios.EiosConfig;
import de.symeda.sormas.api.news.eios.NewsStatus;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.util.DtoHelper;

class EiosFacadeEjbTest extends AbstractBeanTest {

	private static final String BOARD_ITEMS_PATH = "/Items/matching-board/12";

	private WireMockServer wireMockServer;

	@BeforeEach
	void setUp() {
		setUpWireMockServer();
	}

	@AfterEach
	void tearDown() {
		if (wireMockServer != null) {
			wireMockServer.stop();
		}
	}

	@Test
	void fetchEiosArticle() throws Exception {
		stubBoardItems("mockFiles/eiosArticleResponse.json");

		EiosArticleCriteria criteria = new EiosArticleCriteria();
		criteria.setBoardId(12L);
		criteria.setTimeSince(ZonedDateTime.of(2024, 5, 1, 0, 0, 0, 0, ZoneOffset.UTC));
		criteria.setTimeUntil(ZonedDateTime.of(2024, 5, 2, 6, 30, 0, 0, ZoneOffset.ofHours(2)));
		criteria.setStart(0);
		criteria.setLimit(40);
		EiosConfig eiosConfig = getConfigFacade().getEIOSConfig();
		EiosArticlesResponse eiosArticlesResponse = getEiosFacade().fetchEiosArticle(criteria, eiosConfig);

		List<ArticleDto> articles = eiosArticlesResponse.getArticles();
		Assertions.assertNotNull(articles);
		Assertions.assertEquals(10, articles.size());
		// numeric ids of older responses are read as strings
		Assertions.assertNotNull(articles.get(0).getEiosId());
		Assertions.assertNotNull(articles.get(0).getProcessedOnDate());

		verify(
			getRequestedFor(urlPathEqualTo(BOARD_ITEMS_PATH)).withQueryParam("timeSince", equalTo("2024-05-01T00:00:00.000Z"))
				.withQueryParam("timeUntil", equalTo("2024-05-02T04:30:00.000Z"))
				.withQueryParam("start", equalTo("0"))
				.withQueryParam("limit", equalTo("40"))
				.withHeader("Accept-Terms", equalTo("true"))
				.withHeader("X-TermS-Accepted", equalTo("true"))
				.withHeader("Authorization", equalTo("Bearer token")));
	}

	@Test
	void fetchEiosArticleReadsCurrentResponseFormat() throws Exception {
		stubBoardItems("mockFiles/eiosArticleBatchResponse.json");

		EiosArticlesResponse eiosArticlesResponse = getEiosFacade().fetchEiosArticle(boardCriteria(), getConfigFacade().getEIOSConfig());

		Assertions.assertEquals(400L, eiosArticlesResponse.getCount());
		ArticleDto article = eiosArticlesResponse.getArticles().get(0);
		Assertions.assertEquals("443318533", article.getEiosId());
		// pubDate and processedOnDate map to the same property; the later one in the payload (processedOnDate) is kept
		Assertions.assertEquals(ZonedDateTime.of(2024, 5, 9, 1, 30, 14, 614455900, ZoneOffset.UTC), article.getProcessedOnDate());
		Assertions.assertEquals("en", article.getSource().getLanguage().getKey());
		Assertions.assertEquals("English", article.getSource().getLanguage().getLabel());
		Assertions.assertEquals("cn", article.getSource().getCountry().getKey());
		Assertions.assertEquals("China", article.getSource().getCountry().getLabel());
	}

	@Test
	void fetchEiosArticleAcceptsLegacySourceLabels() throws Exception {
		stubFor(
			get(urlPathEqualTo(BOARD_ITEMS_PATH)).willReturn(
				aResponse().withStatus(200)
					.withHeader("Content-Type", "text/plain; charset=utf-8")
					.withBody(
						"{\"count\":1,\"result\":[{\"id\":\"a-1\",\"pubDate\":\"2024-05-09T01:28\",\"source\":{\"language\":\"English\",\"country\":\"China\"}}]}")));

		ArticleDto article = getEiosFacade().fetchEiosArticle(boardCriteria(), getConfigFacade().getEIOSConfig()).getArticles().get(0);

		Assertions.assertEquals("a-1", article.getEiosId());
		Assertions.assertEquals(ZonedDateTime.of(2024, 5, 9, 1, 28, 0, 0, ZoneOffset.UTC), article.getProcessedOnDate());
		Assertions.assertNull(article.getSource().getLanguage().getKey());
		Assertions.assertEquals("English", article.getSource().getLanguage().getLabel());
		Assertions.assertEquals("China", article.getSource().getCountry().getLabel());
	}

	@Test
	void fetchEiosArticleFailsOnErrorStatus() {
		stubFor(get(urlPathEqualTo(BOARD_ITEMS_PATH)).willReturn(aResponse().withStatus(500).withBody("boom")));

		WebApplicationException exception = Assertions
			.assertThrows(WebApplicationException.class, () -> getEiosFacade().fetchEiosArticle(boardCriteria(), getConfigFacade().getEIOSConfig()));
		Assertions.assertTrue(exception.getMessage().contains("500"));
	}

	@Test
	void fetchAndSaveBoardArticlesTest() throws Exception {
		stubBoardItems("mockFiles/eiosArticleResponse.json");
		ZonedDateTime lastSync = ZonedDateTime.of(2024, 5, 1, 12, 0, 0, 0, ZoneOffset.UTC);
		EiosBoardConfig boardConfig = createBoardConfig(lastSync);

		getEiosFacade().fetchAndSaveBoardArticles();

		useNationalAdminLogin();
		NewsCriteria criteria = new NewsCriteria();
		Assertions.assertEquals(10, getNewsFacade().count(criteria));
		List<NewsIndexDto> news = getNewsFacade().getIndexList(criteria, 0, 100, List.of());
		Assertions.assertEquals(10, news.size());

		// the sync window starts five minutes before the last sync and the board remembers the new sync time
		verify(getRequestedFor(urlPathEqualTo(BOARD_ITEMS_PATH)).withQueryParam("timeSince", equalTo("2024-05-01T11:55:00.000Z")));
		ZonedDateTime newSync = getEiosBoardConfigService().getByUuid(boardConfig.getUuid()).getStartDateTime();
		Assertions.assertTrue(newSync.isAfter(lastSync));
	}

	@Test
	void fetchAndSaveBoardArticlesTest_Batch() throws Exception {
		stubBoardItems("mockFiles/eiosArticleBatchResponse.json");
		createBoardConfig(ZonedDateTime.now(ZoneOffset.UTC));

		getEiosFacade().fetchAndSaveBoardArticles();

		// 400 reported articles are fetched in pages of 40; the repeated articles are stored once
		verify(10, getRequestedFor(urlPathEqualTo(BOARD_ITEMS_PATH)));
		verify(getRequestedFor(urlPathEqualTo(BOARD_ITEMS_PATH)).withQueryParam("start", equalTo("360")));
		useNationalAdminLogin();
		Assertions.assertEquals(10, getNewsFacade().count(new NewsCriteria()));
	}

	@Test
	void fetchAndSaveBoardArticlesUpdatesExistingArticle() throws Exception {
		stubBoardItems("mockFiles/eiosArticleResponse.json");
		createBoardConfig(ZonedDateTime.now(ZoneOffset.UTC));

		getEiosFacade().fetchAndSaveBoardArticles();
		getEiosFacade().fetchAndSaveBoardArticles();

		useNationalAdminLogin();
		Assertions.assertEquals(10, getNewsFacade().count(new NewsCriteria()));
	}

	@Test
	void fetchAndSaveBoardArticlesKeepsUserReview() throws Exception {
		stubBoardItems("mockFiles/eiosArticleResponse.json");
		createBoardConfig(ZonedDateTime.now(ZoneOffset.UTC));

		getEiosFacade().fetchAndSaveBoardArticles();
		useNationalAdminLogin();
		List<NewsIndexDto> news = getNewsFacade().getIndexList(new NewsCriteria(), 0, 100, List.of());
		Assertions.assertTrue(news.stream().allMatch(n -> n.getStatus() == NewsStatus.PENDING));
		news.forEach(n -> getNewsFacade().markApprove(new NewsReferenceDto(n.getUuid())));

		// the sync overlap fetches the same articles again; this must not reset the review of the user
		getEiosFacade().fetchAndSaveBoardArticles();

		news = getNewsFacade().getIndexList(new NewsCriteria(), 0, 100, List.of());
		Assertions.assertEquals(10, news.size());
		Assertions.assertTrue(news.stream().allMatch(n -> n.getStatus() == NewsStatus.APPROVED));
	}

	private EiosArticleCriteria boardCriteria() {
		EiosArticleCriteria criteria = new EiosArticleCriteria();
		criteria.setBoardId(12L);
		return criteria;
	}

	private EiosBoardConfig createBoardConfig(ZonedDateTime startDateTime) {
		EiosBoardConfig boardConfig = DtoHelper.fillOrBuildEntity(new EntityDto() {
		}, new EiosBoardConfig(), EiosBoardConfig::new, false);
		boardConfig.setBoardId(12L);
		boardConfig.setStartDateTime(startDateTime);
		boardConfig.setEnabled(true);
		getEiosBoardConfigService().ensurePersisted(boardConfig);
		return boardConfig;
	}

	private void stubBoardItems(String mockFile) throws Exception {
		Path path = Path.of(EiosFacadeEjbTest.class.getClassLoader().getResource(mockFile).toURI());
		stubFor(
			get(urlPathEqualTo(BOARD_ITEMS_PATH)).willReturn(
				aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8").withBody(Files.readString(path))));
	}

	private void setUpWireMockServer() {
		int mockServerPort = 8081;
		MockProducer.mockProperty(EIOS_URL, "http://localhost:" + mockServerPort);
		MockProducer.mockProperty(EIOS_ODI_URL, String.format("http://localhost:%s/token", mockServerPort));
		MockProducer.mockProperty(EIOS_CLIENT_ID, "eios-client");
		MockProducer.mockProperty(EIOS_CLIENT_SECRET, "eios-secret");
		MockProducer.mockProperty(EIOS_SCOPE, "eios-scope");
		wireMockServer = new WireMockServer(mockServerPort);
		WireMock.configureFor("localhost", mockServerPort);
		wireMockServer.start();
		stubFor(
			post(urlEqualTo("/token"))
				.willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("{\"access_token\":\"token\"}")));
	}
}
