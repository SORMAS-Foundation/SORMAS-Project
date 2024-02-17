/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.caze;

import static de.symeda.sormas.ui.caze.CaseDataForm.updateFacilityDetails;
import static de.symeda.sormas.ui.utils.CssStyles.LABEL_WHITE_SPACE_NORMAL;
import static de.symeda.sormas.ui.utils.CssStyles.SOFT_REQUIRED;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Arrays;
import java.util.Collections;

import com.vaadin.ui.Label;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.InfrastructureFieldsHelper;

@SuppressWarnings("ALL")
public class PlaceOfStayEditForm extends AbstractEditForm<CaseDataDto> {

	private static final String SELECT_PLACE_OF_STAY_LABEL_LOC = "selectPlaceOfStayLabelLoc";
	private static final String PLACE_OF_STAY_HEADING_LOC = "placeOfStayHeadingLoc";
	private static final String FACILITY_OR_HOME_LOC = "facilityOrHomeLoc";
	private static final String TYPE_GROUP_LOC = "typeGroupLoc";
	private static final String RESPONSIBLE_JURISDICTION_HEADING_LOC = "responsibleJurisdictionHeadingLoc";
	private static final String DIFFERENT_PLACE_OF_STAY_JURISDICTION = "differentPlaceOfStayJurisdiction";
	private static final String DONT_SHARE_WARNING_LOC = "dontShareWarning";

	private static final String MAIN_HTML_LAYOUT = fluidRowLocs(SELECT_PLACE_OF_STAY_LABEL_LOC)
		+ fluidRowLocs(RESPONSIBLE_JURISDICTION_HEADING_LOC)
		+ fluidRowLocs(CaseDataDto.RESPONSIBLE_REGION, CaseDataDto.RESPONSIBLE_DISTRICT, CaseDataDto.RESPONSIBLE_COMMUNITY)
		+ fluidRowLocs(DIFFERENT_PLACE_OF_STAY_JURISDICTION)
		+ fluidRowLocs(PLACE_OF_STAY_HEADING_LOC)
		+ fluidRowLocs(CaseDataDto.REGION, CaseDataDto.DISTRICT, CaseDataDto.COMMUNITY)
		+ fluidRowLocs(CaseDataDto.HEALTH_FACILITY, CaseDataDto.HEALTH_FACILITY_DETAILS);

	private CheckBox differentPlaceOfStayJurisdiction;
	private ComboBox responsibleRegion;
	private ComboBox responsibleDistrict;
	private ComboBox responsibleCommunity;
	private ComboBox regionCombo;
	private ComboBox districtCombo;
	private ComboBox communityCombo;
	private ComboBox facilityCombo;

	public PlaceOfStayEditForm(CaseDataDto caseDataDto) {

		super(CaseDataDto.class, CaseDataDto.I18N_PREFIX, false, null, null);
		addFields();
	}

	@Override
	protected String createHtmlLayout() {
		return MAIN_HTML_LAYOUT;
	}

