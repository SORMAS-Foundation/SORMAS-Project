package de.symeda.sormas.ui.travelentry.components;

import static de.symeda.sormas.ui.utils.CssStyles.ERROR_COLOR_PRIMARY;
import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.SOFT_REQUIRED;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.style;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLoc;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.vaadin.ui.Label;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.travelentry.DeaContentEntry;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.person.PersonCreateForm;
import de.symeda.sormas.ui.travelentry.DEAFormBuilder;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.InfrastructureFieldsHelper;

public class TravelEntryCreateForm extends AbstractEditForm<TravelEntryDto> {

	private static final long serialVersionUID = 2160497736783946091L;

	private static final String RESPONSIBLE_JURISDICTION_HEADING_LOC = "responsibleJurisdictionHeadingLoc";
	private static final String DIFFERENT_POINT_OF_ENTRY_JURISDICTION = "differentPointOfEntryJurisdiction";
	private static final String POINT_OF_ENTRY_HEADING_LOC = "pointOfEntryHeadingLoc";
	private static final String DEA_CONTENT_LOC = "DEAContentLoc";

	//@formatter:off
	private static final String HTML_LAYOUT = fluidRowLocs(TravelEntryDto.REPORT_DATE, TravelEntryDto.EXTERNAL_ID)
		+ fluidRow(
		fluidColumnLoc(6, 0, TravelEntryDto.DISEASE),
		fluidColumnLoc(6, 0, TravelEntryDto.DISEASE_DETAILS)) 
		+ fluidRowLocs(TravelEntryDto.DISEASE_VARIANT, TravelEntryDto.DISEASE_VARIANT_DETAILS)
		+ fluidRowLocs(TravelEntryDto.DATE_OF_ARRIVAL, "")
		+ fluidRowLocs(RESPONSIBLE_JURISDICTION_HEADING_LOC)
		+ fluidRowLocs(TravelEntryDto.RESPONSIBLE_REGION, TravelEntryDto.RESPONSIBLE_DISTRICT, TravelEntryDto.RESPONSIBLE_COMMUNITY)
		+ fluidRowLocs(DIFFERENT_POINT_OF_ENTRY_JURISDICTION)
		+ fluidRowLocs(POINT_OF_ENTRY_HEADING_LOC)
		+ fluidRowLocs(TravelEntryDto.REGION, TravelEntryDto.DISTRICT)
		+ fluidRowLocs(TravelEntryDto.POINT_OF_ENTRY, TravelEntryDto.POINT_OF_ENTRY_DETAILS)
		+ loc(DEA_CONTENT_LOC)
		+ fluidRowLocs(TravelEntryDto.PERSON);
	//@formatter:on

	private ComboBox districtCombo;
	private ComboBox cbPointOfEntry;
	private ComboBox birthDateDay;
	private DEAFormBuilder deaFormBuilder;
	private ComboBox responsibleRegion;
	private ComboBox responsibleDistrict;
	private ComboBox responsibleCommunity;

	private PersonCreateForm personCreateForm;

	private final PersonReferenceDto personDto;

	public TravelEntryCreateForm() {
		this(null);
	}

	public TravelEntryCreateForm(PersonReferenceDto personDto) {
		super(
			TravelEntryDto.class,
			TravelEntryDto.I18N_PREFIX,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()));
		this.personDto = personDto;
		setWidth(720, Unit.PIXELS);
		hideValidationUntilNextCommit();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		addField(TravelEntryDto.REPORT_DATE, DateField.class);

		TextField externalIdField = addField(TravelEntryDto.EXTERNAL_ID, TextField.class);
		style(externalIdField, ERROR_COLOR_PRIMARY);

		ComboBox diseaseField = addDiseaseField(TravelEntryDto.DISEASE, false, true);
		ComboBox diseaseVariantField = addField(TravelEntryDto.DISEASE_VARIANT, ComboBox.class);
		diseaseVariantField.setNullSelectionAllowed(true);
		diseaseVariantField.setVisible(false);
		addField(TravelEntryDto.DISEASE_DETAILS, TextField.class);
		TextField diseaseVariantDetailsField = addField(TravelEntryDto.DISEASE_VARIANT_DETAILS, TextField.class);
		diseaseVariantDetailsField.setVisible(false);

		addField(TravelEntryDto.DATE_OF_ARRIVAL).setRequired(true);

