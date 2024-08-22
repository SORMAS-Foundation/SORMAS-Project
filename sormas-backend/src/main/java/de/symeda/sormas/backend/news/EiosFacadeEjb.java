package de.symeda.sormas.backend.news;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.news.NewsDto;
import de.symeda.sormas.api.news.eios.ArticleDto;
import de.symeda.sormas.api.news.eios.EiosArticleCriteria;
import de.symeda.sormas.api.news.eios.EiosArticlesResponse;
import de.symeda.sormas.api.news.eios.EiosConfig;
import de.symeda.sormas.api.news.eios.EiosFacade;
import de.symeda.sormas.api.news.eios.NewsStatus;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "EiosFacade")
public class EiosFacadeEjb implements EiosFacade {

	private static final Integer MAX_FETCH_SIZE = 40;
	private final Logger LOGGER = LoggerFactory.getLogger(EiosFacadeEjb.class);
	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;
	@EJB
	private NewsService newsService;

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	@EJB
	private EiosBoardConfigService eiosBoardConfigService;

	public void save(@Valid @NotNull NewsDto newsDto) {
		try {
			News news = newsService.getByUuid(newsDto.getUuid());
			news = fillOrBuildEntity(newsDto, news, true);
			newsService.ensurePersisted(news);
			News article = newsService.getByUuid(news.getUuid());
		} catch (Exception exe) {
			LOGGER.error("Error while saving {}", newsDto, exe);
		}
	}

	public void saveArticle(@Valid @NotNull ArticleDto articleDto) {
		try {
			News existingNews = newsService.getByEiosId(articleDto.getEiosId());
			existingNews = fillOrBuildEntity(articleDto, existingNews, false);
			newsService.ensurePersisted(existingNews);
			News article = newsService.getByEiosId(existingNews.getEiosId());
		} catch (Exception exe) {
			LOGGER.error("Error while saving {}", articleDto, exe);
		}
	}

	public void fetchAndSaveBoardArticles() {
		LOGGER.info("Started fetching article at: {}", new Date());
		long currentTimespan = DateHelper.now();

		EiosConfig eiosConfig = configFacade.getEIOSConfig();
		List<EiosBoardConfig> enabledBoards = eiosBoardConfigService.getEnabledBoards();
		enabledBoards.forEach(eiosBoardConfig -> {
			try {
				EiosArticleCriteria criteria = createCriteriaFromBoardConfig(eiosBoardConfig);
				criteria.setUntilTimespan(currentTimespan);
				batchFetchBatchArticleAndSave(criteria, eiosConfig);
				eiosBoardConfig.setStartTimeStamp(currentTimespan);
				eiosBoardConfigService.ensurePersisted(eiosBoardConfig);
			} catch (Exception e) {
				LOGGER.error("Error while fetching and saving board articles", e);
			}
		});
	}

	private void batchFetchBatchArticleAndSave(EiosArticleCriteria criteria, EiosConfig eiosConfig) throws Exception {
		long total = Integer.MAX_VALUE;
		for (int offset = 0; offset < total; offset += MAX_FETCH_SIZE) {
			criteria.setStart(offset);
			criteria.setLimit(MAX_FETCH_SIZE);
			EiosArticlesResponse eiosArticlesResponse = fetchEiosArticle(criteria, eiosConfig);
			total = eiosArticlesResponse.getCount();
			List<ArticleDto> newsDtos = eiosArticlesResponse.getArticles();
			newsDtos.forEach(eiosArticleDto -> {
				eiosArticleDto.setStatus(NewsStatus.PENDING);
				saveArticle(eiosArticleDto);
			});
		}
	}

	public EiosArticlesResponse fetchEiosArticle(EiosArticleCriteria criteria, EiosConfig eiosConfig) throws Exception {
		final String boardArticlePath = "GetBoardArticles";
		final String url = boardArticlePath + "?" + criteria.toUrlParams();
		EIOSRestClient eiosRestClient = new EIOSRestClient(eiosConfig);
		EiosArticlesResponse eiosArticlesResponse = eiosRestClient.get(url, EiosArticlesResponse.class);
		return eiosArticlesResponse;
	}

	private EiosArticleCriteria createCriteriaFromBoardConfig(EiosBoardConfig eiosBoardConfig) {
		EiosArticleCriteria criteria = new EiosArticleCriteria();
		criteria.setBoardId(eiosBoardConfig.getBoardId());
		criteria.setTimespan(eiosBoardConfig.getStartTimeStamp());
		return criteria;
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
		target.setEiosId(source.getEiosId());
		return target;
	}

	public News fillOrBuildEntity(ArticleDto source, News target, boolean checkChangeDate) {
		if (source == null) {
			return null;
		}
		target = DtoHelper.fillOrBuildEntity(source, target, News::build, checkChangeDate);
		target.setTitle(source.getTitle());
		target.setUrl(source.getLink());
		target.setDescription(source.getDescription());
		target.setEiosUrl(source.getEiosUrl());
		target.setNewsDate(source.getProcessedOnDate());
		target.setRiskLevel(source.getRiskLevel());
		target.setStatus(source.getStatus());
		target.setEiosId(source.getEiosId());
		return target;
	}

	@Stateless
	@LocalBean
	public static class EiosFacadeEjbLocal extends EiosFacadeEjb {
	}
}
