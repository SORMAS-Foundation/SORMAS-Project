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
package de.symeda.sormas.ui.events.sevenoneseven;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Collections;
import java.util.function.Predicate;

import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.event.sevenoneseven.Event717AssessmentDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717BottleneckCategory;
import de.symeda.sormas.api.event.sevenoneseven.Event717BottleneckDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717Interval;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.SormasFieldGroupFieldFactory;

public class Event717BottleneckEditForm extends AbstractEditForm<Event717BottleneckDto> {

	private static final long serialVersionUID = 4526394518357719870L;

	private static final String HTML_LAYOUT = fluidRowLocs(Event717BottleneckDto.TIMELINESS_INTERVAL)
		+ fluidRowLocs(Event717BottleneckDto.CATEGORY)
		+ fluidRowLocs(Event717BottleneckDto.OTHER_CATEGORY_DETAILS)
		+ fluidRowLocs(Event717BottleneckDto.DESCRIPTION);

	private final transient Predicate<Event717Interval> intervalAvailable;

	/**
	 * @param intervalAvailable
	 *            Whether another bottleneck may be assigned to the given interval without exceeding the maximum
	 */
	public Event717BottleneckEditForm(Predicate<Event717Interval> intervalAvailable, boolean create, boolean isEditAllowed) {
		super(Event717BottleneckDto.class, Event717BottleneckDto.I18N_PREFIX, false, null, null, isEditAllowed);
		this.intervalAvailable = intervalAvailable;

		setWidth(540, Unit.PIXELS);
		addFields();

		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {

		ComboBox interval = addField(Event717BottleneckDto.TIMELINESS_INTERVAL, ComboBox.class);
		interval.addValidator(maxEntriesValidator(intervalAvailable, Event717AssessmentDto.BOTTLENECKS));

		ComboBox category = addField(Event717BottleneckDto.CATEGORY, ComboBox.class);
		// show the theme in front of each category; enum combo boxes use the caption property for their item captions
		for (Event717BottleneckCategory value : Event717BottleneckCategory.values()) {
			category.getItem(value)
				.getItemProperty(SormasFieldGroupFieldFactory.CAPTION_PROPERTY_ID)
				.setValue(value.getTheme() != null ? value.getTheme() + " - " + value : value.toString());
		}

		addField(Event717BottleneckDto.OTHER_CATEGORY_DETAILS);
		TextArea description = addField(Event717BottleneckDto.DESCRIPTION, TextArea.class);
		description.setRows(4);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Event717BottleneckDto.OTHER_CATEGORY_DETAILS,
			Event717BottleneckDto.CATEGORY,
			Event717BottleneckCategory.OTHER,
			true);
		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			Event717BottleneckDto.CATEGORY,
			Collections.singletonList(Event717BottleneckDto.OTHER_CATEGORY_DETAILS),
			Collections.singletonList(Event717BottleneckCategory.OTHER));

		setRequired(true, Event717BottleneckDto.TIMELINESS_INTERVAL, Event717BottleneckDto.CATEGORY, Event717BottleneckDto.DESCRIPTION);
	}

	static Validator maxEntriesValidator(Predicate<Event717Interval> intervalAvailable, String listProperty) {

		return value -> {
			if (value != null && !intervalAvailable.test((Event717Interval) value)) {
				throw new Validator.InvalidValueException(
					I18nProperties.getValidationError(
						Validations.event717MaxEntriesExceeded,
						Event717AssessmentDto.MAX_ENTRIES_PER_INTERVAL,
						I18nProperties.getPrefixCaption(Event717AssessmentDto.I18N_PREFIX, listProperty),
						value));
			}
		};
	}
}
