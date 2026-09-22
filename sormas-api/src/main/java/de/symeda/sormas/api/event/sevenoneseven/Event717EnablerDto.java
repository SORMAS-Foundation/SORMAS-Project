/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
package de.symeda.sormas.api.event.sevenoneseven;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.FieldConstraints;

/**
 * An enabler (factor that enabled timely action) for one 7-1-7 interval. Documented for advocacy and to demonstrate impact.
 */
@DependingOnFeatureType(featureType = FeatureType.EVENT_717_ASSESSMENT)
public class Event717EnablerDto extends EntityDto {

	private static final long serialVersionUID = 3811526094417250358L;

	public static final String I18N_PREFIX = "Event717Enabler";

	public static final String TIMELINESS_INTERVAL = "timelinessInterval";
	public static final String DESCRIPTION = "description";

	@NotNull(message = Validations.requiredField)
	private Event717Interval timelinessInterval;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String description;

	public static Event717EnablerDto build(Event717Interval timelinessInterval) {

		Event717EnablerDto enabler = new Event717EnablerDto();
		enabler.setUuid(DataHelper.createUuid());
		enabler.setTimelinessInterval(timelinessInterval);
		return enabler;
	}

	public Event717Interval getTimelinessInterval() {
		return timelinessInterval;
	}

	public void setTimelinessInterval(Event717Interval timelinessInterval) {
		this.timelinessInterval = timelinessInterval;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
