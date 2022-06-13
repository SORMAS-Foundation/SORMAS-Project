/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.clinicalcourse;

import com.j256.ormlite.table.DatabaseTable;

import javax.persistence.Entity;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.EmbeddedAdo;

@Entity(name = ClinicalCourse.TABLE_NAME)
@DatabaseTable(tableName = ClinicalCourse.TABLE_NAME)
@EmbeddedAdo
public class ClinicalCourse extends AbstractDomainObject {

	private static final long serialVersionUID = -2664896907352864261L;

	public static final String TABLE_NAME = "clinicalCourse";
	public static final String I18N_PREFIX = "ClinicalCourse";

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
