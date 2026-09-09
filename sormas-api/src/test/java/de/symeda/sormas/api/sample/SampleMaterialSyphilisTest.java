package de.symeda.sormas.api.sample;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.DiseaseFieldVisibilityChecker;

public class SampleMaterialSyphilisTest {

	private static final List<SampleMaterial> APPROVED = List.of(
		SampleMaterial.AMNIOTIC_FLUID,
		SampleMaterial.BLOOD,
		SampleMaterial.CEREBROSPINAL_FLUID,
		SampleMaterial.CLINICAL_SAMPLE,
		SampleMaterial.CORD_BLOOD,
		SampleMaterial.DRY_BLOOD,
		SampleMaterial.GENITAL_SWAB,
		SampleMaterial.OROPHARYNGEAL_SWAB,
		SampleMaterial.PLACENTA,
		SampleMaterial.RECTAL_SWAB,
		SampleMaterial.SERA,
		SampleMaterial.UNKNOWN);

	private static final List<SampleMaterial> TRIMMED = List.of(
		SampleMaterial.ABSCESS_SWAB,
		SampleMaterial.ANTERIOR_NARES_SWAB,
		SampleMaterial.BONE,
		SampleMaterial.BONE_MARROW,
		SampleMaterial.BRAIN_TISSUE,
		SampleMaterial.BRONCHOALVEOLAR_LAVAGE,
		SampleMaterial.CONJUNCTIVAL_SWAB,
		SampleMaterial.CRUST,
		SampleMaterial.EDTA_WHOLE_BLOOD,
		SampleMaterial.ENDOTRACHEAL_ASPIRATE,
		SampleMaterial.LUNG_TISSUE,
		SampleMaterial.MIDDLE_EAR_FLUID,
		SampleMaterial.NP_ASPIRATE,
		SampleMaterial.NP_SWAB,
		SampleMaterial.NUCHAL_SKIN_BIOPSY,
		SampleMaterial.OP_ASPIRATE,
		SampleMaterial.PERITONEAL_FLUID,
		SampleMaterial.PLASMA,
		SampleMaterial.PLEURAL_FLUID,
		SampleMaterial.SALIVA,
		SampleMaterial.SPUTUM,
		SampleMaterial.STOOL,
		SampleMaterial.SWAB_UNSPECIFIED,
		SampleMaterial.SYNOVIAL_FLUID,
		SampleMaterial.TEARS,
		SampleMaterial.TISSUE,
		SampleMaterial.ULCER_SWAB,
		SampleMaterial.URINE);

	private final DiseaseFieldVisibilityChecker checker = new DiseaseFieldVisibilityChecker(Disease.SYPHILIS);

	@Test
	public void everyApprovedSpecimenIsSelectableForSyphilis() {
		for (SampleMaterial material : APPROVED) {
			assertFalse(material.isDeprecated(), material.name() + " is retired and cannot be on the Syphilis specimen list");
			assertTrue(
				checker.isVisible(SampleMaterial.class, material.name()),
				material.name() + " must be selectable for Syphilis (#14220 specimen list)");
		}
	}

	@Test
	public void everyTrimmedSpecimenIsHiddenForSyphilis() {
		for (SampleMaterial material : TRIMMED) {
			assertFalse(
				checker.isVisible(SampleMaterial.class, material.name()),
				material.name() + " must not be offered for Syphilis (#14220 specimen list)");
		}
	}

	@Test
	public void noUnapprovedSpecimenIsSelectableForSyphilis() {
		for (SampleMaterial material : SampleMaterial.values()) {
			if (APPROVED.contains(material) || material.isDeprecated()) {
				continue;
			}
			assertFalse(
				checker.isVisible(SampleMaterial.class, material.name()),
				material.name() + " leaks into the Syphilis specimen picker — approve it or hide it");
		}
	}
}
