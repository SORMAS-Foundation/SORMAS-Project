package de.symeda.sormas.ui.immunization;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.immunization.components.layout.directory.FilterFormLayout;
import de.symeda.sormas.ui.immunization.components.layout.directory.ImmunizationDataLayout;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.components.expandablebutton.ExpandableButton;

public class ImmunizationsView extends AbstractView {

	public static final String VIEW_NAME = "immunizations";

	private final ImmunizationCriteria criteria;

	private FilterFormLayout filterFormLayout;
	private final ImmunizationDataLayout immunizationDataLayout;

	public ImmunizationsView() {
		super(VIEW_NAME);

		criteria = ViewModelProviders.of(ImmunizationsView.class).get(ImmunizationCriteria.class);
		immunizationDataLayout = new ImmunizationDataLayout(criteria);

		final VerticalLayout gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(immunizationDataLayout);

		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(immunizationDataLayout, 1);
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

		filterFormLayout.setValue(criteria);

		applyingCriteria = false;
	}

	private FilterFormLayout createFilterBar() {
		filterFormLayout = new FilterFormLayout();

		filterFormLayout.addResetHandler(clickEvent -> {
			ViewModelProviders.of(ImmunizationsView.class).remove(ImmunizationCriteria.class);
			navigateTo(null, true);
		});


		filterFormLayout.addApplyHandler(clickEvent -> {
			immunizationDataLayout.refreshGrid();
		});

		return filterFormLayout;
	}
}
