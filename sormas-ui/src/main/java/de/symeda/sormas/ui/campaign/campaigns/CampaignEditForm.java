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

package de.symeda.sormas.ui.campaign.campaigns;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;

public class CampaignEditForm extends AbstractEditForm<CampaignDto> {

	private static final long serialVersionUID = 7762204114905664597L;

	private static final String STATUS_CHANGE = "statusChange";
	private static final String CAMPAIGN_BASIC_HEADING_LOC = "campaignBasicHeadingLoc";
	private static final String USAGE_INFO = "usageInfo";
	private static final String CAMPAIGN_DATA_LOC = "campaignDataLoc";
	private static final String CAMPAIGN_DASHBOARD_LOC = "campaignDashboardLoc";
	private static final String SPACE_LOC = "spaceLoc";

	private static final String HTML_LAYOUT = loc(CAMPAIGN_BASIC_HEADING_LOC)
		+ fluidRowLocs(CampaignDto.UUID, CampaignDto.CREATING_USER)
		+ fluidRowLocs(CampaignDto.START_DATE, CampaignDto.END_DATE)
		+ fluidRowLocs(CampaignDto.NAME)
		+ fluidRowLocs(CampaignDto.DESCRIPTION)
		+ fluidRowLocs(USAGE_INFO)
		+ fluidRowLocs(CAMPAIGN_DATA_LOC)
		+ fluidRowLocs(CAMPAIGN_DASHBOARD_LOC)
		+ fluidRowLocs(SPACE_LOC)
		+ fluidRowLocs(CampaignDto.DELETION_REASON)
		+ fluidRowLocs(CampaignDto.OTHER_DELETION_REASON);

	private final VerticalLayout statusChangeLayout;
	private Boolean isCreateForm = null;
	private CampaignDto campaignDto;

	private CampaignFormsGridComponent campaignFormsGridComponent;
	private CampaignDashboardElementsGridComponent campaignDashboardGridComponent;

	public CampaignEditForm(CampaignDto campaignDto) {

		super(CampaignDto.class, CampaignDto.I18N_PREFIX);
		setWidth(1280, Unit.PIXELS);

		this.campaignDto = campaignDto;
		isCreateForm = campaignDto == null;
		if (isCreateForm) {
			hideValidationUntilNextCommit();
		}
		statusChangeLayout = new VerticalLayout();
		statusChangeLayout.setSpacing(false);
		statusChangeLayout.setMargin(false);
		getContent().addComponent(statusChangeLayout, STATUS_CHANGE);

		addFields();
	}

	@Override
	protected void addFields() {

		if (isCreateForm == null) {
			return;
		}

		Label campaignBasicHeadingLabel = new Label(I18nProperties.getString(Strings.headingCampaignBasics));
		campaignBasicHeadingLabel.addStyleName(H3);
		getContent().addComponent(campaignBasicHeadingLabel, CAMPAIGN_BASIC_HEADING_LOC);

		addField(CampaignDto.UUID, TextField.class);
		addField(CampaignDto.CREATING_USER);

		DateField startDate = addField(CampaignDto.START_DATE, DateField.class);
		startDate.removeAllValidators();
		DateField endDate = addField(CampaignDto.END_DATE, DateField.class);
		endDate.removeAllValidators();
		startDate.addValidator(
			new DateComparisonValidator(
				startDate,
				endDate,
				true,
				true,
				I18nProperties.getValidationError(Validations.beforeDate, startDate.getCaption(), endDate.getCaption())));
		endDate.addValidator(
			new DateComparisonValidator(
				endDate,
				startDate,
				false,
				true,
				I18nProperties.getValidationError(Validations.afterDate, endDate.getCaption(), startDate.getCaption())));

		addField(CampaignDto.NAME);
		TextArea description = addField(CampaignDto.DESCRIPTION, TextArea.class);
		description.setRows(6);

		setReadOnly(true, CampaignDto.UUID, CampaignDto.CREATING_USER);
		setVisible(!isCreateForm, CampaignDto.UUID, CampaignDto.CREATING_USER);

		setRequired(true, CampaignDto.UUID, CampaignDto.CREATING_USER, CampaignDto.START_DATE, CampaignDto.END_DATE, CampaignDto.NAME);

		FieldHelper.addSoftRequiredStyle(description);

		final HorizontalLayout usageLayout = new HorizontalLayout();
		usageLayout.setWidthFull();
		Label usageLabel =
			new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoUsageOfEditableCampaignGrids), ContentMode.HTML);
		usageLabel.setWidthFull();
		usageLayout.addComponent(usageLabel);
		usageLayout.setSpacing(true);
		usageLayout.setMargin(new MarginInfo(true, false, true, false));
		getContent().addComponent(usageLayout, USAGE_INFO);

		campaignFormsGridComponent = new CampaignFormsGridComponent(
			this.campaignDto == null ? Collections.EMPTY_LIST : new ArrayList<>(campaignDto.getCampaignFormMetas()),
			FacadeProvider.getCampaignFormMetaFacade().getAllCampaignFormMetasAsReferences());
		getContent().addComponent(campaignFormsGridComponent, CAMPAIGN_DATA_LOC);

		final List<CampaignDashboardElement> campaignDashboardElements = FacadeProvider.getCampaignFacade().getCampaignDashboardElements(null);
		campaignDashboardGridComponent = new CampaignDashboardElementsGridComponent(
			this.campaignDto == null
				? Collections.EMPTY_LIST
				: FacadeProvider.getCampaignFacade().getCampaignDashboardElements(campaignDto.getUuid()),
			campaignDashboardElements);
		getContent().addComponent(campaignDashboardGridComponent, CAMPAIGN_DASHBOARD_LOC);

		final Label spacer = new Label();
		getContent().addComponent(spacer, SPACE_LOC);

		addField(CampaignDto.DELETION_REASON);
		addField(CampaignDto.OTHER_DELETION_REASON, TextArea.class).setRows(3);
		setVisible(false, CampaignDto.DELETION_REASON, CampaignDto.OTHER_DELETION_REASON);
	}

	@Override
	public CampaignDto getValue() {
		final CampaignDto campaignDto = super.getValue();
		campaignDto.setCampaignFormMetas(new HashSet<>(campaignFormsGridComponent.getItems()));
		campaignDto.setCampaignDashboardElements(campaignDashboardGridComponent.getItems());
		return campaignDto;
	}

	@Override
	public void setValue(CampaignDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);
		campaignFormsGridComponent
			.setSavedItems(newFieldValue.getCampaignFormMetas() != null ? new ArrayList<>(newFieldValue.getCampaignFormMetas()) : new ArrayList<>());

		if (CollectionUtils.isNotEmpty(newFieldValue.getCampaignDashboardElements())) {
			campaignDashboardGridComponent.setSavedItems(
				newFieldValue.getCampaignDashboardElements()
					.stream()
					.sorted(Comparator.comparingInt(CampaignDashboardElement::getOrder))
					.collect(Collectors.toList()));
		}
	}

	@Override
	public void discard() throws SourceException {
		super.discard();
		campaignFormsGridComponent.discardGrid();
		campaignDashboardGridComponent.discardGrid();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
