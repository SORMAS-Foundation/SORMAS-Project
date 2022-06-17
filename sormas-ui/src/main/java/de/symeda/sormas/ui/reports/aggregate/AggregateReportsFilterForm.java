package de.symeda.sormas.ui.reports.aggregate;

import static com.vaadin.v7.data.fieldgroup.DefaultFieldGroupFieldFactory.CAPTION_PROPERTY_ID;

import java.util.List;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.converter.Converter;
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
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;

public class AggregateReportsFilterForm extends AbstractFilterForm<AggregateReportCriteria> {

	private static final String EPI_WEEK_LOC = "epiWeekLoc";

	private ComboBox regionFilter;
	private ComboBox districtFilter;
	private ComboBox facilityFilter;
	private ComboBox pointOfEntryFilter;
	private ComboBox diseaseFilter;

	// Filters
	private HorizontalLayout hlSecondFilterRow;
	private com.vaadin.ui.ComboBox<Integer> cbFromYearFilter;
	private com.vaadin.ui.ComboBox<EpiWeek> cbFromEpiWeekFilter;
	private com.vaadin.ui.ComboBox<Integer> cbToYearFilter;
	private com.vaadin.ui.ComboBox<EpiWeek> cbToEpiWeekFilter;

	protected AggregateReportsFilterForm() {
		super(
			AggregateReportCriteria.class,
			AggregatedCaseCountDto.I18N_PREFIX,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()));
	}

	@Override
	protected String[] getMainFilterLocators() {

		return new String[] {
			AggregateReportCriteria.REGION,
			AggregateReportCriteria.DISTRICT,
			AggregateReportCriteria.HEALTH_FACILITY,
			AggregateReportCriteria.POINT_OF_ENTRY,
			AggregateReportCriteria.DISEASE,
			EPI_WEEK_LOC };
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
		final FacilityReferenceDto userFacility = user.getHealthFacility();
		final PointOfEntryReferenceDto userPointOfEntry = user.getPointOfEntry();

		if (userRegion != null) {
			regionFilter.setEnabled(false);
			districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(userRegion.getUuid()));
			if (userDistrict != null) {
				districtFilter.setEnabled(false);
				facilityFilter.addItems(FacadeProvider.getFacilityFacade().getActiveHospitalsByDistrict(userDistrict, false));
				pointOfEntryFilter.addItems(FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(userDistrict.getUuid(), false));
				if (userFacility != null || userPointOfEntry != null) {
					facilityFilter.setEnabled(false);
					pointOfEntryFilter.setEnabled(false);
				}
			}
		}

		diseaseFilter = addField(FieldConfiguration.pixelSized(AggregateReportCriteria.DISEASE, 200), ComboBox.class);
		List<Disease> aggregateDiseases = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, null, false);
		FieldHelper.updateItems(diseaseFilter, aggregateDiseases);
		for (Object r : aggregateDiseases) {
			diseaseFilter.getItem(r).getItemProperty(CAPTION_PROPERTY_ID).setValue(r.toString());
		}

		getContent().addComponent(createEpiWeekFilterBar(), EPI_WEEK_LOC);
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
				FieldHelper.updateItems(facilityField, FacadeProvider.getFacilityFacade().getActiveHospitalsByDistrict(newDistrict, false));
				FieldHelper
					.updateItems(pointOfEntryField, FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(newDistrict.getUuid(), false));
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

	private HorizontalLayout createEpiWeekFilterBar() {
		hlSecondFilterRow = new HorizontalLayout();
		hlSecondFilterRow.setMargin(false);
		hlSecondFilterRow.setSpacing(true);
		hlSecondFilterRow.setWidthUndefined();

		Label lblFrom = new Label(I18nProperties.getCaption(Captions.from));
		CssStyles.style(lblFrom, CssStyles.LABEL_BOLD, CssStyles.VSPACE_TOP_4);
		hlSecondFilterRow.addComponent(lblFrom);

		cbFromYearFilter = new com.vaadin.ui.ComboBox<>();
		cbFromYearFilter.setId("yearFrom");
		cbFromYearFilter.addValueChangeListener(e -> clearFilterIfEmpty(cbFromYearFilter, cbFromEpiWeekFilter));
		cbFromEpiWeekFilter = new com.vaadin.ui.ComboBox<>();
		cbFromEpiWeekFilter.setId(AggregateReportCriteria.EPI_WEEK_FROM);
		cbFromEpiWeekFilter.addValueChangeListener(e -> {
			getValue().setEpiWeekFrom(e.getValue());
		});
		cbToYearFilter = new com.vaadin.ui.ComboBox<>();
		cbToYearFilter.setId("yearTo");
		cbToYearFilter.addValueChangeListener(e -> clearFilterIfEmpty(cbFromYearFilter, cbToEpiWeekFilter));
		cbToEpiWeekFilter = new com.vaadin.ui.ComboBox<>();
		cbToEpiWeekFilter.setId(AggregateReportCriteria.EPI_WEEK_TO);
		cbToEpiWeekFilter.addValueChangeListener(e -> {
			getValue().setEpiWeekTo(e.getValue());
		});

		cbFromYearFilter.setWidth(140, Unit.PIXELS);
		cbFromYearFilter.setPlaceholder(I18nProperties.getString(Strings.year));
		cbFromYearFilter.setItems(DateHelper.getYearsToNow(2000));
		cbFromYearFilter.addValueChangeListener(e -> {
			cbFromEpiWeekFilter.clear();
			if (e.getValue() != null) {
				cbFromEpiWeekFilter.setItems(DateHelper.createEpiWeekList(e.getValue()));
			}
		});
		hlSecondFilterRow.addComponent(cbFromYearFilter);

		cbFromEpiWeekFilter.setWidth(200, Unit.PIXELS);
		cbFromEpiWeekFilter.setPlaceholder(I18nProperties.getString(Strings.epiWeek));

		hlSecondFilterRow.addComponent(cbFromEpiWeekFilter);

		Label lblTo = new Label(I18nProperties.getCaption(Captions.to));
		CssStyles.style(lblTo, CssStyles.LABEL_BOLD, CssStyles.VSPACE_TOP_4);
		hlSecondFilterRow.addComponent(lblTo);

		cbToYearFilter.setWidth(140, Unit.PIXELS);
		cbToYearFilter.setPlaceholder(I18nProperties.getString(Strings.year));
		cbToYearFilter.setItems(DateHelper.getYearsToNow(2000));
		cbToYearFilter.addValueChangeListener(e -> {
			cbToEpiWeekFilter.clear();
			if (e.getValue() != null) {
				cbToEpiWeekFilter.setItems(DateHelper.createEpiWeekList(e.getValue()));
			}
		});
		hlSecondFilterRow.addComponent(cbToYearFilter);

		cbToEpiWeekFilter.setWidth(200, Unit.PIXELS);
		cbToEpiWeekFilter.setPlaceholder(I18nProperties.getString(Strings.epiWeek));
		hlSecondFilterRow.addComponent(cbToEpiWeekFilter);

		return hlSecondFilterRow;
	}

	private void clearFilterIfEmpty(com.vaadin.ui.ComboBox<?> filter1, com.vaadin.ui.ComboBox<?> filter2) {
		if (filter1.getValue() == null) {
			filter2.clear();
		}
	}

	private void clearFilterIfNotEmpty(com.vaadin.ui.ComboBox<?> filter1, com.vaadin.ui.ComboBox<?> filter2) {
		if (filter1.getValue() != null) {
			filter2.clear();
		}
	}

	@Override
	public void setValue(AggregateReportCriteria newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);

		if (newFieldValue.getEpiWeekFrom() != null) {
			cbFromYearFilter.setValue(newFieldValue.getEpiWeekFrom().getYear());
			cbFromEpiWeekFilter.setValue(newFieldValue.getEpiWeekFrom());
		}

		if (newFieldValue.getEpiWeekTo() != null) {
			cbToYearFilter.setValue(newFieldValue.getEpiWeekTo().getYear());
			cbToEpiWeekFilter.setValue(newFieldValue.getEpiWeekTo());
		}
	}
}
