package de.symeda.sormas.api;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;

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
				|| disease == Disease.MONKEYPOX || (disease == Disease.PLAGUE && plagueType == PlagueType.PNEUMONIC) 
				|| disease == Disease.OTHER;
	}
	
	public static int getIncubationPeriodDays(Disease disease, PlagueType plagueType) {
		
		if (disease == null) {
			return 21; // max
		}

		switch(disease) {
		case EVD:
		case MONKEYPOX:
		case OTHER:
			return 21;
		case AVIAN_INFLUENCA:
			return 17;
		case PLAGUE:
			if (plagueType == null || plagueType == PlagueType.PNEUMONIC) {
				return 7; // worst case
			} else {
				return 0;
			}
		case LASSA:
			return 6;
		default:
			return 21; // max
		}
	}
}
