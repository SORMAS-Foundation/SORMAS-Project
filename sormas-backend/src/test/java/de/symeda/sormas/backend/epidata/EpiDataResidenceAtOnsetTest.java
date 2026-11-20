package de.symeda.sormas.backend.epidata;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;

class EpiDataResidenceAtOnsetTest extends AbstractBeanTest {

	@Test
	void residenceAtOnsetCountryRoundTrips() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = useNationalAdminLogin();
		CountryReferenceDto france = CountryFacadeEjb.toReferenceDto(creator.createCountry("France", "FRA", "250"));
		CaseDataDto caze = creator.createCase(user.toReference(), rdcf, c -> c.setDisease(Disease.RUBELLA));

		caze.getEpiData().setResidenceAtOnsetCountry(france);
		caze.getEpiData().setResidenceAtOnset("Esch-sur-Alzette");
		getCaseFacade().save(caze);

		EpiDataDto saved = getCaseFacade().getByUuid(caze.getUuid()).getEpiData();
		assertEquals(france, saved.getResidenceAtOnsetCountry());
		assertEquals("Esch-sur-Alzette", saved.getResidenceAtOnset());
	}
}
