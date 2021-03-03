/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.configuration.infrastructure;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.facility.FacilityTypeGroup;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.ui.location.AccessibleTextField;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.StringToAngularLocationConverter;

public class FacilityEditForm extends AbstractEditForm<FacilityDto> {

	private static final long serialVersionUID = 1952619382018965255L;

	private static final String TYPE_GROUP_LOC = "typeGroupLoc";

	private static final String HTML_LAYOUT = fluidRowLocs(FacilityDto.NAME)
		+ fluidRowLocs(TYPE_GROUP_LOC, FacilityDto.TYPE)
		+ fluidRowLocs(FacilityDto.REGION, FacilityDto.DISTRICT)
		+ fluidRowLocs(FacilityDto.COMMUNITY)
		+ fluidRowLocs(FacilityDto.STREET, FacilityDto.HOUSE_NUMBER)
		+ fluidRowLocs(FacilityDto.ADDITIONAL_INFORMATION, FacilityDto.POSTAL_CODE)
		+ fluidRowLocs(FacilityDto.AREA_TYPE, FacilityDto.CITY)
		+ fluidRowLocs(FacilityDto.LATITUDE, FacilityDto.LONGITUDE)
		+ fluidRowLocs(RegionDto.EXTERNAL_ID);

	private boolean create;
	private ComboBox typeGroup;

	public FacilityEditForm(boolean create) {
		super(FacilityDto.class, FacilityDto.I18N_PREFIX, false);
		this.create = create;

		setWidth(540, Unit.PIXELS);

		if (create) {
			hideValidationUntilNextCommit();
		}
		addFields();
	}

	@Override
	protected void addFields() {
		addField(FacilityDto.NAME, TextField.class);
		typeGroup = new ComboBox();
		typeGroup.setId("typeGroup");
		typeGroup.setCaption(I18nProperties.getCaption(Captions.Facility_typeGroup));
		typeGroup.addItems(FacilityTypeGroup.values());
		typeGroup.setWidth(100, Unit.PERCENTAGE);
		typeGroup.setEnabled(create);
		getContent().addComponent(typeGroup, TYPE_GROUP_LOC);
		ComboBox type = addField(FacilityDto.TYPE);
		type.removeAllItems();
		type.setEnabled(create);
		ComboBox region = addInfrastructureField(FacilityDto.REGION);
		ComboBox district = addInfrastructureField(FacilityDto.DISTRICT);
		ComboBox community = addInfrastructureField(FacilityDto.COMMUNITY);
		addField(FacilityDto.CITY, TextField.class);
		addField(FacilityDto.POSTAL_CODE, TextField.class);
		addField(FacilityDto.STREET, TextField.class);
		addField(FacilityDto.HOUSE_NUMBER, TextField.class);
		addField(FacilityDto.ADDITIONAL_INFORMATION, TextField.class);
		addField(FacilityDto.AREA_TYPE, ComboBox.class);
		AccessibleTextField latitude = addField(FacilityDto.LATITUDE, AccessibleTextField.class);
		latitude.setConverter(new StringToAngularLocationConverter());
		latitude.setConversionError(I18nProperties.getValidationError(Validations.onlyGeoCoordinatesAllowed, latitude.getCaption()));
		AccessibleTextField longitude = addField(FacilityDto.LONGITUDE, AccessibleTextField.class);
		longitude.setConverter(new StringToAngularLocationConverter());
		longitude.setConversionError(I18nProperties.getValidationError(Validations.onlyGeoCoordinatesAllowed, longitude.getCaption()));
		addField(RegionDto.EXTERNAL_ID, TextField.class);

		setRequired(true, FacilityDto.NAME, TYPE_GROUP_LOC, FacilityDto.TYPE, FacilityDto.REGION, FacilityDto.DISTRICT);

		typeGroup.addValueChangeListener(e -> {
			FieldHelper.updateEnumData(type, FacilityType.getTypes((FacilityTypeGroup) typeGroup.getValue()));
		});

		type.addValueChangeListener(e -> {
			boolean notLab = !FacilityType.LABORATORY.equals(type.getValue());
			region.setRequired(notLab);
			district.setRequired(notLab);
			if (!create) {
				// Disable editing of region, etc. so case references stay correct
				region.setEnabled(false);
				district.setEnabled(false);
				community.setEnabled(false);
			}
		});

		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper
				.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});

		district.addValueChangeListener(e -> {
			FieldHelper.removeItems(community);
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(
				community,
				districtDto != null ? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid()) : null);
		});

		community.addValueChangeListener(e -> {
			@SuppressWarnings("unused")
			CommunityReferenceDto communityDto = (CommunityReferenceDto) e.getProperty().getValue();
		});
		region.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
	}

	@Override
	public void setValue(FacilityDto facilityDto) throws com.vaadin.v7.data.Property.ReadOnlyException, Converter.ConversionException {
		if (facilityDto.getType() != null) {
			typeGroup.setValue(facilityDto.getType().getFacilityTypeGroup());
		}
		super.setValue(facilityDto);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
