package de.symeda.sormas.ui.samples;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class SampleListComponent extends VerticalLayout {

	public static final String LGA = "lga";
	public static final String SEARCH_FIELD = "searchField";

	private SampleGrid grid;

	private VerticalLayout gridLayout;

	public SampleListComponent() {
		setSizeFull();

		grid = new SampleGrid();

		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createTopBar());
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(grid);

		styleGridLayout(gridLayout);
		gridLayout.setMargin(true);

		addComponent(gridLayout);
	}

	public SampleListComponent(CaseReferenceDto caseRef) {
		setSizeFull();
		setMargin(true);

		grid = new SampleGrid(caseRef);
		grid.setHeightMode(HeightMode.ROW);

		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createTopBarForCase(caseRef));
		gridLayout.addComponent(grid);

		gridLayout.setMargin(new MarginInfo(true, false, false, false));
		styleGridLayout(gridLayout);

		addComponent(gridLayout);
	}

	public HorizontalLayout createTopBar() {
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		topLayout.setSizeUndefined();
		topLayout.addStyleName(CssStyles.VSPACE_3);

		HorizontalLayout buttonFilterLayout = new HorizontalLayout();
		{
			Button statusAll = new Button("all", e -> grid.clearShipmentFilters());
			statusAll.setStyleName(ValoTheme.BUTTON_LINK);
			buttonFilterLayout.addComponent(statusAll);
			
			Button notShippedButton = new Button("not shipped", e -> grid.filterForNotShipped());
			notShippedButton.setStyleName(ValoTheme.BUTTON_LINK);
			buttonFilterLayout.addComponent(notShippedButton);
			Button shippedButton = new Button("shipped", e -> grid.filterForShipped());
			shippedButton.setStyleName(ValoTheme.BUTTON_LINK);
			buttonFilterLayout.addComponent(shippedButton);
			Button receivedButton = new Button("received", e -> grid.filterForReceived());
			receivedButton.setStyleName(ValoTheme.BUTTON_LINK);
			buttonFilterLayout.addComponent(receivedButton);
			Button referredButton = new Button("referred to other lab", e -> grid.filterForReferred());
			referredButton.setStyleName(ValoTheme.BUTTON_LINK);
			buttonFilterLayout.addComponent(referredButton);
		}

		topLayout.addComponent(buttonFilterLayout);

		return topLayout;
	}

	public HorizontalLayout createTopBarForCase(CaseReferenceDto caseRef) {
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		topLayout.setWidth(100, Unit.PERCENTAGE);
		topLayout.addStyleName(CssStyles.VSPACE_3);

		HorizontalLayout buttonFilterLayout = new HorizontalLayout();
		{
			Button statusAll = new Button("all", e -> grid.clearShipmentFilters());
			statusAll.setStyleName(ValoTheme.BUTTON_LINK);
			buttonFilterLayout.addComponent(statusAll);

			Button notShippedButton = new Button("not shipped", e -> grid.filterForNotShipped());
			notShippedButton.setStyleName(ValoTheme.BUTTON_LINK);
			buttonFilterLayout.addComponent(notShippedButton);
			Button shippedButton = new Button("shipped", e -> grid.filterForShipped());
			shippedButton.setStyleName(ValoTheme.BUTTON_LINK);
			buttonFilterLayout.addComponent(shippedButton);
			Button receivedButton = new Button("received", e -> grid.filterForReceived());
			receivedButton.setStyleName(ValoTheme.BUTTON_LINK);
			buttonFilterLayout.addComponent(receivedButton);
			Button referredButton = new Button("referred to other lab", e -> grid.filterForReferred());
			referredButton.setStyleName(ValoTheme.BUTTON_LINK);
			buttonFilterLayout.addComponent(referredButton);
		}
		topLayout.addComponent(buttonFilterLayout);

		Button createButton = new Button("New sample");
		createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		createButton.setIcon(FontAwesome.PLUS_CIRCLE);
		createButton.addClickListener(e -> ControllerProvider.getSampleController().create(caseRef, grid));
		topLayout.addComponent(createButton);
		topLayout.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
		topLayout.setExpandRatio(createButton, 1);

		return topLayout;
	}

	public HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();
		filterLayout.addStyleName(CssStyles.VSPACE_3);

        UserDto user = LoginHelper.getCurrentUser();

        ComboBox regionFilter = new ComboBox();
        if (user.getRegion() == null) {
            regionFilter.setWidth(140, Unit.PIXELS);
            regionFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REGION));
            regionFilter.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
            regionFilter.addValueChangeListener(e -> {
            	RegionReferenceDto region = (RegionReferenceDto)e.getProperty().getValue();
            	grid.setRegionFilter(region);
            });
            filterLayout.addComponent(regionFilter);
        }

        ComboBox districtFilter = new ComboBox();
        districtFilter.setWidth(140, Unit.PIXELS);
        districtFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISTRICT));
        districtFilter.setDescription("Select a district in the state");
        districtFilter.addValueChangeListener(e->grid.setDistrictFilter(((DistrictReferenceDto)e.getProperty().getValue())));

        if (user.getRegion() != null) {
            districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(user.getRegion().getUuid()));
            districtFilter.setEnabled(true);
        } else {
            regionFilter.addValueChangeListener(e -> {
            	RegionReferenceDto region = (RegionReferenceDto)e.getProperty().getValue();
            	districtFilter.removeAllItems();
            	if (region != null) {
            		districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(region.getUuid()));
                	districtFilter.setEnabled(true);
            	} else {
                	districtFilter.setEnabled(false);
            	}
            });
            districtFilter.setEnabled(false);
        }
        filterLayout.addComponent(districtFilter);

		ComboBox labFilter = new ComboBox();
		labFilter.setWidth(200, Unit.PIXELS);
		labFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(SampleIndexDto.I18N_PREFIX, SampleIndexDto.LAB));
		labFilter.addItems(FacadeProvider.getFacilityFacade().getAllLaboratories());
		labFilter.addValueChangeListener(e->grid.setLabFilter(((FacilityReferenceDto)e.getProperty().getValue())));
		filterLayout.addComponent(labFilter);

		TextField searchField = new TextField();
		searchField.setWidth(200, Unit.PIXELS);
		searchField.setInputPrompt(I18nProperties.getPrefixFieldCaption(SampleIndexDto.I18N_PREFIX, SEARCH_FIELD));
		searchField.addTextChangeListener(e->grid.filterByText(e.getText()));
		filterLayout.addComponent(searchField);

		return filterLayout;
	}

	public void reload() {
		grid.reload();
	}

	private void styleGridLayout(VerticalLayout gridLayout) {
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");
	}

}
