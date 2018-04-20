package de.symeda.sormas.app.epid;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.backend.epidata.EpiData;

/**
 * Created by Orson on 19/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

//TODO: Load from XML configuration
public abstract class AnimalContact {
    private final int value;
    private final String name;
    private YesNoUnknown state;
    private AnimalContactLayout layout;
    private final AnimalContactCategory category;
    private int mLastCheckedId = -1;

    //layoutId (null, type1, type2) - This should be a class with (controlId, control caption)


    // <editor-fold defaultstate="collapsed" desc="Constants">

    public static final AnimalContact RODENT = new Rodent();
    public static final AnimalContact BAT = new Bat();
    public static final AnimalContact PRIMATE = new Primate();
    public static final AnimalContact SWINE = new Swine();
    public static final AnimalContact BIRD = new Bird();
    public static final AnimalContact EAT_RAW_UNDERCOOKED_BIRD = new EatRawUndercookedBird();
    public static final AnimalContact EXPOSURE_TO_DOMESTICATED_BIRD = new ExposureToDomesticatedBird();
    public static final AnimalContact EXPOSURE_TO_SICK_BIRD = new ExposureToSickBird();
    public static final AnimalContact CATTLE = new Cattle();
    public static final AnimalContact WILD_ANIMAL = new WildAnimal();
    public static final AnimalContact OTHER_ANIMAL = new OtherAnimal();
    public static final AnimalContact CONTACT_WITH_BODY_OF_WATER = new ContactWithBodyOfWater();
    public static final AnimalContact TICK_BITE = new TickBite();
    public static final AnimalContact FLEA_BITE = new FleaBite();

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Contructor">

    private AnimalContact(AnimalContact ac) {
        this.value = ac.getValue();
        this.name = ac.getName();
        this.state = ac.getState();
        this.layout = ac.getLayout();
        this.category = ac.getCategory();
    }

    protected AnimalContact(int value, String name, AnimalContactLayout layout, AnimalContactCategory category) {
        this.value = value;
        this.name = name;
        this.state = YesNoUnknown.UNKNOWN;
        this.layout = layout;
        this.category = category;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters & Setters">

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public AnimalContactCategory getCategory() {
        return category;
    }

    public int getLayoutResourceId() {
        return layout.getLayoutId();
    }

    public boolean hasChildLayout() {
        return layout.getLayoutId() > 0;
    }

    public AnimalContactLayout getLayout() {
        return layout;
    }

    public YesNoUnknown getState() {
        return state;
    }

    public void setState(YesNoUnknown state) {
        this.state = state;
    }

    public int getLastCheckedId() {
        return mLastCheckedId;
    }

    public void setLastCheckedId(int mLastCheckedId) {
        this.mLastCheckedId = mLastCheckedId;
    }


    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Enumurations">

    private static class Rodent extends AnimalContact {
        public Rodent() {
            super(0, "Rodents or their excreta", AnimalContactLayout.NONE, AnimalContactCategory.GENERAL);
        }
    }

    private static class Bat extends AnimalContact {
        public Bat() {
            super(1, "Bats or their excreta", AnimalContactLayout.NONE, AnimalContactCategory.GENERAL);
        }
    }

    private static class Primate extends AnimalContact {
        public Primate() {
            super(2, "Primates (monkeys)", AnimalContactLayout.NONE, AnimalContactCategory.GENERAL);
        }
    }

    private static class Swine extends AnimalContact {
        public Swine() {
            super(3, "Swine", AnimalContactLayout.NONE, AnimalContactCategory.GENERAL);
        }
    }

    private static class Bird extends AnimalContact {
        public Bird() {
            super(4, "Poultry or wild birds", AnimalContactLayout.NONE, AnimalContactCategory.GENERAL);
        }
    }

    private static class EatRawUndercookedBird extends AnimalContact {
        public EatRawUndercookedBird() {
            super(5, "Eating raw or undercooked poultry", AnimalContactLayout.NONE, AnimalContactCategory.GENERAL);
        }
    }

    private static class ExposureToDomesticatedBird extends AnimalContact {
        public ExposureToDomesticatedBird() {
            super(6, "Exposure to poultry or domesticated birds", AnimalContactLayout.DETAILS, AnimalContactCategory.GENERAL);
        }
    }

    private static class ExposureToSickBird extends AnimalContact {
        public ExposureToSickBird() {
            super(7, "Exposure to sick/unexplainedly dead poultry/other domesticated birds", AnimalContactLayout.DETAILS_LAST_EXPOSURE_DATE_N_PLACE, AnimalContactCategory.GENERAL);
        }
    }

    private static class Cattle extends AnimalContact {
        public Cattle() {
            super(8, "Cattle", AnimalContactLayout.NONE, AnimalContactCategory.GENERAL);
        }
    }

    private static class WildAnimal extends AnimalContact {
        public WildAnimal() {
            super(9, "Exposure to wild birds", AnimalContactLayout.SPECIFY_LAST_EXPOSURE_DATE_N_PLACE, AnimalContactCategory.GENERAL);
        }
    }

    private static class OtherAnimal extends AnimalContact {
        public OtherAnimal() {
            super(10, "Other animals", AnimalContactLayout.SPECIFY, AnimalContactCategory.GENERAL);
        }
    }

    private static class ContactWithBodyOfWater extends AnimalContact {
        public ContactWithBodyOfWater() {
            super(11, "Contact with body of water", AnimalContactLayout.DETAILS, AnimalContactCategory.ENVIRONMENTAL_EXPOSURE);
        }
    }

    private static class TickBite extends AnimalContact {
        public TickBite() {
            super(12, "Tick bite", AnimalContactLayout.NONE, AnimalContactCategory.ENVIRONMENTAL_EXPOSURE);
        }
    }

    private static class FleaBite extends AnimalContact {
        public FleaBite() {
            super(13, "Flea bite", AnimalContactLayout.NONE, AnimalContactCategory.ENVIRONMENTAL_EXPOSURE);
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Public Methods">

    public static IAnimalContactValueLoader makeAnimalContacts(AnimalContactCategory category) {
        List<AnimalContact> database = getAnimalContactDatabase();
        List<AnimalContact> newList = new ArrayList<>();

        for (AnimalContact s : database) {
            if (category == s.getCategory()) {
                newList.add(newAnimalContact(s));
            }
        }

        return new ValueLoader(newList);
    }

    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Private Methods">

    private static AnimalContact newAnimalContact(AnimalContact pAnimalContact) {
        AnimalContact newAnimalContact = new AnimalContact(pAnimalContact.getValue(), pAnimalContact.getName(),
                AnimalContactLayout.newAnimalContactLayout(pAnimalContact.getLayout()), pAnimalContact.getCategory()) {
            private AnimalContact _animalContact;

            private AnimalContact init(AnimalContact animalContact) {
                _animalContact = animalContact;

                this.setState(animalContact.getState());
                return this;
            }

        }.init(pAnimalContact);

        return newAnimalContact;
    }

    private static List<AnimalContact> getAnimalContactDatabase() {
        return new ArrayList<AnimalContact>() {{
            add(AnimalContact.RODENT);
            add(AnimalContact.BAT);
            add(AnimalContact.PRIMATE);
            add(AnimalContact.SWINE);
            add(AnimalContact.BIRD);
            add(AnimalContact.EAT_RAW_UNDERCOOKED_BIRD);
            add(AnimalContact.EXPOSURE_TO_DOMESTICATED_BIRD);
            add(AnimalContact.EXPOSURE_TO_SICK_BIRD);
            add(AnimalContact.CATTLE);
            add(AnimalContact.WILD_ANIMAL);
            add(AnimalContact.OTHER_ANIMAL);
            add(AnimalContact.CONTACT_WITH_BODY_OF_WATER);
            add(AnimalContact.TICK_BITE);
        }};
    }

    private static class ValueLoader implements IAnimalContactValueLoader {

        private List<AnimalContact> list;

        public ValueLoader(List<AnimalContact> list) {
            this.list = list;
        }

        @Override
        public List<AnimalContact> unloaded() {
            return list;
        }

        @Override
        public List<AnimalContact> loadState(EpiData record) {
            return AnimalContactFacade.loadState(list, record);
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
        if (!(obj instanceof AnimalContact)) {
            return false;
        }
        AnimalContact other = (AnimalContact) obj;
        return value == other.value;
    }

    @Override
    public String toString() {
        return getName();
    }

    // </editor-fold>


}
