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
package de.symeda.sormas.ui.therapy;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.therapy.PrescriptionCriteria;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.therapy.TreatmentCriteria;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.therapy.TreatmentType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.caze.AbstractCaseView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.MenuBarHelper;

@SuppressWarnings("serial")
public class TherapyView extends AbstractCaseView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/therapy";

	private PrescriptionCriteria prescriptionCriteria;
	private TreatmentCriteria treatmentCriteria;

	private PrescriptionGrid prescriptionGrid;
	private TreatmentGrid treatmentGrid;

	// Filter
	private ComboBox prescriptionTypeFilter;
	private TextField prescriptionTextFilter;
	private ComboBox treatmentTypeFilter;
	private TextField treatmentTextFilter;

	public TherapyView() {
		super(VIEW_NAME, false);

		prescriptionCriteria = ViewModelProviders.of(TherapyView.class).get(PrescriptionCriteria.class);
		treatmentCriteria = ViewModelProviders.of(TherapyView.class).get(TreatmentCriteria.class);
	}

	private VerticalLayout createPrescriptionsHeader() {

		VerticalLayout prescriptionsHeader = new VerticalLayout();
		prescriptionsHeader.setMargin(false);
		prescriptionsHeader.setSpacing(false);
		prescriptionsHeader.setWidth(100, Unit.PERCENTAGE);

		HorizontalLayout headlineRow = new HorizontalLayout();
		headlineRow.setMargin(false);
		headlineRow.setSpacing(true);
		headlineRow.setWidth(100, Unit.PERCENTAGE);
		{
			Label prescriptionsLabel = new Label(I18nProperties.getString(Strings.entityPrescriptions));
			CssStyles.style(prescriptionsLabel, CssStyles.H3);
			headlineRow.addComponent(prescriptionsLabel);
			headlineRow.setExpandRatio(prescriptionsLabel, 1);

			// Bulk operations
			if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
				MenuBar bulkOperationsDropdown = MenuBarHelper.createDropDown(
					Captions.bulkActions,
					new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.bulkDelete), VaadinIcons.TRASH, selectedItem -> {
						ControllerProvider.getTherapyController().deleteAllSelectedPrescriptions(prescriptionGrid.getSelectedRows(), new Runnable() {

							public void run() {
								reloadPrescriptionGrid();
							}
						});
					}));

				headlineRow.addComponent(bulkOperationsDropdown);
				headlineRow.setComponentAlignment(bulkOperationsDropdown, Alignment.MIDDLE_RIGHT);
			}

			Button newPrescriptionButton = ButtonHelper.createButton(Captions.prescriptionNewPrescription, e -> {
				ControllerProvider.getTherapyController().openPrescriptionCreateForm(prescriptionCriteria.getTherapy(), this::reloadPrescriptionGrid);
			}, ValoTheme.BUTTON_PRIMARY);

			headlineRow.addComponent(newPrescriptionButton);

			headlineRow.setComponentAlignment(newPrescriptionButton, Alignment.MIDDLE_RIGHT);
		}
		prescriptionsHeader.addComponent(headlineRow);

		HorizontalLayout filterRow = new HorizontalLayout();
		filterRow.setMargin(false);
		filterRow.setSpacing(true);
		{
			prescriptionTypeFilter = new ComboBox();
			prescriptionTypeFilter.setWidth(140, Unit.PIXELS);
			prescriptionTypeFilter.setInputPrompt(I18nProperties.getPrefixCaption(PrescriptionDto.I18N_PREFIX, PrescriptionDto.PRESCRIPTION_TYPE));
			prescriptionTypeFilter.addItems((Object[]) TreatmentType.values());
			prescriptionTypeFilter.addValueChangeListener(e -> {
				prescriptionCriteria.prescriptionType(((TreatmentType) e.getProperty().getValue()));
				navigateTo(prescriptionCriteria);
			});
			filterRow.addComponent(prescriptionTypeFilter);

			prescriptionTextFilter = new TextField();
			prescriptionTextFilter.setWidth(300, Unit.PIXELS);
			prescriptionTextFilter.setNullRepresentation("");
			prescriptionTextFilter.setInputPrompt(I18nProperties.getString(Strings.promptPrescriptionTextFilter));
			prescriptionTextFilter.addTextChangeListener(e -> {
				prescriptionCriteria.textFilter(e.getText());
				reloadPrescriptionGrid();
			});
			filterRow.addComponent(prescriptionTextFilter);
		}
		prescriptionsHeader.addComponent(filterRow);

		return prescriptionsHeader;
	}

	private VerticalLayout createTreatmentsHeader() {
		VerticalLayout treatmentsHeader = new VerticalLayout();
		treatmentsHeader.setMargin(false);
		treatmentsHeader.setSpacing(false);
		treatmentsHeader.setWidth(100, Unit.PERCENTAGE);

		HorizontalLayout headlineRow = new HorizontalLayout();
		headlineRow.setMargin(false);
		headlineRow.setSpacing(true);
		headlineRow.setWidth(100, Unit.PERCENTAGE);
		{
			Label treatmentsLabel = new Label(I18nProperties.getString(Strings.headingTreatments));
			CssStyles.style(treatmentsLabel, CssStyles.H3);
			headlineRow.addComponent(treatmentsLabel);
			headlineRow.setExpandRatio(treatmentsLabel, 1);

			// Bulk operations
			if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
				MenuBar bulkOperationsDropdown = MenuBarHelper.createDropDown(
					Captions.bulkActions,
					new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.bulkDelete), VaadinIcons.TRASH, selectedItem -> {
						ControllerProvider.getTherapyController().deleteAllSelectedTreatments(treatmentGrid.getSelectedRows(), new Runnable() {

							public void run() {
								reloadTreatmentGrid();
							}
						});
					}));

				headlineRow.addComponent(bulkOperationsDropdown);
				headlineRow.setComponentAlignment(bulkOperationsDropdown, Alignment.MIDDLE_RIGHT);
			}

			Button newTreatmentButton = ButtonHelper.createButton(Captions.treatmentNewTreatment, e -> {
				ControllerProvider.getTherapyController().openTreatmentCreateForm(treatmentCriteria.getTherapy(), this::reloadTreatmentGrid);
			});

			headlineRow.addComponent(newTreatmentButton);
			headlineRow.setComponentAlignment(newTreatmentButton, Alignment.MIDDLE_RIGHT);
		}
		treatmentsHeader.addComponent(headlineRow);

		HorizontalLayout filterRow = new HorizontalLayout();
		filterRow.setMargin(false);
		filterRow.setSpacing(true);
		{
			treatmentTypeFilter = new ComboBox();
			treatmentTypeFilter.setWidth(140, Unit.PIXELS);
			treatmentTypeFilter.setInputPrompt(I18nProperties.getPrefixCaption(TreatmentDto.I18N_PREFIX, TreatmentDto.TREATMENT_TYPE));
			treatmentTypeFilter.addItems((Object[]) TreatmentType.values());
			treatmentTypeFilter.addValueChangeListener(e -> {
				treatmentCriteria.treatmentType(((TreatmentType) e.getProperty().getValue()));
				navigateTo(treatmentCriteria);
			});
			filterRow.addComponent(treatmentTypeFilter);

			treatmentTextFilter = new TextField();
			treatmentTextFilter.setWidth(300, Unit.PIXELS);
			treatmentTextFilter.setNullRepresentation("");
			treatmentTextFilter.setInputPrompt(I18nProperties.getString(Strings.promptTreatmentTextFilter));
			treatmentTextFilter.addTextChangeListener(e -> {
				treatmentCriteria.textFilter(e.getText());
				reloadTreatmentGrid();
			});
			filterRow.addComponent(treatmentTextFilter);
		}
		treatmentsHeader.addComponent(filterRow);

		return treatmentsHeader;
	}

	private void update() {
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(getCaseRef().getUuid());

		// TODO: Remove this once a proper ViewModel system has been introduced
		if (caze.getTherapy() == null) {
			TherapyDto therapy = TherapyDto.build();
			caze.setTherapy(therapy);
			caze = FacadeProvider.getCaseFacade().saveCase(caze);
		}

		prescriptionCriteria.therapy(caze.getTherapy().toReference());
		treatmentCriteria.therapy(caze.getTherapy().toReference());

		applyingCriteria = true;

		prescriptionTypeFilter.setValue(prescriptionCriteria.getPrescriptionType());
		prescriptionTextFilter.setValue(prescriptionCriteria.getTextFilter());
		treatmentTypeFilter.setValue(treatmentCriteria.getTreatmentType());
		treatmentTextFilter.setValue(treatmentCriteria.getTextFilter());

		applyingCriteria = false;
	}

	public void reloadPrescriptionGrid() {
		prescriptionGrid.reload();
		prescriptionGrid.setHeightByRows(Math.max(1, Math.min(prescriptionGrid.getContainer().size(), 5)));
	}

	public void reloadTreatmentGrid() {
		treatmentGrid.reload();
	}

	@Override
	protected void initView(String params) {

		VerticalLayout container = new VerticalLayout();
		container.setSpacing(false);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);

		container.addComponent(createPrescriptionsHeader());

		Boolean caseEditAllowed = isCaseEditAllowed();

		prescriptionGrid = new PrescriptionGrid(this, caseEditAllowed);
		prescriptionGrid.setCriteria(prescriptionCriteria);
		prescriptionGrid.setHeightMode(HeightMode.ROW);
		CssStyles.style(prescriptionGrid, CssStyles.VSPACE_2);
		container.addComponent(prescriptionGrid);

		container.addComponent(createTreatmentsHeader());

		treatmentGrid = new TreatmentGrid(caseEditAllowed);
		treatmentGrid.setCriteria(treatmentCriteria);
		container.addComponent(treatmentGrid);
		container.setExpandRatio(treatmentGrid, 1);

		setSubComponent(container);

		update();
		reloadPrescriptionGrid();
		reloadTreatmentGrid();
	}
}
