/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.therapy;

import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_TOP_4;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.therapy.PrescriptionCriteria;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.therapy.TreatmentCriteria;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.therapy.TreatmentType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldAccessHelper;
import de.symeda.sormas.ui.utils.MenuBarHelper;

public class TherapyForm extends AbstractEditForm<TherapyDto> {

	private static final long serialVersionUID = -4632624848700245693L;

	private static final String DRUD_SUSCEPTIBILITY_LOC = "drugSusceptibilityLoc";
	private static final String PRESCRIPTION_LOC = "prescriptionLoc";
	private static final String PRESCRIPTION_HEADING_LOC = "prescriptionHeadingLoc";
	private static final String PRESCRIPTION_GRID_LOC = "prescriptionGridLoc";
	private static final String TREATMENT_LOC = "treatmentLoc";
	private static final String TREATMENT_HEADING_LOC = "treatmentHeadingLoc";
	private static final String TREATMENT_GRID_LOC = "treatmentGridLoc";
	private static final String EMPTY_HEADING_LOC = "emptyHeadingLoc";

	//@formatter:off
    private static final String MAIN_HTML_LAYOUT =
            fluidRowLocs(TherapyDto.DIRECTLY_OBSERVED_TREATMENT, "", "", "") +
            fluidRowLocs(TherapyDto.MDR_XDR_TUBERCULOSIS, TherapyDto.BEIJING_LINEAGE, "", "") +
            fluidRowLocs(DRUD_SUSCEPTIBILITY_LOC) +
            fluidRowLocs(PRESCRIPTION_LOC) +
            fluidRowLocs(TREATMENT_LOC) +
            fluidRowLocs(EMPTY_HEADING_LOC);

    private static final String PRESCRIPTION_HTML_LAYOUT =
            fluidRowLocs(PRESCRIPTION_HEADING_LOC) +
            fluidRowLocs(PRESCRIPTION_GRID_LOC);

    private static final String TREATMENT_HTML_LAYOUT =
            fluidRowLocs(TREATMENT_HEADING_LOC) +
            fluidRowLocs(TREATMENT_GRID_LOC);
    //@formatter:on

	private final CaseDataDto caze;
	private final Disease disease;

	private CheckBox mdrXdrTuberculosisField;
	private PathogenTestDto latestAntibioticTest;
	private PathogenTestDto latestBejingGenotypingTest;
	private CheckBox beijingLineageField;
	private DrugSusceptibilityResultPanel drugSusceptibilityResultPanel;

	private PrescriptionCriteria prescriptionCriteria;
	private TreatmentCriteria treatmentCriteria;

	private PrescriptionGrid prescriptionGrid;
	private TreatmentGrid treatmentGrid;

	// Filter
	private ComboBox prescriptionTypeFilter;
	private TextField prescriptionTextFilter;
	private ComboBox treatmentTypeFilter;
	private TextField treatmentTextFilter;
	// To avoid form going infinite loop, need two different flags to track whether the criteria are being applied or not
	protected boolean prescriptionApplyingCriteria;
	protected boolean treatmentApplyingCriteria;

	public TherapyForm(CaseDataDto caze, Disease disease, boolean isPseudonymized, boolean inJurisdiction, boolean isEditAllowed) {

		super(
			TherapyDto.class,
			TherapyDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			FieldAccessHelper.getFieldAccessCheckers(inJurisdiction, isPseudonymized),
			isEditAllowed);
		this.caze = caze;
		this.disease = disease;

		prescriptionCriteria = ViewModelProviders.of(TherapyView.class).get(PrescriptionCriteria.class);
		treatmentCriteria = ViewModelProviders.of(TherapyView.class).get(TreatmentCriteria.class);

		setWidth(100, Unit.PERCENTAGE);

		addFields();
	}

	@Override
	protected String createHtmlLayout() {
		return MAIN_HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		boolean isLuxembourgServer = FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG);

		CheckBox dotField = addField(TherapyDto.DIRECTLY_OBSERVED_TREATMENT, CheckBox.class);
		dotField.addStyleName(VSPACE_3);
		dotField.setVisible(isLuxembourgServer && disease == Disease.TUBERCULOSIS);

		mdrXdrTuberculosisField = addField(TherapyDto.MDR_XDR_TUBERCULOSIS, CheckBox.class);
		mdrXdrTuberculosisField.addStyleName(VSPACE_3);
		mdrXdrTuberculosisField.setVisible(isLuxembourgServer && disease == Disease.TUBERCULOSIS);
		mdrXdrTuberculosisField.setEnabled(false);

		beijingLineageField = addField(TherapyDto.BEIJING_LINEAGE, CheckBox.class);
		beijingLineageField.addStyleName(VSPACE_3);
		beijingLineageField.setVisible(isLuxembourgServer && disease == Disease.TUBERCULOSIS);

