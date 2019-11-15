package de.symeda.sormas.api.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;

public class DataHelperTest {

	@Test
	public void testGetHumanClassName() throws Exception {
		assertEquals("CaseData", DataHelper.getHumanClassName(CaseDataDto.class));
		assertEquals("Disease", DataHelper.getHumanClassName(Disease.class));
	}

}
