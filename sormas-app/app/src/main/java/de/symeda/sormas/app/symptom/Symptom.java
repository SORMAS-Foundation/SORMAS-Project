package de.symeda.sormas.app.symptom;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.app.backend.symptoms.Symptoms;

/**
 * Created by Orson on 03/01/2018.
 */

public abstract class Symptom {

    private final int value;
    private final String name;
    private SymptomState state;
    private boolean hasDetail;
    private String detail;
    private int mLastCheckedId = -1;

    // <editor-fold defaultstate="collapsed" desc="Constants">

    public static final Symptom FEVER = new Fever();
    public static final Symptom VOMITING = new Vomiting();
    public static final Symptom DIARRHEA = new Diarrhea();
    public static final Symptom BLOOD_IN_STOOL = new BloodInStool();
    public static final Symptom NAUSEA = new Nausea();
    public static final Symptom ABDOMINAL_PAIN = new AbdominalPain();
    public static final Symptom HEAD_ACHE = new Headache();
    public static final Symptom MUSCLE_PAIN = new MusclePain();
    public static final Symptom FATIGUE_GENERAL_WEAKNESS = new FatigueGeneralWeakness();
    public static final Symptom UNEXPLAINED_BLEEDING_BRUISING = new UnexplainedBleedingBruising();
    public static final Symptom BLEEDING_GUM = new BleedingGum();
    public static final Symptom BLEEDING_FROM_INJECTION_SITE = new BleedingFromInjectionSite();
    public static final Symptom NOSE_BLEED = new NoseBleed();
    public static final Symptom BLOODY_BLACK_STOOL = new BloodyBlackStool();
    public static final Symptom BLOOD_IN_VOMIT = new BloodInVomit();
    public static final Symptom DIGESTED_BLOOD_IN_VOMIT = new DigestedBloodInVomit();
    public static final Symptom COUGHING_BLOOD = new CoughingBlood();
    public static final Symptom BLEEDING_FROM_VAGINA = new BleedingFromVagina();
    public static final Symptom BRUISED_SKIN = new BruisedSkin();
    public static final Symptom BLOOD_IN_URINE = new BloodInUrine();
    public static final Symptom OTHER_HEMORRHAGIC = new OtherHemorrhagic();
    public static final Symptom SKIN_RASH = new SkinRash();
    public static final Symptom STIFF_NECK = new StiffNeck();
    public static final Symptom SORE_THROAT = new SoreThroat();
    public static final Symptom COUGH = new Cough();
    public static final Symptom RUNNY_NOSE = new RunnyNose();
    public static final Symptom DIFFICULTY_BREATHING = new DifficultyBreathing();
    public static final Symptom CHEST_PAIN = new ChestPain();
    public static final Symptom CONFUSED_OR_DISORIENTED = new ConfusedOrDisoriented();
    public static final Symptom CONVULSION_OR_SEIZURES = new ConvulsionsOrSeizures();
    public static final Symptom ALTERED_CONSCIOUSNESS = new AlteredConsciousness();
    public static final Symptom CONJUNCTIVITIS = new Conjunctivitis();
    public static final Symptom PAIN_BEHIND_EYES = new PainBehindEyes();
    public static final Symptom KOPLIK_SPOTS = new KoplikSpots();
    public static final Symptom THROMBOCYTOPENIA = new Thrombocytopenia();
    public static final Symptom MIDDLE_EAR_INFLAMMATION = new MiddleEarInflammation();
    public static final Symptom ACUTE_HEARING_LOSS = new AcuteHearingLoss();
    public static final Symptom DEHYDRATION = new Dehydration();
    public static final Symptom LOSS_OF_APPETITE = new LossOfAppetite();
    public static final Symptom REFUSAL_TO_FEED = new RefusalToFeed();
    public static final Symptom JOINT_PAIN = new JointPain();
    public static final Symptom SHOCK = new Shock();
    public static final Symptom HICCUPS = new Hiccups();
    public static final Symptom OTHER_NON_HEMORRHAGIC = new OtherNonHemorrhagic();
    public static final Symptom BACKACHE = new Backache();
    public static final Symptom BLEEDING_FROM_EYES = new BleedingFromEyes();
    public static final Symptom JAUNDICE = new Jaundice();
    public static final Symptom DARK_URINE = new DarkUrine();
    public static final Symptom BLEEDING_FROM_STOMACH = new BleedingFromStomach();
    public static final Symptom RAPID_BREATHING = new RapidBreathing();
    public static final Symptom SWOLLEN_GLANDS = new SwollenGlands();
    public static final Symptom CUTANEOUS_ERUPTION = new CutaneousEruption();
    public static final Symptom CHILLS_OR_SWEAT = new ChillsOrSweat();
    public static final Symptom LESIONS_THAT_ITCH = new LesionsThatItch();
    public static final Symptom BEDRIDDEN = new Bedridden();
    public static final Symptom ORAL_ULCERS = new OralUlcers();
    public static final Symptom PAINFUL_LYMPHADENITIS = new PainfulLymphadenitis();
    public static final Symptom BLACKENING_DEATH_OF_TISSUE = new BlackeningDeathOfTissue();
    public static final Symptom BUBOES_GROIN_ARMPIT_NECK = new BuboesGroinArmpitNeck();
    public static final Symptom BULGING_FONTANELLE = new BulgingFontanelle();

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Contructor">

