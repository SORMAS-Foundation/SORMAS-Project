package de.symeda.sormas.backend.news;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jetbrains.annotations.NotNull;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless
@LocalBean
public class EiosBoardConfigService extends BaseAdoService<EiosBoardConfig> {

	public EiosBoardConfigService() {
		super(EiosBoardConfig.class);
	}

	public void manageEiosConfigAtStartUp(String boardIds) {
		List<Long> boardIdList =
			Arrays.stream(boardIds.split(",")).filter(s -> !s.isEmpty()).map(String::trim).map(Long::parseLong).collect(Collectors.toList());
		List<EiosBoardConfig> currentConfigs = getAll();
		enableBoard(boardIdList, currentConfigs);
		disableBoard(boardIdList, currentConfigs);
	}

	public List<EiosBoardConfig> getEnabledBoards() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EiosBoardConfig> cq = cb.createQuery(EiosBoardConfig.class);
		Root<EiosBoardConfig> boardConfigRoot = cq.from(EiosBoardConfig.class);
		CriteriaQuery<EiosBoardConfig> enabledBoard = cq.select(boardConfigRoot).where(cb.isTrue(boardConfigRoot.get(EiosBoardConfig.ENABLE)));
		return em.createQuery(enabledBoard).getResultList();
	}

	private void disableBoard(List<Long> boardIdList, List<EiosBoardConfig> currentConfigs) {
		currentConfigs.stream().filter(c -> !boardIdList.contains(c.getBoardId())).forEach(c -> {
			c.setEnabled(false);
			ensurePersisted(c);
		});
	}

	private void enableBoard(List<Long> boardIdList, List<EiosBoardConfig> currentConfigs) {
		for (Long boardId : boardIdList) {
			EiosBoardConfig config =
				currentConfigs.stream().filter(c -> c.getBoardId().equals(boardId)).findFirst().orElseGet(() -> createNewBoard(boardId));
			config.setEnabled(true);
			ensurePersisted(config);
		}
	}

	@NotNull
	private EiosBoardConfig createNewBoard(Long boardId) {
		EiosBoardConfig newConfig = DtoHelper.fillOrBuildEntity(new EntityDto() {
		}, new EiosBoardConfig(), EiosBoardConfig::new, false);
		newConfig.setBoardId(boardId);
		newConfig.setStartTimeStamp(new Date().getTime());
		return newConfig;
	}

}
