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

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class CommunityEditForm extends AbstractEditForm<CommunityDto> {

	private static final long serialVersionUID = 6726008587163831260L;
	
	private static final String REGION_LOC = Captions.region;
	
	private static final String HTML_LAYOUT =
			LayoutUtil.loc(CommunityDto.NAME)
			+ LayoutUtil.fluidRowLocs(REGION_LOC, CommunityDto.DISTRICT);

	private boolean create;
	
	public CommunityEditForm(UserRight editOrCreateUserRight, boolean create) {
		super(CommunityDto.class, CommunityDto.I18N_PREFIX, editOrCreateUserRight, false);
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
		ComboBox region = new ComboBox();
		region.setCaption(I18nProperties.getPrefixCaption(CommunityDto.I18N_PREFIX, REGION_LOC));
		region.setWidth(100, Unit.PERCENTAGE);
		getContent().addComponent(region, REGION_LOC);
		ComboBox district = addField(CommunityDto.DISTRICT, ComboBox.class);
		
		setRequired(true, CommunityDto.NAME, CommunityDto.DISTRICT);
		region.setRequired(true);
		
		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(district,
					regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});
		
		district.addValueChangeListener(e -> {
			if (e.getProperty().getValue() != null && region.getValue() == null) {
				DistrictDto communityDistrict = FacadeProvider.getDistrictFacade().getDistrictByUuid(((DistrictReferenceDto) e.getProperty().getValue()).getUuid());
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
