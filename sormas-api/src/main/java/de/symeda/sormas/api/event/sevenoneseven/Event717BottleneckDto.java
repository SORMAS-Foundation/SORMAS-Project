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
import de.symeda.sormas.api.utils.SensitiveData;

/**
 * A bottleneck (factor that prevented timely action) for one 7-1-7 interval, categorized by one of the common 7-1-7
 * bottleneck categories.
 */
@DependingOnFeatureType(featureType = FeatureType.EVENT_717_ASSESSMENT)
public class Event717BottleneckDto extends EntityDto {

	private static final long serialVersionUID = 6140233514069426380L;

	public static final String I18N_PREFIX = "Event717Bottleneck";

	public static final String TIMELINESS_INTERVAL = "timelinessInterval";
	public static final String DESCRIPTION = "description";
	public static final String CATEGORY = "category";
	public static final String OTHER_CATEGORY_DETAILS = "otherCategoryDetails";

	@NotNull(message = Validations.requiredField)
	private Event717Interval timelinessInterval;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String description;
	private Event717BottleneckCategory category;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String otherCategoryDetails;

	public static Event717BottleneckDto build(Event717Interval timelinessInterval) {

		Event717BottleneckDto bottleneck = new Event717BottleneckDto();
		bottleneck.setUuid(DataHelper.createUuid());
		bottleneck.setTimelinessInterval(timelinessInterval);
		return bottleneck;
	}

	public Event717BottleneckReferenceDto toReference() {
		return new Event717BottleneckReferenceDto(getUuid(), description);
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

	public Event717BottleneckCategory getCategory() {
		return category;
	}

	public void setCategory(Event717BottleneckCategory category) {
		this.category = category;
	}

	public String getOtherCategoryDetails() {
		return otherCategoryDetails;
	}

	public void setOtherCategoryDetails(String otherCategoryDetails) {
		this.otherCategoryDetails = otherCategoryDetails;
	}
}
