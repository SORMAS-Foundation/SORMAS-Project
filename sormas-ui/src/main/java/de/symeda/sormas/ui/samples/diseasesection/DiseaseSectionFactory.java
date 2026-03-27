package de.symeda.sormas.ui.samples.diseasesection;

import de.symeda.sormas.api.Disease;

/**
 * Factory for component-based disease sections.
 */
public final class DiseaseSectionFactory {

	private DiseaseSectionFactory() {
	}

	public static AbstractDiseaseSectionComponent forDisease(Disease disease) {
		if (disease == null) {
			return new DefaultSectionComponent();
		}
		switch (disease) {
		case TUBERCULOSIS:
		case LATENT_TUBERCULOSIS:
			return new TuberculosisSectionComponent();
		case MEASLES:
			return new MeaslesSectionComponent();
		case CRYPTOSPORIDIOSIS:
			return new CryptosporidiosisSectionComponent();
		case INVASIVE_MENINGOCOCCAL_INFECTION:
			return new ImiSectionComponent();
		case INVASIVE_PNEUMOCOCCAL_INFECTION:
			return new IpiSectionComponent();
		case CSM:
			return new CsmSectionComponent();
		default:
			return new DefaultSectionComponent();
		}
	}
}