		List<SampleDto> samples = FacadeProvider.getSampleFacade().getByCaseUuids(Collections.singletonList(caze.getUuid()));
		List<String> sampleUuids = Collections.emptyList();
		if (samples != null && !samples.isEmpty()) {
			sampleUuids = samples.stream().map(SampleDto::getUuid).collect(Collectors.toList());
		}

		List<PathogenTestDto> pathogenTests = FacadeProvider.getPathogenTestFacade().getBySampleUuids(sampleUuids);
		if (pathogenTests != null && !pathogenTests.isEmpty()) {
			for (PathogenTestDto pathogenTest : pathogenTests) {
				if (pathogenTest.getTestType() == PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY) {
					latestAntibioticTest = pathogenTest;
					break;
				}
			}

			for (PathogenTestDto pathogenTest : pathogenTests) {
				if (pathogenTest.getTestType() == PathogenTestType.BEIJINGGENOTYPING) {
					latestBejingGenotypingTest = pathogenTest;
					break;
				}
			}
		}

		drugSusceptibilityResultPanel = new DrugSusceptibilityResultPanel(latestAntibioticTest);
		drugSusceptibilityResultPanel.setVisible(false);
		getContent().addComponent(drugSusceptibilityResultPanel, DRUD_SUSCEPTIBILITY_LOC);
		drugSusceptibilityResultPanel.addStyleNames(VSPACE_TOP_4, VSPACE_3);

		//prescription
		CustomLayout prescriptionsLayout = new CustomLayout();
		prescriptionsLayout.setTemplateContents(PRESCRIPTION_HTML_LAYOUT);

		prescriptionsLayout.addComponent(createPrescriptionsHeader(), PRESCRIPTION_HEADING_LOC);

		prescriptionGrid = new PrescriptionGrid(
			this,
			caze.isPseudonymized(),
			UiUtil.permitted(isEditAllowed(), UserRight.CASE_EDIT, UserRight.PRESCRIPTION_EDIT),
			UiUtil.permitted(isEditAllowed(), UserRight.PRESCRIPTION_DELETE));

		prescriptionGrid.setCriteria(prescriptionCriteria);
		prescriptionGrid.setHeightMode(HeightMode.ROW);
		CssStyles.style(prescriptionGrid, CssStyles.VSPACE_2);

		prescriptionsLayout.addComponent(prescriptionGrid, PRESCRIPTION_GRID_LOC);

		getContent().addComponent(prescriptionsLayout, PRESCRIPTION_LOC);

		//treatment
		CustomLayout treatmentsLayout = new CustomLayout();
		treatmentsLayout.setTemplateContents(TREATMENT_HTML_LAYOUT);

		treatmentsLayout.addComponent(createTreatmentsHeader(), TREATMENT_HEADING_LOC);

		treatmentGrid = new TreatmentGrid(
			caze.isPseudonymized(),
			UiUtil.permitted(isEditAllowed(), UserRight.CASE_EDIT, UserRight.TREATMENT_EDIT),
			UiUtil.permitted(isEditAllowed(), UserRight.TREATMENT_DELETE),
			UiUtil.permitted(isEditAllowed(), UserRight.CASE_EDIT, UserRight.PRESCRIPTION_EDIT),
			UiUtil.permitted(isEditAllowed(), UserRight.PRESCRIPTION_DELETE));
		treatmentGrid.setCriteria(treatmentCriteria);
		treatmentGrid.setHeightMode(HeightMode.ROW);
		CssStyles.style(treatmentGrid, CssStyles.VSPACE_2);
		treatmentsLayout.addComponent(treatmentGrid, TREATMENT_GRID_LOC);

		getContent().addComponent(treatmentsLayout, TREATMENT_LOC);

		update(caze);
		reloadPrescriptionGrid();
		reloadTreatmentGrid();
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
			if (UiUtil.permitted(isEditAllowed(), UserRight.PERFORM_BULK_OPERATIONS, UserRight.CASE_EDIT)) {
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

			if (UiUtil.permitted(isEditAllowed(), UserRight.CASE_EDIT)) {
				Button newPrescriptionButton = ButtonHelper.createButton(Captions.prescriptionNewPrescription, e -> {
					ControllerProvider.getTherapyController()
						.openPrescriptionCreateForm(prescriptionCriteria.getTherapy(), this::reloadPrescriptionGrid);
				}, ValoTheme.BUTTON_PRIMARY);
				if (!UiUtil.permitted(UserRight.PRESCRIPTION_CREATE)) {
					newPrescriptionButton.setEnabled(false);
				}
				headlineRow.addComponent(newPrescriptionButton);
				headlineRow.setComponentAlignment(newPrescriptionButton, Alignment.MIDDLE_RIGHT);
			}

		}
		prescriptionsHeader.addComponent(headlineRow);

