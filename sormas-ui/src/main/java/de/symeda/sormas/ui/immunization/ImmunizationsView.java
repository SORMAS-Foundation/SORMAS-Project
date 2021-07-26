package de.symeda.sormas.ui.immunization;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.immunization.components.filter.ImmunizationFilterForm;
import de.symeda.sormas.ui.immunization.components.grid.ImmunizationGrid;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.expandablebutton.ExpandableButton;

public class ImmunizationsView extends AbstractView {

	public static final String VIEW_NAME = "immunizations";

	private final ImmunizationGrid grid;
	private final ImmunizationCriteria criteria;

	private ImmunizationFilterForm filterForm;

	public ImmunizationsView() {
		super(VIEW_NAME);

		criteria = ViewModelProviders.of(ImmunizationsView.class).get(ImmunizationCriteria.class);
		grid = new ImmunizationGrid(criteria);

		final VerticalLayout gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(createStatusFilterBar());
		gridLayout.addComponent(grid);

		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");

		addComponent(gridLayout);

		UserProvider currentUser = UserProvider.getCurrent();
		if (currentUser != null && currentUser.hasUserRight(UserRight.IMMUNIZATION_CREATE)) {
			final ExpandableButton createButton =
				new ExpandableButton(Captions.immunizationNewImmunization).expand(e -> ControllerProvider.getImmunizationController().create());
			addHeaderComponent(createButton);
		}
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}
		updateFilterComponents();
	}

	private void updateFilterComponents() {
		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

		/*
		 * if (relevanceStatusFilter != null) {
		 * relevanceStatusFilter.setValue(criteria.getRelevanceStatus());
		 * }
		 */

		filterForm.setValue(criteria);

		applyingCriteria = false;
	}

	private VerticalLayout createFilterBar() {
		VerticalLayout filterLayout = new VerticalLayout();
		filterLayout.setSpacing(false);
		filterLayout.setMargin(false);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		filterForm = new ImmunizationFilterForm();
		filterForm.addValueChangeListener(e -> {
			if (!filterForm.hasFilter()) {
				navigateTo(null);
			}
		});

		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(ImmunizationsView.class).remove(ImmunizationCriteria.class);
			navigateTo(null, true);
		});

		filterForm.addApplyHandler(clickEvent -> ((ImmunizationGrid) grid).reload());
		filterLayout.addComponent(filterForm);

		return filterLayout;
	}

	private HorizontalLayout createStatusFilterBar() {
		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setMargin(false);
		statusFilterLayout.setWidth(100, Unit.PERCENTAGE);
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);

		// Show active/archived/all dropdown
		/*
		 * if (Objects.nonNull(UserProvider.getCurrent()) && UserProvider.getCurrent().hasUserRight(UserRight.TRAVEL_ENTRY_VIEW)) {
		 * int daysAfterTravelEntryGetsArchived = FacadeProvider.getConfigFacade().getDaysAfterTravelEntryGetsArchived();
		 * if (daysAfterTravelEntryGetsArchived > 0) {
		 * relevanceStatusInfoLabel = new Label(
		 * VaadinIcons.INFO_CIRCLE.getHtml() + " "
		 * + String.format(I18nProperties.getString(Strings.infoArchivedTravelEntries), daysAfterTravelEntryGetsArchived),
		 * ContentMode.HTML);
		 * relevanceStatusInfoLabel.setVisible(false);
		 * relevanceStatusInfoLabel.addStyleName(CssStyles.LABEL_VERTICAL_ALIGN_SUPER);
		 * actionButtonsLayout.addComponent(relevanceStatusInfoLabel);
		 * actionButtonsLayout.setComponentAlignment(relevanceStatusInfoLabel, Alignment.MIDDLE_RIGHT);
		 * }
		 * relevanceStatusFilter = ComboBoxHelper.createComboBoxV7();
		 * relevanceStatusFilter.setId("relevanceStatus");
		 * relevanceStatusFilter.setWidth(140, Unit.PIXELS);
		 * relevanceStatusFilter.setNullSelectionAllowed(false);
		 * relevanceStatusFilter.addItems((Object[]) EntityRelevanceStatus.values());
		 * relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE,
		 * I18nProperties.getCaption(Captions.travelEntryActiveTravelEntries));
		 * relevanceStatusFilter
		 * .setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(Captions.travelEntryArchivedTravelEntries));
		 * relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ALL, I18nProperties.getCaption(Captions.travelEntryAllTravelEntries));
		 * relevanceStatusFilter.addValueChangeListener(e -> {
		 * relevanceStatusInfoLabel.setVisible(EntityRelevanceStatus.ARCHIVED.equals(e.getProperty().getValue()));
		 * criteria.relevanceStatus((EntityRelevanceStatus) e.getProperty().getValue());
		 * navigateTo(criteria);
		 * });
		 * actionButtonsLayout.addComponent(relevanceStatusFilter);
		 * }
		 * statusFilterLayout.addComponent(actionButtonsLayout);
		 * statusFilterLayout.setComponentAlignment(actionButtonsLayout, Alignment.TOP_RIGHT);
		 * statusFilterLayout.setExpandRatio(actionButtonsLayout, 1);
		 */

		return statusFilterLayout;
	}
}
