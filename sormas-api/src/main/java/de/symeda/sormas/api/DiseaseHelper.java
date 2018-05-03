package de.symeda.sormas.api;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.DataHelper;

public class DiseaseHelper {

	/**
	 * Checks whether the given symptoms match the clinical criteria of one of the three Plague types.
	 * 
	 * @param symptoms The symptoms of a case with the Plague disease
	 * @return One of the three Plague types if the clinical criteria are met, null otherwise
	 */
	public static PlagueType getPlagueTypeForSymptoms(SymptomsDto symptoms) {
		if (symptoms.getFever() == SymptomState.YES) {
			if (symptoms.getPainfulLymphadenitis() == SymptomState.YES) {
				return PlagueType.BUBONIC;
			} else if (symptoms.getCough() == SymptomState.YES || symptoms.getChestPain() == SymptomState.YES ||
					symptoms.getCoughingBlood() == SymptomState.YES) {
				return PlagueType.PNEUMONIC;
			} else if (symptoms.getChillsSweats() == SymptomState.YES) {
				return PlagueType.SEPTICAEMIC;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static boolean hasContactFollowUp(Disease disease, PlagueType plagueType) {
		return disease == Disease.EVD || disease == Disease.LASSA || disease == Disease.AVIAN_INFLUENCA 
				|| disease == Disease.MONKEYPOX || (disease == Disease.PLAGUE) || disease == Disease.OTHER;
	}

	public static int getIncubationPeriodDays(Disease disease, PlagueType plagueType) {

		if (disease == null) {
			return 21; // max
		}

		switch(disease) {
		case EVD:
		case MEASLES:
		case MONKEYPOX:
		case LASSA:
		case OTHER:
			return 21;
		case AVIAN_INFLUENCA:
			return 17;
		case DENGUE:
			return 14;
		case CSM:
			return 10;
		case PLAGUE:
			return 7;
		case YELLOW_FEVER:
			return 6;
		case CHOLERA:
			return 5;
		default:
			return 21; // max
		}
	}
	
	public static String toString(Disease disease, String diseaseDetails) {
		return disease != Disease.OTHER 
				? (disease != null ? disease.toShortString() : "")
				: DataHelper.toStringNullable(diseaseDetails);
	}
}
