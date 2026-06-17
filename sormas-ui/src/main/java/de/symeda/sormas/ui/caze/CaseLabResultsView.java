/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.ui.caze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.therapy.Drug;
import de.symeda.sormas.api.therapy.DrugSusceptibilityDto;
import de.symeda.sormas.api.therapy.DrugSusceptibilityType;
import de.symeda.sormas.api.therapy.SusceptibilityMethod;
import de.symeda.sormas.api.therapy.SusceptibilitySurveillanceType;
import de.symeda.sormas.api.utils.AnnotationFieldHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;

/**
 * The case "Laboratory results" tab (#13948, issue #13955): an editable header (date other / external
 * comments, with the symptom-onset date read-only) plus three read-only overview tables — pathogen
 * tests, samples, and the flattened drug-susceptibility (AST) results.
 */
@SuppressWarnings("serial")
public class CaseLabResultsView extends AbstractCaseView {

	private static final Logger logger = LoggerFactory.getLogger(CaseLabResultsView.class);

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/labresults";

	private static final String SUSCEPTIBILITY_SUFFIX = "Susceptibility";

	private CommitDiscardWrapperComponent<CaseLabResultsForm> editComponent;
	private VerticalLayout tablesLayout;

	public CaseLabResultsView() {
		super(VIEW_NAME, true);
	}

	@Override
	protected void initView(String params) {

		editComponent = ControllerProvider.getCaseController().getLabResultsEditComponent(getCaseRef().getUuid(), isEditAllowed());

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);

		// Editable header (onset read-only + date other + external comments).
		container.addComponent(editComponent);

		// The three read-only tables live in their own layout so they can be refreshed (e.g. after editing a
		// test) without rebuilding the editable header and discarding the user's un-committed header edits.
		tablesLayout = new VerticalLayout();
		tablesLayout.setMargin(false);
		tablesLayout.setSpacing(false);
		tablesLayout.setWidth(100, Unit.PERCENTAGE);
		container.addComponent(tablesLayout);

		refreshTables();

