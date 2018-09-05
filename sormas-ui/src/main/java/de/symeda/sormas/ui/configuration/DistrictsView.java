package de.symeda.sormas.ui.configuration;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class DistrictsView extends AbstractConfigurationView {

	private static final long serialVersionUID = -3487830069266335042L;

	public static final String SEARCH = "search";

	public static final String VIEW_NAME = "configuration/districts";
	
	private HorizontalLayout filterLayout;
	private VerticalLayout gridLayout;
	private DistrictsGrid grid;
	protected Button createButton;

	public DistrictsView() {
		super(VIEW_NAME);
		grid = new DistrictsGrid();
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setSizeFull();
		gridLayout.setStyleName("crud-main-layout");
		grid.reload();

		if (LoginHelper.hasUserRight(UserRight.INFRASTRUCTURE_CREATE)) {
			createButton = new Button("new district");
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(FontAwesome.PLUS_CIRCLE);
			createButton.addClickListener(
					e -> ControllerProvider.getInfrastructureController().createDistrict("Create new district"));
			addHeaderComponent(createButton);
		}
		
		addComponent(gridLayout);
	}

	private HorizontalLayout createFilterBar() {
		filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();

		TextField searchField = new TextField();
		searchField.setWidth(200, Unit.PIXELS);
		searchField.setInputPrompt(I18nProperties.getText(SEARCH));
		searchField.addTextChangeListener(e -> {
			grid.filterByText(e.getText());
		});
		CssStyles.style(searchField, CssStyles.FORCE_CAPTION);
		filterLayout.addComponent(searchField);

		ComboBox regionFilter = new ComboBox();
		regionFilter.setWidth(140, Unit.PIXELS);
		regionFilter.setCaption("Region");
		regionFilter.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
		regionFilter.addValueChangeListener(e -> {
			RegionReferenceDto region = (RegionReferenceDto) e.getProperty().getValue();
			grid.setRegionFilter(region);

		});
		filterLayout.addComponent(regionFilter);

		return filterLayout;
	}	
	
}
