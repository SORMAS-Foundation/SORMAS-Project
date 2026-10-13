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

import java.util.List;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.event.sevenoneseven.Event717BottleneckReferenceDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717CorrectiveActionDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717CorrectiveActionPriority;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;

public class Event717CorrectiveActionEditForm extends AbstractEditForm<Event717CorrectiveActionDto> {

	private static final long serialVersionUID = 7209214634806541786L;

	private static final String HTML_LAYOUT = fluidRowLocs(Event717CorrectiveActionDto.PROPOSED_ACTION)
		+ fluidRowLocs(Event717CorrectiveActionDto.BOTTLENECK)
		+ fluidRowLocs(Event717CorrectiveActionDto.PRIORITIZATION, Event717CorrectiveActionDto.PROGRESS_STATUS)
		+ fluidRowLocs(Event717CorrectiveActionDto.RESPONSIBLE_AUTHORITY)
		+ fluidRowLocs(Event717CorrectiveActionDto.TARGET_START_DATE, Event717CorrectiveActionDto.TARGET_END_DATE)
		+ fluidRowLocs(Event717CorrectiveActionDto.PLANNING_FUNDING_OPPORTUNITIES)
		+ fluidRowLocs(Event717CorrectiveActionDto.NEXT_STEPS);

	private final transient List<Event717BottleneckReferenceDto> availableBottlenecks;

	/**
	 * @param availableBottlenecks
	 *            The bottlenecks of the assessment that can be addressed by the action
	 */
	public Event717CorrectiveActionEditForm(
		List<Event717BottleneckReferenceDto> availableBottlenecks,
		boolean create,
		UiFieldAccessCheckers<Event717CorrectiveActionDto> fieldAccessCheckers,
		boolean isEditAllowed) {
		super(Event717CorrectiveActionDto.class, Event717CorrectiveActionDto.I18N_PREFIX, false, null, fieldAccessCheckers, isEditAllowed);
		this.availableBottlenecks = availableBottlenecks;

		setWidth(640, Unit.PIXELS);
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

		TextArea proposedAction = addField(Event717CorrectiveActionDto.PROPOSED_ACTION, TextArea.class);
		proposedAction.setRows(3);

		ComboBox bottleneck = addField(Event717CorrectiveActionDto.BOTTLENECK, ComboBox.class);
		bottleneck.addItems(availableBottlenecks);

		addField(Event717CorrectiveActionDto.PRIORITIZATION, ComboBox.class);
		addField(Event717CorrectiveActionDto.PROGRESS_STATUS, ComboBox.class);
		addField(Event717CorrectiveActionDto.RESPONSIBLE_AUTHORITY);

		// target dates are planned and may be in the future
		DateField targetStartDate = addDateField(Event717CorrectiveActionDto.TARGET_START_DATE, DateField.class, -1);
		DateField targetEndDate = addDateField(Event717CorrectiveActionDto.TARGET_END_DATE, DateField.class, -1);
		DateComparisonValidator.addStartEndValidators(targetStartDate, targetEndDate);

		TextArea planningFundingOpportunities = addField(Event717CorrectiveActionDto.PLANNING_FUNDING_OPPORTUNITIES, TextArea.class);
		planningFundingOpportunities.setRows(3);
		TextArea nextSteps = addField(Event717CorrectiveActionDto.NEXT_STEPS, TextArea.class);
		nextSteps.setRows(3);

		// opportunities for planning and funding only apply to longer-term actions
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Event717CorrectiveActionDto.PLANNING_FUNDING_OPPORTUNITIES,
			Event717CorrectiveActionDto.PRIORITIZATION,
			Event717CorrectiveActionPriority.LONGER_TERM,
			true);

		setRequired(true, Event717CorrectiveActionDto.PROPOSED_ACTION);
		// after the required fields, because hidden fields are not required
		initializeAccessAndAllowedAccesses();
	}
}
