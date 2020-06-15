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
package de.symeda.sormas.ui.clinicalcourse;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.shared.ui.grid.HeightMode;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.caze.AbstractCaseView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.MenuBarHelper;

@SuppressWarnings("serial")
public class ClinicalCourseView extends AbstractCaseView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/clinicalcourse";

	private ClinicalVisitCriteria clinicalVisitCriteria;
	private ClinicalVisitGrid clinicalVisitGrid;

	public ClinicalCourseView() {

		super(VIEW_NAME, true);

		clinicalVisitCriteria = ViewModelProviders.of(ClinicalCourseView.class).get(ClinicalVisitCriteria.class);
	}

	private VerticalLayout createClinicalVisitsHeader() {

		VerticalLayout clinicalVisitsHeader = new VerticalLayout();
		clinicalVisitsHeader.setMargin(false);
		clinicalVisitsHeader.setSpacing(false);
		clinicalVisitsHeader.setWidth(100, Unit.PERCENTAGE);

		HorizontalLayout headlineRow = new HorizontalLayout();
		headlineRow.setMargin(false);
		headlineRow.setSpacing(true);
		headlineRow.setWidth(100, Unit.PERCENTAGE);
		{
			Label clinicalVisitsLabel = new Label(I18nProperties.getString(Strings.entityClinicalVisits));
			CssStyles.style(clinicalVisitsLabel, CssStyles.H3);
			headlineRow.addComponent(clinicalVisitsLabel);
			headlineRow.setExpandRatio(clinicalVisitsLabel, 1);

			// Bulk operations
			if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
				MenuBar bulkOperationsDropdown = MenuBarHelper.createDropDown(
					Captions.bulkActions,
					new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.bulkDelete), VaadinIcons.TRASH, selectedItem -> {
						ControllerProvider.getClinicalCourseController()
							.deleteAllSelectedClinicalVisits(clinicalVisitGrid.getSelectedRows(), new Runnable() {

								public void run() {
									clinicalVisitGrid.reload();
								}
							});
					}));

				headlineRow.addComponent(bulkOperationsDropdown);
				headlineRow.setComponentAlignment(bulkOperationsDropdown, Alignment.MIDDLE_RIGHT);
			}

			Button newClinicalVisitButton = ButtonHelper.createButton(Captions.clinicalVisitNewClinicalVisit, e -> {
				ControllerProvider.getClinicalCourseController()
					.openClinicalVisitCreateForm(clinicalVisitCriteria.getClinicalCourse(), getCaseRef().getUuid(), this::reloadClinicalVisitGrid);
			}, ValoTheme.BUTTON_PRIMARY);

			headlineRow.addComponent(newClinicalVisitButton);

			headlineRow.setComponentAlignment(newClinicalVisitButton, Alignment.MIDDLE_RIGHT);
		}
		clinicalVisitsHeader.addComponent(headlineRow);

		return clinicalVisitsHeader;
	}

	private void update() {

		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(getCaseRef().getUuid());
		clinicalVisitCriteria.clinicalCourse(caze.getClinicalCourse().toReference());
	}

	public void reloadClinicalVisitGrid() {

		clinicalVisitGrid.reload();
		clinicalVisitGrid.setHeightByRows(Math.max(1, Math.min(clinicalVisitGrid.getContainer().size(), 10)));
	}

	@Override
	protected void initView(String params) {

		// TODO: Remove this once a proper ViewModel system has been introduced
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(getCaseRef().getUuid());
		if (caze.getClinicalCourse() == null) {
			ClinicalCourseDto clinicalCourse = ClinicalCourseDto.build();
			caze.setClinicalCourse(clinicalCourse);
			caze = FacadeProvider.getCaseFacade().saveCase(caze);
		}

		VerticalLayout container = new VerticalLayout();
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);

		container.addComponent(createClinicalVisitsHeader());

		clinicalVisitGrid = new ClinicalVisitGrid(getCaseRef(), isCaseEditAllowed());
		clinicalVisitGrid.setCriteria(clinicalVisitCriteria);
		clinicalVisitGrid.setHeightMode(HeightMode.ROW);
		CssStyles.style(clinicalVisitGrid, CssStyles.VSPACE_3);
		container.addComponent(clinicalVisitGrid);

		CommitDiscardWrapperComponent<ClinicalCourseForm> clinicalCourseComponent =
			ControllerProvider.getCaseController().getClinicalCourseComponent(getCaseRef().getUuid(), isCaseEditAllowed());
		clinicalCourseComponent.setMargin(false);
		container.addComponent(clinicalCourseComponent);

		setSubComponent(container);

		update();
		reloadClinicalVisitGrid();
	}
}
