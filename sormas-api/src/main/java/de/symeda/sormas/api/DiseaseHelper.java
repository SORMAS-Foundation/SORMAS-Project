package de.symeda.sormas.api;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;

public class DiseaseHelper {

	public static boolean hasContactFollowUp(CaseDataDto caze) {
		Disease disease = caze.getDisease();

		return disease == Disease.EVD || disease == Disease.LASSA || disease == Disease.AVIAN_INFLUENCA 
				|| disease == Disease.MONKEYPOX || (disease == Disease.PLAGUE && caze.getPlagueType() == PlagueType.PNEUMONIC) 
				|| disease == Disease.OTHER;
	}
	
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

}
