package de.symeda.sormas.api.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;

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

	@Test
	public void testShortUuid() {
		EntityDto entityDto = new EntityDto() {
		};
		entityDto.setUuid("ABCDEF-GHIJKL");

		assertEquals("ABCDEF", DataHelper.getShortUuid(entityDto));

		ReferenceDto referenceDto = new ReferenceDto() {
		};
		referenceDto.setUuid("MNOPQR-STUVWX");

		assertEquals("MNOPQR", DataHelper.getShortUuid(referenceDto));

		assertEquals("UZOUEH", DataHelper.getShortUuid("UZOUEH-HP7DRG-YOJ74F-PXWL2JZ4"));
		assertNull(DataHelper.getShortUuid((String) null));

		boolean exceptionThrown = false;
		try {
			assertEquals("A", DataHelper.getShortUuid("A"));
			fail("getShortUuid should not be graceful on Uuids that are too short.");
		} catch (StringIndexOutOfBoundsException e) {
			exceptionThrown = true;
		}

		assertTrue("getShortUuid should throw StringIndexOutOfBoundsException on Uuids that are too short.", exceptionThrown);
	}

	@Test
	public void testEqualsTimeStampAndDate() {
		LocalDateTime localDateTime = LocalDateTime.of(2021, 11, 25, 11, 22);
		long time = localDateTime.toEpochSecond(ZoneOffset.UTC);

		assertTrue(DataHelper.equal(new Date(time), new Timestamp(time)));
		assertTrue(DataHelper.equal(new Timestamp(time), new Date(time)));
		assertFalse(DataHelper.equal(new Timestamp(time), null));
		assertFalse(DataHelper.equal(null, new Date(time)));
		assertFalse(DataHelper.equal("notdate", new Date(time)));
		assertFalse(DataHelper.equal(new Date(time), "notdate"));
	}
}
