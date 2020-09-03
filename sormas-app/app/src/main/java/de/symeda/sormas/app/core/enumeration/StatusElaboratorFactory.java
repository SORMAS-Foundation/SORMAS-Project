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

package de.symeda.sormas.app.core.enumeration;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.sample.ShipmentStatus;

public class StatusElaboratorFactory {

	public static StatusElaborator getElaborator(Enum e) {
		if (e == null)
			throw new NullPointerException("Enum arugment for StatusElaboratorFactory cannot be null");

		StatusElaborator result;

		if (e instanceof StatusElaborator) {
			result = (StatusElaborator) e;
		} else if (e instanceof EventStatus) {
			result = new EventStatusElaborator((EventStatus) e);
		} else if (e instanceof FollowUpStatus) {
			result = new FollowUpStatusElaborator((FollowUpStatus) e);
		} else if (e instanceof InvestigationStatus) {
			result = new InvestigationStatusElaborator((InvestigationStatus) e);
		} else if (e instanceof ShipmentStatus) {
			result = new ShipmentStatusElaborator((ShipmentStatus) e);
		} else if (e instanceof TaskStatus) {
			result = new TaskStatusElaborator((TaskStatus) e);
		} else if (e instanceof VisitStatus) {
			result = new VisitStatusElaborator((VisitStatus) e);
		} else if (e instanceof CaseClassification) {
			result = new CaseClassificationElaborator((CaseClassification) e);
		} else if (e instanceof ContactClassification) {
			result = new ContactClassificationElaborator((ContactClassification) e);
		} else if (e instanceof PathogenTestResultType) {
			result = new PathogenTestResultTypeElaborator((PathogenTestResultType) e);
		} else {
			throw new IllegalArgumentException(e.getDeclaringClass().getName());
		}

		return result;
	}
}
