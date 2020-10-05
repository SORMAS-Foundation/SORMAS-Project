package de.symeda.sormas.api.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class DataHelperTest {

	@Test
	public void testGetHumanClassName() {

		assertEquals("CaseData", DataHelper.getHumanClassName(CaseDataDto.class));
		assertEquals("Disease", DataHelper.getHumanClassName(Disease.class));
		assertEquals("RegionReference", DataHelper.getHumanClassName(RegionReferenceDto.class));
	}

	@Test
	public void testTryParseInt() {

		assertNull(DataHelper.tryParseInt(null));
		assertNull(DataHelper.tryParseInt("null"));
		assertNull(DataHelper.tryParseInt("null1"));
		assertNull(DataHelper.tryParseInt("1null"));
		assertEquals((Integer) 1, DataHelper.tryParseInt("1"));
		assertEquals((Integer) Integer.MAX_VALUE, DataHelper.tryParseInt(String.valueOf(Integer.MAX_VALUE)));
		assertNull(DataHelper.tryParseInt(String.valueOf(Integer.MAX_VALUE) + "0"));
	}

	@Test
	public void testTryParseLong() {

		assertNull(DataHelper.tryParseLong(null));
		assertNull(DataHelper.tryParseLong("null"));
		assertNull(DataHelper.tryParseLong("null1"));
		assertNull(DataHelper.tryParseLong("1null"));
		assertEquals((Long) 1l, DataHelper.tryParseLong("1"));
		assertEquals((Long) Long.MAX_VALUE, DataHelper.tryParseLong(String.valueOf(Long.MAX_VALUE)));
		assertNull(DataHelper.tryParseLong(String.valueOf(Long.MAX_VALUE) + "0"));
	}
}
