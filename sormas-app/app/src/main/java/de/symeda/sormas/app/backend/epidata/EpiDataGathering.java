/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.epidata;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.EmbeddedAdo;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.util.DateFormatHelper;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

@Entity(name = EpiDataGathering.TABLE_NAME)
@DatabaseTable(tableName = EpiDataGathering.TABLE_NAME)
@EmbeddedAdo(parentAccessor = EpiDataGathering.EPI_DATA)
public class EpiDataGathering extends AbstractDomainObject {

    private static final long serialVersionUID = 5491651166245301869L;

    public static final String TABLE_NAME = "epidatagathering";
    public static final String I18N_PREFIX = "EpiDataGathering";

    public static final String EPI_DATA = "epiData";
    public static final String GATHERING_ADDRESS = "gatheringAddress";

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private EpiData epiData;

    @Column(length=512)
    private String description;

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date gatheringDate;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 2)
    private Location gatheringAddress;

    public EpiData getEpiData() {
        return epiData;
    }

    public void setEpiData(EpiData epiData) {
        this.epiData = epiData;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getGatheringDate() {
        return gatheringDate;
    }

    public void setGatheringDate(Date gatheringDate) {
        this.gatheringDate = gatheringDate;
    }

    public Location getGatheringAddress() {
        return gatheringAddress;
    }

    public void setGatheringAddress(Location gatheringAddress) {
        this.gatheringAddress = gatheringAddress;
    }

    @Override
    public String toString() {
        return super.toString() + " " + DateFormatHelper.formatLocalDate(getGatheringDate());
    }

    @Override
    public String getI18nPrefix() {
        return I18N_PREFIX;
    }
}
