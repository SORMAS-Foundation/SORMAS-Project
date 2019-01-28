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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.configuration.infrastructure;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.StringToAngularLocationConverter;

public class FacilityEditForm extends AbstractEditForm<FacilityDto> {

	private static final long serialVersionUID = 1952619382018965255L;

	private static final String HTML_LAYOUT = LayoutUtil.fluidRowLocs(FacilityDto.NAME, FacilityDto.REGION)
			+ LayoutUtil.fluidRowLocs(FacilityDto.DISTRICT, FacilityDto.COMMUNITY)
			+ LayoutUtil.fluidRowLocs(FacilityDto.CITY)
			+ LayoutUtil.fluidRowLocs(FacilityDto.LATITUDE, FacilityDto.LONGITUDE);

	private boolean laboratory;
	private boolean create;

	public FacilityEditForm(UserRight editOrCreateUserRight, boolean create, boolean laboratory) {
		super(FacilityDto.class, FacilityDto.I18N_PREFIX, editOrCreateUserRight, false);
		this.create = create;
		this.laboratory = laboratory;

		setWidth(540, Unit.PIXELS);

		if (create) {
			hideValidationUntilNextCommit();
		}
		addFields();
	}

	@Override
	protected void addFields() {
		TextField name = addField(FacilityDto.NAME, TextField.class);
		ComboBox region = addField(FacilityDto.REGION, ComboBox.class);
		ComboBox district = addField(FacilityDto.DISTRICT, ComboBox.class);
		ComboBox community = addField(FacilityDto.COMMUNITY, ComboBox.class);
		addField(FacilityDto.CITY, TextField.class);
		TextField latitude = addField(FacilityDto.LATITUDE, TextField.class);
		latitude.setConverter(new StringToAngularLocationConverter());
		latitude.setConversionError("Only geo coordinate values are allowed for " + latitude.getCaption());
		TextField longitude = addField(FacilityDto.LONGITUDE, TextField.class);
		longitude.setConverter(new StringToAngularLocationConverter());
		longitude.setConversionError("Only geo coordinate values are allowed for " + longitude.getCaption());

		name.setRequired(true);
		if (!laboratory) {
			region.setRequired(true);
			district.setRequired(true);
			community.setRequired(true);
		}

		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(district,
					regionDto != null ? FacadeProvider.getDistrictFacade().getAllByRegion(regionDto.getUuid()) : null);
		});

		district.addValueChangeListener(e -> {
			FieldHelper.removeItems(community);
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(community,
					districtDto != null ? FacadeProvider.getCommunityFacade().getAllByDistrict(districtDto.getUuid())
							: null);
		});

		community.addValueChangeListener(e -> {
			@SuppressWarnings("unused")
			CommunityReferenceDto communityDto = (CommunityReferenceDto) e.getProperty().getValue();
		});
		region.addItems(FacadeProvider.getRegionFacade().getAllAsReference());

		if (!create) {
			if (!laboratory) {
				// Disable editing of region, etc. so case references stay correct
				region.setEnabled(false);
				district.setEnabled(false);
				community.setEnabled(false);
			}
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
