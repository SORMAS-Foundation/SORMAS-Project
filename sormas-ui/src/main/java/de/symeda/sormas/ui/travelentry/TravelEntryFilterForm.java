package de.symeda.sormas.ui.travelentry;

import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldConfiguration;

public class TravelEntryFilterForm extends AbstractFilterForm<TravelEntryCriteria> {

	private static final String CHECKBOX_STYLE = CssStyles.CHECKBOX_FILTER_INLINE + " " + CssStyles.VSPACE_3;

	protected TravelEntryFilterForm() {
		super(TravelEntryCriteria.class, TravelEntryDto.I18N_PREFIX);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			TravelEntryCriteria.NAME_UUID_EXTERNAL_ID_LIKE,
			TravelEntryCriteria.ONLY_RECOVERED_ENTRIES,
			TravelEntryCriteria.ONLY_VACCINATED_ENTRIES,
			TravelEntryCriteria.ONLY_ENTRIES_TESTED_NEGATIVE,
			TravelEntryCriteria.ONLY_ENTRIES_CONVERTED_TO_CASE };
	}

	@Override
	protected void addFields() {
		final TextField searchField = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				TravelEntryCriteria.NAME_UUID_EXTERNAL_ID_LIKE,
				I18nProperties.getString(Strings.promptTravelEntrySearchField),
				200));
		searchField.setNullRepresentation("");

		addField(
			FieldConfiguration.withCaptionAndStyle(
				TravelEntryCriteria.ONLY_RECOVERED_ENTRIES,
				I18nProperties.getCaption(Captions.travelEntryOnlyRecoveredEntries),
				null,
				CHECKBOX_STYLE),
			CheckBox.class);

		addField(
			FieldConfiguration.withCaptionAndStyle(
				TravelEntryCriteria.ONLY_VACCINATED_ENTRIES,
				I18nProperties.getCaption(Captions.travelEntryOnlyVaccinatedEntries),
				null,
				CHECKBOX_STYLE),
			CheckBox.class);

		addField(
			FieldConfiguration.withCaptionAndStyle(
				TravelEntryCriteria.ONLY_ENTRIES_TESTED_NEGATIVE,
				I18nProperties.getCaption(Captions.travelEntryOnlyEntriesTestedNegative),
				null,
				CHECKBOX_STYLE),
			CheckBox.class);

		addField(
			FieldConfiguration.withCaptionAndStyle(
				TravelEntryCriteria.ONLY_ENTRIES_CONVERTED_TO_CASE,
				I18nProperties.getCaption(Captions.travelEntryOnlyEntriesConvertedToCase),
				null,
				CHECKBOX_STYLE),
			CheckBox.class);
	}
}