		Label jurisdictionHeadingLabel = new Label(I18nProperties.getString(Strings.headingResponsibleJurisdiction));
		jurisdictionHeadingLabel.addStyleName(H3);
		getContent().addComponent(jurisdictionHeadingLabel, RESPONSIBLE_JURISDICTION_HEADING_LOC);

		responsibleRegion = addInfrastructureField(TravelEntryDto.RESPONSIBLE_REGION);
		responsibleRegion.setRequired(true);
		responsibleDistrict = addInfrastructureField(TravelEntryDto.RESPONSIBLE_DISTRICT);
		responsibleDistrict.setRequired(true);
		responsibleCommunity = addInfrastructureField(TravelEntryDto.RESPONSIBLE_COMMUNITY);
		responsibleCommunity.setNullSelectionAllowed(true);
		responsibleCommunity.addStyleName(SOFT_REQUIRED);

		InfrastructureFieldsHelper.initInfrastructureFields(responsibleRegion, responsibleDistrict, responsibleCommunity);

		CheckBox differentPointOfEntryJurisdiction = addCustomField(DIFFERENT_POINT_OF_ENTRY_JURISDICTION, Boolean.class, CheckBox.class);
		differentPointOfEntryJurisdiction.addStyleName(VSPACE_3);

		Label placeOfStayHeadingLabel = new Label(I18nProperties.getCaption(Captions.travelEntryPointOfEntry));
		placeOfStayHeadingLabel.addStyleName(H3);
		getContent().addComponent(placeOfStayHeadingLabel, POINT_OF_ENTRY_HEADING_LOC);

		ComboBox regionCombo = addInfrastructureField(TravelEntryDto.REGION);
		districtCombo = addInfrastructureField(TravelEntryDto.DISTRICT);

		cbPointOfEntry = addInfrastructureField(TravelEntryDto.POINT_OF_ENTRY);
		cbPointOfEntry.setImmediate(true);
		TextField tfPointOfEntryDetails = addField(TravelEntryDto.POINT_OF_ENTRY_DETAILS, TextField.class);
		tfPointOfEntryDetails.setVisible(false);

		personCreateForm = new PersonCreateForm(false, true, false);
		personCreateForm.setWidth(100, Unit.PERCENTAGE);
		personCreateForm.setValue(new PersonDto());
		getContent().addComponent(personCreateForm, TravelEntryDto.PERSON);

