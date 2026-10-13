package de.symeda.sormas.backend.news;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "EiosFacade")
public class EiosFacadeEjb implements EiosFacade {

	private static final Integer MAX_FETCH_SIZE = 40;
	private static final Duration SYNC_OVERLAP = Duration.ofMinutes(5);
	private static final DateTimeFormatter EIOS_DATE_TIME_FORMATTER =
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);
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
			newsService.saveEiosArticleInNewTransaction(articleDto);
		} catch (Exception exe) {
			LOGGER.error("Error while saving EIOS article {}", articleDto.getEiosId(), exe);
		}
	}

	/**
	 * Runs without a transaction so the HTTP paging is not held in one long transaction; every article and the board sync time
	 * are saved in their own transaction.
	 */
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void fetchAndSaveBoardArticles() {
		LOGGER.info("Started fetching article at: {}", new Date());
		ZonedDateTime currentTime = ZonedDateTime.now(ZoneOffset.UTC);

		EiosConfig eiosConfig = configFacade.getEIOSConfig();
		List<EiosBoardConfig> enabledBoards = eiosBoardConfigService.getEnabledBoards();
		enabledBoards.forEach(eiosBoardConfig -> {
			try {
				EiosArticleCriteria criteria = createCriteriaFromBoardConfig(eiosBoardConfig);
				criteria.setTimeUntil(currentTime);
				batchFetchBatchArticleAndSave(criteria, eiosConfig);
				eiosBoardConfigService.updateStartDateTimeInNewTransaction(eiosBoardConfig.getUuid(), currentTime);
			} catch (Exception e) {
				LOGGER.error("Error while fetching and saving articles of board {}", eiosBoardConfig.getBoardId(), e);
			}
		});
	}

	private void batchFetchBatchArticleAndSave(EiosArticleCriteria criteria, EiosConfig eiosConfig) throws Exception {
		for (int offset = 0;; offset += MAX_FETCH_SIZE) {
			criteria.setStart(offset);
			criteria.setLimit(MAX_FETCH_SIZE);
			EiosArticlesResponse eiosArticlesResponse = fetchEiosArticle(criteria, eiosConfig);
			if (eiosArticlesResponse == null) {
				LOGGER.warn("EIOS response is null for board {} at offset {}", criteria.getBoardId(), offset);
				break;
			}

			List<ArticleDto> newsDtos = eiosArticlesResponse.getArticles();
			if (newsDtos == null || newsDtos.isEmpty()) {
				LOGGER.info("No EIOS articles returned for board {} at offset {}", criteria.getBoardId(), offset);
				break;
			}

			Long total = eiosArticlesResponse.getCount();
			LOGGER.info("Fetched page with {} articles; reported total: {}", newsDtos.size(), total);
			newsDtos.stream().filter(Objects::nonNull).forEach(this::saveArticle);

			if (total != null && offset + MAX_FETCH_SIZE >= total) {
				break;
			}
			if (total == null && newsDtos.size() < MAX_FETCH_SIZE) {
				break;
			}
		}
	}

	public EiosArticlesResponse fetchEiosArticle(EiosArticleCriteria criteria, EiosConfig eiosConfig) throws Exception {
		final String boardArticlePath = String.format("Items/matching-board/%s", criteria.getBoardId());
		final String url = boardArticlePath + "?" + buildArticleQueryParams(criteria);
		LOGGER.info("Fetching EIOS articles with URL: {}", url);
		EIOSRestClient eiosRestClient = new EIOSRestClient(eiosConfig);
		return eiosRestClient.get(url, EiosArticlesResponse.class);
	}

	private String buildArticleQueryParams(EiosArticleCriteria criteria) {
		StringBuilder urlFilter = new StringBuilder();
		appendUrlParam(urlFilter, "timeSince", formatDateTime(criteria.getTimeSince()));
		appendUrlParam(urlFilter, "timeUntil", formatDateTime(criteria.getTimeUntil()));
		appendUrlParam(urlFilter, "start", criteria.getStart());
		appendUrlParam(urlFilter, "limit", criteria.getLimit());
		return urlFilter.toString();
	}

	private String formatDateTime(ZonedDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return EIOS_DATE_TIME_FORMATTER.format(dateTime.toInstant());
	}

	private void appendUrlParam(StringBuilder queryBuilder, String key, Object value) {
		if (value == null) {
			return;
		}
		if (queryBuilder.length() > 0) {
			queryBuilder.append('&');
		}
		queryBuilder.append(key).append('=').append(URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8));
	}

	private EiosArticleCriteria createCriteriaFromBoardConfig(EiosBoardConfig eiosBoardConfig) {
		EiosArticleCriteria criteria = new EiosArticleCriteria();
		criteria.setBoardId(eiosBoardConfig.getBoardId());
		ZonedDateTime boardSyncTime = eiosBoardConfig.getStartDateTime();
		if (boardSyncTime == null) {
			boardSyncTime = ZonedDateTime.now(ZoneOffset.UTC);
		}
		// overlap with the previous sync so articles EIOS indexes with a small delay are not missed
		criteria.setTimeSince(boardSyncTime.minus(SYNC_OVERLAP));
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

	@Stateless
	@LocalBean
	public static class EiosFacadeEjbLocal extends EiosFacadeEjb {
	}
}
