package de.symeda.sormas.ui.user;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRoleCriteria;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ComboBoxHelper;

public class UserRolesView extends AbstractUserView {

	private static final long serialVersionUID = -3533557348112305469L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/userroles";

	private UserRoleCriteria criteria;

	private UserRoleGrid grid;
	private Button btnExport;
	private Button createButton;

	private ComboBox userRolesFilter;
	private ComboBox jurisdictionFilter;

	private VerticalLayout gridLayout;

	public UserRolesView() {
		super(VIEW_NAME);

		criteria = ViewModelProviders.of(UserRolesView.class).get(UserRoleCriteria.class);

		grid = new UserRoleGrid();
		// grid.setCriteria(criteria);
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());

		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");

		addComponent(gridLayout);

		//TODO: check if rights should be added to the buttons
		btnExport = ButtonHelper.createIconButton(Captions.export, VaadinIcons.DOWNLOAD, null, ValoTheme.BUTTON_PRIMARY);
		addHeaderComponent(btnExport);

		createButton = ButtonHelper.createIconButton(
			Captions.userRoleNewUserRole,
			VaadinIcons.PLUS_CIRCLE,
			e -> ControllerProvider.getUserRoleController().create(),
			ValoTheme.BUTTON_PRIMARY);

		addHeaderComponent(createButton);
	}

	public HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();

		jurisdictionFilter = ComboBoxHelper.createComboBoxV7();
		jurisdictionFilter.setId(UserDto.JURISDICTION_LEVEL);
		jurisdictionFilter.setWidth(200, Unit.PIXELS);

		//userRolesFilter.setInputPrompt(I18nProperties.getPrefixCaption(UserDto.I18N_PREFIX, UserDto.JURISDICTION_LEVEL));
		/*
		 * userRolesFilter.addItems(FacadeProvider.getUserRoleFacade().getAllActiveAsReference());
		 * userRolesFilter.addValueChangeListener(e -> {
		 * criteria.userRole((UserRoleReferenceDto) e.getProperty().getValue());
		 * navigateTo(criteria);
		 * });
		 */
		filterLayout.addComponent(jurisdictionFilter);

		return filterLayout;
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		super.enter(event);
	}
}
