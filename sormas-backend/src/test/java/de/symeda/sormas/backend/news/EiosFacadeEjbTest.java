package de.symeda.sormas.backend.news;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static de.symeda.sormas.backend.common.ConfigFacadeEjb.EIOS_CLIENT_ID;
import static de.symeda.sormas.backend.common.ConfigFacadeEjb.EIOS_CLIENT_SECRET;
import static de.symeda.sormas.backend.common.ConfigFacadeEjb.EIOS_ODI_URL;
import static de.symeda.sormas.backend.common.ConfigFacadeEjb.EIOS_SCOPE;
import static de.symeda.sormas.backend.common.ConfigFacadeEjb.EIOS_URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.news.NewsCriteria;
import de.symeda.sormas.api.news.NewsIndexDto;
import de.symeda.sormas.api.news.eios.ArticleDto;
import de.symeda.sormas.api.news.eios.EiosArticleCriteria;
import de.symeda.sormas.api.news.eios.EiosArticlesResponse;
import de.symeda.sormas.api.news.eios.EiosConfig;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.util.DtoHelper;

class EiosFacadeEjbTest extends AbstractBeanTest {

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
		Path path = Path.of(EiosFacadeEjbTest.class.getClassLoader().getResource("mockFiles/eiosArticleResponse.json").toURI());
		var response = Files.readString(path);
		stubFor(
			get(urlPathEqualTo("/GetBoardArticles")).withQueryParam("boardId", equalTo("12"))
				.willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8").withBody(response)));

		EiosFacadeEjb.EiosFacadeEjbLocal eiosFacade = getEiosFacade();
		EiosArticleCriteria criteria = new EiosArticleCriteria();
		criteria.setTimespan(new Date(2000, 1, 1).getTime());
		criteria.setBoardId(12L);
		EiosConfig eiosConfig = getConfigFacade().getEIOSConfig();
		EiosArticlesResponse eiosArticlesResponse = eiosFacade.fetchEiosArticle(criteria, eiosConfig);
		List<ArticleDto> newsDtos = eiosArticlesResponse.getArticles();
		Assertions.assertNotNull(newsDtos);
		Assertions.assertEquals(10, newsDtos.size());
	}

	@Test
	void fetchAndSaveBoardArticlesTest() throws Exception {
		Path path = Path.of(EiosFacadeEjbTest.class.getClassLoader().getResource("mockFiles/eiosArticleResponse.json").toURI());
		var response = Files.readString(path);
		stubFor(
			get(urlPathEqualTo("/GetBoardArticles")).withQueryParam("boardId", equalTo("12"))
				.willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8").withBody(response)));
		EiosBoardConfig newConfig = DtoHelper.fillOrBuildEntity(new EntityDto() {
		}, new EiosBoardConfig(), EiosBoardConfig::new, false);
		newConfig.setBoardId(12L);
		newConfig.setStartTimeStamp(DateHelper.now());
		newConfig.setEnabled(true);
		getEiosBoardConfigService().ensurePersisted(newConfig);
		EiosFacadeEjb.EiosFacadeEjbLocal eiosFacade = getEiosFacade();
		eiosFacade.fetchAndSaveBoardArticles();
		useNationalAdminLogin();
		NewsCriteria criteria = new NewsCriteria();
		long count = getNewsFacade().count(criteria);
		Assertions.assertEquals(10, count);

		List<NewsIndexDto> eiosArticles = getNewsFacade().getIndexList(criteria, 0, 100, List.of());
		Assertions.assertEquals(10, eiosArticles.size());
	}

	@Test
	void fetchAndSaveBoardArticlesTest_Batch() throws Exception {
		Path path = Path.of(EiosFacadeEjbTest.class.getClassLoader().getResource("mockFiles/eiosArticleBatchResponse.json").toURI());
		var response = Files.readString(path);
		stubFor(
			get(urlPathEqualTo("/GetBoardArticles")).withQueryParam("boardId", equalTo("12"))
				.willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8").withBody(response)));
		EiosBoardConfig newConfig = DtoHelper.fillOrBuildEntity(new EntityDto() {
		}, new EiosBoardConfig(), EiosBoardConfig::new, false);
		newConfig.setBoardId(12L);
		newConfig.setStartTimeStamp(DateHelper.now());
		newConfig.setEnabled(true);
		getEiosBoardConfigService().ensurePersisted(newConfig);
		EiosFacadeEjb.EiosFacadeEjbLocal eiosFacade = getEiosFacade();
		eiosFacade.fetchAndSaveBoardArticles();
		useNationalAdminLogin();
		NewsCriteria criteria = new NewsCriteria();
		long count = getNewsFacade().count(criteria);
		Assertions.assertEquals(10, count);
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
