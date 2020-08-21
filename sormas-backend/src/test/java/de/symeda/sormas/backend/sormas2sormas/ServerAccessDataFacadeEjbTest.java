package de.symeda.sormas.backend.sormas2sormas;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;

import de.symeda.sormas.api.sormas2sormas.ServerAccessDataDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public class ServerAccessDataFacadeEjbTest extends AbstractBeanTest {

	private static final String TEST_SORMAS2SORMAS_PATH = "src/test/resources/sormas2sormas/";

	@InjectMocks
	private ServerAccessDataFacadeEjb.ServerAccessDataFacadeEjbLocal serverAccessDataFacade;

	@Mock
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getServerAccessDataListTest() {
		File file = new File(TEST_SORMAS2SORMAS_PATH);
		String absolutePath = file.getAbsolutePath();
		Mockito.when(configFacade.getSormas2sormasFilesPath()).thenReturn(absolutePath);

		ServerAccessDataDto dto1 = new ServerAccessDataDto();
		dto1.setCommonName("testID");
		dto1.setHealthDepartment("testName");
		dto1.setUrl("testURL");

		ServerAccessDataDto dto2 = new ServerAccessDataDto();
		dto2.setCommonName("testID2");
		dto2.setHealthDepartment("testName2");
		dto2.setUrl("testURL2");

		List<ServerAccessDataDto> expected = Lists.newArrayList(dto1, dto2);

		List<ServerAccessDataDto> actual = serverAccessDataFacade.getServerAccessDataList();
		assertThat(actual, is(expected));
	}
}
