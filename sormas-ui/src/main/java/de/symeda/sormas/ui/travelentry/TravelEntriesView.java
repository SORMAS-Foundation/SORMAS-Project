package de.symeda.sormas.ui.travelentry;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.FilteredGrid;

public class TravelEntriesView extends AbstractView {

	public static final String VIEW_NAME = "entries";

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
