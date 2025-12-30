/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.infrastructure.forms;

import java.io.Serializable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FormType;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class FormBuilderCriteria extends BaseCriteria implements Serializable, Cloneable {

	private static final long serialVersionUID = -7653585175036656542L;

	public static final String FORM_TYPE = "formType";
	public static final String DISEASE = "disease";
	public static final String ACTIVE = "active";
	public static final String RELEVANCE_STATUS = "relevanceStatus";

	private FormType formType;
	private Disease disease;
	private Boolean active;
	private EntityRelevanceStatus relevanceStatus;

	public FormType getFormType() {
		return formType;
	}

	public FormBuilderCriteria formType(FormType formType) {
		this.formType = formType;
		return this;
	}

	public Disease getDisease() {
		return disease;
	}

	public FormBuilderCriteria disease(Disease disease) {
		this.disease = disease;
		return this;
	}

	public Boolean getActive() {
		return active;
	}

	public FormBuilderCriteria active(Boolean active) {
		this.active = active;
		return this;
	}

	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}

	public FormBuilderCriteria relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
		return this;
	}
}

