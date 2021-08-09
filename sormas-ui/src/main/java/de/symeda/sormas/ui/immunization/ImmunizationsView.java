package de.symeda.sormas.ui.immunization;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.immunization.components.filter.FilterFormLayout;
import de.symeda.sormas.ui.immunization.components.grid.ImmunizationGrid;
import de.symeda.sormas.ui.immunization.components.status.StatusBarLayout;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.expandablebutton.ExpandableButton;

public class ImmunizationsView extends AbstractView {

	public static final String VIEW_NAME = "immunizations";

	private final ImmunizationCriteria criteria;

	private final Label noDiseaseInfoLabel;
	private final ImmunizationGrid grid;

	private FilterFormLayout filterFormLayout;
	private StatusBarLayout statusBarLayout;

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

		noDiseaseInfoLabel = new Label(I18nProperties.getString(Strings.infoNoDiseaseSelected));
		noDiseaseInfoLabel.addStyleNames(CssStyles.LAYOUT_MINIMAL, CssStyles.H3, CssStyles.VSPACE_TOP_0, CssStyles.ALIGN_CENTER);
		addComponent(noDiseaseInfoLabel);

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

		Disease disease = criteria.getDisease();
		if (disease == null) {
			statusBarLayout.setVisible(false);
			grid.setVisible(false);
			noDiseaseInfoLabel.setVisible(true);
		} else {
			statusBarLayout.setVisible(true);
			grid.setVisible(true);
			noDiseaseInfoLabel.setVisible(false);

			statusBarLayout.updateActiveBadge(grid.getItemCount());
		}

		applyingCriteria = false;
	}

	private FilterFormLayout createFilterBar() {
		filterFormLayout = new FilterFormLayout();

		filterFormLayout.addResetHandler(clickEvent -> {
			ViewModelProviders.of(ImmunizationsView.class).remove(ImmunizationCriteria.class);
			navigateTo(null, true);
		});

		filterFormLayout.addApplyHandler(clickEvent -> {
			ImmunizationCriteria filterFormValue = filterFormLayout.getValue();
			Disease disease = filterFormValue.getDisease();
			if (disease == null) {
				statusBarLayout.setVisible(false);
				grid.setVisible(false);
				noDiseaseInfoLabel.setVisible(true);
			} else {
				statusBarLayout.setVisible(true);
				grid.setVisible(true);
				noDiseaseInfoLabel.setVisible(false);

				grid.reload();
				statusBarLayout.updateActiveBadge(grid.getItemCount());
			}
		});

		return filterFormLayout;
	}

	private StatusBarLayout createStatusFilterBar() {

		statusBarLayout = new StatusBarLayout();
		statusBarLayout.addItem(Captions.all, e -> navigateTo(criteria));

		return statusBarLayout;
	}
}
