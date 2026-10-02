package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.Disease;

public class SampleMaterialGonococcalInfectionTest extends AbstractSampleMaterialTest {

	@Override
	protected SampleMaterial[] getAllowedSampleMaterial() {
		return new SampleMaterial[] {
			SampleMaterial.GENITAL_SWAB,
			SampleMaterial.CLINICAL_SAMPLE,
			SampleMaterial.URINE,
			SampleMaterial.RECTAL_SWAB,
			SampleMaterial.OROPHARYNGEAL_SWAB,
			SampleMaterial.CONJUNCTIVAL_SWAB,
			SampleMaterial.BLOOD,
			SampleMaterial.CEREBROSPINAL_FLUID,
			SampleMaterial.SYNOVIAL_FLUID };
	}

	@Override
	protected Disease getDisease() {
		return Disease.GONOCOCCAL_INFECTION;
	}
}
