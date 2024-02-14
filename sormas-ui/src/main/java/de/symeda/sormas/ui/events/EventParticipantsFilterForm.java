package de.symeda.sormas.ui.events;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;

public class EventParticipantsFilterForm extends AbstractFilterForm<EventParticipantCriteria> {

	protected EventParticipantsFilterForm() {
		super(EventParticipantCriteria.class, EventParticipantIndexDto.I18N_PREFIX, null);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			EventParticipantCriteria.BIRTHDATE_YYYY,
			EventParticipantCriteria.BIRTHDATE_MM,
			EventParticipantCriteria.BIRTHDATE_DD,
			EventParticipantCriteria.FREE_TEXT,
			EventParticipantCriteria.PATHOGENTESTRESULT,
			EventParticipantCriteria.VACCINATION_STATUS,
			EventParticipantCriteria.ONLY_COUNT_CONTACT_WITH_SOURCE_CASE_IN_EVENT };
	}

	@Override
	protected void addFields() {

		addBirthDateFields(
			getContent(),
			EventParticipantCriteria.BIRTHDATE_YYYY,
			EventParticipantCriteria.BIRTHDATE_MM,
			EventParticipantCriteria.BIRTHDATE_DD);

		addField(
			FieldConfiguration.withCaptionAndPixelSized(
				EventParticipantCriteria.PATHOGENTESTRESULT,
				I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.PATHOGEN_TEST_RESULT),
				180),
			ComboBox.class);

		addField(FieldConfiguration.pixelSized(EventParticipantCriteria.VACCINATION_STATUS, 140), ComboBox.class);

		TextField searchField = addField(
			FieldConfiguration
				.withCaptionAndPixelSized(EventCriteria.FREE_TEXT, I18nProperties.getString(Strings.promptEventParticipantsSearchField), 200));
		searchField.setNullRepresentation("");

		addField(
			FieldConfiguration.withCaptionAndStyle(
				EventParticipantCriteria.ONLY_COUNT_CONTACT_WITH_SOURCE_CASE_IN_EVENT,
				I18nProperties.getCaption(Captions.eventParticipantContactCountOnlyWithSourceCaseInEvent),
				null,
				CssStyles.CHECKBOX_FILTER_INLINE),
			CheckBox.class);
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		super.applyDependenciesOnFieldChange(propertyId, event);

		switch (propertyId) {
		case EventParticipantCriteria.BIRTHDATE_MM: {
			Integer birthMM = (Integer) event.getProperty().getValue();

			ComboBox birthDayDD = getField(EventParticipantCriteria.BIRTHDATE_DD);
			birthDayDD.setEnabled(birthMM != null);
			FieldHelper.updateItems(
				birthDayDD,
				DateHelper.getDaysInMonth(
					(Integer) getField(EventParticipantCriteria.BIRTHDATE_MM).getValue(),
					(Integer) getField(EventParticipantCriteria.BIRTHDATE_YYYY).getValue()));

			break;
		}
		}
	}

	@Override
	protected void applyDependenciesOnNewValue(EventParticipantCriteria criteria) {
		ComboBox birthDateDD = getField(EventParticipantCriteria.BIRTHDATE_DD);
		if (getField(EventParticipantCriteria.BIRTHDATE_YYYY).getValue() != null
			&& getField(EventParticipantCriteria.BIRTHDATE_MM).getValue() != null) {
			birthDateDD.addItems(
				DateHelper.getDaysInMonth(
					(Integer) getField(EventParticipantCriteria.BIRTHDATE_MM).getValue(),
					(Integer) getField(EventParticipantCriteria.BIRTHDATE_YYYY).getValue()));
			birthDateDD.setEnabled(true);
		} else {
			birthDateDD.clear();
			birthDateDD.setEnabled(false);
		}
	}
}
