package de.symeda.sormas.ui.campaign.campaignstatistics;

import java.util.function.Consumer;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.campaign.statistics.CampaignStatisticsCriteria;
import de.symeda.sormas.api.campaign.statistics.CampaignStatisticsDto;
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

public class CampaignStatisticsFilterForm extends AbstractFilterForm<CampaignStatisticsCriteria> {

	private Consumer<CampaignFormMetaReferenceDto> formMetaChangedCallback;
	private ComboBox cbCampaignForm;

	protected CampaignStatisticsFilterForm() {

		super(
			CampaignStatisticsCriteria.class,
			CampaignStatisticsDto.I18N_PREFIX,
			JurisdictionFieldConfig
				.withPrefillOnHide(CampaignStatisticsCriteria.REGION, CampaignStatisticsCriteria.DISTRICT, CampaignStatisticsCriteria.COMMUNITY));
		formActionButtonsComponent.style(CssStyles.FORCE_CAPTION);
		formActionButtonsComponent.setSpacing(false);
		formActionButtonsComponent.setSizeFull();
		formActionButtonsComponent.setMargin(new MarginInfo(false, false, false, true));
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			CampaignStatisticsCriteria.CAMPAIGN_FORM_META,
			CampaignStatisticsCriteria.REGION,
			CampaignStatisticsCriteria.DISTRICT,
			CampaignStatisticsCriteria.COMMUNITY };
	}

	@Override
	protected void addFields() {

		cbCampaignForm = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				CampaignStatisticsCriteria.CAMPAIGN_FORM_META,
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
			FieldConfiguration.withCaptionAndPixelSized(CampaignStatisticsCriteria.REGION, I18nProperties.getCaption(Captions.Campaign_region), 200));
		regionFilter.setInputPrompt(I18nProperties.getString(Strings.promptAllRegions));
		regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());

		ComboBox districtFilter = addField(
			FieldConfiguration
				.withCaptionAndPixelSized(CampaignStatisticsCriteria.DISTRICT, I18nProperties.getCaption(Captions.Campaign_district), 200));
		districtFilter.setInputPrompt(I18nProperties.getString(Strings.promptAllDistricts));

		ComboBox communityFilter = addField(
			FieldConfiguration
				.withCaptionAndPixelSized(CampaignStatisticsCriteria.COMMUNITY, I18nProperties.getCaption(Captions.Campaign_community), 200));
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
	protected void applyDependenciesOnNewValue(CampaignStatisticsCriteria criteria) {
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
