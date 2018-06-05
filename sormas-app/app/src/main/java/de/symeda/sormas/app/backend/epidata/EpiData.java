package de.symeda.sormas.app.backend.epidata;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.epidata.AnimalCondition;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.epidata.WaterSource;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.EmbeddedAdo;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

@Entity(name = EpiData.TABLE_NAME)
@DatabaseTable(tableName = EpiData.TABLE_NAME)
@EmbeddedAdo
public class EpiData extends AbstractDomainObject {

    private static final long serialVersionUID = -8294812479501735785L;

    public static final String TABLE_NAME = "epidata";
    public static final String I18N_PREFIX = "EpiData";

    @Enumerated(EnumType.STRING)
    private YesNoUnknown burialAttended;
    @Enumerated(EnumType.STRING)
    private YesNoUnknown gatheringAttended;
    @Enumerated(EnumType.STRING)
    private YesNoUnknown traveled;

    @Enumerated(EnumType.STRING)
    private YesNoUnknown directContactConfirmedCase;
    @Enumerated(EnumType.STRING)
    private YesNoUnknown directContactProbableCase;
    @Enumerated(EnumType.STRING)
    private YesNoUnknown closeContactProbableCase;
    @Enumerated(EnumType.STRING)
    private YesNoUnknown areaConfirmedCases;

    @Enumerated(EnumType.STRING)
    private YesNoUnknown processingConfirmedCaseFluidUnsafe;
    @Enumerated(EnumType.STRING)
    private YesNoUnknown percutaneousCaseBlood;
    @DatabaseField(dataType = DataType.ENUM_STRING, columnName = "wildbirdsLocation")
    private YesNoUnknown directContactDeadUnsafe;
    @DatabaseField(dataType = DataType.ENUM_STRING, columnName = "wildbirdsDetails")
    private YesNoUnknown processingSuspectedCaseSampleUnsafe;

    @DatabaseField(dataType = DataType.ENUM_STRING, columnName = "wildbirds")
    private YesNoUnknown areaInfectedAnimals;
    @DatabaseField(dataType = DataType.ENUM_STRING, columnName = "poultrySick")
    private YesNoUnknown sickDeadAnimals;
    @Column(length=512)
    @DatabaseField(columnName = "poultrySickDetails")
    private String sickDeadAnimalsDetails;
    @DatabaseField(dataType = DataType.DATE_LONG, columnName = "poultryDate")
    private Date sickDeadAnimalsDate;
    @Column(length=512)
    @DatabaseField(columnName = "poultryLocation")
    private String sickDeadAnimalsLocation;
    @DatabaseField(dataType = DataType.ENUM_STRING, columnName = "poultry")
    private YesNoUnknown eatingRawAnimalsInInfectedArea;
    @DatabaseField(dataType = DataType.ENUM_STRING, columnName = "poultryEat")
    private YesNoUnknown eatingRawAnimals;
    @Column(length=512)
    @DatabaseField(columnName = "poultryDetails")
    private String eatingRawAnimalsDetails;

    @Enumerated(EnumType.STRING)
    private YesNoUnknown rodents;
    @Enumerated(EnumType.STRING)
    private YesNoUnknown bats;
    @Enumerated(EnumType.STRING)
    private YesNoUnknown primates;
    @Enumerated(EnumType.STRING)
    private YesNoUnknown swine;
    @Enumerated(EnumType.STRING)
    private YesNoUnknown birds;
    @Enumerated(EnumType.STRING)
    private YesNoUnknown cattle;
    @Enumerated(EnumType.STRING)
    private YesNoUnknown otherAnimals;
    @Column(length=512)
    private String otherAnimalsDetails;

    @Enumerated(EnumType.STRING)
    private WaterSource waterSource;
    @Column(length=512)
    private String waterSourceOther;
    @Enumerated(EnumType.STRING)
    private YesNoUnknown waterBody;
    @Column(length=512)
    private String waterBodyDetails;

    @Enumerated(EnumType.STRING)
    private YesNoUnknown tickBite;
    @Enumerated(EnumType.STRING)
    private YesNoUnknown fleaBite;

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date dateOfLastExposure;
    @Column(length=512)
    private String placeOfLastExposure;
    @Enumerated(EnumType.STRING)
    private AnimalCondition animalCondition;

    @DatabaseField(dataType = DataType.DATE_LONG, columnName = "wildbirdsDate")
    private Date unusedDate;

    // just for reference, not persisted in DB
    private List<EpiDataBurial> burials = new ArrayList<>();
    private List<EpiDataGathering> gatherings = new ArrayList<>();
    private List<EpiDataTravel> travels = new ArrayList<>();

