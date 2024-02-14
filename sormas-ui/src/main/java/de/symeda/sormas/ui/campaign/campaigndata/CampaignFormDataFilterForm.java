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

import java.util.function.Consumer;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;

public class CampaignFormDataFilterForm extends AbstractFilterForm<CampaignFormDataCriteria> {

	private static final long serialVersionUID = 718816470397296272L;

	private Consumer<CampaignFormMetaReferenceDto> formMetaChangedCallback;
	private ComboBox cbCampaignForm;

	protected CampaignFormDataFilterForm() {

		super(
			CampaignFormDataCriteria.class,
			CampaignFormDataDto.I18N_PREFIX,
			JurisdictionFieldConfig
				.of(CampaignFormDataCriteria.REGION, CampaignFormDataCriteria.DISTRICT, CampaignFormDataCriteria.COMMUNITY));
		formActionButtonsComponent.style(CssStyles.FORCE_CAPTION);
		formActionButtonsComponent.setSpacing(false);
		formActionButtonsComponent.setSizeFull();
		formActionButtonsComponent.setMargin(new MarginInfo(false, false, false, true));
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			CampaignFormDataCriteria.CAMPAIGN_FORM_META,
			CampaignFormDataCriteria.REGION,
			CampaignFormDataCriteria.DISTRICT,
			CampaignFormDataCriteria.COMMUNITY };
	}

	@Override
	protected void addFields() {

		cbCampaignForm = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				CampaignFormDataCriteria.CAMPAIGN_FORM_META,
				I18nProperties.getPrefixCaption(CampaignFormDataDto.I18N_PREFIX, CampaignFormDataDto.CAMPAIGN_FORM_META),
				200));
		cbCampaignForm.addItems(FacadeProvider.getCampaignFormMetaFacade().getAllCampaignFormMetasAsReferences());

		FieldHelper.addSoftRequiredStyle(cbCampaignForm);

		if (formMetaChangedCallback != null) {
			cbCampaignForm.addValueChangeListener(e -> {
				formMetaChangedCallback.accept((CampaignFormMetaReferenceDto) e.getProperty().getValue());
			});
		}

		ComboBox regionFilter = addField(
			FieldConfiguration.withCaptionAndPixelSized(CampaignFormDataCriteria.REGION, I18nProperties.getCaption(Captions.Campaign_region), 200));
		regionFilter.setInputPrompt(I18nProperties.getString(Strings.promptAllRegions));
		regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());

		ComboBox districtFilter = addField(
			FieldConfiguration
				.withCaptionAndPixelSized(CampaignFormDataCriteria.DISTRICT, I18nProperties.getCaption(Captions.Campaign_district), 200));
		districtFilter.setInputPrompt(I18nProperties.getString(Strings.promptAllDistricts));

		ComboBox communityFilter = addField(
			FieldConfiguration
				.withCaptionAndPixelSized(CampaignFormDataCriteria.COMMUNITY, I18nProperties.getCaption(Captions.Campaign_community), 200));
		communityFilter.setInputPrompt(I18nProperties.getString(Strings.promptAllCommunities));

		UserDto user = currentUserDto();
		final RegionReferenceDto userRegion = user.getRegion();
		final DistrictReferenceDto userDistrict = user.getDistrict();
		final CommunityReferenceDto userCommunity = user.getCommunity();
		if (userRegion != null) {
			regionFilter.setEnabled(false);
			districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(userRegion.getUuid()));
			if (userDistrict != null) {
				districtFilter.setEnabled(false);
				communityFilter.addItems(FacadeProvider.getCommunityFacade().getAllActiveByDistrict(userDistrict.getUuid()));
				if (userCommunity != null) {
					communityFilter.setEnabled(false);
				}
			}
		}
	}

	@Override
	protected void applyFieldConfiguration(FieldConfiguration configuration, Field field) {
		super.applyFieldConfiguration(configuration, field);
		if (configuration.getCaption() != null) {
			field.setCaption(configuration.getCaption());
		}
	}

	@Override
	protected <T1 extends Field> void formatField(T1 field, String propertyId) {
		super.formatField(field, propertyId);
		field.addStyleName(CssStyles.CAPTION_ON_TOP);
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		super.applyDependenciesOnFieldChange(propertyId, event);

		switch (propertyId) {
		case CampaignFormDataDto.REGION:
			RegionReferenceDto region = (RegionReferenceDto) event.getProperty().getValue();
			if (region != null) {
				districtFilter.removeAllItems();
				districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			} else {
				districtFilter.removeAllItems();
				districtFilter.clear();
			}
			break;
		case CampaignFormDataDto.DISTRICT:
			DistrictReferenceDto district = (DistrictReferenceDto) event.getProperty().getValue();

			if (district != null) {
				communityFilter.removeAllItems();
				communityFilter.addItems(FacadeProvider.getCommunityFacade().getAllActiveByDistrict(district.getUuid()));
			} else {
				communityFilter.removeAllItems();
				communityFilter.clear();
			}
			break;
		}
	}

	@Override
	protected void applyDependenciesOnNewValue(CampaignFormDataCriteria criteria) {
		cbCampaignForm.removeAllItems();
		if (criteria.getCampaign() != null) {
			cbCampaignForm
				.addItems(FacadeProvider.getCampaignFormMetaFacade().getCampaignFormMetasAsReferencesByCampaign(criteria.getCampaign().getUuid()));
		} else {
			cbCampaignForm.addItems(FacadeProvider.getCampaignFormMetaFacade().getAllCampaignFormMetasAsReferences());
		}
	}

	public void setFormMetaChangedCallback(Consumer<CampaignFormMetaReferenceDto> formMetaChangedCallback) {
		this.formMetaChangedCallback = formMetaChangedCallback;

		if (cbCampaignForm != null) {
			cbCampaignForm.addValueChangeListener(e -> formMetaChangedCallback.accept((CampaignFormMetaReferenceDto) e.getProperty().getValue()));
		}
	}

}
