package de.symeda.sormas.app.epid;

import de.symeda.sormas.app.R;

import java.util.Date;

/**
 * Created by Orson on 19/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public abstract class AnimalContactLayout {

    private final int value;
    private final String name;
    private final int layoutId;

    private String detailOrSpecify;
    private Date lastExposureDate;
    private String lastExposurePlace;

    // <editor-fold defaultstate="collapsed" desc="Constants">

    public static final AnimalContactLayout NONE = new None();
    public static final AnimalContactLayout DETAILS = new DetailsLayout();
    public static final AnimalContactLayout SPECIFY = new SpecifyLayout();
    public static final AnimalContactLayout DETAILS_LAST_EXPOSURE_DATE_N_PLACE = new DetailsLastExposureLayout();
    public static final AnimalContactLayout SPECIFY_LAST_EXPOSURE_DATE_N_PLACE = new SpecifyLastExposureLayout();

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Contructor">

    private AnimalContactLayout(AnimalContactLayout ac) {
        this.value = ac.getValue();
        this.name = ac.getName();
        this.layoutId = ac.getLayoutId();
    }

    protected AnimalContactLayout(int value, String name, int layoutId) {
        this.value = value;
        this.name = name;
        this.layoutId = layoutId;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters & Setters">

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public String getDetailOrSpecify() {
        return detailOrSpecify;
    }

    public void setDetailOrSpecify(String detailOrSpecify) {
        this.detailOrSpecify = detailOrSpecify;
    }

    public Date getLastExposureDate() {
        return lastExposureDate;
    }

    public void setLastExposureDate(Date lastExposureDate) {
        this.lastExposureDate = lastExposureDate;
    }

    public String getLastExposurePlace() {
        return lastExposurePlace;
    }

    public void setLastExposurePlace(String lastExposurePlace) {
        this.lastExposurePlace = lastExposurePlace;
    }


    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Enumurations">

    private static class None extends AnimalContactLayout {
        public None() {
            super(0, "None", -1);
        }
    }

    private static class DetailsLayout extends AnimalContactLayout {
        public DetailsLayout() {
            super(1, "DetailsLayout", R.layout.row_animal_contact_details_child_layout);
        }
    }

    private static class SpecifyLayout extends AnimalContactLayout {
        public SpecifyLayout() {
            super(2, "SpecifyLayout", R.layout.row_animal_contact_specify_child_layout);
        }
    }

    private static class DetailsLastExposureLayout extends AnimalContactLayout {
        public DetailsLastExposureLayout() {
            super(3, "DetailsLayout, Last Exposure Date and Place", R.layout.row_animal_contact_details_last_exposure_child_layout);
        }
    }

    private static class SpecifyLastExposureLayout extends AnimalContactLayout {
        public SpecifyLastExposureLayout() {
            super(4, "SpecifyLayout, Last Exposure Date and Place", R.layout.row_animal_contact_specify_last_exposure_child_layout);
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Private Methods">

    public static AnimalContactLayout newAnimalContactLayout(AnimalContactLayout s) {
        AnimalContactLayout newAnimalContactLayout = new AnimalContactLayout(s.getValue(), s.getName(), s.getLayoutId()) {
            private AnimalContactLayout _s;

            private AnimalContactLayout init(AnimalContactLayout animalContactLayout) {
                _s = animalContactLayout;

                this.setDetailOrSpecify(animalContactLayout.getDetailOrSpecify());
                this.setLastExposureDate(animalContactLayout.getLastExposureDate());
                this.setLastExposurePlace(animalContactLayout.getLastExposurePlace());

                return this;
            }

        }.init(s);

        return newAnimalContactLayout;
    }

    // </editor-fold>
}
