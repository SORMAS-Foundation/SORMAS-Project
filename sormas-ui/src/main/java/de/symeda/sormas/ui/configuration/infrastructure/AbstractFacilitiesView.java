/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.configuration.infrastructure;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.CurrentUser;
import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;

public abstract class AbstractFacilitiesView extends AbstractConfigurationView {

	private static final long serialVersionUID = -2015225571046243640L;

	public static final String SEARCH = "search";

	//	private HorizontalLayout headerLayout;
	private HorizontalLayout filterLayout;
	private VerticalLayout gridLayout;
	protected FacilitiesGrid grid;
	protected Button createButton;
	protected Button exportButton;

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
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setSizeFull();
		gridLayout.setStyleName("crud-main-layout");
		grid.reload();

		exportButton = new Button("Export");
		exportButton.setDescription("Export the columns and rows that are shown in the table below.");
		exportButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		exportButton.setIcon(FontAwesome.TABLE);
		addHeaderComponent(exportButton);

		if (CurrentUser.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_CREATE)) {
			createButton = new Button();
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(FontAwesome.PLUS_CIRCLE);
			addHeaderComponent(createButton);
		}

		addComponent(gridLayout);
	}

	//	TODO additional filter bar (active, archived and other)
	//	private HorizontalLayout createHeaderBar() {
	//		headerLayout = new HorizontalLayout();
	//		headerLayout.setSpacing(true);
	//		headerLayout.setWidth(100, Unit.PERCENTAGE);
	//
	//		return headerLayout;
	//	}

	private HorizontalLayout createFilterBar() {
		filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);

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
		regionFilter.setCaption(I18nProperties.getPrefixFieldCaption(FacilityDto.I18N_PREFIX, FacilityDto.REGION));
		regionFilter.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
		regionFilter.addValueChangeListener(e -> {
			RegionReferenceDto region = (RegionReferenceDto) e.getProperty().getValue();
			grid.setRegionFilter(region);
			FieldHelper.updateItems(districtFilter,
					region != null ? FacadeProvider.getDistrictFacade().getAllByRegion(region.getUuid()) : null);

		});
		filterLayout.addComponent(regionFilter);

		districtFilter = new ComboBox();
		districtFilter.setWidth(140, Unit.PIXELS);
		districtFilter.setCaption(I18nProperties.getPrefixFieldCaption(FacilityDto.I18N_PREFIX, FacilityDto.DISTRICT));
		districtFilter.addValueChangeListener(e -> {
			DistrictReferenceDto district = (DistrictReferenceDto) e.getProperty().getValue();
			grid.setDistrictFilter(district);
			FieldHelper.updateItems(communityFilter,
					district != null ? FacadeProvider.getCommunityFacade().getAllByDistrict(district.getUuid()) : null);
		});
		filterLayout.addComponent(districtFilter);

		communityFilter = new ComboBox();
		communityFilter.setWidth(140, Unit.PIXELS);
		communityFilter.setCaption(I18nProperties.getPrefixFieldCaption(FacilityDto.I18N_PREFIX, FacilityDto.COMMUNITY));
		communityFilter.addValueChangeListener(e -> {
			CommunityReferenceDto community = (CommunityReferenceDto) e.getProperty().getValue();
			grid.setCommunityFilter(community);
		});
		filterLayout.addComponent(communityFilter);

		return filterLayout;
	}

}
