package de.symeda.sormas.ui.person;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.FilteredGrid;

public class PersonsView extends AbstractView {

	public static final String VIEW_NAME = "persons";

	private final PersonCriteria criteria;
	private final FilteredGrid<?, PersonCriteria> grid;
	private PersonFilterForm filterForm;

	public PersonsView() {
		super(VIEW_NAME);

		criteria = ViewModelProviders.of(PersonsView.class).get(PersonCriteria.class);
		grid = new PersonGrid(criteria);
		final VerticalLayout gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
//		gridLayout.addComponent(createRelatedToFilterBar());
		gridLayout.addComponent(grid);

		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");

//		grid.getDataProvider().addDataProviderListener(e -> updateStatusButtons());

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

//		updateStatusButtons();

		filterForm.setValue(criteria);

		applyingCriteria = false;
	}

	public VerticalLayout createFilterBar() {
		VerticalLayout filterLayout = new VerticalLayout();
		filterLayout.setSpacing(false);
		filterLayout.setMargin(false);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		filterForm = new PersonFilterForm();
		filterForm.addValueChangeListener(e -> {
			if (!filterForm.hasFilter()) {
				navigateTo(null);
			}
		});
		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(PersonsView.class).remove(PersonCriteria.class);
			navigateTo(null, true);
		});
		filterForm.addApplyHandler(e -> ((PersonGrid) grid).reload());
		filterLayout.addComponent(filterForm);

		return filterLayout;
	}
}
