package de.symeda.sormas.ui.environment;

import static de.symeda.sormas.ui.utils.CssStyles.ERROR_COLOR_PRIMARY;
import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.style;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.List;

import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.environment.EnvironmentDto;
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
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;

public class EnvironmentCreateForm extends AbstractEditForm<EnvironmentDto> {

	private static final String LOCATION_HEADING_LOC = "locationHeadingLoc";

	//@formatter:off
    private static final String HTML_LAYOUT = fluidRowLocs(EnvironmentDto.REPORT_DATE, EnvironmentDto.EXTERNAL_ID)
            + fluidRowLocs(EnvironmentDto.ENVIRONMENT_MEDIA, "") + fluidRowLocs(EnvironmentDto.ENVIRONMENT_NAME, "")+
            loc(LOCATION_HEADING_LOC) +
            fluidRowLocs(EnvironmentDto.LOCATION) +
            fluidRowLocs("", EnvironmentDto.RESPONSIBLE_USER);
    //@formatter:on

	public EnvironmentCreateForm() {
		super(
			EnvironmentDto.class,
			EnvironmentDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			UiFieldAccessCheckers.getNoop());
		addFields();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {

		addField(EnvironmentDto.REPORT_DATE, DateField.class);
		TextField externalIdField = addField(EnvironmentDto.EXTERNAL_ID, TextField.class);
		style(externalIdField, ERROR_COLOR_PRIMARY);

		addField(EnvironmentDto.ENVIRONMENT_MEDIA, ComboBox.class);
		addField(EnvironmentDto.ENVIRONMENT_NAME, TextField.class);

		Label locationHeadingLabel = new Label(I18nProperties.getString(Strings.headingLocation));
		locationHeadingLabel.addStyleName(H3);
		getContent().addComponent(locationHeadingLabel, LOCATION_HEADING_LOC);

		LocationEditForm locationForm = addField(EnvironmentDto.LOCATION, LocationEditForm.class);
		locationForm.setCaption(null);
		locationForm.setDistrictRequired();
		locationForm.setGpsCoordinatesRequired();

		ComboBox regionField = (ComboBox) locationForm.getFieldGroup().getField(LocationDto.REGION);
		ComboBox districtField = (ComboBox) locationForm.getFieldGroup().getField(LocationDto.DISTRICT);

		ComboBox responsibleUserField = addField(EnvironmentDto.RESPONSIBLE_USER, ComboBox.class);
		responsibleUserField.setNullSelectionAllowed(true);
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

		setRequired(true, EnvironmentDto.REPORT_DATE, EnvironmentDto.ENVIRONMENT_MEDIA, EnvironmentDto.ENVIRONMENT_NAME);
	}
}