		setSubComponent(container);
	}

	private void refreshTables() {

		if (tablesLayout == null) {
			return;
		}
		tablesLayout.removeAllComponents();

		Language userLanguage = I18nProperties.getUserLanguage();

		List<SampleDto> samples = FacadeProvider.getSampleFacade().getByCaseUuids(Collections.singletonList(getCaseRef().getUuid()));
		if (samples == null) {
			samples = Collections.emptyList();
		}
		List<String> sampleUuids = samples.stream().map(SampleDto::getUuid).collect(Collectors.toList());

		List<PathogenTestDto> tests =
			sampleUuids.isEmpty() ? new ArrayList<>() : new ArrayList<>(FacadeProvider.getPathogenTestFacade().getBySampleUuids(sampleUuids));
		// Newest first.
		tests.sort(Comparator.comparing(PathogenTestDto::getTestDateTime, Comparator.nullsLast(Comparator.reverseOrder())));

		Map<String, SampleDto> sampleByUuid = samples.stream().collect(Collectors.toMap(SampleDto::getUuid, Function.identity()));

		List<PathogenTestDto> astTests = tests.stream()
			.filter(t -> t.getTestType() == PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY && t.getDrugSusceptibility() != null)
			.collect(Collectors.toList());
		List<PathogenTestDto> displayTests =
			tests.stream().filter(t -> t.getTestType() != PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY).collect(Collectors.toList());

		// "Tests performed" counts ALL pathogen tests of the sample, including AST tests (whose results are
		// shown in the separate drug-susceptibility table).
		Map<String, Long> testCountBySampleUuid =
			tests.stream().filter(t -> t.getSample() != null).collect(Collectors.groupingBy(t -> t.getSample().getUuid(), Collectors.counting()));

		tablesLayout.addComponent(buildSectionHeader(Captions.caseLabResultsTestsHeading));
		tablesLayout.addComponent(
			displayTests.isEmpty() ? emptyInfoLabel(Strings.infoNoCaseLabResultsTests) : buildTestsGrid(displayTests, sampleByUuid, userLanguage));

		tablesLayout.addComponent(buildSectionHeader(Captions.caseLabResultsSamplesHeading));
		tablesLayout.addComponent(
			samples.isEmpty() ? emptyInfoLabel(Strings.infoNoCaseLabResultsSamples) : buildSamplesGrid(samples, testCountBySampleUuid, userLanguage));

		// The drug-susceptibility table is shown only when antimicrobial susceptibility testing exists.
		if (!astTests.isEmpty()) {
			tablesLayout.addComponent(buildSectionHeader(Captions.caseLabResultsDrugSusceptibilityHeading));
			tablesLayout.addComponent(buildAstGrid(astTests));
		}
	}

	private Label buildSectionHeader(String captionKey) {
		Label label = new Label(I18nProperties.getCaption(captionKey));
		label.addStyleName(CssStyles.H3);
		CssStyles.style(label, CssStyles.VSPACE_TOP_3);
		return label;
	}

	private Label emptyInfoLabel(String stringKey) {
		Label label = new Label(I18nProperties.getString(stringKey));
		CssStyles.style(label, CssStyles.VSPACE_3);
		return label;
	}

	private Grid<PathogenTestDto> buildTestsGrid(List<PathogenTestDto> rows, Map<String, SampleDto> sampleByUuid, Language userLanguage) {

		Grid<PathogenTestDto> grid = new Grid<>(PathogenTestDto.class);
		grid.setColumns();
		grid.setWidth(100, Unit.PERCENTAGE);
		grid.setHeightByRows(Math.max(1, Math.min(rows.size(), 10)));

		grid.addColumn(PathogenTestDto::getTestType)
			.setCaption(I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_TYPE));
		grid.addColumn(PathogenTestDto::getTestedDisease)
			.setCaption(I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TESTED_DISEASE));
		grid.addColumn(PathogenTestDto::getTestResult)
			.setCaption(I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_RESULT));
		grid.addColumn(PathogenTestDto::getTestResultText)
			.setCaption(I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_RESULT_TEXT));
		grid.addColumn(t -> t.getSample() != null ? t.getSample().buildCaption() : null)
			.setCaption(I18nProperties.getCaption(Captions.caseLabResultsSample));
		grid.addColumn(t -> collectionDateOf(t, sampleByUuid))
			.setCaption(I18nProperties.getCaption(Captions.caseLabResultsDateCollected))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateFormat(userLanguage)));
		grid.addColumn(PathogenTestDto::getTestDateTime)
			.setCaption(I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_DATE_TIME))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));
		grid.addColumn(PathogenTestDto::getRetestRequested)
			.setCaption(I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.RETEST_REQUESTED));
		grid.addColumn(t -> t.getLab() != null ? t.getLab().buildCaption() : null)
			.setCaption(I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.LAB));

		grid.setItems(rows);

		grid.addItemClickListener(e -> {
			if (e.getMouseEventDetails().isDoubleClick() && e.getItem() != null) {
				ControllerProvider.getPathogenTestController().edit(e.getItem().getUuid(), this::reload, isEditAllowed(), false);
			}
		});

		return grid;
	}

	private Grid<SampleDto> buildSamplesGrid(List<SampleDto> rows, Map<String, Long> testCountBySampleUuid, Language userLanguage) {

		Grid<SampleDto> grid = new Grid<>(SampleDto.class);
		grid.setColumns();
		grid.setWidth(100, Unit.PERCENTAGE);
		grid.setHeightByRows(Math.max(1, Math.min(rows.size(), 10)));

		grid.addColumn(SampleDto::getSampleMaterial).setCaption(I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_MATERIAL));
		grid.addColumn(SampleDto::getFieldSampleID).setCaption(I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.FIELD_SAMPLE_ID));
		grid.addColumn(SampleDto::getSampleDateTime)
			.setCaption(I18nProperties.getCaption(Captions.caseLabResultsDateCollected))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateFormat(userLanguage)));
		grid.addColumn(SampleDto::getShipmentDate)
			.setCaption(I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SHIPMENT_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateFormat(userLanguage)));
		grid.addColumn(SampleDto::getReceivedDate)
			.setCaption(I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.RECEIVED_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateFormat(userLanguage)));
		grid.addColumn(SampleDto::getSpecimenCondition)
			.setCaption(I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SPECIMEN_CONDITION));
		grid.addColumn(s -> s.getLab() != null ? s.getLab().buildCaption() : null)
			.setCaption(I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.LAB));
		grid.addColumn(s -> testCountBySampleUuid.getOrDefault(s.getUuid(), 0L))
			.setCaption(I18nProperties.getCaption(Captions.caseLabResultsTestsPerformed));
		grid.addColumn(SampleDto::getComment).setCaption(I18nProperties.getCaption(Captions.caseLabResultsComments));

		grid.setItems(rows);

		grid.addItemClickListener(e -> {
			if (e.getMouseEventDetails().isDoubleClick() && e.getItem() != null) {
				ControllerProvider.getSampleController().navigateToData(e.getItem().getUuid());
			}
		});

		return grid;
	}

	private Grid<AstRow> buildAstGrid(List<PathogenTestDto> astTests) {

		List<AstRow> rows = new ArrayList<>();
		for (PathogenTestDto test : astTests) {
			rows.addAll(flattenDrugSusceptibility(test));
		}

		Grid<AstRow> grid = new Grid<>(AstRow.class);
		grid.setColumns();
		grid.setWidth(100, Unit.PERCENTAGE);
		grid.setHeightByRows(Math.max(1, Math.min(rows.size(), 10)));

		grid.addColumn(AstRow::getAntibiotic).setCaption(I18nProperties.getCaption(Captions.caseLabResultsAntibiotic));
		grid.addColumn(AstRow::getMethod).setCaption(I18nProperties.getCaption(Captions.caseLabResultsMethod));
		grid.addColumn(AstRow::getMic).setCaption(I18nProperties.getCaption(Captions.caseLabResultsMicValue));
		grid.addColumn(AstRow::getZoneDiameter).setCaption(I18nProperties.getCaption(Captions.caseLabResultsZoneDiameter));
		grid.addColumn(AstRow::getClinicalInterpretation).setCaption(I18nProperties.getCaption(Captions.caseLabResultsClinicalInterpretation));
		grid.addColumn(AstRow::getSurveillanceInterpretation)
			.setCaption(I18nProperties.getCaption(Captions.caseLabResultsSurveillanceInterpretation));

		grid.setItems(rows);
		return grid;
	}

	/**
	 * Flattens the single, flat {@link DrugSusceptibilityDto} of an AST test into one row per antibiotic
	 * that applies to the tested disease (per the {@code @Diseases}/{@code @ApplicableToPathogenTests}
	 * annotations), reading the MIC, zone diameter, method, clinical (S/I/R) and surveillance (WT/NWT)
	 * values reflectively.
	 */
	private List<AstRow> flattenDrugSusceptibility(PathogenTestDto test) {

		List<AstRow> rows = new ArrayList<>();
		DrugSusceptibilityDto ds = test.getDrugSusceptibility();
		if (ds == null || test.getTestedDisease() == null) {
			return rows;
		}

		List<String> applicableFields = AnnotationFieldHelper
			.getFieldNamesWithMatchingDiseaseAndTestAnnotations(DrugSusceptibilityDto.class, test.getTestedDisease(), test.getTestType());

		for (String fieldName : applicableFields) {
			if (fieldName == null || fieldName.isEmpty() || !fieldName.endsWith(SUSCEPTIBILITY_SUFFIX)) {
				continue;
			}
			String base = fieldName.substring(0, fieldName.length() - SUSCEPTIBILITY_SUFFIX.length());
			if (base.isEmpty()) {
				continue;
			}
			String capitalized = Character.toUpperCase(base.charAt(0)) + base.substring(1);

			// Not every drug defines every measurement (e.g. the Shigellosis drugs only have MIC + S/I/R);
			// read each value defensively so a missing getter just leaves that cell blank.
			DrugSusceptibilityType clinical = readProperty(ds, capitalized + SUSCEPTIBILITY_SUFFIX, DrugSusceptibilityType.class);
			SusceptibilityMethod method = readProperty(ds, capitalized + "Method", SusceptibilityMethod.class);
			Float mic = readProperty(ds, capitalized + "Mic", Float.class);
			Float zoneDiameter = readProperty(ds, capitalized + "ZoneDiameter", Float.class);
			SusceptibilitySurveillanceType surveillance = readProperty(ds, capitalized + "Surveillance", SusceptibilitySurveillanceType.class);

			Drug drug = resolveDrug(base);
			rows.add(
				new AstRow(
					drug != null ? I18nProperties.getEnumCaption(drug) : base,
					method != null ? I18nProperties.getEnumCaption(method) : null,
					mic,
					zoneDiameter,
					clinical != null ? I18nProperties.getEnumCaption(clinical) : null,
					surveillance != null ? I18nProperties.getEnumCaption(surveillance) : null));
		}

		return rows;
	}

	/**
	 * Reads a {@link DrugSusceptibilityDto} property by its getter name, returning {@code null} when the
	 * getter does not exist (drugs vary in which measurements they record) or cannot be read.
	 */
	private <T> T readProperty(DrugSusceptibilityDto ds, String propertyName, Class<T> type) {
		try {
			Object value = ds.getClass().getMethod("get" + propertyName).invoke(ds);
			return type.isInstance(value) ? type.cast(value) : null;
		} catch (NoSuchMethodException e) {
			return null;
		} catch (ReflectiveOperationException | RuntimeException e) {
			logger.warn("Could not read drug susceptibility property '{}': {}", propertyName, e.getMessage());
			return null;
		}
	}

	/**
	 * Maps a drug-susceptibility field base (e.g. {@code trimethoprimSulfamethoxazole}) to its {@link Drug}
	 * constant by normalising away underscores/case, so multi-word constants like
	 * {@code TRIMETHOPRIM_SULFAMETHOXAZOLE} resolve correctly. Returns {@code null} if no match.
	 */
	private Drug resolveDrug(String base) {
		String normalized = base.replace("_", "").toLowerCase();
		for (Drug drug : Drug.values()) {
			if (drug.name().replace("_", "").toLowerCase().equals(normalized)) {
				return drug;
			}
		}
		return null;
	}

	private static java.util.Date collectionDateOf(PathogenTestDto test, Map<String, SampleDto> sampleByUuid) {
		if (test.getSample() == null) {
			return null;
		}
		SampleDto sample = sampleByUuid.get(test.getSample().getUuid());
		return sample != null ? sample.getSampleDateTime() : null;
	}

	private void reload() {
		// Refresh only the data tables (a test/sample may have changed). Leave the editable header alone so
		// the user's un-committed header edits are preserved.
		refreshTables();
	}

	/**
	 * View-model for one antibiotic row of the AST table.
	 */
	public static final class AstRow {

		private final String antibiotic;
		private final String method;
		private final Float mic;
		private final Float zoneDiameter;
		private final String clinicalInterpretation;
		private final String surveillanceInterpretation;

		AstRow(String antibiotic, String method, Float mic, Float zoneDiameter, String clinicalInterpretation, String surveillanceInterpretation) {
			this.antibiotic = antibiotic;
			this.method = method;
			this.mic = mic;
			this.zoneDiameter = zoneDiameter;
			this.clinicalInterpretation = clinicalInterpretation;
			this.surveillanceInterpretation = surveillanceInterpretation;
		}

		public String getAntibiotic() {
			return antibiotic;
		}

		public String getMethod() {
			return method;
		}

		public Float getMic() {
			return mic;
		}

		public Float getZoneDiameter() {
			return zoneDiameter;
		}

		public String getClinicalInterpretation() {
			return clinicalInterpretation;
		}

		public String getSurveillanceInterpretation() {
			return surveillanceInterpretation;
		}
	}
}
