package de.symeda.sormas.app.symptom;

import java.util.Date;

/**
 * Created by Orson on 19/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class LesionChildViewModel implements ISymptomViewModel {

    private Symptom symptom;
    private Symptom lesionsThatItches;
    private Symptom lesionsInSameState;
    private Symptom lesionsSameSize;
    private Symptom lesionsDeepAndProfound;
    private Symptom lesionsResemblePic1;
    private Symptom lesionsResemblePic2;
    private Symptom lesionsResemblePic3;
    private Symptom lesionsResemblePic4;
    private Date lesionsOnsetDate;

    private boolean locationFace;
    private boolean locationLegs;
    private boolean locationSolesOfFeet;
    private boolean locationPalmOfHands;
    private boolean locationThroax;
    private boolean locationArms;
    private boolean locationGenitals;
    private boolean locationAllBody;


    public LesionChildViewModel(Symptom s) {
        this.symptom = s;

        this.lesionsThatItches = Symptom.LESIONS_THAT_ITCH;
        this.lesionsInSameState = Symptom.LESIONS_SAME_STATE;
        this.lesionsSameSize = Symptom.LESIONS_SAME_SIZE;
        this.lesionsDeepAndProfound = Symptom.LESIONS_SAME_PROFOUND;
        this.lesionsResemblePic1 = Symptom.LESIONS_LIKE_PIC1;
        this.lesionsResemblePic2 = Symptom.LESIONS_LIKE_PIC2;
        this.lesionsResemblePic3 = Symptom.LESIONS_LIKE_PIC3;
        this.lesionsResemblePic4 = Symptom.LESIONS_LIKE_PIC4;
    }


    public Symptom getLesionsThatItches() {
        return lesionsThatItches;
    }

    public void setLesionsThatItches(Symptom lesionsThatItches) {
        this.lesionsThatItches = lesionsThatItches;
    }

    public Symptom getLesionsInSameState() {
        return lesionsInSameState;
    }

    public void setLesionsInSameState(Symptom lesionsInSameState) {
        this.lesionsInSameState = lesionsInSameState;
    }

    public Symptom getLesionsSameSize() {
        return lesionsSameSize;
    }

    public void setLesionsSameSize(Symptom lesionsSameSize) {
        this.lesionsSameSize = lesionsSameSize;
    }

    public Symptom getLesionsDeepAndProfound() {
        return lesionsDeepAndProfound;
    }

    public void setLesionsDeepAndProfound(Symptom lesionsDeepAndProfound) {
        this.lesionsDeepAndProfound = lesionsDeepAndProfound;
    }

    public Symptom getLesionsResemblePic1() {
        return lesionsResemblePic1;
    }

    public void setLesionsResemblePic1(Symptom lesionsResemblePic1) {
        this.lesionsResemblePic1 = lesionsResemblePic1;
    }

    public Symptom getLesionsResemblePic2() {
        return lesionsResemblePic2;
    }

    public void setLesionsResemblePic2(Symptom lesionsResemblePic2) {
        this.lesionsResemblePic2 = lesionsResemblePic2;
    }

    public Symptom getLesionsResemblePic3() {
        return lesionsResemblePic3;
    }

    public void setLesionsResemblePic3(Symptom lesionsResemblePic3) {
        this.lesionsResemblePic3 = lesionsResemblePic3;
    }

    public Symptom getLesionsResemblePic4() {
        return lesionsResemblePic4;
    }

    public void setLesionsResemblePic4(Symptom lesionsResemblePic4) {
        this.lesionsResemblePic4 = lesionsResemblePic4;
    }

    public Date getLesionsOnsetDate() {
        return lesionsOnsetDate;
    }

    public void setLesionsOnsetDate(Date lesionsOnsetDate) {
        this.lesionsOnsetDate = lesionsOnsetDate;
    }

    public boolean isLocationFace() {
        return locationFace;
    }

    public void setLocationFace(boolean locationFace) {
        this.locationFace = locationFace;
    }

    public boolean isLocationLegs() {
        return locationLegs;
    }

    public void setLocationLegs(boolean locationLegs) {
        this.locationLegs = locationLegs;
    }

    public boolean isLocationSolesOfFeet() {
        return locationSolesOfFeet;
    }

    public void setLocationSolesOfFeet(boolean locationSolesOfFeet) {
        this.locationSolesOfFeet = locationSolesOfFeet;
    }

    public boolean isLocationPalmOfHands() {
        return locationPalmOfHands;
    }

    public void setLocationPalmOfHands(boolean locationPalmOfHands) {
        this.locationPalmOfHands = locationPalmOfHands;
    }

    public boolean isLocationThroax() {
        return locationThroax;
    }

    public void setLocationThroax(boolean locationThroax) {
        this.locationThroax = locationThroax;
    }

    public boolean isLocationArms() {
        return locationArms;
    }

    public void setLocationArms(boolean locationArms) {
        this.locationArms = locationArms;
    }

    public boolean isLocationGenitals() {
        return locationGenitals;
    }

    public void setLocationGenitals(boolean locationGenitals) {
        this.locationGenitals = locationGenitals;
    }

    public boolean isLocationAllBody() {
        return locationAllBody;
    }

    public void setLocationAllBody(boolean locationAllBody) {
        this.locationAllBody = locationAllBody;
    }
}
