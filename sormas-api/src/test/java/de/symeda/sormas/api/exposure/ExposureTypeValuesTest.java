package de.symeda.sormas.api.exposure;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.List;

import org.junit.jupiter.api.Test;

class ExposureTypeValuesTest {

	private static final List<ExposureType> RETIRED_ACTIVITIES =
		List.of(ExposureType.FOOD, ExposureType.RECREATIONAL_WATER, ExposureType.FLOOD_EXPOSURE);

	@Test
	void retiredActivitiesAreNeverOffered() {
		List<ExposureType> offered = ExposureType.getValues(EnumSet.allOf(ExposureCategory.class));

		RETIRED_ACTIVITIES.forEach(type -> assertFalse(offered.contains(type), type.name()));
	}

	@Test
	void choleraNoLongerOffersFloodedArea() {
		List<ExposureType> offered = ExposureType.getValues(EnumSet.of(ExposureCategory.WATER_BORNE, ExposureCategory.FOOD_BORNE));

		assertFalse(offered.contains(ExposureType.FLOOD_EXPOSURE));
	}

	@Test
	void directContactActivitiesStillOffered() {
		List<ExposureType> offered = ExposureType.getValues(EnumSet.of(ExposureCategory.DIRECT_CONTACT));

		assertTrue(offered.contains(ExposureType.SEXUAL_CONTACT));
		assertTrue(offered.contains(ExposureType.SYMPTOMATIC_CONTACT));
	}
}
