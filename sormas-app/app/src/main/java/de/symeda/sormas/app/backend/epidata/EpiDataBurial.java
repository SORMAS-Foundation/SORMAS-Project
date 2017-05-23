package de.symeda.sormas.app.backend.epidata;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.EmbeddedAdo;
import de.symeda.sormas.app.backend.location.Location;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

@Entity(name = EpiDataBurial.TABLE_NAME)
@DatabaseTable(tableName = EpiDataBurial.TABLE_NAME)
@EmbeddedAdo
public class EpiDataBurial extends AbstractDomainObject {

    private static final long serialVersionUID = 866789458483672591L;

    public static final String TABLE_NAME = "epidataburial";

    public static final String EPI_DATA = "epiData";
    public static final String BURIAL_ADDRESS = "burialAddress";


    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private EpiData epiData;

    @Column(length=512)
    private String burialPersonname;

    @Column(length=512)
    private String burialRelation;

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date burialDateFrom;

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date burialDateTo;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 2)
    private Location burialAddress;

    @Enumerated(EnumType.STRING)
    private YesNoUnknown burialIll;

    @Enumerated(EnumType.STRING)
    private YesNoUnknown burialTouching;

    public EpiData getEpiData() {
        return epiData;
    }

    public void setEpiData(EpiData epiData) {
        this.epiData = epiData;
    }

    public String getBurialPersonname() {
        return burialPersonname;
    }

    public void setBurialPersonname(String burialPersonname) {
        this.burialPersonname = burialPersonname;
    }

    public String getBurialRelation() {
        return burialRelation;
    }

    public void setBurialRelation(String burialRelation) {
        this.burialRelation = burialRelation;
    }

    public Date getBurialDateFrom() {
        return burialDateFrom;
    }

    public void setBurialDateFrom(Date burialDateFrom) {
        this.burialDateFrom = burialDateFrom;
    }

    public Date getBurialDateTo() {
        return burialDateTo;
    }

    public void setBurialDateTo(Date burialDateTo) {
        this.burialDateTo = burialDateTo;
    }

    public Location getBurialAddress() {
        return burialAddress;
    }

    public void setBurialAddress(Location burialAddress) {
        this.burialAddress = burialAddress;
    }

    public YesNoUnknown getBurialIll() {
        return burialIll;
    }

    public void setBurialIll(YesNoUnknown burialIll) {
        this.burialIll = burialIll;
    }

    public YesNoUnknown getBurialTouching() {
        return burialTouching;
    }

    public void setBurialTouching(YesNoUnknown burialTouching) {
        this.burialTouching = burialTouching;
    }
}
