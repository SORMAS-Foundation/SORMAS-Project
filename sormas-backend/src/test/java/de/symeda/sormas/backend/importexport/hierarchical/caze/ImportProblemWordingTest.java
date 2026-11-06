package de.symeda.sormas.backend.importexport.hierarchical.caze;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.backend.util.ValidationProblem;

public class ImportProblemWordingTest {

	@Test
	public void aFacilityTypeThatIsNoPlaceOfStayNamesTheType() {
		ValidationProblem problem = new ValidationProblem(
			Validations.notAccomodationFacilityType,
			"form wording",
			new Object[] {
				FacilityType.LABORATORY },
			"facilityType");

		assertEquals(I18nProperties.getValidationError(Validations.importFacilityTypeNotAccommodation, FacilityType.LABORATORY), ImportProblemWording.reword(problem));
	}

	@Test
	public void aProblemWithoutImportWordingKeepsItsMessage() {
		assertEquals("as is", ImportProblemWording.reword(new ValidationProblem(Validations.validDisease, "as is", "disease")));
	}

	@Test
	public void aProblemWithoutKeyKeepsItsMessage() {
		assertEquals("as is", ImportProblemWording.reword(new ValidationProblem(null, "as is")));
	}
}
