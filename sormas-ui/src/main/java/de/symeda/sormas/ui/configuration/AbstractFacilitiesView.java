package de.symeda.sormas.ui.configuration;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;

/**
 * @author Christopher Riedel
 *
 */
public class AbstractFacilitiesView extends AbstractConfigurationView {

	private static final long serialVersionUID = -2015225571046243640L;

	public static final String SEARCH_FIELD = "Search...";

	private HorizontalLayout headerLayout;
	private HorizontalLayout filterayout;
	private VerticalLayout gridLayout;
	private FacilitiesGrid grid;
	protected Button createButton;

	private ComboBox districtFilter;

	private ComboBox communityFilter;

	protected AbstractFacilitiesView(String viewName, boolean showLaboratories) {
		super(viewName);
		grid = new FacilitiesGrid();
		gridLayout = new VerticalLayout();
//		gridLayout.addComponent(createHeaderBar());
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(grid);
		grid.setTypeFilter(showLaboratories);
		grid.reload();
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setStyleName("crud-main-layout");

		if (LoginHelper.hasUserRight(UserRight.FACILITIES_CREATE)) {
			createButton = new Button();
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(FontAwesome.PLUS_CIRCLE);
			addHeaderComponent(createButton);
		}

		addComponent(gridLayout);
	}

//	TODO additional filterbar (active, archieved and other)
	@SuppressWarnings("unused")
	private HorizontalLayout createHeaderBar() {
		headerLayout = new HorizontalLayout();
		headerLayout.setSpacing(true);
		headerLayout.setWidth(100, Unit.PERCENTAGE);

		return headerLayout;
	}

	private HorizontalLayout createFilterBar() {

		filterayout = new HorizontalLayout();
		filterayout.setSpacing(true);

		TextField searchField = new TextField();
		searchField.setWidth(200, Unit.PIXELS);

		searchField.setInputPrompt(SEARCH_FIELD);
		searchField.addTextChangeListener(e -> {
			grid.filterByText(e.getText());
		});
		CssStyles.style(searchField, CssStyles.FORCE_CAPTION);
		filterayout.addComponent(searchField);

		ComboBox regionFilter = new ComboBox();
		regionFilter.setWidth(140, Unit.PIXELS);
		regionFilter.setCaption("Region");
		regionFilter.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
		regionFilter.addValueChangeListener(e -> {
			RegionReferenceDto region = (RegionReferenceDto) e.getProperty().getValue();
			grid.setRegionFilter(region);
			FieldHelper.updateItems(districtFilter,
					region != null ? FacadeProvider.getDistrictFacade().getAllByRegion(region.getUuid()) : null);

		});
		filterayout.addComponent(regionFilter);

		districtFilter = new ComboBox();
		districtFilter.setWidth(140, Unit.PIXELS);
		districtFilter.setCaption("District");
		districtFilter.addValueChangeListener(e -> {
			RegionReferenceDto region = (RegionReferenceDto) regionFilter.getValue();
			DistrictReferenceDto district = (DistrictReferenceDto) e.getProperty().getValue();
			grid.setDistrictFilter(region, district);
			FieldHelper.updateItems(communityFilter,
					district != null ? FacadeProvider.getCommunityFacade().getAllByDistrict(district.getUuid()) : null);
		});
		filterayout.addComponent(districtFilter);

		communityFilter = new ComboBox();
		communityFilter.setWidth(140, Unit.PIXELS);
		communityFilter.setCaption("Community");
		communityFilter.addValueChangeListener(e -> {
			RegionReferenceDto region = (RegionReferenceDto) regionFilter.getValue();
			DistrictReferenceDto district = (DistrictReferenceDto) districtFilter.getValue();
			CommunityReferenceDto community = (CommunityReferenceDto) e.getProperty().getValue();
			grid.setCommunityFilter(region, district, community);
		});
		filterayout.addComponent(communityFilter);

		return filterayout;
	}

}
