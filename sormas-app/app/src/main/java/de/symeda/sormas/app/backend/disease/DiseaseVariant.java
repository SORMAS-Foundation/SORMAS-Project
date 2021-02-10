/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.disease;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

@Entity(name = DiseaseVariant.TABLE_NAME)
@DatabaseTable(tableName = DiseaseVariant.TABLE_NAME)
public class DiseaseVariant extends AbstractDomainObject {

	private static final long serialVersionUID = -8897782495898605114L;

	public static final String TABLE_NAME = "diseaseVariant";
	public static final String I18N_PREFIX = "DiseaseVariant";

	public static final String DISEASE = "disease";

	@Enumerated(EnumType.STRING)
	private Disease disease;

	@DatabaseField
	private String name;

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	@Override
	public String toString() {
		return getName();
	}
}
