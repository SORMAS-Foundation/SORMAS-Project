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
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;

public class CommunityEditForm extends AbstractEditForm<CommunityDto> {

	private static final long serialVersionUID = 6726008587163831260L;

	private static final String HTML_LAYOUT =
		loc(CommunityDto.NAME) + fluidRowLocs(CommunityDto.REGION, CommunityDto.DISTRICT) + fluidRowLocs(RegionDto.EXTERNAL_ID);

	private boolean create;

	public CommunityEditForm(boolean create) {

		super(CommunityDto.class, CommunityDto.I18N_PREFIX, false);
		this.create = create;

		setWidth(540, Unit.PIXELS);

		if (create) {
			hideValidationUntilNextCommit();
		}
		addFields();
	}

	@Override
	protected void addFields() {

		addField(CommunityDto.NAME, TextField.class);
		ComboBox region = addInfrastructureField(CommunityDto.REGION);
		ComboBox district = addInfrastructureField(CommunityDto.DISTRICT);
		addField(RegionDto.EXTERNAL_ID, TextField.class);

		setRequired(true, CommunityDto.NAME, CommunityDto.REGION, CommunityDto.DISTRICT);

		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper
				.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});

		district.addValueChangeListener(e -> {
			if (e.getProperty().getValue() != null && region.getValue() == null) {
				DistrictDto communityDistrict =
					FacadeProvider.getDistrictFacade().getDistrictByUuid(((DistrictReferenceDto) e.getProperty().getValue()).getUuid());
				region.setValue(communityDistrict.getRegion());
			}
		});

		region.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());

		// TODO: Workaround until cases and other data is properly transfered when infrastructure data changes
		if (!create) {
			region.setEnabled(false);
			district.setEnabled(false);
		}

	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
