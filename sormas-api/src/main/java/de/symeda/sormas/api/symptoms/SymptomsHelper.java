package de.symeda.sormas.api.symptoms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SymptomsHelper {

    public static List<Float> getTemperatureValues() {
		List<Float> x = new ArrayList<Float>();
		for (int i=350; i<=440; i++) {
			x.add(i / 10.0f);
		}
		return x;
	}
    
    public static String getTemperatureString(float value) {
    	return String.format("%.1f °C", value);
    }
    
    public static void updateIsSymptomatic(SymptomsDto dto) {
    	if (dto == null) {
    		return;
    	}
    	
    	if (dto.getTemperature() != null && dto.getTemperature() >= 38.0f) {
    		dto.setSymptomatic(true);
    		return;
    	}
    	
    	List<SymptomState> unconditionalSymptoms = Arrays.asList(dto.getFever(), dto.getVomiting(), dto.getDiarrhea(), dto.getBloodInStool(),
    			dto.getNausea(), dto.getAbdominalPain(), dto.getHeadache(), dto.getMusclePain(), dto.getFatigueWeakness(), dto.getSkinRash(),
    			dto.getNeckStiffness(), dto.getSoreThroat(), dto.getCough(), dto.getRunnyNose(), dto.getDifficultyBreathing(),
    			dto.getChestPain(), dto.getConfusedDisoriented(), dto.getSeizures(), dto.getAlteredConsciousness(), dto.getConjunctivitis(),
    			dto.getEyePainLightSensitive(), dto.getKopliksSpots(), dto.getThrobocytopenia(), dto.getOtitisMedia(), dto.getHearingloss(),
    			dto.getDehydration(), dto.getAnorexiaAppetiteLoss(), dto.getRefusalFeedorDrink(), dto.getJointPain(), dto.getShock(),
    			dto.getHiccups(), dto.getBackache(), dto.getJaundice(), dto.getDarkUrine(), dto.getRapidBreathing(), dto.getSwollenGlands(), 
    			dto.getLesions(), dto.getLymphadenopathyInguinal(), dto.getLymphadenopathyAxillary(), dto.getLymphadenopathyCervical(), 
    			dto.getChillsSweats(), dto.getBedridden(), dto.getOralUlcers(), dto.getPainfulLymphadenitis(), dto.getBlackeningDeathOfTissue(),
    			dto.getBuboesGroinArmpitNeck(), dto.getBulgingFontanelle(), dto.getUnexplainedBleeding(), dto.getOtherNonHemorrhagicSymptoms());
    	
    	for (SymptomState symptom : unconditionalSymptoms) {
    		if (symptom == SymptomState.YES) {
    			dto.setSymptomatic(true);
    			return;
    		}
    	}
    	
    	dto.setSymptomatic(false);
    }
    
}
