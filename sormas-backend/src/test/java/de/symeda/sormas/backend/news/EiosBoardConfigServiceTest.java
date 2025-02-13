package de.symeda.sormas.backend.news;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.util.DtoHelper;

class EiosBoardConfigServiceTest extends AbstractBeanTest {

	@Test
	void manageEiosConfigAtStartUp() {
		EiosBoardConfigService eiosBoardConfigService = getEiosBoardConfigService();
		eiosBoardConfigService.manageEiosConfigAtStartUp("1,2,3,4");
		List<EiosBoardConfig> boardConfigs = eiosBoardConfigService.getAll();
		Assertions.assertEquals(4, boardConfigs.size());
	}

	@Test
	void manageEiosConfigAtStartUp_WithEmptyBoardIds() {
		EiosBoardConfigService eiosBoardConfigService = getEiosBoardConfigService();
		eiosBoardConfigService.manageEiosConfigAtStartUp("");
		List<EiosBoardConfig> boardConfigs = eiosBoardConfigService.getAll();
		Assertions.assertEquals(0, boardConfigs.size());
	}

	@Test
	void manageEiosConfigAtStartUp_EnableBoardToDisable() {
		EiosBoardConfigService eiosBoardConfigService = getEiosBoardConfigService();
		EiosBoardConfig eiosBoardConfig = DtoHelper.fillOrBuildEntity(new EntityDto() {
		}, new EiosBoardConfig(), EiosBoardConfig::new, false);
		eiosBoardConfig.setBoardId(5L);
		eiosBoardConfig.setEnabled(true);
		eiosBoardConfigService.ensurePersisted(eiosBoardConfig);

		eiosBoardConfigService.manageEiosConfigAtStartUp("1,2,3,4");
		List<EiosBoardConfig> boardConfigs = eiosBoardConfigService.getAll();
		Assertions.assertEquals(5, boardConfigs.size());

		List<EiosBoardConfig> enabledBoards = eiosBoardConfigService.getEnabledBoards();
		Assertions.assertEquals(4, enabledBoards.size());
	}
}
