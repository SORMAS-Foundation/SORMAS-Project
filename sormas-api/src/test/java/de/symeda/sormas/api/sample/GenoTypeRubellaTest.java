package de.symeda.sormas.api.sample;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.DiseaseFieldVisibilityChecker;

public class GenoTypeRubellaTest {

	private static final List<GenoType> RUBE6_GENOTYPES = List.of(
		GenoType.GENOTYPE_1A,
		GenoType.GENOTYPE_1B,
		GenoType.GENOTYPE_1C,
		GenoType.GENOTYPE_1D,
		GenoType.GENOTYPE_1E,
		GenoType.GENOTYPE_1F,
		GenoType.GENOTYPE_1G,
		GenoType.GENOTYPE_1H,
		GenoType.GENOTYPE_1I,
		GenoType.GENOTYPE_1J,
		GenoType.GENOTYPE_2A,
		GenoType.GENOTYPE_2B,
		GenoType.GENOTYPE_2C,
		GenoType.GENOTYPE_NA,
		GenoType.GENOTYPE_UNK,
		GenoType.OTHER);

	private final DiseaseFieldVisibilityChecker rubellaChecker = new DiseaseFieldVisibilityChecker(Disease.RUBELLA);
	private final DiseaseFieldVisibilityChecker congenitalRubellaChecker = new DiseaseFieldVisibilityChecker(Disease.CONGENITAL_RUBELLA);

	@Test
	public void everyRube6GenotypeIsSelectableForRubella() {
		for (GenoType genoType : RUBE6_GENOTYPES) {
			assertTrue(
				rubellaChecker.isVisible(GenoType.class, genoType.name()),
				genoType.name() + " must be selectable for Rubella (#14293 genotype list)");
		}
	}

	@Test
	public void everyRube6GenotypeIsSelectableForCongenitalRubella() {
		for (GenoType genoType : RUBE6_GENOTYPES) {
			assertTrue(
				congenitalRubellaChecker.isVisible(GenoType.class, genoType.name()),
				genoType.name() + " must be selectable for Congenital Rubella Syndrome (#14293 genotype list)");
		}
	}

	@Test
	public void noUnapprovedGenotypeIsSelectableForRubella() {
		for (GenoType genoType : GenoType.values()) {
			if (RUBE6_GENOTYPES.contains(genoType) || genoType == GenoType.UNKNOWN) {
				continue;
			}
			assertFalse(
				rubellaChecker.isVisible(GenoType.class, genoType.name()),
				genoType.name() + " leaks into the Rubella genotype picker — hide it (#14293)");
		}
	}

	@Test
	public void noUnapprovedGenotypeIsSelectableForCongenitalRubella() {
		for (GenoType genoType : GenoType.values()) {
			if (RUBE6_GENOTYPES.contains(genoType) || genoType == GenoType.UNKNOWN) {
				continue;
			}
			assertFalse(
				congenitalRubellaChecker.isVisible(GenoType.class, genoType.name()),
				genoType.name() + " leaks into the Congenital Rubella Syndrome genotype picker — hide it (#14293)");
		}
	}

	@Test
	public void genoTypeFieldIsVisibleForRubellaAndCongenitalRubella() {
		assertTrue(
			rubellaChecker.isVisible(PathogenTestDto.class, PathogenTestDto.GENOTYPE),
			"PathogenTestDto.genoType must be visible for Rubella (#14293)");
		assertTrue(
			congenitalRubellaChecker.isVisible(PathogenTestDto.class, PathogenTestDto.GENOTYPE),
			"PathogenTestDto.genoType must be visible for Congenital Rubella Syndrome (#14293)");
	}

	@Test
	public void genoTypeTextFieldIsVisibleForRubellaAndCongenitalRubella() {
		assertTrue(
			rubellaChecker.isVisible(PathogenTestDto.class, PathogenTestDto.GENOTYPE_TEXT),
			"PathogenTestDto.genoTypeText must be visible for Rubella (#14293)");
		assertTrue(
			congenitalRubellaChecker.isVisible(PathogenTestDto.class, PathogenTestDto.GENOTYPE_TEXT),
			"PathogenTestDto.genoTypeText must be visible for Congenital Rubella Syndrome (#14293)");
	}
}
