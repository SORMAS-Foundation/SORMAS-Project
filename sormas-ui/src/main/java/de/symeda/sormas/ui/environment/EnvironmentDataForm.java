package de.symeda.sormas.ui.environment;

import static de.symeda.sormas.ui.utils.CssStyles.ERROR_COLOR_PRIMARY;
import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.style;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentInfrastructureDetails;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.environment.WaterType;
import de.symeda.sormas.api.environment.WaterUse;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CheckBoxTree;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.ResizableTextAreaWrapper;
import de.symeda.sormas.ui.utils.UserField;

public class EnvironmentDataForm extends AbstractEditForm<EnvironmentDto> {

	private static final String LOCATION_HEADING_LOC = "locationHeadingLoc";
	private static final String WATER_USE_HEADING_LOC = "waterUseHeadingLoc";

	//@formatter:off
	private static final String HTML_LAYOUT = fluidRowLocs(EnvironmentDto.UUID, EnvironmentDto.EXTERNAL_ID) +
			fluidRowLocs(6, EnvironmentDto.REPORT_DATE, 3, EnvironmentDto.REPORTING_USER, 3, "") +
			fluidRowLocs(EnvironmentDto.INVESTIGATION_STATUS, "") +
			fluidRowLocs(EnvironmentDto.ENVIRONMENT_MEDIA, "") +
			fluidRowLocs(EnvironmentDto.WATER_TYPE, EnvironmentDto.OTHER_WATER_TYPE) +
			fluidRowLocs(EnvironmentDto.INFRASTUCTURE_DETAILS, EnvironmentDto.OTHER_INFRASTRUCTUIRE_DETAILS) +
			fluidRowLocs(WATER_USE_HEADING_LOC) +
			fluidRowLocs(EnvironmentDto.WATER_USE) +
			fluidRowLocs(EnvironmentDto.OTHER_WATER_USE) +
			fluidRowLocs(EnvironmentDto.ENVIRONMENT_NAME, "")+
			fluidRowLocs(EnvironmentDto.DESCRIPTION) +
			loc(LOCATION_HEADING_LOC) +
			fluidRowLocs(EnvironmentDto.LOCATION) +
			fluidRowLocs("", EnvironmentDto.RESPONSIBLE_USER) +
			fluidRowLocs(EnvironmentDto.DELETION_REASON) +
			fluidRowLocs(EnvironmentDto.OTHER_DELETION_REASON);
    //@formatter:on

