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
package de.symeda.sormas.ui.therapy;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.therapy.PrescriptionCriteria;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.therapy.TreatmentCriteria;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.therapy.TreatmentType;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.caze.AbstractCaseView;
import de.symeda.sormas.ui.utils.CssStyles;

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
		super(VIEW_NAME);

		prescriptionCriteria = ViewModelProviders.of(TherapyView.class).get(PrescriptionCriteria.class);
		treatmentCriteria = ViewModelProviders.of(TherapyView.class).get(TreatmentCriteria.class);

		VerticalLayout container = new VerticalLayout();
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);

		container.addComponent(createPrescriptionsHeader());

		prescriptionGrid = new PrescriptionGrid(this);
		prescriptionGrid.setCriteria(prescriptionCriteria);
		prescriptionGrid.setHeightMode(HeightMode.ROW);
		prescriptionGrid.setHeightByRows(5);
		CssStyles.style(prescriptionGrid, CssStyles.VSPACE_3);
		container.addComponent(prescriptionGrid);

		container.addComponent(createTreatmentsHeader());

		treatmentGrid = new TreatmentGrid();
		treatmentGrid.setCriteria(treatmentCriteria);
		container.addComponent(treatmentGrid);
		container.setExpandRatio(treatmentGrid, 1);

		setSubComponent(container);
	}

	private VerticalLayout createPrescriptionsHeader() {
		VerticalLayout prescriptionsHeader = new VerticalLayout();
		prescriptionsHeader.setWidth(100, Unit.PERCENTAGE);

		HorizontalLayout headlineRow = new HorizontalLayout();
		headlineRow.setWidth(100, Unit.PERCENTAGE);
		{
			Label prescriptionsLabel = new Label(I18nProperties.getPrefixCaption(TherapyDto.I18N_PREFIX, "prescriptions"));
			CssStyles.style(prescriptionsLabel, CssStyles.H3);
			headlineRow.addComponent(prescriptionsLabel);

			Button newPrescriptionButton = new Button(I18nProperties.getPrefixCaption(TherapyDto.I18N_PREFIX, "newPrescription"));
			CssStyles.style(newPrescriptionButton, ValoTheme.BUTTON_PRIMARY);
			newPrescriptionButton.addClickListener(e -> {
				ControllerProvider.getTherapyController().openPrescriptionCreateForm(prescriptionCriteria.getTherapy(), this::reloadPrescriptionGrid);
			});
			headlineRow.addComponent(newPrescriptionButton);

			headlineRow.setComponentAlignment(newPrescriptionButton, Alignment.MIDDLE_RIGHT);
		}
		prescriptionsHeader.addComponent(headlineRow);

		HorizontalLayout filterRow = new HorizontalLayout();
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
			prescriptionTextFilter.setInputPrompt(I18nProperties.getPrefixCaption(PrescriptionDto.I18N_PREFIX, "textFilter"));
			prescriptionTextFilter.addTextChangeListener(e -> {
				prescriptionCriteria.textFilter(e.getText());
				navigateTo(prescriptionCriteria);
			});
			filterRow.addComponent(prescriptionTextFilter);
		}
		prescriptionsHeader.addComponent(filterRow);

		return prescriptionsHeader;
	}

	private VerticalLayout createTreatmentsHeader() {
		VerticalLayout treatmentsHeader = new VerticalLayout();
		treatmentsHeader.setWidth(100, Unit.PERCENTAGE);

		HorizontalLayout headlineRow = new HorizontalLayout();
		headlineRow.setWidth(100, Unit.PERCENTAGE);
		{
			Label treatmentsLabel = new Label(I18nProperties.getPrefixCaption(TherapyDto.I18N_PREFIX, "treatments"));
			CssStyles.style(treatmentsLabel, CssStyles.H3);
			headlineRow.addComponent(treatmentsLabel);

			Button newTreatmentButton = new Button(I18nProperties.getPrefixCaption(TherapyDto.I18N_PREFIX, "newTreatment"));
			CssStyles.style(newTreatmentButton, ValoTheme.BUTTON_PRIMARY);
			newTreatmentButton.addClickListener(e -> {
				ControllerProvider.getTherapyController().openTreatmentCreateForm(treatmentCriteria.getTherapy(), this::reloadTreatmentGrid);
			});
			headlineRow.addComponent(newTreatmentButton);

			headlineRow.setComponentAlignment(newTreatmentButton, Alignment.MIDDLE_RIGHT);
		}
		treatmentsHeader.addComponent(headlineRow);

		HorizontalLayout filterRow = new HorizontalLayout();
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
			treatmentTextFilter.setInputPrompt(I18nProperties.getPrefixCaption(TreatmentDto.I18N_PREFIX, "textFilter"));
			treatmentTextFilter.addTextChangeListener(e -> {
				treatmentCriteria.textFilter(e.getText());
				navigateTo(treatmentCriteria);
			});
			filterRow.addComponent(treatmentTextFilter);
		}
		treatmentsHeader.addComponent(filterRow);

		return treatmentsHeader;
	}

	private void update() {
		if (prescriptionCriteria.getTherapy() == null || treatmentCriteria.getTherapy() == null) {	
			CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(getCaseRef().getUuid());
			
			// TODO: Remove this once a proper ViewModel system has been introduced
			if (caze.getTherapy() == null) {
				TherapyDto therapy = TherapyDto.build();
				caze.setTherapy(therapy);
				caze = FacadeProvider.getCaseFacade().saveCase(caze);
			}
			
			prescriptionCriteria.therapy(caze.getTherapy());
			treatmentCriteria.therapy(caze.getTherapy());
		}
		
		applyingCriteria = true;
		
		prescriptionTypeFilter.setValue(prescriptionCriteria.getPrescriptionType());
		prescriptionTextFilter.setValue(prescriptionCriteria.getTextFilter());
		treatmentTypeFilter.setValue(treatmentCriteria.getTreatmentType());
		treatmentTextFilter.setValue(treatmentCriteria.getTextFilter());
		
		applyingCriteria = false;
	}
	
	public void reloadPrescriptionGrid() {
		prescriptionGrid.reload();
	}
	
	public void reloadTreatmentGrid() {
		treatmentGrid.reload();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		update();
		reloadPrescriptionGrid();
		reloadTreatmentGrid();
	}

}
