package de.symeda.sormas.ui.person;

import java.util.HashMap;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonAssociation;
import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class PersonsView extends AbstractView {

	public static final String VIEW_NAME = "persons";

	private final PersonCriteria criteria;
	private final FilteredGrid<?, PersonCriteria> grid;
	private HashMap<Button, String> associationButtons;
	private Button activeAssociationButton;
	private PersonFilterForm filterForm;

	public PersonsView() {
		super(VIEW_NAME);

		criteria = ViewModelProviders.of(PersonsView.class).get(PersonCriteria.class);
		grid = new PersonGrid(criteria);
		final VerticalLayout gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		final HorizontalLayout associationFilterBar = createAssociationFilterBar();
		gridLayout.addComponent(associationFilterBar);
		gridLayout.setComponentAlignment(associationFilterBar, Alignment.MIDDLE_RIGHT);
		gridLayout.addComponent(grid);

		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");

		grid.getDataProvider().addDataProviderListener(e -> updateAssociationButtons());

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

		updateAssociationButtons();

		filterForm.setValue(criteria);

		applyingCriteria = false;
	}

	private void updateAssociationButtons() {

		associationButtons.keySet().forEach(b -> {
			CssStyles.style(b, CssStyles.BUTTON_FILTER_LIGHT);
			b.setCaption(associationButtons.get(b));
			if (b.getData() == criteria.getPersonAssociation()) {
				activeAssociationButton = b;
			}
		});
		CssStyles.removeStyles(activeAssociationButton, CssStyles.BUTTON_FILTER_LIGHT);
		if (activeAssociationButton != null) {
			activeAssociationButton.setCaption(
				associationButtons.get(activeAssociationButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getItemCount())));
		}
	}

	public VerticalLayout createFilterBar() {
		VerticalLayout filterLayout = new VerticalLayout();
		filterLayout.setSpacing(false);
		filterLayout.setMargin(false);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		final UserDto user = UserProvider.getCurrent().getUser();
		criteria.setRegion(user.getRegion());
		criteria.setDistrict(user.getDistrict());
		criteria.setCommunity(user.getCommunity());
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

	public HorizontalLayout createAssociationFilterBar() {
		HorizontalLayout associationFilterLayout = new HorizontalLayout();
		associationFilterLayout.setSpacing(true);
		associationFilterLayout.setMargin(false);
		associationFilterLayout.setWidth(100, Unit.PERCENTAGE);
		associationFilterLayout.addStyleName(CssStyles.VSPACE_3);

		associationButtons = new HashMap<>();

		Button statusAll = ButtonHelper.createButton(Captions.all, e -> {
			criteria.personAssociation(null);
			navigateTo(criteria);
		}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
		statusAll.setCaptionAsHtml(true);

		associationFilterLayout.addComponent(statusAll);
		associationFilterLayout.setComponentAlignment(statusAll, Alignment.MIDDLE_LEFT);
		associationButtons.put(statusAll, I18nProperties.getCaption(Captions.all));
		activeAssociationButton = statusAll;

		for (PersonAssociation association : PersonAssociation.values()) {
			Button associationButton = ButtonHelper.createButton(association.toString(), e -> {
				criteria.personAssociation(association);
				navigateTo(criteria);
			}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);
			associationButton.setData(association);
			associationButton.setCaptionAsHtml(true);

			associationFilterLayout.addComponent(associationButton);
			associationFilterLayout.setComponentAlignment(associationButton, Alignment.MIDDLE_LEFT);
			associationButtons.put(associationButton, association.toString());
		}

		Label emptyLabel = new Label("");
		associationFilterLayout.addComponent(emptyLabel);
		associationFilterLayout.setComponentAlignment(emptyLabel, Alignment.MIDDLE_RIGHT);
		associationFilterLayout.setExpandRatio(emptyLabel, 1);

		return associationFilterLayout;
	}
}
