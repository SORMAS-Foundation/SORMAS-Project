/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.campaign.campaigndata;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.FieldConfiguration;

public class CampaignFormDataFilterForm extends AbstractFilterForm<CampaignFormDataCriteria> {

	private static final long serialVersionUID = 718816470397296272L;

	protected CampaignFormDataFilterForm() {
		super(CampaignFormDataCriteria.class, CampaignFormDataDto.I18N_PREFIX);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			CampaignFormDataCriteria.CAMPAIGN,
			CampaignFormDataCriteria.CAMPAIGN_FORM,
			CampaignFormDataCriteria.REGION,
			CampaignFormDataCriteria.DISTRICT,
			CampaignFormDataCriteria.COMMUNITY };
	}

	@Override
	protected void addFields() {
		ComboBox cbCampaign = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				CampaignFormDataCriteria.CAMPAIGN,
				I18nProperties.getPrefixCaption(CampaignFormDataDto.I18N_PREFIX, CampaignFormDataDto.CAMPAIGN),
				200));
		cbCampaign.addItems(FacadeProvider.getCampaignFacade().getAllCampaignsAsReference());

		ComboBox cbCampaignForm = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				CampaignFormDataCriteria.CAMPAIGN_FORM,
				I18nProperties.getPrefixCaption(CampaignFormDataDto.I18N_PREFIX, CampaignFormDataDto.CAMPAIGN_FORM),
				200));
		cbCampaignForm.addItems(FacadeProvider.getCampaignFormFacade().getAllCampaignFormsAsReferences());

		UserDto user = UserProvider.getCurrent().getUser();
		if (user.getRegion() == null) {
			ComboBox cbRegion = addField(
				FieldConfiguration.withCaptionAndPixelSized(
					CampaignFormDataCriteria.REGION,
					I18nProperties.getPrefixCaption(CampaignFormDataDto.I18N_PREFIX, CampaignFormDataDto.REGION),
					200));
			cbRegion.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
		}

		if (user.getDistrict() == null) {
			ComboBox cbDistrict = addField(
				FieldConfiguration.withCaptionAndPixelSized(
					CampaignFormDataCriteria.DISTRICT,
					I18nProperties.getPrefixCaption(CampaignFormDataDto.I18N_PREFIX, CampaignFormDataDto.DISTRICT),
					200));

			if (user.getRegion() != null) {
				cbDistrict.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(user.getRegion().getUuid()));
			}
		}

		if (user.getCommunity() == null) {
			ComboBox cbCommunity = addField(
				FieldConfiguration.withCaptionAndPixelSized(
					CampaignFormDataCriteria.COMMUNITY,
					I18nProperties.getPrefixCaption(CampaignFormDataDto.I18N_PREFIX, CampaignFormDataDto.COMMUNITY),
					200));

			if (user.getDistrict() != null) {
				cbCommunity.addItems(FacadeProvider.getCommunityFacade().getAllActiveByDistrict(user.getDistrict().getUuid()));
			}
		}
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		super.applyDependenciesOnFieldChange(propertyId, event);

		CampaignFormDataCriteria criteria = getValue();

		switch (propertyId) {
		case CampaignFormDataDto.REGION:
			RegionReferenceDto region = (RegionReferenceDto) event.getProperty().getValue();

			if (!DataHelper.equal(region, criteria.getRegion())) {
				getField(CaseDataDto.DISTRICT).setValue(null);
			}

			break;
		case CampaignFormDataDto.DISTRICT:
			DistrictReferenceDto district = (DistrictReferenceDto) event.getProperty().getValue();

			if (!DataHelper.equal(district, criteria.getDistrict())) {
				getField(CaseDataDto.COMMUNITY).setValue(null);
			}

			break;
		}
	}

	@Override
	protected void applyDependenciesOnNewValue(CampaignFormDataCriteria criteria) {
		ComboBox cbRegion = (ComboBox) getField(CampaignFormDataDto.REGION);
		ComboBox cbDistrict = (ComboBox) getField(CampaignFormDataDto.DISTRICT);
		ComboBox cbCommunity = (ComboBox) getField(CampaignFormDataDto.COMMUNITY);

		if (cbRegion != null && cbDistrict != null) {
			RegionReferenceDto region = criteria.getRegion();
			cbDistrict.setEnabled(region != null);
			if (region != null) {
				cbDistrict.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			}
		}

		if (cbDistrict != null && cbCommunity != null) {
			DistrictReferenceDto district = criteria.getDistrict();
			cbCommunity.setEnabled(district != null);
			if (district != null) {
				cbCommunity.addItems(FacadeProvider.getCommunityFacade().getAllActiveByDistrict(district.getUuid()));
			}
		}
	}
}
