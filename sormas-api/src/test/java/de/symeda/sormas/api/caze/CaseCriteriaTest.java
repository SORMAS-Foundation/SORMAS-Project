package de.symeda.sormas.api.caze;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class CaseCriteriaTest {

	@Test
	public void testFromToUrlParams() {

		CaseCriteria criteria = new CaseCriteria();
		DistrictReferenceDto district = new DistrictReferenceDto(DataHelper.createUuid(), null, null);
		criteria.setDistrict(district);
		criteria.setNameUuidEpidNumberLike("test AHSDBSD-ADS");
		criteria.setEventLike("test EVENT");
		criteria.setDisease(Disease.CSM);

		CaseCriteria generatedCriteria = new CaseCriteria();
		generatedCriteria.fromUrlParams(criteria.toUrlParams());
		assertEquals(criteria.getDistrict(), generatedCriteria.getDistrict());
		assertEquals(criteria.getDisease(), generatedCriteria.getDisease());
		assertEquals(criteria.getEventLike(), generatedCriteria.getEventLike());
	}
}
