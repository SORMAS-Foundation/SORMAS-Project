package de.symeda.sormas.app.symptom;

import java.util.List;

import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.core.Callback;

/**
 * Created by Orson on 15/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class SymptomFacade {


    public static List<Symptom> loadState(final List<Symptom> list, final Symptoms record) {
        if (record == null)
            return list;

        for (final Symptom symptom : list) {
            if (symptom.equals(Symptom.FEVER)) {
                symptom.setState(record.getFever());
            } else if (symptom.equals(Symptom.VOMITING)) {
                symptom.setState(record.getVomiting());
            } else if (symptom.equals(Symptom.DIARRHEA)) {
                symptom.setState(record.getDiarrhea());
            } else if (symptom.equals(Symptom.BLOOD_IN_STOOL)) {
                symptom.setState(record.getBloodInStool());
            } else if (symptom.equals(Symptom.NAUSEA)) {
                symptom.setState(record.getNausea());
            } else if (symptom.equals(Symptom.ABDOMINAL_PAIN)) {
                symptom.setState(record.getAbdominalPain());
            } else if (symptom.equals(Symptom.HEAD_ACHE)) {
                symptom.setState(record.getHeadache());
            } else if (symptom.equals(Symptom.MUSCLE_PAIN)) {
                symptom.setState(record.getMusclePain());
            } else if (symptom.equals(Symptom.FATIGUE_GENERAL_WEAKNESS)) {
                symptom.setState(record.getFatigueWeakness());
            } else if (symptom.equals(Symptom.UNEXPLAINED_BLEEDING_BRUISING)) {
                symptom.setState(record.getUnexplainedBleeding());
            } else if (symptom.equals(Symptom.BLEEDING_GUM)) {
                symptom.setState(record.getGumsBleeding());
            } else if (symptom.equals(Symptom.BLEEDING_FROM_INJECTION_SITE)) {
                symptom.setState(record.getInjectionSiteBleeding());
            } else if (symptom.equals(Symptom.NOSE_BLEED)) {
                symptom.setState(record.getNoseBleeding());
            } else if (symptom.equals(Symptom.BLOODY_BLACK_STOOL)) {
                symptom.setState(record.getBloodyBlackStool());
            } else if (symptom.equals(Symptom.BLOOD_IN_VOMIT)) {
                symptom.setState(record.getRedBloodVomit());
            } else if (symptom.equals(Symptom.DIGESTED_BLOOD_IN_VOMIT)) {
                symptom.setState(record.getDigestedBloodVomit());
            } else if (symptom.equals(Symptom.COUGHING_BLOOD)) {
                symptom.setState(record.getCoughingBlood());
            } else if (symptom.equals(Symptom.BLEEDING_FROM_VAGINA)) {
                symptom.setState(record.getBleedingVagina());
            } else if (symptom.equals(Symptom.BRUISED_SKIN)) {
                symptom.setState(record.getSkinBruising());
            } else if (symptom.equals(Symptom.BLOOD_IN_URINE)) {
                symptom.setState(record.getBloodUrine());
            } else if (symptom.equals(Symptom.OTHER_HEMORRHAGIC)) {
                symptom.setState(record.getOtherHemorrhagicSymptoms());

                if (symptom.getChildViewModel() instanceof DetailsViewModel) {
                    ((DetailsViewModel)symptom.getChildViewModel()).setDetail(record.getOtherHemorrhagicSymptomsText());
                }
            } else if (symptom.equals(Symptom.SKIN_RASH)) {
                symptom.setState(record.getSkinRash());
            } else if (symptom.equals(Symptom.STIFF_NECK)) {
                symptom.setState(record.getNeckStiffness());
            } else if (symptom.equals(Symptom.SORE_THROAT)) {
                symptom.setState(record.getSoreThroat());
            } else if (symptom.equals(Symptom.COUGH)) {
                symptom.setState(record.getCough());
            } else if (symptom.equals(Symptom.RUNNY_NOSE)) {
                symptom.setState(record.getRunnyNose());
            } else if (symptom.equals(Symptom.DIFFICULTY_BREATHING)) {
                symptom.setState(record.getDifficultyBreathing());
            } else if (symptom.equals(Symptom.CHEST_PAIN)) {
                symptom.setState(record.getChestPain());
            } else if (symptom.equals(Symptom.CONFUSED_OR_DISORIENTED)) {
                symptom.setState(record.getConfusedDisoriented());
            } else if (symptom.equals(Symptom.CONVULSION_OR_SEIZURES)) {
                symptom.setState(record.getSeizures());
            } else if (symptom.equals(Symptom.ALTERED_CONSCIOUSNESS)) {
                symptom.setState(record.getAlteredConsciousness());
            } else if (symptom.equals(Symptom.CONJUNCTIVITIS)) {
                symptom.setState(record.getConjunctivitis());
            } else if (symptom.equals(Symptom.PAIN_BEHIND_EYES)) {
                symptom.setState(record.getEyePainLightSensitive());
            } else if (symptom.equals(Symptom.KOPLIK_SPOTS)) {
                symptom.setState(record.getKopliksSpots());
            } else if (symptom.equals(Symptom.THROMBOCYTOPENIA)) {
                symptom.setState(record.getThrobocytopenia());
            } else if (symptom.equals(Symptom.MIDDLE_EAR_INFLAMMATION)) {
                symptom.setState(record.getFever());
            } else if (symptom.equals(Symptom.ACUTE_HEARING_LOSS)) {
                symptom.setState(record.getHearingloss());
            } else if (symptom.equals(Symptom.DEHYDRATION)) {
                symptom.setState(record.getDehydration());
            } else if (symptom.equals(Symptom.LOSS_OF_APPETITE)) {
                symptom.setState(record.getAnorexiaAppetiteLoss());
            } else if (symptom.equals(Symptom.REFUSAL_TO_FEED)) {
                symptom.setState(record.getRefusalFeedorDrink());
            } else if (symptom.equals(Symptom.JOINT_PAIN)) {
                symptom.setState(record.getJointPain());
            } else if (symptom.equals(Symptom.SHOCK)) {
                symptom.setState(record.getShock());
            } else if (symptom.equals(Symptom.HICCUPS)) {
                symptom.setState(record.getHiccups());
            } else if (symptom.equals(Symptom.OTHER_NON_HEMORRHAGIC)) {
                symptom.setState(record.getOtherNonHemorrhagicSymptoms());

                if (symptom.getChildViewModel() instanceof DetailsViewModel) {
                    ((DetailsViewModel)symptom.getChildViewModel()).setDetail(record.getOtherNonHemorrhagicSymptomsText());
                }
            } else if (symptom.equals(Symptom.BACKACHE)) {
                symptom.setState(record.getBackache());
            } else if (symptom.equals(Symptom.BLEEDING_FROM_EYES)) {
                symptom.setState(record.getEyesBleeding());
            } else if (symptom.equals(Symptom.JAUNDICE)) {
                symptom.setState(record.getJaundice());
            } else if (symptom.equals(Symptom.DARK_URINE)) {
                symptom.setState(record.getDarkUrine());
            } else if (symptom.equals(Symptom.BLEEDING_FROM_STOMACH)) {
                symptom.setState(record.getStomachBleeding());
            } else if (symptom.equals(Symptom.RAPID_BREATHING)) {
                symptom.setState(record.getRapidBreathing());
            } else if (symptom.equals(Symptom.SWOLLEN_GLANDS)) {
                symptom.setState(record.getSwollenGlands());
            } else if (symptom.equals(Symptom.CUTANEOUS_ERUPTION)) {
                symptom.setState(record.getFever());
            } else if (symptom.equals(Symptom.CHILLS_OR_SWEAT)) {
                symptom.setState(record.getChillsSweats());
            } else if (symptom.equals(Symptom.BEDRIDDEN)) {
                symptom.setState(record.getBedridden());
            } else if (symptom.equals(Symptom.ORAL_ULCERS)) {
                symptom.setState(record.getOralUlcers());
            } else if (symptom.equals(Symptom.PAINFUL_LYMPHADENITIS)) {
                symptom.setState(record.getPainfulLymphadenitis());
            } else if (symptom.equals(Symptom.BLACKENING_DEATH_OF_TISSUE)) {
                symptom.setState(record.getBlackeningDeathOfTissue());
            } else if (symptom.equals(Symptom.BUBOES_GROIN_ARMPIT_NECK)) {
                symptom.setState(record.getBuboesGroinArmpitNeck());
            } else if (symptom.equals(Symptom.BULGING_FONTANELLE)) {
                symptom.setState(record.getBulgingFontanelle());
            } else if (symptom.equals(Symptom.DIFFICULTY_SWALLOWING)) {
                symptom.setState(record.getBulgingFontanelle());
            } else if (symptom.equals(Symptom.LESIONS)) {
                symptom.setState(record.getLesions(), new Callback.IAction<List<Symptom<LesionChildViewModel>>>() {
                    @Override
                    public void call(List<Symptom<LesionChildViewModel>> result) {
                        if (result == null)
                            return;

                        for (Symptom s : result) {
                            if (s.equals(Symptom.LESIONS_THAT_ITCH)) {
                                s.setState(record.getLesionsThatItch());
                            } else if (s.equals(Symptom.LESIONS_SAME_STATE)) {
                                s.setState(record.getLesionsSameState());
                            } else if (s.equals(Symptom.LESIONS_SAME_SIZE)) {
                                s.setState(record.getLesionsSameSize());
                            } else if (s.equals(Symptom.LESIONS_SAME_PROFOUND)) {
                                s.setState(record.getLesionsDeepProfound());
                            } else if (s.equals(Symptom.LESIONS_LIKE_PIC1)) {
                                s.setState(record.getLesionsResembleImg1());
                            } else if (s.equals(Symptom.LESIONS_LIKE_PIC2)) {
                                s.setState(record.getLesionsResembleImg2());
                            } else if (s.equals(Symptom.LESIONS_LIKE_PIC3)) {
                                s.setState(record.getLesionsResembleImg3());
                            } else if (s.equals(Symptom.LESIONS_LIKE_PIC4)) {
                                s.setState(record.getLesionsResembleImg4());
                            }
                        }
                    }
                });

                LesionChildViewModel viewModel = ((LesionChildViewModel)symptom.getChildViewModel());

                viewModel.setLocationFace(record.getLesionsFace() == null? false : record.getLesionsFace());
                viewModel.setLocationLegs(record.getLesionsLegs() == null? false : record.getLesionsLegs());
                viewModel.setLocationSolesOfFeet(record.getLesionsSolesFeet() == null? false : record.getLesionsSolesFeet());
                viewModel.setLocationPalmOfHands(record.getLesionsPalmsHands() == null? false : record.getLesionsPalmsHands());
                viewModel.setLocationThroax(record.getLesionsThorax() == null? false : record.getLesionsThorax());
                viewModel.setLocationArms(record.getLesionsArms() == null? false : record.getLesionsArms());
                viewModel.setLocationGenitals(record.getLesionsGenitals() == null? false : record.getLesionsGenitals());
                viewModel.setLocationAllBody(record.getLesionsAllOverBody() == null? false : record.getLesionsAllOverBody());

                viewModel.setLesionsOnsetDate(record.getLesionsOnsetDate());

            } else if (symptom.equals(Symptom.LYMPHADENOPATHY_INGUINAL)) {
                symptom.setState(record.getLymphadenopathyInguinal());
            } else if (symptom.equals(Symptom.LYMPHADENOPATHY_AXILLARY)) {
                symptom.setState(record.getLymphadenopathyAxillary());
            } else if (symptom.equals(Symptom.LYMPHADENOPATHY_CERVICAL)) {
                symptom.setState(record.getLymphadenopathyCervical());
            }
        }

        return list;
    }
}