	public EnvironmentDataForm(boolean isPseudonymized, boolean inJurisdiction, boolean isEditAllowed) {
		super(
			EnvironmentDto.class,
			EnvironmentDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			UiFieldAccessCheckers.forDataAccessLevel(UserProvider.getCurrent().getPseudonymizableDataAccessLevel(inJurisdiction), isPseudonymized),
			isEditAllowed);
		addFields();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {

		TextField environmentUuidFields = addField(EnvironmentDto.UUID, TextField.class);
		environmentUuidFields.setReadOnly(true);

		addField(EnvironmentDto.REPORT_DATE, DateField.class);

		TextField externalIdField = addField(EnvironmentDto.EXTERNAL_ID, TextField.class);
		style(externalIdField, ERROR_COLOR_PRIMARY);

		UserField reportingUser = addField(EnvironmentDto.REPORTING_USER, UserField.class);
		reportingUser.setParentPseudonymizedSupplier(() -> getValue().isPseudonymized());
		reportingUser.setReadOnly(true);

		addField(EnvironmentDto.INVESTIGATION_STATUS, ComboBox.class);
		ComboBox environmentMedia = addField(EnvironmentDto.ENVIRONMENT_MEDIA, ComboBox.class);

		ComboBox waterType = addField(EnvironmentDto.WATER_TYPE, ComboBox.class);
		TextField otherWaterType = addField(EnvironmentDto.OTHER_WATER_TYPE, TextField.class);
		otherWaterType.setInputPrompt(I18nProperties.getString(Strings.pleaseSpecify));

		ComboBox infrastructureDetails = addField(EnvironmentDto.INFRASTUCTURE_DETAILS, ComboBox.class);
		TextField otherInfrastructureDetails = addField(EnvironmentDto.OTHER_INFRASTRUCTUIRE_DETAILS, TextField.class);
		otherInfrastructureDetails.setInputPrompt(I18nProperties.getString(Strings.pleaseSpecify));

		Label useOfWaterHeading = new Label(I18nProperties.getString(Strings.headingWaterUse));
		CssStyles.style(useOfWaterHeading, CssStyles.LABEL_XLARGE, CssStyles.VSPACE_TOP_3);
		getContent().addComponent(useOfWaterHeading, WATER_USE_HEADING_LOC);

		TextField otherWaterUse = addField(EnvironmentDto.OTHER_WATER_USE, TextField.class);
		otherWaterUse.setInputPrompt(I18nProperties.getString(Strings.pleaseSpecify));

		CheckBoxTree<WaterUse> waterUseCheckBoxTree = addField(EnvironmentDto.WATER_USE, CheckBoxTree.class);
		waterUseCheckBoxTree.setEnumType(WaterUse.class, null);

		waterUseCheckBoxTree.addValueChangeListener((e) -> {
			if (e.getProperty().getValue() != null) {
				if (Boolean.TRUE.equals(((Map<WaterUse, Boolean>) e.getProperty().getValue()).get(WaterUse.OTHER))) {
					otherWaterUse.setVisible(true);
				} else {
					otherWaterUse.setVisible(false);
					otherWaterUse.clear();
				}
			}
		});

		addField(EnvironmentDto.ENVIRONMENT_NAME, TextField.class);

		TextArea description = addField(EnvironmentDto.DESCRIPTION, TextArea.class, new ResizableTextAreaWrapper<>());
		description.setRows(2);

		Label locationHeadingLabel = new Label(I18nProperties.getString(Strings.headingLocation));
		locationHeadingLabel.addStyleName(H3);
		getContent().addComponent(locationHeadingLabel, LOCATION_HEADING_LOC);

		LocationEditForm locationForm = addField(EnvironmentDto.LOCATION, LocationEditForm.class);
		locationForm.setCaption(null);
		locationForm.setDistrictRequired();
		locationForm.setGpsCoordinatesRequired();

		ComboBox regionField = (ComboBox) locationForm.getFieldGroup().getField(LocationDto.REGION);
		ComboBox districtField = (ComboBox) locationForm.getFieldGroup().getField(LocationDto.DISTRICT);

		UserField responsibleUserField = addField(EnvironmentDto.RESPONSIBLE_USER, UserField.class);
		responsibleUserField.setParentPseudonymizedSupplier(() -> getValue().isPseudonymized());
		responsibleUserField.setEnabled(true);

		addField(EnvironmentDto.DELETION_REASON);
		addField(EnvironmentDto.OTHER_DELETION_REASON, TextArea.class).setRows(3);
		setVisible(false, EnvironmentDto.DELETION_REASON, EnvironmentDto.OTHER_DELETION_REASON);

		// responsible users
		List<UserReferenceDto> responsibleUsers = FacadeProvider.getUserFacade()
			.getUserRefsByInfrastructure(null, JurisdictionLevel.NATION, JurisdictionLevel.NATION, null, UserRight.ENVIRONMENT_EDIT);
		FieldHelper.updateItems(responsibleUserField, responsibleUsers);

		regionField.addValueChangeListener(e -> {
			List<UserReferenceDto> filteredResponsibleUsers;
			RegionReferenceDto region = (RegionReferenceDto) e.getProperty().getValue();
			if (region == null) {
				filteredResponsibleUsers = FacadeProvider.getUserFacade()
					.getUserRefsByInfrastructure(null, JurisdictionLevel.NATION, JurisdictionLevel.NATION, null, UserRight.ENVIRONMENT_EDIT);
			} else {
				filteredResponsibleUsers = FacadeProvider.getUserFacade()
					.getUserRefsByInfrastructure(region, JurisdictionLevel.REGION, JurisdictionLevel.NATION, null, UserRight.ENVIRONMENT_EDIT);
			}

			FieldHelper.updateItems(responsibleUserField, filteredResponsibleUsers);
		});

		districtField.addValueChangeListener(e -> {
			DistrictReferenceDto district = (DistrictReferenceDto) districtField.getValue();
			List<UserReferenceDto> filteredResponsibleUsers;
			if (district != null) {
				filteredResponsibleUsers = FacadeProvider.getUserFacade()
					.getUserRefsByInfrastructure(district, JurisdictionLevel.DISTRICT, JurisdictionLevel.NATION, null, UserRight.ENVIRONMENT_EDIT);
			} else {
				RegionReferenceDto region = (RegionReferenceDto) regionField.getValue();
				if (region != null) {
					filteredResponsibleUsers = FacadeProvider.getUserFacade()
						.getUserRefsByInfrastructure(region, JurisdictionLevel.REGION, JurisdictionLevel.NATION, null, UserRight.ENVIRONMENT_EDIT);
				} else {
					filteredResponsibleUsers = FacadeProvider.getUserFacade()
						.getUserRefsByInfrastructure(null, JurisdictionLevel.NATION, JurisdictionLevel.NATION, null, UserRight.ENVIRONMENT_EDIT);
				}
			}
			FieldHelper.updateItems(responsibleUserField, filteredResponsibleUsers);
		});

		setRequired(
			true,
			EnvironmentDto.REPORT_DATE,
			EnvironmentDto.INVESTIGATION_STATUS,
			EnvironmentDto.ENVIRONMENT_MEDIA,
			EnvironmentDto.ENVIRONMENT_NAME);

		environmentMedia.addValueChangeListener(valueChangeEvent -> {
			if (EnvironmentMedia.WATER.equals(valueChangeEvent.getProperty().getValue())) {
				waterType.setVisible(true);
				infrastructureDetails.setVisible(true);
				waterUseCheckBoxTree.setVisible(true);
				useOfWaterHeading.setVisible(true);
			} else {
				useOfWaterHeading.setVisible(false);
				waterType.setVisible(false);
				infrastructureDetails.setVisible(false);
				waterUseCheckBoxTree.setVisible(false);
				otherWaterUse.setVisible(false);
				waterType.clear();
				infrastructureDetails.clear();
				waterUseCheckBoxTree.clear();
				waterUseCheckBoxTree.clear();
				otherWaterUse.clear();
			}
		});

		FieldHelper.setVisibleWhen(getFieldGroup(), EnvironmentDto.OTHER_WATER_TYPE, EnvironmentDto.WATER_TYPE, Arrays.asList(WaterType.OTHER), true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			EnvironmentDto.OTHER_INFRASTRUCTUIRE_DETAILS,
			EnvironmentDto.INFRASTUCTURE_DETAILS,
			Arrays.asList(EnvironmentInfrastructureDetails.OTHER),
			true);

		addValueChangeListener(e -> {
			if (getValue().getWaterUse() != null && Boolean.TRUE.equals((getValue().getWaterUse().get(WaterUse.OTHER)))) {
				otherWaterUse.setVisible(true);
			} else {
				otherWaterUse.setVisible(false);
				otherWaterUse.clear();
			}
		});
	}
}