    /*protected Symptom(int value, String name, SymptomState state) {
        this(value, name, state, false, "");
    }*/
    private Symptom(Symptom s) {
        this.value = s.getValue();
        this.name = s.getName();
        this.state = s.getState();
        this.hasDetail = s.hasDetail();
        this.detail = s.getDetail();
    }

    protected Symptom(int value, String name, boolean hasDetail) {
        this.value = value;
        this.name = name;
        this.state = SymptomState.UNKNOWN;
        this.hasDetail = hasDetail;
        this.detail = "";
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters & Setters">

    public String getName() {
        return this.name;
    }

    public SymptomState getState() {
        return this.state;
    }

    public void setState(SymptomState state) {
        this.state = state;
    }

    public int getLastCheckedId() {
        return mLastCheckedId;
    }

    public void setLastCheckedId(int mLastCheckedId) {
        this.mLastCheckedId = mLastCheckedId;
    }

    public boolean hasDetail() {
        return hasDetail;
    }

    public void setHasDetail(boolean hasDetail) {
        this.hasDetail = hasDetail;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getValue() {
        return value;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Enumurations">

    private static class Fever extends Symptom {
        public Fever() {
            super(0, "Fever", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.CHOLERA);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.OTHER);
            }};
        }
    }

    private static class Vomiting extends Symptom {
        public Vomiting() {
            super(1, "Vomiting", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.OTHER);
            }};
        }
    }

    private static class Diarrhea extends Symptom {
        public Diarrhea() {
            super(2, "Diarrhea", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class BloodInStool extends Symptom {
        public BloodInStool() {
            super(3, "Blood in Stool", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class Nausea extends Symptom {
        public Nausea() {
            super(4, "Nausea", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class AbdominalPain extends Symptom {
        public AbdominalPain() {
            super(5, "Abdominal pain", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class Headache extends Symptom {
        public Headache() {
            super(6, "Headache", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class MusclePain extends Symptom {
        public MusclePain() {
            super(7, "Muscle pain", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class FatigueGeneralWeakness extends Symptom {
        public FatigueGeneralWeakness() {
            super(8, "Fatigue/general weakness", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class UnexplainedBleedingBruising extends Symptom {
        public UnexplainedBleedingBruising() {
            super(9, "Unexplained bleeding or bruising", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class BleedingGum extends Symptom {
        public BleedingGum() {
            super(10, "Bleeding of the gums", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class BleedingFromInjectionSite extends Symptom {
        public BleedingFromInjectionSite() {
            super(11, "Bleeding from injection site", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class NoseBleed extends Symptom {
        public NoseBleed() {
            super(12, "Nose bleed (epistaxis)", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class BloodyBlackStool extends Symptom {
        public BloodyBlackStool() {
            super(13, "Bloody or black stools (melena)", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class BloodInVomit extends Symptom {
        public BloodInVomit() {
            super(14, "Fresh/red blood in vomit (hematemesis)", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class DigestedBloodInVomit extends Symptom {
        public DigestedBloodInVomit() {
            super(15, "Digested blood\"coffee grounds\" in vomit", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class CoughingBlood extends Symptom {
        public CoughingBlood() {
            super(16, "Coughing up blood (haemoptysis)", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class BleedingFromVagina extends Symptom {
        public BleedingFromVagina() {
            super(17, "Bleeding from vagina, other than menstruation", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class BruisedSkin extends Symptom {
        public BruisedSkin() {
            super(18, "Bruising of the skin (petechiae/ecchymosis)", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class BloodInUrine extends Symptom {
        public BloodInUrine() {
            super(19, "Blood in urine (hematuria)", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class OtherHemorrhagic extends Symptom {
        public OtherHemorrhagic() {
            super(20, "Other hemorrhagic symptoms", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class SkinRash extends Symptom {
        public SkinRash() {
            super(21, "Skin rash", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class StiffNeck extends Symptom {
        public StiffNeck() {
            super(22, "Stiff neck", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class SoreThroat extends Symptom {
        public SoreThroat() {
            super(23, "Sore throat/pharyngitis", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class Cough extends Symptom {
        public Cough() {
            super(24, "Cough", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class RunnyNose extends Symptom {
        public RunnyNose() {
            super(25, "Runny nose", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class DifficultyBreathing extends Symptom {
        public DifficultyBreathing() {
            super(26, "Difficulty breathing", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class ChestPain extends Symptom {
        public ChestPain() {
            super(27, "Chest pain", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class ConfusedOrDisoriented extends Symptom {
        public ConfusedOrDisoriented() {
            super(28, "Confused or disoriented", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class ConvulsionsOrSeizures extends Symptom {
        public ConvulsionsOrSeizures() {
            super(29, "Convulsions or Seizures", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.YELLOW_FEVER);
                add(Disease.OTHER);
            }};
        }
    }

    private static class AlteredConsciousness extends Symptom {
        public AlteredConsciousness() {
            super(30, "Altered level of consciousness", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.YELLOW_FEVER);
                add(Disease.OTHER);
            }};
        }
    }

    private static class Conjunctivitis extends Symptom {
        public Conjunctivitis() {
            super(31, "Conjunctivitis (red eyes)", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.YELLOW_FEVER);
                add(Disease.OTHER);
            }};
        }
    }

    private static class PainBehindEyes extends Symptom {
        public PainBehindEyes() {
            super(32, "Pain behind eyes/Sensitivity to light", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.YELLOW_FEVER);
                add(Disease.OTHER);
            }};
        }
    }

    private static class KoplikSpots extends Symptom {
        public KoplikSpots() {
            super(33, "Koplik's Spots", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.YELLOW_FEVER);
                add(Disease.OTHER);
            }};
        }
    }

    private static class Thrombocytopenia extends Symptom {
        public Thrombocytopenia() {
            super(34, "Thrombocytopenia", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.YELLOW_FEVER);
                add(Disease.OTHER);
            }};
        }
    }

    private static class MiddleEarInflammation extends Symptom {
        public MiddleEarInflammation() {
            super(35, "Middle ear inflammation (otitis media)", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.OTHER);
            }};
        }
    }

    private static class AcuteHearingLoss extends Symptom {
        public AcuteHearingLoss() {
            super(36, "Acute hearing loss", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.OTHER);
            }};
        }
    }

    private static class Dehydration extends Symptom {
        public Dehydration() {
            super(37, "Dehydration", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.OTHER);
            }};
        }
    }

    private static class LossOfAppetite extends Symptom {
        public LossOfAppetite() {
            super(38, "Anorexia/loss of appetite", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.OTHER);
            }};
        }
    }

    private static class RefusalToFeed extends Symptom {
        public RefusalToFeed() {
            super(39, "Refusal to feed or drink", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.OTHER);
            }};
        }
    }

    private static class JointPain extends Symptom {
        public JointPain() {
            super(40, "Joint pain or arthritis", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.OTHER);
            }};
        }
    }

    private static class Shock extends Symptom {
        public Shock() {
            super(41, "Shock (Systolic bp <90)", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class Hiccups extends Symptom {
        public Hiccups() {
            super(42, "Hiccups", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.CHOLERA);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class OtherNonHemorrhagic extends Symptom {
        public OtherNonHemorrhagic() {
            super(43, "Other clinical symptom", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.CHOLERA);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class Backache extends Symptom {
        public Backache() {
            super(44, "Backache", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.CHOLERA);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class BleedingFromEyes extends Symptom {
        public BleedingFromEyes() {
            super(45, "Bleeding from the eyes", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.CHOLERA);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class Jaundice extends Symptom {
        public Jaundice() {
            super(46, "Jaundice", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.CHOLERA);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class DarkUrine extends Symptom {
        public DarkUrine() {
            super(47, "Dark Urine", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.CHOLERA);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class BleedingFromStomach extends Symptom {
        public BleedingFromStomach() {
            super(48, "Bleeding from the stomach", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.CHOLERA);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class RapidBreathing extends Symptom {
        public RapidBreathing() {
            super(49, "Rapid breathing", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.CHOLERA);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class SwollenGlands extends Symptom {
        public SwollenGlands() {
            super(50, "Swollen glands", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.CHOLERA);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class CutaneousEruption extends Symptom {
        public CutaneousEruption() {
            super(51, "Cutaneous eruption", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.CHOLERA);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class ChillsOrSweat extends Symptom {
        public ChillsOrSweat() {
            super(52, "Chills or sweats", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.CHOLERA);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class LesionsThatItch extends Symptom {
        public LesionsThatItch() {
            super(53, "Lesions that itch", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.CHOLERA);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class Bedridden extends Symptom {
        public Bedridden() {
            super(54, "Is the patient bedridden?", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.CHOLERA);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class OralUlcers extends Symptom {
        public OralUlcers() {
            super(55, "Oral ulcers", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.CHOLERA);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class PainfulLymphadenitis extends Symptom {
        public PainfulLymphadenitis() {
            super(56, "Painful lymphadenitis", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.CHOLERA);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class BlackeningDeathOfTissue extends Symptom {
        public BlackeningDeathOfTissue() {
            super(57, "Blackening and death of tissue in extremities", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.CHOLERA);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class BuboesGroinArmpitNeck extends Symptom {
        public BuboesGroinArmpitNeck() {
            super(58, "Buboes in the groin, armpit or neck", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.CHOLERA);
                add(Disease.MEASLES);
                add(Disease.YELLOW_FEVER);
                add(Disease.DENGUE);
                add(Disease.OTHER);
            }};
        }
    }

    private static class BulgingFontanelle extends Symptom {
        public BulgingFontanelle() {
            super(59, "Bulging fontanelle", false);
        }

        @Override
        public List<Disease> getSupportDisease() {
            return new ArrayList<Disease>() {{
                add(Disease.EVD);
                add(Disease.LASSA);
                add(Disease.AVIAN_INFLUENCA);
                add(Disease.CSM);
                add(Disease.CHOLERA);
                add(Disease.MEASLES);
                add(Disease.OTHER);
            }};
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Public Methods">

    public static ISymptomValueLoader makeSymptoms(Disease disease) {
        List<Symptom> database = getSymptomDatabase();
        List<Symptom> newList = new ArrayList<>();

        for (Symptom s : database) {
            if (s.getSupportDisease().contains(disease)) {
                newList.add(newSymptom(s));
            }
        }

        return new ValueLoader(newList);
    }

    public abstract List<Disease> getSupportDisease();

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Private Methods">

    private static Symptom newSymptom(Symptom s) {
        Symptom newSymptom = new Symptom(s.getValue(), s.getName(), s.hasDetail()) {
            private Symptom _s;
            private boolean _copiedSupportedDiseases;
            private List<Disease> _originalList;
            private List<Disease> _copy;

            @Override
            public List<Disease> getSupportDisease() {
                if (!_copiedSupportedDiseases) {
                    _originalList = _s.getSupportDisease();
                    _copy = new ArrayList<Disease>(_originalList.size());

                    for (Disease d: _originalList) {
                        _copy.add(d);
                    }
                }

                _copiedSupportedDiseases = true;

                return _copy;
            }

            private Symptom init(Symptom symptom) {
                _s = symptom;
                _copiedSupportedDiseases = false;

                this.setState(symptom.getState());
                this.setDetail(symptom.getDetail());
                return this;
            }

        }.init(s);

        return newSymptom;
    }

    private static List<Symptom> getSymptomDatabase() {
        return new ArrayList<Symptom>() {{
            add(Symptom.FEVER);
            add(Symptom.VOMITING);
            add(Symptom.DIARRHEA);
            add(Symptom.BLOOD_IN_STOOL);
            add(Symptom.NAUSEA);
            add(Symptom.ABDOMINAL_PAIN);
            add(Symptom.HEAD_ACHE);
            add(Symptom.MUSCLE_PAIN);
            add(Symptom.FATIGUE_GENERAL_WEAKNESS);
            add(Symptom.UNEXPLAINED_BLEEDING_BRUISING);
            add(Symptom.BLEEDING_GUM);
            add(Symptom.BLEEDING_FROM_INJECTION_SITE);
            add(Symptom.NOSE_BLEED);
            add(Symptom.BLOODY_BLACK_STOOL);
            add(Symptom.BLOOD_IN_VOMIT);
            add(Symptom.DIGESTED_BLOOD_IN_VOMIT);
            add(Symptom.COUGHING_BLOOD);
            add(Symptom.BLEEDING_FROM_VAGINA);
            add(Symptom.BRUISED_SKIN);
            add(Symptom.BLOOD_IN_URINE);
            add(Symptom.OTHER_HEMORRHAGIC);
            add(Symptom.SKIN_RASH);
            add(Symptom.STIFF_NECK);
            add(Symptom.SORE_THROAT);
            add(Symptom.COUGH);
            add(Symptom.RUNNY_NOSE);
            add(Symptom.DIFFICULTY_BREATHING);
            add(Symptom.CHEST_PAIN);
            add(Symptom.CONFUSED_OR_DISORIENTED);
            add(Symptom.CONVULSION_OR_SEIZURES);
            add(Symptom.ALTERED_CONSCIOUSNESS);
            add(Symptom.CONJUNCTIVITIS);
            add(Symptom.PAIN_BEHIND_EYES);
            add(Symptom.KOPLIK_SPOTS);
            add(Symptom.THROMBOCYTOPENIA);
            add(Symptom.MIDDLE_EAR_INFLAMMATION);
            add(Symptom.ACUTE_HEARING_LOSS);
            add(Symptom.DEHYDRATION);
            add(Symptom.LOSS_OF_APPETITE);
            add(Symptom.REFUSAL_TO_FEED);
            add(Symptom.JOINT_PAIN);
            add(Symptom.SHOCK);
            add(Symptom.HICCUPS);
            add(Symptom.OTHER_NON_HEMORRHAGIC);
            add(Symptom.BACKACHE);
            add(Symptom.BLEEDING_FROM_EYES);
            add(Symptom.JAUNDICE);
            add(Symptom.DARK_URINE);
            add(Symptom.BLEEDING_FROM_STOMACH);
            add(Symptom.RAPID_BREATHING);
            add(Symptom.SWOLLEN_GLANDS);
            add(Symptom.CUTANEOUS_ERUPTION);
            add(Symptom.CHILLS_OR_SWEAT);
            add(Symptom.LESIONS_THAT_ITCH);
            add(Symptom.BEDRIDDEN);
            add(Symptom.ORAL_ULCERS);
            add(Symptom.PAINFUL_LYMPHADENITIS);
            add(Symptom.BLACKENING_DEATH_OF_TISSUE);
            add(Symptom.BUBOES_GROIN_ARMPIT_NECK);
            add(Symptom.BULGING_FONTANELLE);
        }};
    }

    private static class ValueLoader implements ISymptomValueLoader {

        private List<Symptom> list;

        public ValueLoader(List<Symptom> list) {
            this.list = list;
        }

        @Override
        public List<Symptom> unloaded() {
            return list;
        }

        @Override
        public List<Symptom> loadState(Symptoms record) {
            return SymptomFacade.loadState(list, record);
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Overrides">

    @Override
    public int hashCode() {
        return value + 37 * value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Symptom)) {
            return false;
        }
        Symptom other = (Symptom) obj;
        return value == other.value;
    }

    @Override
    public String toString() {
        return getName();
    }

    // </editor-fold>
}
