/*
 * This file is part of SORMAS®.
 *
 * SORMAS® is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SORMAS® is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SORMAS®.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * This file is part of SORMAS®.
 *
 * SORMAS® is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SORMAS® is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SORMAS®.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.classification;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

@Entity(name = DiseaseClassification.TABLE_NAME)
@DatabaseTable(tableName = DiseaseClassification.TABLE_NAME)
public class DiseaseClassification extends AbstractDomainObject {

    public static final String TABLE_NAME = "diseaseclassification";

    @Enumerated(EnumType.STRING)
    private Disease disease;

    @Column(length = 2147483647)
    private String suspectCriteria;

    @Column(length = 2147483647)
    private String probableCriteria;

    @Column(length = 2147483647)
    private String confirmedCriteria;

    public Disease getDisease() {
        return disease;
    }

    public void setDisease(Disease disease) {
        this.disease = disease;
    }

    public String getSuspectCriteria() {
        return suspectCriteria;
    }

    public void setSuspectCriteria(String suspectCriteria) {
        this.suspectCriteria = suspectCriteria;
    }

    public String getProbableCriteria() {
        return probableCriteria;
    }

    public void setProbableCriteria(String probableCriteria) {
        this.probableCriteria = probableCriteria;
    }

    public String getConfirmedCriteria() {
        return confirmedCriteria;
    }

    public void setConfirmedCriteria(String confirmedCriteria) {
        this.confirmedCriteria = confirmedCriteria;
    }

}