package de.symeda.sormas.backend.importexport.hierarchical.caze;

import java.util.Arrays;
import java.util.Map;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.backend.util.ValidationProblem;

final class ImportProblemWording {

	private static final Map<String, String> IMPORT_KEYS = Map.of(
		Validations.validFacility,
		Validations.importPlaceOfStayMissing,
		Validations.noFacilityType,
		Validations.importFacilityTypeMissing,
		Validations.notAccomodationFacilityType,
		Validations.importFacilityTypeNotAccommodation,
		Validations.validPointOfEntry,
		Validations.importPointOfEntryMissing,
		Validations.validResponsibleRegion,
		Validations.importResponsibleRegionMissing,
		Validations.validResponsibleDistrict,
		Validations.importResponsibleDistrictMissing);

	private ImportProblemWording() {
	}

	private static Object[] messageArgs(ValidationProblem problem) {
		return Arrays.stream(problem.getMessageArgs()).map(arg -> arg instanceof FacilityType ? ((FacilityType) arg).name() : arg).toArray();
	}

	static String reword(ValidationProblem problem) {
		String importKey = problem.getValidationKey() == null ? null : IMPORT_KEYS.get(problem.getValidationKey());
		return importKey == null ? problem.getMessage() : I18nProperties.getValidationError(importKey, messageArgs(problem));
	}
}
