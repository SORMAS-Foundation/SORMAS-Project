/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.survey;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.uuid.AbstractUuidDto;

public class SurveyIndexDto extends AbstractUuidDto {

	private static final long serialVersionUID = -5888585683689386052L;

	public static final String DISEASE = "disease";
	public static final String SURVEY_NAME = "name";

	private final String name;
	private final Disease disease;

	public SurveyIndexDto(String uuid, String name, Disease disease) {
		super(uuid);
		this.name = name;
		this.disease = disease;
	}

	public String getName() {
		return name;
	}

	public Disease getDisease() {
		return disease;
	}
}
