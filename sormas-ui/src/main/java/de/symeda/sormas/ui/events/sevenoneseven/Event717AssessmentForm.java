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

import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.divCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.divsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.h3;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717AssessmentDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717BottleneckCategory;
import de.symeda.sormas.api.event.sevenoneseven.Event717BottleneckDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717EarlyResponseAction;
import de.symeda.sormas.api.event.sevenoneseven.Event717EnablerDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessCalculator;
import de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessStatus;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.components.MultilineLabel;

/**
 * Form for the 7-1-7 assessment of an event, following the steps of the 7-1-7 assessment tool. The timeliness of the entered
 * dates is shown live in a separate {@link Event717TimelinessPanel}.
 */
public class Event717AssessmentForm extends AbstractEditForm<Event717AssessmentDto> {

	private static final long serialVersionUID = -6021953452938102436L;

	private static final String LOC_INFO = "info";
	private static final String LOC_MILESTONES_HEADING = "milestonesHeading";
	private static final String LOC_EARLY_RESPONSE_HEADING = "earlyResponseHeading";
	private static final String LOC_BOTTLENECKS_ENABLERS_HEADING = "bottlenecksEnablersHeading";
	private static final String LOC_CORRECTIVE_ACTIONS_HEADING = "correctiveActionsHeading";
	private static final String LOC_REPORT_HEADING = "reportHeading";
	private static final String LOC_EARLY_RESPONSE_ACTIONS = "earlyResponseActions";

	private static final String CARD = CssStyles.VIEW_SECTION_MARGIN_X_4 + " " + CssStyles.VSPACE_TOP_3;

	//@formatter:off
	private static final String HTML_LAYOUT =
		divCss(CssStyles.VIEW_SECTION_MARGIN_TOP_4_MARGIN_X_4,
			loc(LOC_MILESTONES_HEADING)
				+ loc(LOC_INFO)
				+ fluidRowLocs(4, Event717AssessmentDto.DATE_OF_EMERGENCE, 8, Event717AssessmentDto.EMERGENCE_NARRATIVE)
				+ fluidRowLocs(4, Event717AssessmentDto.DATE_OF_DETECTION, 8, Event717AssessmentDto.DETECTION_NARRATIVE)
				+ fluidRowLocs(4, Event717AssessmentDto.DATE_OF_NOTIFICATION, 8, Event717AssessmentDto.NOTIFICATION_NARRATIVE)
		)
		+ divCss(CARD,
			loc(LOC_EARLY_RESPONSE_HEADING)
				+ loc(LOC_EARLY_RESPONSE_ACTIONS)
				+ fluidRowLocs(4, Event717AssessmentDto.EARLY_RESPONSE_COMPLETION_DATE, 8, Event717AssessmentDto.EARLY_RESPONSE_COMPLETION_NARRATIVE)
		)
		+ divCss(CARD,
			loc(LOC_BOTTLENECKS_ENABLERS_HEADING)
				+ fluidRowLocs(Event717AssessmentDto.BOTTLENECKS)
				+ fluidRowLocs(Event717AssessmentDto.ENABLERS)
		)
		+ divCss(CARD,
			loc(LOC_CORRECTIVE_ACTIONS_HEADING)
				+ fluidRowLocs(Event717AssessmentDto.CORRECTIVE_ACTIONS)
		)
		+ divCss(CARD + " " + CssStyles.VSPACE_2,
			loc(LOC_REPORT_HEADING)
				+ fluidRowLocs(
					Event717AssessmentDto.REPORT_COMPLETED_DATE,
					Event717AssessmentDto.REPORT_COMPLETED_BY_USER,
					Event717AssessmentDto.REPORT_COMPLETED_BY_NAME)
				+ fluidRowLocs(4, Event717AssessmentDto.OUTBREAK_END_DATE, 8, "")
				+ fluidRowLocs(Event717AssessmentDto.GENERAL_NOTES)
		);
	//@formatter:on

	private final EventReferenceDto eventRef;
	private final boolean isEditAllowed;
	private final Event717TimelinessPanel timelinessPanel;

	private final Map<Event717EarlyResponseAction, Event717EarlyResponseActionsTable.ActionFields> earlyResponseActionFields =
		new EnumMap<>(Event717EarlyResponseAction.class);
	private TextField earlyResponseCompletionDate;
	private Event717BottlenecksField bottlenecksField;

	public Event717AssessmentForm(EventReferenceDto eventRef, boolean isEditAllowed) {
		super(Event717AssessmentDto.class, Event717AssessmentDto.I18N_PREFIX, false, null, null, isEditAllowed);
		this.eventRef = eventRef;
		this.isEditAllowed = isEditAllowed;
		this.timelinessPanel = new Event717TimelinessPanel(true);

		setWidth(100, Unit.PERCENTAGE);
		addFields();
	}

	private static String notApplicableProperty(Event717EarlyResponseAction action) {
		return Event717AssessmentDto.getEarlyResponseActionDateProperty(action).replace("Date", "NotApplicable");
	}

