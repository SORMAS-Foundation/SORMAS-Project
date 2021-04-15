package de.symeda.sormas.ui.events;

import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldConfiguration;

public class EventParticipantsFilterForm extends AbstractFilterForm<EventParticipantCriteria> {

	protected EventParticipantsFilterForm() {
		super(EventParticipantCriteria.class, EventParticipantIndexDto.I18N_PREFIX);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			EventParticipantCriteria.BIRTHDATE_YYYY,
			EventParticipantCriteria.BIRTHDATE_MM,
			EventParticipantCriteria.BIRTHDATE_DD,
			EventParticipantCriteria.FREE_TEXT,
			EventParticipantCriteria.PATHOGENTESTRESULT,
			EventParticipantCriteria.ONLY_COUNT_CONTACT_WITH_SOURCE_CASE_IN_EVENT };
	}

	@Override
	protected void addFields() {

		ComboBox birthDateYYYY = addField(EventParticipantCriteria.BIRTHDATE_YYYY, ComboBox.class);
		birthDateYYYY.setInputPrompt(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE_YYYY));
		birthDateYYYY.setWidth(140, Unit.PIXELS);
		birthDateYYYY.addItems(DateHelper.getYearsToNow());
		birthDateYYYY.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ID_TOSTRING);
		ComboBox birthDateMM = addField(EventParticipantCriteria.BIRTHDATE_MM, ComboBox.class);
		birthDateMM.setInputPrompt(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE_MM));
		birthDateMM.setWidth(140, Unit.PIXELS);
		birthDateMM.addItems(DateHelper.getMonthsInYear());
		ComboBox birthDateDD = addField(EventParticipantCriteria.BIRTHDATE_DD, ComboBox.class);
		birthDateDD.setInputPrompt(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE_DD));
		birthDateDD.setWidth(140, Unit.PIXELS);

		addField(
			FieldConfiguration.withCaptionAndPixelSized(
				EventParticipantCriteria.PATHOGENTESTRESULT,
				I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.PATHOGEN_TEST_RESULT),
				180),
			ComboBox.class);

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
}