		regionCombo.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		regionCombo.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper
				.updateItems(districtCombo, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});
		districtCombo.addValueChangeListener(e -> {
			if (differentPointOfEntryJurisdiction.getValue()) {
				DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
				getPointsOfEntryForDistrict(districtDto);
			}
		});

		differentPointOfEntryJurisdiction.addValueChangeListener(v -> {
			if (differentPointOfEntryJurisdiction.getValue()) {
				cbPointOfEntry.removeAllItems();
			} else {
				getPointsOfEntryForDistrict((DistrictReferenceDto) responsibleDistrict.getValue());
			}
		});

		// Set initial visibilities & accesses
		initializeVisibilitiesAndAllowedVisibilities();

		setRequired(true, TravelEntryDto.REPORT_DATE, TravelEntryDto.POINT_OF_ENTRY, TravelEntryDto.DISEASE);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Collections.singletonList(TravelEntryDto.DISEASE_DETAILS),
			TravelEntryDto.DISEASE,
			Collections.singletonList(Disease.OTHER),
			true);
		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			TravelEntryDto.DISEASE,
			Collections.singletonList(TravelEntryDto.DISEASE_DETAILS),
			Collections.singletonList(Disease.OTHER));

		cbPointOfEntry.addValueChangeListener(e -> updatePointOfEntryFields(cbPointOfEntry, tfPointOfEntryDetails));

		FieldHelper.setVisibleWhen(
			differentPointOfEntryJurisdiction,
			Arrays.asList(regionCombo, districtCombo),
			Collections.singletonList(Boolean.TRUE),
			true);

		FieldHelper.setRequiredWhen(
			differentPointOfEntryJurisdiction,
			Arrays.asList(regionCombo, districtCombo),
			Collections.singletonList(Boolean.TRUE),
			false,
			null);

		responsibleDistrict.addValueChangeListener(e -> {
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			getPointsOfEntryForDistrict(districtDto);
		});

		diseaseField.addValueChangeListener((ValueChangeListener) valueChangeEvent -> {
			Disease disease = (Disease) valueChangeEvent.getProperty().getValue();
			List<DiseaseVariant> diseaseVariants =
				FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, disease);
			FieldHelper.updateItems(diseaseVariantField, diseaseVariants);
			diseaseVariantField
				.setVisible(disease != null && isVisibleAllowed(TravelEntryDto.DISEASE_VARIANT) && CollectionUtils.isNotEmpty(diseaseVariants));
			personCreateForm.updatePresentConditionEnum(disease);
		});
		diseaseVariantField.addValueChangeListener(e -> {
			DiseaseVariant diseaseVariant = (DiseaseVariant) e.getProperty().getValue();
			diseaseVariantDetailsField.setVisible(diseaseVariant != null && diseaseVariant.matchPropertyValue(DiseaseVariant.HAS_DETAILS, true));
		});

		addValueChangeListener(e -> {
			if (personDto != null) {
				personCreateForm.setVisible(false);
				personCreateForm.setReadOnly(false);
			} else {
				personCreateForm.enablePersonFields(true);
			}
		});
	}

	public PersonDto getPerson() {
		PersonDto person = PersonDto.build();
		personCreateForm.transferDataToPerson(person);
		return person;
	}

	public PersonDto getSearchedPerson() {
		return personCreateForm.getSearchedPerson();
	}

	public void setPerson(PersonDto person) {
		personCreateForm.setPerson(person);
	}

	public void setDiseaseReadOnly(boolean readOnly) {
		getField(CaseDataDto.DISEASE).setEnabled(!readOnly);
	}

	private void getPointsOfEntryForDistrict(DistrictReferenceDto districtDto) {
		FieldHelper.updateItems(
			cbPointOfEntry,
			districtDto != null ? FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(districtDto.getUuid(), true) : null);
	}

	private void updatePointOfEntryFields(ComboBox cbPointOfEntry, TextField tfPointOfEntryDetails) {

		if (cbPointOfEntry.getValue() != null) {
			boolean isOtherPointOfEntry = ((PointOfEntryReferenceDto) cbPointOfEntry.getValue()).isOtherPointOfEntry();
			setVisible(isOtherPointOfEntry, TravelEntryDto.POINT_OF_ENTRY_DETAILS);
			setRequired(isOtherPointOfEntry, TravelEntryDto.POINT_OF_ENTRY_DETAILS);
			if (!isOtherPointOfEntry) {
				tfPointOfEntryDetails.clear();
			}
		} else {
			tfPointOfEntryDetails.setVisible(false);
			tfPointOfEntryDetails.setRequired(false);
			tfPointOfEntryDetails.clear();
		}
	}

	private void buildDeaContent(TravelEntryDto newFieldValue) {
		final List<DeaContentEntry> deaContent = newFieldValue.getDeaContent();
		if (CollectionUtils.isNotEmpty(deaContent)) {
			deaFormBuilder = new DEAFormBuilder(deaContent, true);
			deaFormBuilder.buildForm();
			getContent().addComponent(deaFormBuilder.getLayout(), DEA_CONTENT_LOC);
		}
	}

	@Override
	public TravelEntryDto getValue() {
		TravelEntryDto travelEntryDto = super.getValue();
		if (deaFormBuilder != null) {
			travelEntryDto.setDeaContent(deaFormBuilder.getDeaContentEntries());
		}
		return travelEntryDto;
	}

	@Override
	public void setValue(TravelEntryDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);
		buildDeaContent(newFieldValue);

		UserProvider currentUserProvider = UserProvider.getCurrent();
		JurisdictionLevel userJurisditionLevel =
			currentUserProvider != null ? UserProvider.getCurrent().getJurisdictionLevel() : JurisdictionLevel.NONE;

		if (userJurisditionLevel == JurisdictionLevel.HEALTH_FACILITY) {
			FacilityDto facility = FacadeProvider.getFacilityFacade().getByUuid(currentUserProvider.getUser().getHealthFacility().getUuid());
			responsibleRegion.setValue(facility.getRegion());
			responsibleRegion.setReadOnly(true);
			responsibleDistrict.setValue(facility.getDistrict());
			responsibleDistrict.setReadOnly(true);
			responsibleCommunity.setValue(facility.getCommunity());
			responsibleCommunity.setReadOnly(true);
		}
	}
}
