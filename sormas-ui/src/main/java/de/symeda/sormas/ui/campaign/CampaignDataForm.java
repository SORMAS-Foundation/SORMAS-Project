package de.symeda.sormas.ui.campaign;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;

public class CampaignDataForm extends AbstractEditForm<CampaignDto> {

	private static final long serialVersionUID = 7762204114905664597L;

	private static final String STATUS_CHANGE = "statusChange";
	private static final String CAMPAIGN_DATA_HEADING_LOC = "campaignDataHeadingLoc";

	private static final String HTML_LAYOUT = loc(CAMPAIGN_DATA_HEADING_LOC)
		+ fluidRowLocs(CampaignDto.UUID, CampaignDto.CREATING_USER)
		+ fluidRowLocs(CampaignDto.START_DATE, CampaignDto.END_DATE)
		+ fluidRowLocs(CampaignDto.NAME)
		+ fluidRowLocs(CampaignDto.DESCRIPTION);

	private final VerticalLayout statusChangeLayout;
	private Boolean isCreateForm = null;

	public CampaignDataForm(boolean create) {

		super(CampaignDto.class, CampaignDto.I18N_PREFIX);

		isCreateForm = create;
		if (create) {
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

		Label campaignDataHeadingLabel = new Label(I18nProperties.getString(Strings.headingCampaignData));
		campaignDataHeadingLabel.addStyleName(H3);
		getContent().addComponent(campaignDataHeadingLabel, CAMPAIGN_DATA_HEADING_LOC);

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
		description.setRows(3);

		setReadOnly(true, CampaignDto.UUID, CampaignDto.CREATING_USER);
		setVisible(!isCreateForm, CampaignDto.UUID, CampaignDto.CREATING_USER);

		setRequired(true, CampaignDto.UUID, CampaignDto.CREATING_USER, CampaignDto.START_DATE, CampaignDto.END_DATE, CampaignDto.NAME);

		FieldHelper.addSoftRequiredStyle(description);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
