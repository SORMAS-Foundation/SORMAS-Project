package de.symeda.sormas.ui.travelentry;

import java.util.Objects;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.travelentry.importer.TravelEntryImportLayout;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.expandablebutton.ExpandableButton;

public class TravelEntriesView extends AbstractView {

	public static final String VIEW_NAME = "travelEntries";

	private final TravelEntryCriteria criteria;
	private final FilteredGrid<?, TravelEntryCriteria> grid;
	private TravelEntryFilterForm filterForm;

	// Filters
	private Label relevanceStatusInfoLabel;
	private ComboBox relevanceStatusFilter;

	public TravelEntriesView() {
		super(VIEW_NAME);

		criteria = ViewModelProviders.of(TravelEntriesView.class).get(TravelEntryCriteria.class);
		if (criteria.getRelevanceStatus() == null) {
			criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		}

		grid = new TravelEntryGrid(criteria);

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

		if (UserProvider.getCurrent().hasUserRight(UserRight.TRAVEL_ENTRY_CREATE)) {
			addHeaderComponent(ButtonHelper.createIconButton(I18nProperties.getCaption(Captions.actionImport), VaadinIcons.UPLOAD, e -> {
				Window popupWindow = VaadinUiUtil.showPopupWindow(new TravelEntryImportLayout());
				popupWindow.setCaption(I18nProperties.getString(Strings.headingImportTravelEntries));
				popupWindow.addCloseListener(c -> ((TravelEntryGrid) grid).reload());
			}));

			long countTravelEntries = FacadeProvider.getTravelEntryFacade().count(new TravelEntryCriteria(), true);
			if (countTravelEntries > 0) {
				final ExpandableButton createButton =
					new ExpandableButton(Captions.travelEntryNewTravelEntry).expand(e -> ControllerProvider.getTravelEntryController().create(null));
				addHeaderComponent(createButton);
			}
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

	public void updateFilterComponents() {
		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

		if (relevanceStatusFilter != null) {
			relevanceStatusFilter.setValue(criteria.getRelevanceStatus());
		}

		filterForm.setValue(criteria);

		applyingCriteria = false;
	}

	public VerticalLayout createFilterBar() {
		VerticalLayout filterLayout = new VerticalLayout();
		filterLayout.setSpacing(false);
		filterLayout.setMargin(false);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		filterForm = new TravelEntryFilterForm();
		filterForm.addValueChangeListener(e -> {
			if (!filterForm.hasFilter()) {
				navigateTo(null);
			}
		});

		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(TravelEntriesView.class).remove(TravelEntryCriteria.class);
			navigateTo(null, true);
		});

		filterForm.addApplyHandler(clickEvent -> ((TravelEntryGrid) grid).reload());
		filterLayout.addComponent(filterForm);

		return filterLayout;
	}

	public HorizontalLayout createStatusFilterBar() {
		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setMargin(false);
		statusFilterLayout.setWidth(100, Unit.PERCENTAGE);
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);

		// Show active/archived/all dropdown
		if (Objects.nonNull(UserProvider.getCurrent()) && UserProvider.getCurrent().hasUserRight(UserRight.TRAVEL_ENTRY_VIEW)) {
			int daysAfterTravelEntryGetsArchived = FacadeProvider.getConfigFacade().getDaysAfterTravelEntryGetsArchived();
			if (daysAfterTravelEntryGetsArchived > 0) {
				relevanceStatusInfoLabel = new Label(
					VaadinIcons.INFO_CIRCLE.getHtml() + " "
						+ String.format(I18nProperties.getString(Strings.infoArchivedTravelEntries), daysAfterTravelEntryGetsArchived),
					ContentMode.HTML);
				relevanceStatusInfoLabel.setVisible(false);
				relevanceStatusInfoLabel.addStyleName(CssStyles.LABEL_VERTICAL_ALIGN_SUPER);
				actionButtonsLayout.addComponent(relevanceStatusInfoLabel);
				actionButtonsLayout.setComponentAlignment(relevanceStatusInfoLabel, Alignment.MIDDLE_RIGHT);
			}
			relevanceStatusFilter = ComboBoxHelper.createComboBoxV7();
			relevanceStatusFilter.setId("relevanceStatus");
			relevanceStatusFilter.setWidth(140, Unit.PIXELS);
			relevanceStatusFilter.setNullSelectionAllowed(false);
			relevanceStatusFilter.addItems((Object[]) EntityRelevanceStatus.values());
			relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(Captions.travelEntryActiveTravelEntries));
			relevanceStatusFilter
				.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(Captions.travelEntryArchivedTravelEntries));
			relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ALL, I18nProperties.getCaption(Captions.travelEntryAllTravelEntries));
			relevanceStatusFilter.addValueChangeListener(e -> {
				relevanceStatusInfoLabel.setVisible(EntityRelevanceStatus.ARCHIVED.equals(e.getProperty().getValue()));
				criteria.relevanceStatus((EntityRelevanceStatus) e.getProperty().getValue());
				navigateTo(criteria);
			});
			actionButtonsLayout.addComponent(relevanceStatusFilter);
		}
		statusFilterLayout.addComponent(actionButtonsLayout);
		statusFilterLayout.setComponentAlignment(actionButtonsLayout, Alignment.TOP_RIGHT);
		statusFilterLayout.setExpandRatio(actionButtonsLayout, 1);

		return statusFilterLayout;
	}

}
