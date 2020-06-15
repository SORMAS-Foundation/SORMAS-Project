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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.user;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link UserController} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class UsersView extends AbstractView {

	private static final long serialVersionUID = -3533557348144005469L;

	public static final String VIEW_NAME = "users";
	public static final String ACTIVE_FILTER = I18nProperties.getString(Strings.active);
	public static final String INACTIVE_FILTER = I18nProperties.getString(Strings.inactive);

	private UserCriteria criteria;

	private UserGrid grid;
	private Button createButton;

	private VerticalLayout gridLayout;

	// Filters
	private ComboBox activeFilter;
	private ComboBox userRolesFilter;
	private ComboBox regionFilter;
	private ComboBox districtFilter;
	private TextField searchField;

	private Label totalLabelValue;

	public UsersView() {
		super(VIEW_NAME);

		criteria = ViewModelProviders.of(UsersView.class).get(UserCriteria.class);

		grid = new UserGrid();
		grid.setCriteria(criteria);
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(createRowCountLayout());
		gridLayout.addComponent(grid);

		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");

		addComponent(gridLayout);

		if (UserProvider.getCurrent().hasUserRight(UserRight.USER_CREATE)) {
			createButton = ButtonHelper.createIconButton(
				Captions.userNewUser,
				VaadinIcons.PLUS_CIRCLE,
				e -> ControllerProvider.getUserController().create(),
				ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(createButton);
		}
	}

	public HorizontalLayout createFilterBar() {

		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();
		filterLayout.addStyleName(CssStyles.VSPACE_3);

		activeFilter = new ComboBox();
		activeFilter.setId(UserDto.ACTIVE);
		activeFilter.setWidth(200, Unit.PIXELS);
		activeFilter.setInputPrompt(I18nProperties.getPrefixCaption(UserDto.I18N_PREFIX, UserDto.ACTIVE));
		activeFilter.addItems(ACTIVE_FILTER, INACTIVE_FILTER);
		activeFilter.addValueChangeListener(e -> {
			criteria.active(
				ACTIVE_FILTER.equals(e.getProperty().getValue())
					? Boolean.TRUE
					: INACTIVE_FILTER.equals(e.getProperty().getValue()) ? Boolean.FALSE : null);
			navigateTo(criteria);
		});
		filterLayout.addComponent(activeFilter);

		userRolesFilter = new ComboBox();
		userRolesFilter.setId(UserDto.USER_ROLES);
		userRolesFilter.setWidth(200, Unit.PIXELS);
		userRolesFilter.setInputPrompt(I18nProperties.getPrefixCaption(UserDto.I18N_PREFIX, UserDto.USER_ROLES));
		userRolesFilter.addItems(UserRole.getAssignableRoles(UserProvider.getCurrent().getUserRoles()));
		userRolesFilter.addValueChangeListener(e -> {
			criteria.userRole((UserRole) e.getProperty().getValue());
			navigateTo(criteria);
		});
		filterLayout.addComponent(userRolesFilter);

		UserDto user = UserProvider.getCurrent().getUser();

		regionFilter = new ComboBox();
		regionFilter.setId(CaseDataDto.REGION);

		if (user.getRegion() == null) {
			regionFilter.setWidth(140, Unit.PIXELS);
			regionFilter.setInputPrompt(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REGION));
			regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
			regionFilter.addValueChangeListener(e -> {
				RegionReferenceDto region = (RegionReferenceDto) e.getProperty().getValue();

				if (!DataHelper.equal(region, criteria.getRegion())) {
					criteria.district(null);
				}

				criteria.region(region);
				navigateTo(criteria);
			});
			filterLayout.addComponent(regionFilter);
		}

		districtFilter = new ComboBox();
		districtFilter.setId(CaseDataDto.DISTRICT);
		districtFilter.setWidth(140, Unit.PIXELS);
		districtFilter.setInputPrompt(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISTRICT));
		districtFilter.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));
		districtFilter.addValueChangeListener(e -> {
			DistrictReferenceDto district = (DistrictReferenceDto) e.getProperty().getValue();
			criteria.district(district);
			navigateTo(criteria);
		});
		filterLayout.addComponent(districtFilter);

		searchField = new TextField();
		searchField.setId("search");
		searchField.setWidth(200, Unit.PIXELS);
		searchField.setNullRepresentation("");
		searchField.setInputPrompt(I18nProperties.getString(Strings.promptUserSearch));
		searchField.setImmediate(true);
		searchField.addTextChangeListener(e -> {
			criteria.freeText(e.getText());
			grid.reload();
			totalLabelValue.setValue(String.valueOf(grid.getItemCount()));
		});
		filterLayout.addComponent(searchField);

		return filterLayout;
	}

	public HorizontalLayout createRowCountLayout() {
		HorizontalLayout totalLayout = new HorizontalLayout();
		totalLayout.setMargin(false);
		totalLayout.addStyleName(CssStyles.VSPACE_4);
		totalLayout.setSpacing(true);
		totalLayout.setWidth(100, Unit.PERCENTAGE);

		Label labelTotal = new Label(I18nProperties.getString(Strings.labelNumberOfUsers)+":");
		labelTotal.addStyleNames(CssStyles.LABEL_BOLD, CssStyles.VSPACE_TOP_NONE);
		totalLayout.addComponent(labelTotal);
		totalLayout.setExpandRatio(labelTotal, 1);
		totalLayout.setComponentAlignment(labelTotal, Alignment.MIDDLE_RIGHT);

		totalLabelValue = new Label(String.valueOf(grid.getItemCount()));
		totalLabelValue.addStyleNames(CssStyles.LABEL_BOLD, CssStyles.VSPACE_TOP_NONE);
		totalLayout.addComponent(totalLabelValue);
		totalLayout.setComponentAlignment(totalLabelValue, Alignment.MIDDLE_RIGHT);

		return totalLayout;
	}

	@Override
	public void enter(ViewChangeEvent event) {

		if (event != null) {
			String params = event.getParameters().trim();
			if (params.startsWith("?")) {
				params = params.substring(1);
				criteria.fromUrlParams(params);
			}
			updateFilterComponents();
		}
		grid.reload();
	}

	public void updateFilterComponents() {

		applyingCriteria = true;
		UserDto user = UserProvider.getCurrent().getUser();

		activeFilter.setValue(criteria.getActive() == null ? null : criteria.getActive() ? ACTIVE_FILTER : INACTIVE_FILTER);
		userRolesFilter.setValue(criteria.getUserRole());
		regionFilter.setValue(criteria.getRegion());

		if (user.getRegion() != null && user.getDistrict() == null) {
			districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(user.getRegion().getUuid()));
			districtFilter.setEnabled(true);
		} else if (criteria.getRegion() != null) {
			districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(criteria.getRegion().getUuid()));
			districtFilter.setEnabled(true);
		} else {
			districtFilter.setEnabled(false);
		}

		districtFilter.setValue(criteria.getDistrict());
		searchField.setValue(criteria.getFreeText());

		applyingCriteria = false;
	}
}
