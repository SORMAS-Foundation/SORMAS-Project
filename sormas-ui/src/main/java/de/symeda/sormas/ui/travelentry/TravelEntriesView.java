package de.symeda.sormas.ui.travelentry;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.configuration.TravelEntryImportLayout;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.expandablebutton.ExpandableButton;

public class TravelEntriesView extends AbstractView {

	public static final String VIEW_NAME = "travelEntries";

	private final TravelEntryCriteria criteria;
	private final FilteredGrid<?, TravelEntryCriteria> grid;
	private TravelEntryFilterForm filterForm;

	public TravelEntriesView() {
		super(VIEW_NAME);

		criteria = ViewModelProviders.of(TravelEntriesView.class).get(TravelEntryCriteria.class);
		grid = new TravelEntryGrid(criteria);

		final VerticalLayout gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
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
			}));

			final ExpandableButton createButton =
				new ExpandableButton(Captions.travelEntryNewTravelEntry).expand(e -> ControllerProvider.getTravelEntryController().create());
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

	public void updateFilterComponents() {
		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

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
}