	@Override
	protected void addFields() {

		Label infoPlaceOfStayInHospital = new Label(I18nProperties.getString(Strings.infoPlaceOfStayInHospital));
		infoPlaceOfStayInHospital.addStyleNames(VSPACE_3, LABEL_WHITE_SPACE_NORMAL);
		getContent().addComponent(infoPlaceOfStayInHospital, SELECT_PLACE_OF_STAY_LABEL_LOC);

		responsibleRegion = addInfrastructureField(CaseDataDto.RESPONSIBLE_REGION);
		responsibleRegion.setRequired(true);
		responsibleRegion.setReadOnly(true);

		responsibleDistrict = addInfrastructureField(CaseDataDto.RESPONSIBLE_DISTRICT);
		responsibleDistrict.setRequired(true);
		responsibleDistrict.setReadOnly(true);
		responsibleCommunity = addInfrastructureField(CaseDataDto.RESPONSIBLE_COMMUNITY);
		responsibleCommunity.setNullSelectionAllowed(true);
		responsibleCommunity.addStyleName(SOFT_REQUIRED);
		responsibleCommunity.setReadOnly(true);
		InfrastructureFieldsHelper.initInfrastructureFields(responsibleRegion, responsibleDistrict, responsibleCommunity);

		regionCombo = addInfrastructureField(CaseDataDto.REGION);
		regionCombo.setVisible(false);
		districtCombo = addInfrastructureField(CaseDataDto.DISTRICT);
		districtCombo.setVisible(false);
		communityCombo = addInfrastructureField(CaseDataDto.COMMUNITY);
		communityCombo.setNullSelectionAllowed(true);
		communityCombo.addStyleName(SOFT_REQUIRED);
		communityCombo.setVisible(false);

		facilityCombo = addInfrastructureField(CaseDataDto.HEALTH_FACILITY);
		facilityCombo.setImmediate(true);
		facilityCombo.setRequired(true);
		TextField facilityDetails = addField(CaseDataDto.HEALTH_FACILITY_DETAILS, TextField.class);
		facilityDetails.setVisible(false);
		facilityCombo.addValueChangeListener(e -> updateFacilityDetails(facilityCombo, facilityDetails));

		InfrastructureFieldsHelper.initInfrastructureFields(responsibleRegion, responsibleDistrict, responsibleCommunity);

		differentPlaceOfStayJurisdiction = addCustomField(DIFFERENT_PLACE_OF_STAY_JURISDICTION, Boolean.class, CheckBox.class);
		differentPlaceOfStayJurisdiction.addStyleName(VSPACE_3);

		if (UserProvider.getCurrent().getJurisdictionLevel() == JurisdictionLevel.HEALTH_FACILITY) {
			differentPlaceOfStayJurisdiction.setEnabled(false);
			differentPlaceOfStayJurisdiction.setVisible(false);
		}

		FieldHelper.setVisibleWhen(
			differentPlaceOfStayJurisdiction,
			Arrays.asList(regionCombo, districtCombo, communityCombo),
			Collections.singletonList(Boolean.TRUE),
			true);
		FieldHelper.setRequiredWhen(
			differentPlaceOfStayJurisdiction,
			Arrays.asList(regionCombo, districtCombo),
			Collections.singletonList(Boolean.TRUE),
			false,
			null);

		regionCombo.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		regionCombo.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper
				.updateItems(districtCombo, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
			updateFacility();
		});
		districtCombo.addValueChangeListener(e -> {
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(
				communityCombo,
				districtDto != null ? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid()) : null);
			updateFacility();
		});
		communityCombo.addValueChangeListener(e -> updateFacility());

		differentPlaceOfStayJurisdiction.addValueChangeListener(e -> {
			updateFacility();
		});

		addValueChangeListener(e -> {
			updateFacility();
		});
	}

	private void updateFacility() {
		final DistrictReferenceDto district;
		final CommunityReferenceDto community;

		if (Boolean.TRUE.equals(differentPlaceOfStayJurisdiction.getValue())) {
			district = (DistrictReferenceDto) districtCombo.getValue();
			community = (CommunityReferenceDto) communityCombo.getValue();
		} else {
			district = (DistrictReferenceDto) responsibleDistrict.getValue();
			community = (CommunityReferenceDto) responsibleCommunity.getValue();
		}

		if (community != null) {
			FieldHelper.updateItems(
				facilityCombo,
				FacadeProvider.getFacilityFacade().getActiveFacilitiesByCommunityAndType(community, FacilityType.HOSPITAL, true, false));
			facilityCombo.setEnabled(true);
		} else if (district != null) {
			FieldHelper.updateItems(
				facilityCombo,
				FacadeProvider.getFacilityFacade().getActiveFacilitiesByDistrictAndType(district, FacilityType.HOSPITAL, true, false));
			facilityCombo.setEnabled(true);
		} else {
			FieldHelper.removeItems(facilityCombo);
			facilityCombo.setEnabled(false);
		}
	}

	@Override
	public void setValue(CaseDataDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);
		if (newFieldValue.getRegion() != null && newFieldValue.getRegion().getUuid() != newFieldValue.getResponsibleRegion().getUuid()) {
			differentPlaceOfStayJurisdiction.setValue(true);
		}
	}
}
