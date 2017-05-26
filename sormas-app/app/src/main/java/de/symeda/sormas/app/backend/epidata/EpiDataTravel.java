package de.symeda.sormas.app.backend.epidata;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.epidata.TravelType;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.EmbeddedAdo;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

@Entity(name = EpiDataTravel.TABLE_NAME)
@DatabaseTable(tableName = EpiDataTravel.TABLE_NAME)
@EmbeddedAdo(parentAccessor = EpiDataTravel.EPI_DATA)
public class EpiDataTravel extends AbstractDomainObject {

    private static final long serialVersionUID = -4280455878066233175L;

    public static final String TABLE_NAME = "epidatatravel";
    public static final String EPI_DATA = "epiData";

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private EpiData epiData;

    @Enumerated(EnumType.STRING)
    private TravelType travelType;

    @Column(length=512)
    private String travelDestination;

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date travelDateFrom;

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date travelDateTo;

    public EpiData getEpiData() {
        return epiData;
    }

    public void setEpiData(EpiData epiData) {
        this.epiData = epiData;
    }

    public TravelType getTravelType() {
        return travelType;
    }

    public void setTravelType(TravelType travelType) {
        this.travelType = travelType;
    }

    public String getTravelDestination() {
        return travelDestination;
    }

    public void setTravelDestination(String travelDestination) {
        this.travelDestination = travelDestination;
    }

    public Date getTravelDateFrom() {
        return travelDateFrom;
    }

    public void setTravelDateFrom(Date travelDateFrom) {
        this.travelDateFrom = travelDateFrom;
    }

    public Date getTravelDateTo() {
        return travelDateTo;
    }

    public void setTravelDateTo(Date travelDateTo) {
        this.travelDateTo = travelDateTo;
    }
}