    public YesNoUnknown getBurialAttended() {
        return burialAttended;
    }

    public void setBurialAttended(YesNoUnknown burialAttended) {
        this.burialAttended = burialAttended;
    }

    public YesNoUnknown getGatheringAttended() {
        return gatheringAttended;
    }

    public void setGatheringAttended(YesNoUnknown gatheringAttended) {
        this.gatheringAttended = gatheringAttended;
    }

    public YesNoUnknown getTraveled() {
        return traveled;
    }

    public void setTraveled(YesNoUnknown traveled) {
        this.traveled = traveled;
    }

    public YesNoUnknown getRodents() {
        return rodents;
    }

    public void setRodents(YesNoUnknown rodents) {
        this.rodents = rodents;
    }

    public YesNoUnknown getBats() {
        return bats;
    }

    public void setBats(YesNoUnknown bats) {
        this.bats = bats;
    }

    public YesNoUnknown getPrimates() {
        return primates;
    }

    public void setPrimates(YesNoUnknown primates) {
        this.primates = primates;
    }

    public YesNoUnknown getSwine() {
        return swine;
    }

    public void setSwine(YesNoUnknown swine) {
        this.swine = swine;
    }

    public YesNoUnknown getBirds() {
        return birds;
    }

    public void setBirds(YesNoUnknown birds) {
        this.birds = birds;
    }

    public YesNoUnknown getCattle() {
        return cattle;
    }

    public void setCattle(YesNoUnknown cattle) {
        this.cattle = cattle;
    }

    public YesNoUnknown getOtherAnimals() {
        return otherAnimals;
    }

    public void setOtherAnimals(YesNoUnknown otherAnimals) {
        this.otherAnimals = otherAnimals;
    }

    public String getOtherAnimalsDetails() {
        return otherAnimalsDetails;
    }

    public void setOtherAnimalsDetails(String otherAnimalsDetails) {
        this.otherAnimalsDetails = otherAnimalsDetails;
    }

    public WaterSource getWaterSource() {
        return waterSource;
    }

    public void setWaterSource(WaterSource waterSource) {
        this.waterSource = waterSource;
    }

    public String getWaterSourceOther() {
        return waterSourceOther;
    }

    public void setWaterSourceOther(String waterSourceOther) {
        this.waterSourceOther = waterSourceOther;
    }

    public YesNoUnknown getWaterBody() {
        return waterBody;
    }

    public void setWaterBody(YesNoUnknown waterBody) {
        this.waterBody = waterBody;
    }

    public String getWaterBodyDetails() {
        return waterBodyDetails;
    }

    public void setWaterBodyDetails(String waterBodyDetails) {
        this.waterBodyDetails = waterBodyDetails;
    }

    public YesNoUnknown getTickBite() {
        return tickBite;
    }

    public void setTickBite(YesNoUnknown tickBite) {
        this.tickBite = tickBite;
    }

    public YesNoUnknown getFleaBite() {
        return fleaBite;
    }

    public void setFleaBite(YesNoUnknown fleaBite) {
        this.fleaBite = fleaBite;
    }

    public Date getDateOfLastExposure() {
        return dateOfLastExposure;
    }

    public void setDateOfLastExposure(Date dateOfLastExposure) {
        this.dateOfLastExposure = dateOfLastExposure;
    }

    public String getPlaceOfLastExposure() {
        return placeOfLastExposure;
    }

    public void setPlaceOfLastExposure(String placeOfLastExposure) {
        this.placeOfLastExposure = placeOfLastExposure;
    }

    public AnimalCondition getAnimalCondition() {
        return animalCondition;
    }

    public void setAnimalCondition(AnimalCondition animalCondition) {
        this.animalCondition = animalCondition;
    }

    public List<EpiDataBurial> getBurials() {
        return burials;
    }

    public void setBurials(List<EpiDataBurial> burials) {
        this.burials = burials;
    }

    public List<EpiDataGathering> getGatherings() {
        return gatherings;
    }

    public void setGatherings(List<EpiDataGathering> gatherings) {
        this.gatherings = gatherings;
    }

    public List<EpiDataTravel> getTravels() {
        return travels;
    }

    public void setTravels(List<EpiDataTravel> travels) {
        this.travels = travels;
    }

    public YesNoUnknown getDirectContactConfirmedCase() {
        return directContactConfirmedCase;
    }

    public void setDirectContactConfirmedCase(YesNoUnknown directContactConfirmedCase) {
        this.directContactConfirmedCase = directContactConfirmedCase;
    }