		HorizontalLayout filterRow = new HorizontalLayout();
		filterRow.setMargin(false);
		filterRow.setSpacing(true);
		{
			prescriptionTypeFilter = ComboBoxHelper.createComboBoxV7();
			prescriptionTypeFilter.setWidth(140, Unit.PIXELS);
			prescriptionTypeFilter.setInputPrompt(I18nProperties.getPrefixCaption(PrescriptionDto.I18N_PREFIX, PrescriptionDto.PRESCRIPTION_TYPE));
			prescriptionTypeFilter.addItems((Object[]) TreatmentType.values());
			prescriptionTypeFilter.addValueChangeListener(e -> {
				prescriptionCriteria.prescriptionType(((TreatmentType) e.getProperty().getValue()));
				ControllerProvider.getTherapyController().navigateTo(prescriptionApplyingCriteria, true, prescriptionCriteria);

				prescriptionApplyingCriteria = false;
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
			if (UiUtil.permitted(isEditAllowed(), UserRight.PERFORM_BULK_OPERATIONS, UserRight.CASE_EDIT)) {
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

			if (UiUtil.permitted(isEditAllowed(), UserRight.CASE_EDIT)) {
				Button newTreatmentButton = ButtonHelper.createButton(Captions.treatmentNewTreatment, e -> {
					ControllerProvider.getTherapyController().openTreatmentCreateForm(treatmentCriteria.getTherapy(), this::reloadTreatmentGrid);
				});
				if (!UiUtil.permitted(UserRight.TREATMENT_CREATE)) {
					newTreatmentButton.setEnabled(false);
				}
				headlineRow.addComponent(newTreatmentButton);
				headlineRow.setComponentAlignment(newTreatmentButton, Alignment.MIDDLE_RIGHT);
			}

		}
		treatmentsHeader.addComponent(headlineRow);

		HorizontalLayout filterRow = new HorizontalLayout();
		filterRow.setMargin(false);
		filterRow.setSpacing(true);
		{
			treatmentTypeFilter = ComboBoxHelper.createComboBoxV7();
			treatmentTypeFilter.setWidth(140, Unit.PIXELS);
			treatmentTypeFilter.setInputPrompt(I18nProperties.getPrefixCaption(TreatmentDto.I18N_PREFIX, TreatmentDto.TREATMENT_TYPE));
			treatmentTypeFilter.addItems((Object[]) TreatmentType.values());
			treatmentTypeFilter.addValueChangeListener(e -> {
				treatmentCriteria.treatmentType(((TreatmentType) e.getProperty().getValue()));
				ControllerProvider.getTherapyController().navigateTo(treatmentApplyingCriteria, true, treatmentCriteria);

				treatmentApplyingCriteria = false;
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

	private void update(CaseDataDto caze) {
		// TODO: Remove this once a proper ViewModel system has been introduced
		if (caze.getTherapy() == null) {
			TherapyDto therapy = TherapyDto.build();
			caze.setTherapy(therapy);
			caze = FacadeProvider.getCaseFacade().save(caze);
		}

		prescriptionCriteria.therapy(caze.getTherapy().toReference());
		treatmentCriteria.therapy(caze.getTherapy().toReference());

		prescriptionApplyingCriteria = true;

		prescriptionTypeFilter.setValue(prescriptionCriteria.getPrescriptionType());
		prescriptionTextFilter.setValue(prescriptionCriteria.getTextFilter());
		treatmentApplyingCriteria = true;
		treatmentTypeFilter.setValue(treatmentCriteria.getTreatmentType());
		treatmentTextFilter.setValue(treatmentCriteria.getTextFilter());

		prescriptionApplyingCriteria = false;
		treatmentApplyingCriteria = false;
	}

	public void reloadPrescriptionGrid() {
		prescriptionGrid.reload();
		prescriptionGrid.setHeightByRows(Math.max(1, Math.min(prescriptionGrid.getContainer().size(), 5)));
	}

	public void reloadTreatmentGrid() {
		treatmentGrid.reload();
	}

	private boolean isEditAllowed() {
		return FacadeProvider.getCaseFacade().isEditAllowed(caze.toReference().getUuid());
	}

	@Override
	public void attach() {
		super.attach();

		if (getValue() != null) {
			if (getValue().isMdrXdrTuberculosis()) {
				drugSusceptibilityResultPanel.setVisible(true);
			}
		}

		if (latestBejingGenotypingTest != null) {
			beijingLineageField.setValue(true);
		}
	}
}
