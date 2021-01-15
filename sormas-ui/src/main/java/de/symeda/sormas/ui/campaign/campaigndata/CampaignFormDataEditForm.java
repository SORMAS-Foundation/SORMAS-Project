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

import com.vaadin.ui.GridLayout;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.form.CampaignFormDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.ui.campaign.CampaignFormBuilder;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

public class CampaignFormDataEditForm extends AbstractEditForm<CampaignFormDataDto> {

	public static final String CAMPAIGN_FORM_LOC = "campaignFormLoc";

	private static final String HTML_LAYOUT = fluidRowLocs(CampaignFormDataDto.CAMPAIGN, CampaignFormDataDto.FORM_DATE, "")
		+ fluidRowLocs(CampaignFormDataDto.REGION, CampaignFormDataDto.DISTRICT, CampaignFormDataDto.COMMUNITY)
		+ loc(CAMPAIGN_FORM_LOC);

	private static final long serialVersionUID = -8974009722689546941L;

	private CampaignFormBuilder campaignFormBuilder;

	public CampaignFormDataEditForm(boolean create) {
		super(CampaignFormDataDto.class, CampaignFormDataDto.I18N_PREFIX);

		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	@Override
	protected void addFields() {
		ComboBox cbCampaign = addField(CampaignFormDataDto.CAMPAIGN, ComboBox.class);
		cbCampaign.addItems(FacadeProvider.getCampaignFacade().getAllCampaignsAsReference());

		ComboBox cbRegion = addInfrastructureField(CampaignFormDataDto.REGION);
		ComboBox cbDistrict = addInfrastructureField(CampaignFormDataDto.DISTRICT);
		ComboBox cbCommunity = addInfrastructureField(CampaignFormDataDto.COMMUNITY);

		addField(CampaignFormDataDto.FORM_DATE, DateField.class);

		setRequired(
			true,
			CampaignFormDataDto.CAMPAIGN,
			CampaignFormDataDto.FORM_DATE,
			CampaignFormDataDto.REGION,
			CampaignFormDataDto.DISTRICT,
			CampaignFormDataDto.COMMUNITY);

		addInfrastructureListeners(cbRegion, cbDistrict, cbCommunity);
		cbRegion.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
	}

	private void addInfrastructureListeners(ComboBox cbRegion, ComboBox cbDistrict, ComboBox cbCommunity) {
		cbRegion.addValueChangeListener(e -> {
			RegionReferenceDto region = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(cbDistrict, region != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()) : null);
		});

		cbDistrict.addValueChangeListener(e -> {
			DistrictReferenceDto district = (DistrictReferenceDto) e.getProperty().getValue();
			FieldHelper
				.updateItems(cbCommunity, district != null ? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(district.getUuid()) : null);
		});
	}

	@Override
	public CampaignFormDataDto getValue() {
		CampaignFormDataDto value = super.getValue();

		if (campaignFormBuilder == null) {
			throw new RuntimeException("Campaign form builder has not been initialized");
		}

		value.setFormValues(campaignFormBuilder.getFormValues());

		return value;
	}

	@Override
	public void setValue(CampaignFormDataDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);

		buildCampaignForm(newFieldValue);
	}

	@Override
	public void validate() throws Validator.InvalidValueException {
		super.validate();

		if (campaignFormBuilder == null) {
			throw new RuntimeException("Campaign form builder has not been initialized");
		}

		campaignFormBuilder.validateFields();
	}

	public void resetFormValues() {
		campaignFormBuilder.resetFormValues();
	}

	private void buildCampaignForm(CampaignFormDataDto campaignFormData) {
		GridLayout campaignFormLayout = new GridLayout(12, 1);
		campaignFormLayout.setWidth(100, Unit.PERCENTAGE);
		CssStyles.style(campaignFormLayout, CssStyles.VSPACE_3);

		CampaignFormDto campaignForm = FacadeProvider.getCampaignFormFacade().getCampaignFormByUuid(campaignFormData.getCampaignForm().getUuid());
		campaignFormBuilder = new CampaignFormBuilder(
			campaignForm.getCampaignFormElements(),
			campaignFormData.getFormValues(),
			campaignFormLayout,
			campaignForm.getCampaignFormTranslations());

		campaignFormBuilder.buildForm();

		getContent().addComponent(campaignFormLayout, CAMPAIGN_FORM_LOC);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