    public YesNoUnknown getDirectContactProbableCase() {
        return directContactProbableCase;
    }

    public void setDirectContactProbableCase(YesNoUnknown directContactProbableCase) {
        this.directContactProbableCase = directContactProbableCase;
    }

    public YesNoUnknown getCloseContactProbableCase() {
        return closeContactProbableCase;
    }

    public void setCloseContactProbableCase(YesNoUnknown closeContactProbableCase) {
        this.closeContactProbableCase = closeContactProbableCase;
    }

    public YesNoUnknown getAreaConfirmedCases() {
        return areaConfirmedCases;
    }

    public void setAreaConfirmedCases(YesNoUnknown areaConfirmedCases) {
        this.areaConfirmedCases = areaConfirmedCases;
    }

    public YesNoUnknown getProcessingConfirmedCaseFluidUnsafe() {
        return processingConfirmedCaseFluidUnsafe;
    }

    public void setProcessingConfirmedCaseFluidUnsafe(YesNoUnknown processingConfirmedCaseFluidUnsafe) {
        this.processingConfirmedCaseFluidUnsafe = processingConfirmedCaseFluidUnsafe;
    }

    public YesNoUnknown getPercutaneousCaseBlood() {
        return percutaneousCaseBlood;
    }

    public void setPercutaneousCaseBlood(YesNoUnknown percutaneousCaseBlood) {
        this.percutaneousCaseBlood = percutaneousCaseBlood;
    }

    public YesNoUnknown getDirectContactDeadUnsafe() {
        return directContactDeadUnsafe;
    }

    public void setDirectContactDeadUnsafe(YesNoUnknown directContactDeadUnsafe) {
        this.directContactDeadUnsafe = directContactDeadUnsafe;
    }

    public YesNoUnknown getProcessingSuspectedCaseSampleUnsafe() {
        return processingSuspectedCaseSampleUnsafe;
    }

    public void setProcessingSuspectedCaseSampleUnsafe(YesNoUnknown processingSuspectedCaseSampleUnsafe) {
        this.processingSuspectedCaseSampleUnsafe = processingSuspectedCaseSampleUnsafe;
    }

    public YesNoUnknown getAreaInfectedAnimals() {
        return areaInfectedAnimals;
    }

    public void setAreaInfectedAnimals(YesNoUnknown areaInfectedAnimals) {
        this.areaInfectedAnimals = areaInfectedAnimals;
    }

    public YesNoUnknown getSickDeadAnimals() {
        return sickDeadAnimals;
    }

    public void setSickDeadAnimals(YesNoUnknown sickDeadAnimals) {
        this.sickDeadAnimals = sickDeadAnimals;
    }

    public String getSickDeadAnimalsDetails() {
        return sickDeadAnimalsDetails;
    }

    public void setSickDeadAnimalsDetails(String sickDeadAnimalsDetails) {
        this.sickDeadAnimalsDetails = sickDeadAnimalsDetails;
    }

    public Date getSickDeadAnimalsDate() {
        return sickDeadAnimalsDate;
    }

    public void setSickDeadAnimalsDate(Date sickDeadAnimalsDate) {
        this.sickDeadAnimalsDate = sickDeadAnimalsDate;
    }

    public String getSickDeadAnimalsLocation() {
        return sickDeadAnimalsLocation;
    }

    public void setSickDeadAnimalsLocation(String sickDeadAnimalsLocation) {
        this.sickDeadAnimalsLocation = sickDeadAnimalsLocation;
    }

    public YesNoUnknown getEatingRawAnimalsInInfectedArea() {
        return eatingRawAnimalsInInfectedArea;
    }

    public void setEatingRawAnimalsInInfectedArea(YesNoUnknown eatingRawAnimalsInInfectedArea) {
        this.eatingRawAnimalsInInfectedArea = eatingRawAnimalsInInfectedArea;
    }

    public YesNoUnknown getEatingRawAnimals() {
        return eatingRawAnimals;
    }

    public void setEatingRawAnimals(YesNoUnknown eatingRawAnimals) {
        this.eatingRawAnimals = eatingRawAnimals;
    }

    public String getEatingRawAnimalsDetails() {
        return eatingRawAnimalsDetails;
    }

    public void setEatingRawAnimalsDetails(String eatingRawAnimalsDetails) {
        this.eatingRawAnimalsDetails = eatingRawAnimalsDetails;
    }

    @Override
    public String getI18nPrefix() {
        return I18N_PREFIX;
    }
}
