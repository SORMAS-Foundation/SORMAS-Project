/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.region;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.symeda.sormas.app.backend.common.InfrastructureAdo;

@Entity(name = District.TABLE_NAME)
@DatabaseTable(tableName = District.TABLE_NAME)
public class SubContinent extends InfrastructureAdo {

    public static final String TABLE_NAME = "subContinent";
    public static final String I18N_PREFIX = "SubContinent";

    public static final String DEFAULT_NAME = "defaultName";
    public static final String CONTINENT = "continent";

    @Column(columnDefinition = "text")
    private String defaultName;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false, maxForeignAutoRefreshLevel = 3)
    @ManyToOne(cascade = CascadeType.REFRESH, optional = false)
    @JoinColumn(nullable = false)
    private Continent continent;

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    @Override
    public String toString() {
        return getDefaultName();
    }

    @Override
    public String getI18nPrefix() {
        return I18N_PREFIX;
    }
}
