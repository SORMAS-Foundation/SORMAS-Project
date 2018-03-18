package de.symeda.sormas.app.symptom;

import java.util.List;

import de.symeda.sormas.app.backend.symptoms.Symptoms;

/**
 * Created by Orson on 15/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class SymptomFacade {


    public static List<Symptom> loadState(List<Symptom> list, Symptoms record) {
        if (record == null)
            return list;

        for (Symptom symptom : list) {
            if (symptom.equals(Symptom.FEVER)) {
                symptom.setState(record.getFever());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.VOMITING)) {
                symptom.setState(record.getVomiting());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.DIARRHEA)) {
                symptom.setState(record.getDiarrhea());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.BLOOD_IN_STOOL)) {
                symptom.setState(record.getBloodInStool());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.NAUSEA)) {
                symptom.setState(record.getNausea());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.ABDOMINAL_PAIN)) {
                symptom.setState(record.getAbdominalPain());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.HEAD_ACHE)) {
                symptom.setState(record.getHeadache());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.MUSCLE_PAIN)) {
                symptom.setState(record.getMusclePain());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.FATIGUE_GENERAL_WEAKNESS)) {
                symptom.setState(record.getFatigueWeakness());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.UNEXPLAINED_BLEEDING_BRUISING)) {
                symptom.setState(record.getUnexplainedBleeding());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.BLEEDING_GUM)) {
                symptom.setState(record.getGumsBleeding());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.BLEEDING_FROM_INJECTION_SITE)) {
                symptom.setState(record.getInjectionSiteBleeding());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.NOSE_BLEED)) {
                symptom.setState(record.getNoseBleeding());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.BLOODY_BLACK_STOOL)) {
                symptom.setState(record.getBloodyBlackStool());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.BLOOD_IN_VOMIT)) {
                symptom.setState(record.getRedBloodVomit());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.DIGESTED_BLOOD_IN_VOMIT)) {
                symptom.setState(record.getDigestedBloodVomit());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.COUGHING_BLOOD)) {
                symptom.setState(record.getCoughingBlood());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.BLEEDING_FROM_VAGINA)) {
                symptom.setState(record.getBleedingVagina());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.BRUISED_SKIN)) {
                symptom.setState(record.getSkinBruising());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.BLOOD_IN_URINE)) {
                symptom.setState(record.getBloodUrine());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.OTHER_HEMORRHAGIC)) {
                symptom.setState(record.getOtherHemorrhagicSymptoms());
                symptom.setDetail(record.getOtherHemorrhagicSymptomsText());
            } else if (symptom.equals(Symptom.SKIN_RASH)) {
                symptom.setState(record.getSkinRash());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.STIFF_NECK)) {
                symptom.setState(record.getNeckStiffness());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.SORE_THROAT)) {
                symptom.setState(record.getSoreThroat());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.COUGH)) {
                symptom.setState(record.getCough());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.RUNNY_NOSE)) {
                symptom.setState(record.getRunnyNose());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.DIFFICULTY_BREATHING)) {
                symptom.setState(record.getDifficultyBreathing());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.CHEST_PAIN)) {
                symptom.setState(record.getChestPain());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.CONFUSED_OR_DISORIENTED)) {
                symptom.setState(record.getConfusedDisoriented());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.CONVULSION_OR_SEIZURES)) {
                symptom.setState(record.getSeizures());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.ALTERED_CONSCIOUSNESS)) {
                symptom.setState(record.getAlteredConsciousness());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.CONJUNCTIVITIS)) {
                symptom.setState(record.getConjunctivitis());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.PAIN_BEHIND_EYES)) {
                symptom.setState(record.getEyePainLightSensitive());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.KOPLIK_SPOTS)) {
                symptom.setState(record.getKopliksSpots());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.THROMBOCYTOPENIA)) {
                symptom.setState(record.getThrobocytopenia());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.MIDDLE_EAR_INFLAMMATION)) {
                symptom.setState(record.getFever());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.ACUTE_HEARING_LOSS)) {
                symptom.setState(record.getHearingloss());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.DEHYDRATION)) {
                symptom.setState(record.getDehydration());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.LOSS_OF_APPETITE)) {
                symptom.setState(record.getAnorexiaAppetiteLoss());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.REFUSAL_TO_FEED)) {
                symptom.setState(record.getRefusalFeedorDrink());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.JOINT_PAIN)) {
                symptom.setState(record.getJointPain());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.SHOCK)) {
                symptom.setState(record.getShock());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.HICCUPS)) {
                symptom.setState(record.getHiccups());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.OTHER_NON_HEMORRHAGIC)) {
                symptom.setState(record.getOtherNonHemorrhagicSymptoms());
                symptom.setDetail(record.getOtherNonHemorrhagicSymptomsText());
            } else if (symptom.equals(Symptom.BACKACHE)) {
                symptom.setState(record.getBackache());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.BLEEDING_FROM_EYES)) {
                symptom.setState(record.getEyesBleeding());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.JAUNDICE)) {
                symptom.setState(record.getJaundice());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.DARK_URINE)) {
                symptom.setState(record.getDarkUrine());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.BLEEDING_FROM_STOMACH)) {
                symptom.setState(record.getStomachBleeding());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.RAPID_BREATHING)) {
                symptom.setState(record.getRapidBreathing());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.SWOLLEN_GLANDS)) {
                symptom.setState(record.getSwollenGlands());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.CUTANEOUS_ERUPTION)) {
                symptom.setState(record.getFever());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.CHILLS_OR_SWEAT)) {
                symptom.setState(record.getChillsSweats());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.LESIONS_THAT_ITCH)) {
                symptom.setState(record.getLesionsThatItch());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.BEDRIDDEN)) {
                symptom.setState(record.getBedridden());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.ORAL_ULCERS)) {
                symptom.setState(record.getOralUlcers());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.PAINFUL_LYMPHADENITIS)) {
                symptom.setState(record.getPainfulLymphadenitis());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.BLACKENING_DEATH_OF_TISSUE)) {
                symptom.setState(record.getBlackeningDeathOfTissue());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.BUBOES_GROIN_ARMPIT_NECK)) {
                symptom.setState(record.getBuboesGroinArmpitNeck());
                symptom.setDetail("");
            } else if (symptom.equals(Symptom.BULGING_FONTANELLE)) {
                symptom.setState(record.getBulgingFontanelle());
                symptom.setDetail("");
            }
        }

        return list;
    }
}
