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
    private YesNoUnknown poultryEat;

    @Enumerated(EnumType.STRING)
    private YesNoUnknown poultry;

    @Column(length=512)
    private String poultryDetails;

    @Enumerated(EnumType.STRING)
    private YesNoUnknown poultrySick;

    @Column(length=512)
    private String poultrySickDetails;

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date poultryDate;

    @Column(length=512)
    private String poultryLocation;

    @Enumerated(EnumType.STRING)
    private YesNoUnknown wildbirds;

    @Column(length=512)
    private String wildbirdsDetails;

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date wildbirdsDate;

    @Column(length=512)
    private String wildbirdsLocation;

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

    public YesNoUnknown getPoultryEat() {
        return poultryEat;
    }

    public void setPoultryEat(YesNoUnknown poultryEat) {
        this.poultryEat = poultryEat;
    }

    public YesNoUnknown getPoultry() {
        return poultry;
    }

    public void setPoultry(YesNoUnknown poultry) {
        this.poultry = poultry;
    }

    public String getPoultryDetails() {
        return poultryDetails;
    }

    public void setPoultryDetails(String poultryDetails) {
        this.poultryDetails = poultryDetails;
    }

    public YesNoUnknown getPoultrySick() {
        return poultrySick;
    }

    public void setPoultrySick(YesNoUnknown poultrySick) {
        this.poultrySick = poultrySick;
    }

    public String getPoultrySickDetails() {
        return poultrySickDetails;
    }

    public void setPoultrySickDetails(String poultrySickDetails) {
        this.poultrySickDetails = poultrySickDetails;
    }

    public Date getPoultryDate() {
        return poultryDate;
    }

    public void setPoultryDate(Date poultryDate) {
        this.poultryDate = poultryDate;
    }

    public String getPoultryLocation() {
        return poultryLocation;
    }

    public void setPoultryLocation(String poultryLocation) {
        this.poultryLocation = poultryLocation;
    }

    public YesNoUnknown getWildbirds() {
        return wildbirds;
    }

    public void setWildbirds(YesNoUnknown wildbirds) {
        this.wildbirds = wildbirds;
    }

    public String getWildbirdsDetails() {
        return wildbirdsDetails;
    }

    public void setWildbirdsDetails(String wildbirdsDetails) {
        this.wildbirdsDetails = wildbirdsDetails;
    }

    public Date getWildbirdsDate() {
        return wildbirdsDate;
    }

    public void setWildbirdsDate(Date wildbirdsDate) {
        this.wildbirdsDate = wildbirdsDate;
    }

    public String getWildbirdsLocation() {
        return wildbirdsLocation;
    }

    public void setWildbirdsLocation(String wildbirdsLocation) {
        this.wildbirdsLocation = wildbirdsLocation;
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

    @Override
    public String getI18nPrefix() {
        return I18N_PREFIX;
    }
}
