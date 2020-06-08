/*******************************************************************************
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.utils;

import com.vaadin.v7.data.validator.AbstractValidator;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.utils.ValidationException;

@SuppressWarnings("serial")
public class TaskStatusValidator extends AbstractValidator<TaskStatus> {

	private String caseUuid;

	public TaskStatusValidator(String caseUuid, String errorMessage) {
		super(errorMessage);
		this.caseUuid = caseUuid;
	}

	@Override
	protected boolean isValidValue(TaskStatus value) {

		if (value == TaskStatus.DONE) {
			try {
				CaseLogic.validateInvestigationDoneAllowed(FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid));
				return true;
			} catch (ValidationException e) {
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	public Class<TaskStatus> getType() {
		return TaskStatus.class;
	}
}
