package de.symeda.sormas.ui.campaign;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.TextField;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.campaign.CampaignCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class CampaignsView extends AbstractView {

	private static final long serialVersionUID = 4551760940640983434L;

	public static final String VIEW_NAME = "campaigns";

	private CampaignCriteria criteria;
	private VerticalLayout gridLayout;
	private CampaignGrid grid;
	private Button createButton;

	// Filter
	private TextField searchField;
	private com.vaadin.v7.ui.ComboBox relevanceStatusFilter;

	public CampaignsView() {
		super(VIEW_NAME);

		ViewModelProviders.of(getClass()).get(ViewConfiguration.class);

		criteria = ViewModelProviders.of(CampaignsView.class).get(CampaignCriteria.class);
		if (criteria.getRelevanceStatus() == null) {
			criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		}

		grid = new CampaignGrid(criteria);
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");

		addComponent(gridLayout);

		if (UserProvider.getCurrent().hasUserRight(UserRight.CAMPAIGN_EDIT)) {
			createButton = ButtonHelper.createIconButton(Captions.campaignNewCampaign, VaadinIcons.PLUS_CIRCLE,
					e -> ControllerProvider.getCampaignController().createOrEdit(null),
					ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(createButton);
		}
	}

	private HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setWidth(100, Unit.PERCENTAGE);
		filterLayout.setSpacing(true);

		searchField = new TextField();
		searchField.setId("search");
		searchField.setWidth(200, Unit.PIXELS);
		searchField.setNullRepresentation("");
		searchField.setInputPrompt(I18nProperties.getString(Strings.promptCampaignSearch));
		searchField.setImmediate(true);
		searchField.addTextChangeListener(e -> {
			criteria.freeText(e.getText());
			grid.reload();
		});
		filterLayout.addComponent(searchField);

		// Show active/archived/all dropdown
		relevanceStatusFilter = new com.vaadin.v7.ui.ComboBox();
		relevanceStatusFilter.setId("relevanceStatus");
		relevanceStatusFilter.setWidth(140, Unit.PIXELS);
		relevanceStatusFilter.setNullSelectionAllowed(false);
		relevanceStatusFilter.addItems((Object[]) EntityRelevanceStatus.values());
		relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(Captions.campaignActiveCampaigns));
		relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(Captions.campaignArchivedCampaigns));
		relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ALL, I18nProperties.getCaption(Captions.campaignAllCampaigns));
		relevanceStatusFilter.addValueChangeListener(e -> {
				criteria.relevanceStatus((EntityRelevanceStatus) e.getProperty().getValue());
				navigateTo(criteria);
		});
		filterLayout.addComponent(relevanceStatusFilter);
		filterLayout.setComponentAlignment(relevanceStatusFilter, Alignment.MIDDLE_RIGHT);
		filterLayout.setExpandRatio(relevanceStatusFilter, 1);

		return filterLayout;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		if (event != null) {
			String params = event.getParameters().trim();
			if (params.startsWith("?")) {
				params = params.substring(1);
				criteria.fromUrlParams(params);
			}
			updateFilterComponents();
		}
		grid.reload();
	}

	private void updateFilterComponents() {
		applyingCriteria = true;

		if (relevanceStatusFilter != null) {
			relevanceStatusFilter.setValue(criteria.getRelevanceStatus());
		}
		searchField.setValue(criteria.getFreeText());

		applyingCriteria = false;
	}


}
