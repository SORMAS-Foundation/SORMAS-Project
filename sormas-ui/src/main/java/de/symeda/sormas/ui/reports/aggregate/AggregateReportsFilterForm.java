package de.symeda.sormas.ui.reports.aggregate;

import static com.vaadin.v7.data.fieldgroup.DefaultFieldGroupFieldFactory.CAPTION_PROPERTY_ID;

import java.util.List;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.report.AggregateReportCriteria;
import de.symeda.sormas.api.report.AggregatedCaseCountDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;

public class AggregateReportsFilterForm extends AbstractFilterForm<AggregateReportCriteria> {

	private ComboBox regionFilter;
	private ComboBox districtFilter;
	private ComboBox facilityFilter;
	private ComboBox pointOfEntryFilter;
	private ComboBox diseaseFilter;

	protected AggregateReportsFilterForm() {
		super(AggregateReportCriteria.class, AggregatedCaseCountDto.I18N_PREFIX);
	}

	@Override
	protected String[] getMainFilterLocators() {

		return new String[] {
			AggregateReportCriteria.REGION,
			AggregateReportCriteria.DISTRICT,
			AggregateReportCriteria.HEALTH_FACILITY,
			AggregateReportCriteria.POINT_OF_ENTRY,
			AggregateReportCriteria.DISEASE };
	}

	@Override
	protected void addFields() {

		regionFilter =
			addField(FieldConfiguration.withCaptionAndPixelSized(AggregateReportCriteria.REGION, I18nProperties.getCaption(Captions.Region), 200));
		regionFilter.setInputPrompt(I18nProperties.getString(Strings.promptAllRegions));
		regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());

		districtFilter = addField(
			FieldConfiguration.withCaptionAndPixelSized(AggregateReportCriteria.DISTRICT, I18nProperties.getCaption(Captions.District), 200));
		districtFilter.setInputPrompt(I18nProperties.getString(Strings.promptAllDistricts));

		if (!UserProvider.getCurrent().isPortHealthUser()) {
			facilityFilter = addField(getContent(), FieldConfiguration.pixelSized(AggregateReportCriteria.HEALTH_FACILITY, 200));
			facilityFilter.setDescription(I18nProperties.getDescription(Descriptions.descFacilityFilter));
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.PORT_HEALTH_INFO_VIEW)) {
			pointOfEntryFilter = addField(getContent(), FieldConfiguration.pixelSized(CaseDataDto.POINT_OF_ENTRY, 200));
			pointOfEntryFilter.setDescription(I18nProperties.getDescription(Descriptions.descPointOfEntryFilter));
		}

		UserDto user = currentUserDto();
		final RegionReferenceDto userRegion = user.getRegion();
		final DistrictReferenceDto userDistrict = user.getDistrict();

		if (userRegion != null) {
			regionFilter.setEnabled(false);
			districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(userRegion.getUuid()));
			if (userDistrict != null) {
				districtFilter.setEnabled(false);
			}
		}

		diseaseFilter = addField(FieldConfiguration.pixelSized(AggregateReportCriteria.DISEASE, 200), ComboBox.class);
		List<Disease> aggregateDiseases = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, null, false);
		FieldHelper.updateItems(diseaseFilter, aggregateDiseases);
		for (Object r : aggregateDiseases) {
			diseaseFilter.getItem(r).getItemProperty(CAPTION_PROPERTY_ID).setValue(r.toString());
		}
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		super.applyDependenciesOnFieldChange(propertyId, event);

		final ComboBox districtField = getField(AggregateReportCriteria.DISTRICT);
		final ComboBox facilityField = getField(AggregateReportCriteria.HEALTH_FACILITY);
		final ComboBox pointOfEntryField = getField(AggregateReportCriteria.POINT_OF_ENTRY);

		final UserDto user = currentUserDto();

		switch (propertyId) {
		case AggregateReportCriteria.REGION: {
			final RegionReferenceDto region = user.getRegion() != null ? user.getRegion() : (RegionReferenceDto) event.getProperty().getValue();
			if (region != null) {
				enableFields(districtField);
				FieldHelper.updateItems(districtField, FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
				facilityField.clear();
				pointOfEntryField.clear();
			} else {
				clearAndDisableFields(districtField, facilityField, pointOfEntryField);
			}
			break;
		}

		case AggregateReportCriteria.DISTRICT: {
			final DistrictReferenceDto newDistrict = (DistrictReferenceDto) event.getProperty().getValue();
			if (newDistrict != null) {
				clearAndDisableFields(facilityField, pointOfEntryField);
				enableFields(facilityField, pointOfEntryField);
				FieldHelper.updateItems(facilityField, FacadeProvider.getFacilityFacade().getActiveHospitalsByDistrict(newDistrict, true));
				FieldHelper
					.updateItems(pointOfEntryField, FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(newDistrict.getUuid(), true));
			} else {
				clearAndDisableFields(facilityField, pointOfEntryField);
			}
			break;
		}
		case AggregateReportCriteria.HEALTH_FACILITY: {
			final FacilityReferenceDto facility = (FacilityReferenceDto) event.getProperty().getValue();

			if (facility != null) {
				clearAndDisableFields(pointOfEntryField);
			} else {
				enableFields(pointOfEntryField);
			}
			break;
		}
		case AggregateReportCriteria.POINT_OF_ENTRY: {
			final PointOfEntryReferenceDto pointOfEntry = (PointOfEntryReferenceDto) event.getProperty().getValue();

			if (pointOfEntry != null) {
				clearAndDisableFields(facilityField);
			} else {
				enableFields(facilityField);
			}
			break;
		}
		}
	}
}
