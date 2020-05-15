package de.symeda.sormas.ui.samples;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.FieldConfiguration;

public class SampleGridFilterForm extends AbstractFilterForm<SampleCriteria> {
	protected SampleGridFilterForm() {
		super(SampleCriteria.class, SampleIndexDto.I18N_PREFIX);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[]{SampleCriteria.PATHOGEN_TEST_RESULT, SampleCriteria.SPECIMEN_CONDITION, SampleCriteria.CASE_CLASSIFICATION,
				SampleCriteria.DISEASE, SampleCriteria.REGION, SampleCriteria.DISTRICT, SampleCriteria.LAB, SampleCriteria.CASE_CODE_ID_LIKE};
	}

	@Override
	protected void addFields() {
		addField(FieldConfiguration.withCaptionAndPixelSized(SampleCriteria.PATHOGEN_TEST_RESULT, I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_RESULT), 140));
		addField(FieldConfiguration.withCaptionAndPixelSized(SampleCriteria.SPECIMEN_CONDITION, I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SPECIMEN_CONDITION), 140));
		addField(FieldConfiguration.withCaptionAndPixelSized(SampleCriteria.CASE_CLASSIFICATION, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.CASE_CLASSIFICATION), 140));
		addField(FieldConfiguration.withCaptionAndPixelSized(SampleCriteria.DISEASE, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE), 140));

		UserDto user = UserProvider.getCurrent().getUser();
		if (user.getRegion() == null) {
			ComboBox regionField = addField(FieldConfiguration.withCaptionAndPixelSized(SampleCriteria.REGION, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REGION), 140));
			regionField.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
		}

		addField(FieldConfiguration.withCaptionAndPixelSized(SampleCriteria.DISTRICT, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISTRICT), 140));

		ComboBox labField = addField(FieldConfiguration.withCaptionAndPixelSized(SampleCriteria.LAB, I18nProperties.getPrefixCaption(SampleIndexDto.I18N_PREFIX, SampleIndexDto.LAB), 140));
		labField.addItems(FacadeProvider.getFacilityFacade().getAllActiveLaboratories(true));

		TextField searchField = addField(FieldConfiguration.withCaptionAndPixelSized(SampleCriteria.CASE_CODE_ID_LIKE, I18nProperties.getString(Strings.promptSamplesSearchField), 200));
		searchField.setNullRepresentation("");
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		switch (propertyId) {
			case SampleCriteria.REGION: {
				getField(SampleCriteria.DISTRICT).setValue(null);
				break;
			}
		}
	}

	@Override
	protected void applyDependenciesOnNewValue(SampleCriteria newValue) {
		UserDto user = UserProvider.getCurrent().getUser();

		ComboBox districtField = (ComboBox) getField(SampleCriteria.DISTRICT);
		if (user.getRegion() != null) {
			districtField.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(user.getRegion().getUuid()));
			districtField.setEnabled(true);
		} else {
			RegionReferenceDto region = newValue.getRegion();
			if (region != null) {
				districtField.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
				districtField.setEnabled(true);
			} else {
				districtField.setEnabled(false);
			}
		}
	}

	public TextField getSearchField() {
		return (TextField)getField(SampleCriteria.CASE_CODE_ID_LIKE);
	}
}