	private static String narrativeProperty(Event717EarlyResponseAction action) {
		return Event717AssessmentDto.getEarlyResponseActionDateProperty(action).replace("Date", "Narrative");
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {

		getContent().addComponent(
			new MultilineLabel(divsCss(VSPACE_3, I18nProperties.getString(Strings.infoEvent717Assessment)), ContentMode.HTML),
			LOC_INFO);

		addHeading(Strings.headingEvent717Milestones, LOC_MILESTONES_HEADING);
		addField(Event717AssessmentDto.DATE_OF_EMERGENCE, DateField.class);
		addNarrativeField(Event717AssessmentDto.EMERGENCE_NARRATIVE, null);
		addField(Event717AssessmentDto.DATE_OF_DETECTION, DateField.class);
		addNarrativeField(Event717AssessmentDto.DETECTION_NARRATIVE, null);
		addField(Event717AssessmentDto.DATE_OF_NOTIFICATION, DateField.class);
		addNarrativeField(Event717AssessmentDto.NOTIFICATION_NARRATIVE, null);

		getContent().addComponent(
			new MultilineLabel(
				h3(I18nProperties.getString(Strings.headingEvent717EarlyResponseActions))
					+ divsCss(VSPACE_3, I18nProperties.getString(Strings.infoEvent717EarlyResponseActions)),
				ContentMode.HTML),
			LOC_EARLY_RESPONSE_HEADING);
		for (Event717EarlyResponseAction action : Event717EarlyResponseAction.values()) {
			bindEarlyResponseActionFields(action);
		}
		getContent().addComponent(
			new Event717EarlyResponseActionsTable(earlyResponseActionFields, isEditAllowed),
			LOC_EARLY_RESPONSE_ACTIONS);

		// derived from the action dates and calculated again by the server on save, therefore not bound
		earlyResponseCompletionDate =
			new TextField(I18nProperties.getPrefixCaption(Event717AssessmentDto.I18N_PREFIX, Event717AssessmentDto.EARLY_RESPONSE_COMPLETION_DATE));
		earlyResponseCompletionDate.setWidth(100, Unit.PERCENTAGE);
		earlyResponseCompletionDate.setNullRepresentation("");
		earlyResponseCompletionDate.setReadOnly(true);
		getContent().addComponent(earlyResponseCompletionDate, Event717AssessmentDto.EARLY_RESPONSE_COMPLETION_DATE);
		addNarrativeField(
			Event717AssessmentDto.EARLY_RESPONSE_COMPLETION_NARRATIVE,
			I18nProperties.getPrefixCaption(Event717AssessmentDto.I18N_PREFIX, Event717AssessmentDto.EARLY_RESPONSE_COMPLETION_NARRATIVE));

		addHeading(Strings.headingEvent717BottlenecksEnablers, LOC_BOTTLENECKS_ENABLERS_HEADING);

		// the entries are bound to the form but displayed grouped by interval instead of in the table of the field
		bottlenecksField = new Event717BottlenecksField(isEditAllowed);
		getFieldGroup().bind(bottlenecksField, Event717AssessmentDto.BOTTLENECKS);
		getContent().addComponent(
			new Event717IntervalEntriesLayout<>(
				I18nProperties.getPrefixCaption(Event717AssessmentDto.I18N_PREFIX, Event717AssessmentDto.BOTTLENECKS),
				bottlenecksField,
				Event717BottleneckDto::getTimelinessInterval,
				Event717BottleneckDto::getDescription,
				Event717AssessmentForm::getCategoryCaption,
				bottlenecksField::createEntryForInterval,
				isEditAllowed),
			Event717AssessmentDto.BOTTLENECKS);

		Event717EnablersField enablersField = new Event717EnablersField(isEditAllowed);
		getFieldGroup().bind(enablersField, Event717AssessmentDto.ENABLERS);
		getContent().addComponent(
			new Event717IntervalEntriesLayout<>(
				I18nProperties.getPrefixCaption(Event717AssessmentDto.I18N_PREFIX, Event717AssessmentDto.ENABLERS),
				enablersField,
				Event717EnablerDto::getTimelinessInterval,
				Event717EnablerDto::getDescription,
				null,
				enablersField::createEntryForInterval,
				isEditAllowed),
			Event717AssessmentDto.ENABLERS);

		addHeading(Strings.headingEvent717CorrectiveActions, LOC_CORRECTIVE_ACTIONS_HEADING);
		Event717CorrectiveActionsField correctiveActionsField = addField(
			Event717AssessmentDto.CORRECTIVE_ACTIONS,
			new Event717CorrectiveActionsField(() -> bottlenecksField.getValue(), isEditAllowed));
		correctiveActionsField.setWidthFull();
		bottlenecksField.addValueChangeListener(e -> correctiveActionsField.removeInvalidBottleneckReferences());

		addHeading(Strings.headingEvent717ReportDetails, LOC_REPORT_HEADING);
		addField(Event717AssessmentDto.REPORT_COMPLETED_DATE, DateField.class);
		addField(Event717AssessmentDto.REPORT_COMPLETED_BY_USER, ComboBox.class);
		addField(Event717AssessmentDto.REPORT_COMPLETED_BY_NAME);
		addField(Event717AssessmentDto.OUTBREAK_END_DATE, DateField.class);
		addNarrativeField(Event717AssessmentDto.GENERAL_NOTES, I18nProperties.getPrefixCaption(Event717AssessmentDto.I18N_PREFIX, Event717AssessmentDto.GENERAL_NOTES));

		for (String dateProperty : new String[] {
			Event717AssessmentDto.DATE_OF_EMERGENCE,
			Event717AssessmentDto.DATE_OF_DETECTION,
			Event717AssessmentDto.DATE_OF_NOTIFICATION }) {
			getField(dateProperty).addValueChangeListener(e -> updateTimeliness());
		}
	}

	/**
	 * Binds the fields of an early response action without adding them to the layout. They are displayed and edited through
	 * the {@link Event717EarlyResponseActionsTable}.
	 */
	private void bindEarlyResponseActionFields(Event717EarlyResponseAction action) {

		DateField date = createField(Event717AssessmentDto.getEarlyResponseActionDateProperty(action), DateField.class);
		CheckBox notApplicable = createField(notApplicableProperty(action), CheckBox.class);
		TextArea narrative = createField(narrativeProperty(action), TextArea.class);
		narrative.setNullRepresentation("");

		date.addValueChangeListener(e -> updateTimeliness());
		notApplicable.addValueChangeListener(e -> updateTimeliness());

		earlyResponseActionFields.put(action, new Event717EarlyResponseActionsTable.ActionFields(date, notApplicable, narrative));
	}

	private void addNarrativeField(String propertyId, String caption) {

		TextArea narrative = addField(propertyId, TextArea.class);
		narrative.setRows(2);
		narrative.setCaption(caption != null ? caption : I18nProperties.getPrefixCaption(Event717AssessmentDto.I18N_PREFIX, "narrative"));
	}

	private static String getCategoryCaption(Event717BottleneckDto bottleneck) {

		if (bottleneck.getCategory() == null) {
			return null;
		}
		return bottleneck.getCategory() == Event717BottleneckCategory.OTHER && bottleneck.getOtherCategoryDetails() != null
			? bottleneck.getCategory() + ": " + bottleneck.getOtherCategoryDetails()
			: bottleneck.getCategory().toString();
	}

	private void addHeading(String headingKey, String location) {

		Label heading = new Label(I18nProperties.getString(headingKey));
		heading.addStyleName(CssStyles.H3);
		getContent().addComponent(heading, location);
	}

	@Override
	public void setValue(Event717AssessmentDto newFieldValue) {

		ComboBox reportCompletedByUser = getField(Event717AssessmentDto.REPORT_COMPLETED_BY_USER);
		reportCompletedByUser.removeAllItems();
		// the current user can be selected; a previously selected user remains available
		UserReferenceDto currentUser = UiUtil.getUserReference();
		if (currentUser != null) {
			reportCompletedByUser.addItem(currentUser);
		}
		if (newFieldValue.getReportCompletedByUser() != null) {
			reportCompletedByUser.addItem(newFieldValue.getReportCompletedByUser());
		}

		super.setValue(newFieldValue);
		updateTimeliness();
	}

	/**
	 * Recalculates the timeliness from the values currently entered in the form.
	 */
	private void updateTimeliness() {

		if (getFieldGroup().getItemDataSource() == null || earlyResponseCompletionDate == null) {
			return;
		}

		Event717AssessmentDto current = Event717AssessmentDto.build(eventRef);
		current.setDateOfEmergence((Date) getField(Event717AssessmentDto.DATE_OF_EMERGENCE).getValue());
		current.setDateOfDetection((Date) getField(Event717AssessmentDto.DATE_OF_DETECTION).getValue());
		current.setDateOfNotification((Date) getField(Event717AssessmentDto.DATE_OF_NOTIFICATION).getValue());
		for (Event717EarlyResponseAction action : Event717EarlyResponseAction.values()) {
			current.setEarlyResponseActionDate(action, (Date) getField(Event717AssessmentDto.getEarlyResponseActionDateProperty(action)).getValue());
			current.setEarlyResponseActionNotApplicable(action, Boolean.TRUE.equals(getField(notApplicableProperty(action)).getValue()));
		}

		Event717TimelinessDto timeliness = Event717TimelinessCalculator.calculate(current);
		timelinessPanel.setValue(timeliness);

		earlyResponseCompletionDate.setReadOnly(false);
		earlyResponseCompletionDate.setValue(
			timeliness.isEarlyResponseIncomplete()
				? Event717TimelinessStatus.INCOMPLETE.toString()
				: DateFormatHelper.formatDate(timeliness.getEarlyResponseCompletionDate()));
		earlyResponseCompletionDate.setReadOnly(true);
	}

	public Event717TimelinessPanel getTimelinessPanel() {
		return timelinessPanel;
	}
}
