package de.symeda.sormas.api.infrastructure;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;

public final class InfrastructureHelper {

	private InfrastructureHelper() {
		// Hide Utility Class Constructor
	}

	public static final int CASE_INCIDENCE_DIVISOR = 100000;
	
	public static String buildPointOfEntryString(String pointOfEntryUuid, String pointOfEntryName, String pointOfEntryDetails) {
		StringBuilder result = new StringBuilder();
		result.append(buildPointOfEntryString(pointOfEntryUuid, pointOfEntryName));

		if (!DataHelper.isNullOrEmpty(pointOfEntryDetails)) {
			if (result.length() > 0) {
				result.append(" - ");			
			}
			result.append(pointOfEntryDetails);
		}		
		return result.toString();
	}

	public static String buildPointOfEntryString(String pointOfEntryUuid, String pointOfEntryName) {
		if (pointOfEntryUuid != null) {
			if (pointOfEntryUuid.equals(PointOfEntryDto.OTHER_AIRPORT_UUID)) {
				return I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, PointOfEntryDto.OTHER_AIRPORT);
			}
			if (pointOfEntryUuid.equals(PointOfEntryDto.OTHER_SEAPORT_UUID)) {
				return I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, PointOfEntryDto.OTHER_SEAPORT);
			}
			if (pointOfEntryUuid.equals(PointOfEntryDto.OTHER_GROUND_CROSSING_UUID)) {
				return I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, PointOfEntryDto.OTHER_GROUND_CROSSING);
			}
			if (pointOfEntryUuid.equals(PointOfEntryDto.OTHER_POE_UUID)) {
				return I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, PointOfEntryDto.OTHER_POE);
			}
		}

		StringBuilder caption = new StringBuilder();
		if (!DataHelper.isNullOrEmpty(pointOfEntryName)) {
			caption.append(pointOfEntryName);
		}

		return caption.toString();
	}

	public static Integer getProjectedPopulation(Integer population, Date collectionDate, Float growthRate) {
		if (population != null && population > 0) {
			Integer yearsBetween = DateHelper.getYearsBetween(collectionDate, new Date());
			int calculatedYears = 0;
			while (calculatedYears < yearsBetween) {
				population += (int) (population / 100 * growthRate);
				calculatedYears++;
			}
		}

		return population;
	}

	public static BigDecimal getCaseIncidence(int caseCount, int population, int divisor) {
		return new BigDecimal(caseCount).divide(
				new BigDecimal((double)population / divisor), 2,
				RoundingMode.HALF_UP);
	}

}
