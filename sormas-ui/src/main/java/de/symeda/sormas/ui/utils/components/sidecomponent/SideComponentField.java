package de.symeda.sormas.ui.utils.components.sidecomponent;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.Biotype;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.Serotype;
import de.symeda.sormas.api.sample.TargetTest;
import de.symeda.sormas.api.sample.ToxinResult;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class SideComponentField extends HorizontalLayout {

	private static final long serialVersionUID = -7617896760817891457L;

	private final VerticalLayout mainLayout;

	public SideComponentField() {
		setMargin(false);
		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST_ENTRY);

		mainLayout = new VerticalLayout();
		mainLayout.setWidth(100, Unit.PERCENTAGE);
		mainLayout.setMargin(false);
		mainLayout.setSpacing(false);
		addComponent(mainLayout);
		setExpandRatio(mainLayout, 1);
	}

	public void addComponentToField(Component component) {
		mainLayout.addComponent(component);
	}

	public void addEditButton(String id, Button.ClickListener editClickListener) {
		Button editButton = ButtonHelper
			.createIconButtonWithCaption(id, null, VaadinIcons.PENCIL, editClickListener, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);

		addComponent(editButton);
		setComponentAlignment(editButton, Alignment.TOP_RIGHT);
		setExpandRatio(editButton, 0);
	}

	public void addViewButton(String id, Button.ClickListener viewClickListener) {
		addViewButton(id, viewClickListener, VaadinIcons.EYE);
	}

	public void addViewButton(String id, Button.ClickListener viewClickListener, VaadinIcons icon) {
		Button viewButton =
			ButtonHelper.createIconButtonWithCaption(id, null, icon, viewClickListener, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);
		addComponent(viewButton);
		setComponentAlignment(viewButton, Alignment.TOP_RIGHT);
		setExpandRatio(viewButton, 0);
		viewButton.setEnabled(true);
	}

	@Override
	public void setEnabled(boolean enabled) {
		mainLayout.setEnabled(enabled);
	}

	public void addActionButton(String id, Button.ClickListener actionClickListener, boolean isEditEntry) {
		Button actionButton = ButtonHelper.createIconButtonWithCaption(
			isEditEntry ? "edit" + id : "view" + id,
			null,
			isEditEntry ? VaadinIcons.PENCIL : VaadinIcons.EYE,
			actionClickListener,
			ValoTheme.BUTTON_LINK,
			CssStyles.BUTTON_COMPACT);
		addComponent(actionButton);
		setComponentAlignment(actionButton, Alignment.TOP_RIGHT);
		setExpandRatio(actionButton, 0);
	}

	public void addDeleteButton(String id, Button.ClickListener actionClickListener) {
		Button actionButton = ButtonHelper.createIconButtonWithCaption(
			"delete" + id,
			null,
			VaadinIcons.TRASH,
			actionClickListener,
			ValoTheme.BUTTON_LINK,
			CssStyles.BUTTON_COMPACT);
		addComponent(actionButton);
		setComponentAlignment(actionButton, Alignment.TOP_RIGHT);
		setExpandRatio(actionButton, 0);
	}

	public void setActive() {
		mainLayout.addStyleName(CssStyles.ACTIVE_SIDE_COMPONENT_ELEMENT);
	}

	/** Disease -> the test methods whose variant field the side component can show. Invariant, so built once. */
	private static final Map<Disease, List<PathogenTestType>> VARIANT_MAP;
	static {
		Map<Disease, List<PathogenTestType>> map = new EnumMap<>(Disease.class);
		map.put(
			Disease.MALARIA,
			Arrays.asList(
				PathogenTestType.THIN_BLOOD_SMEAR,
				PathogenTestType.LATERAL_FLOW_ASSAY,
				PathogenTestType.PCR_RT_PCR,
				PathogenTestType.Q_PCR,
				PathogenTestType.LAMP,
				PathogenTestType.INDIRECT_FLUORESCENT_ANTIBODY,
				PathogenTestType.OTHER_MOLECULAR_ASSAY,
				PathogenTestType.OTHER_SEROLOGICAL_TEST,
				PathogenTestType.OTHER_ANTIGEN_DETECTION_TEST,
				PathogenTestType.ENZYME_LINKED_IMMUNOSORBENT_ASSAY));
		map.put(Disease.DENGUE, Arrays.asList(PathogenTestType.NAAT, PathogenTestType.NEUTRALIZING_ANTIBODIES, PathogenTestType.PCR_RT_PCR));
		map.put(Disease.SHIGELLOSIS, Arrays.asList(PathogenTestType.SEROGROUPING, PathogenTestType.SEROTYPING));
		map.put(Disease.MEASLES, Arrays.asList(PathogenTestType.GENOTYPING));
		map.put(
			Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
			Arrays.asList(
				PathogenTestType.SEROGROUPING,
				PathogenTestType.MULTILOCUS_SEQUENCE_TYPING,
				PathogenTestType.SLIDE_AGGLUTINATION,
				PathogenTestType.WHOLE_GENOME_SEQUENCING,
				PathogenTestType.SEQUENCING));
		map.put(
			Disease.TUBERCULOSIS,
			Arrays.asList(
				PathogenTestType.MICROSCOPY,
				PathogenTestType.BEIJINGGENOTYPING,
				PathogenTestType.SPOLIGOTYPING,
				PathogenTestType.MIRU_PATTERN_CODE));
		map.put(Disease.MUMPS, Arrays.asList(PathogenTestType.GENOTYPING));
		map.put(Disease.DIPHTHERIA, Arrays.asList(PathogenTestType.ELEK_TEST, PathogenTestType.CULTURE, PathogenTestType.PCR_RT_PCR));
		map.replaceAll((disease, testTypes) -> Collections.unmodifiableList(testTypes));
		VARIANT_MAP = Collections.unmodifiableMap(map);
	}

	/** Disease -> the logic that extracts the variant string for that disease's relevant test types. */
	private static final Map<Disease, Function<PathogenTestDto, String>> VARIANT_EXTRACTORS;
	static {
		Map<Disease, Function<PathogenTestDto, String>> extractors = new EnumMap<>(Disease.class);
		extractors.put(Disease.TUBERCULOSIS, SideComponentField::determineTuberculosisVariant);
		extractors.put(Disease.MALARIA, SideComponentField::determineSpecieVariant);
		extractors.put(Disease.SHIGELLOSIS, SideComponentField::determineSpecieVariant);
		extractors.put(Disease.INVASIVE_PNEUMOCOCCAL_INFECTION, SideComponentField::determineSerotypeVariant);
		extractors.put(Disease.DENGUE, SideComponentField::determineDengueVariant);
		extractors.put(Disease.MUMPS, SideComponentField::determineGenoTypeVariant);
		extractors.put(Disease.DIPHTHERIA, SideComponentField::determineDiphtheriaVariant);
		VARIANT_EXTRACTORS = Collections.unmodifiableMap(extractors);
	}

	/**
	 * To display the variant of the pathogen test in the side component, we need to check the disease and test type to determine which
	 * field contains the variant information, and if there are any "other" options that require us to show the free-text field instead.
	 *
	 * @param pathogenTest
	 * @return
	 */
	public String determineSideComponentVariant(PathogenTestDto pathogenTest) {
		if (pathogenTest.getTestType() == null || pathogenTest.getTestedDisease() == null) {
			return null;
		}

		Disease disease = pathogenTest.getTestedDisease();
		List<PathogenTestType> relevantTestTypes = VARIANT_MAP.get(disease);
		if (relevantTestTypes == null || !relevantTestTypes.contains(pathogenTest.getTestType())) {
			return null;
		}

		Function<PathogenTestDto, String> extractor = VARIANT_EXTRACTORS.get(disease);
		return extractor != null ? extractor.apply(pathogenTest) : null;
	}

	private static String determineTuberculosisVariant(PathogenTestDto pathogenTest) {
		switch (pathogenTest.getTestType()) {
		case MICROSCOPY:
			return abbreviateOrEmpty(pathogenTest.getTestScale());
		case BEIJINGGENOTYPING:
			return abbreviateOrEmpty(pathogenTest.getStrainCallStatus());
		case SPOLIGOTYPING:
			return abbreviateOrEmpty(pathogenTest.getSpecie());
		case MIRU_PATTERN_CODE:
			return abbreviate(pathogenTest.getPatternProfile());
		default:
			return null;
		}
	}

	// shared by MALARIA and SHIGELLOSIS: handling other specie
	private static String determineSpecieVariant(PathogenTestDto pathogenTest) {
		return abbreviateOtherAware(pathogenTest.getSpecie(), PathogenSpecie.OTHER, pathogenTest.getSpecieText());
	}

	// IPI serotyping stores the serogroup/serotype in the free-text field; show it instead of the plain result
	private static String determineSerotypeVariant(PathogenTestDto pathogenTest) {
		if (!DataHelper.isNullOrEmpty(pathogenTest.getSerotypeText())) {
			return abbreviate(pathogenTest.getSerotypeText());
		} else if (pathogenTest.getSerotype() != null) {
			return abbreviate(pathogenTest.getSerotype().toString());
		}
		return null;
	}

	// handling other serotypes
	private static String determineDengueVariant(PathogenTestDto pathogenTest) {
		return abbreviateOtherAware(pathogenTest.getSerotype(), Serotype.OTHER, pathogenTest.getSerotypeText());
	}

	private static String determineGenoTypeVariant(PathogenTestDto pathogenTest) {
		if (!DataHelper.isNullOrEmpty(pathogenTest.getGenoTypeText())) {
			return abbreviate(pathogenTest.getGenoTypeText());
		} else if (pathogenTest.getGenoType() != null) {
			return abbreviateOrEmpty(pathogenTest.getGenoType());
		}
		return null;
	}

	private static String determineDiphtheriaVariant(PathogenTestDto pathogenTest) {
		switch (pathogenTest.getTestType()) {
		case ELEK_TEST:
			return determineElekTestVariant(pathogenTest);
		case CULTURE:
			return determineCultureVariant(pathogenTest);
		case PCR_RT_PCR:
			return determinePcrVariant(pathogenTest);
		default:
			return abbreviateOrEmpty(pathogenTest.getTestResult());
		}
	}

	private static String determineElekTestVariant(PathogenTestDto pathogenTest) {
		if (PathogenTestResultType.POSITIVE == pathogenTest.getTestResult()) {
			return abbreviate(I18nProperties.getEnumCaption(ToxinResult.POSITIVE));
		} else if (PathogenTestResultType.NEGATIVE == pathogenTest.getTestResult()) {
			return abbreviate(I18nProperties.getEnumCaption(ToxinResult.NEGATIVE));
		}
		return abbreviateOrEmpty(pathogenTest.getTestResult());
	}

	// Culture positive should add the Specie + Biotype details to the side component.
	private static String determineCultureVariant(PathogenTestDto pathogenTest) {
		if (PathogenTestResultType.POSITIVE != pathogenTest.getTestResult()) {
			return abbreviateOrEmpty(pathogenTest.getTestResult());
		}
		String specie = abbreviateOtherAware(pathogenTest.getSpecie(), PathogenSpecie.OTHER, pathogenTest.getSpecieText());
		String biotype = abbreviateOtherAware(pathogenTest.getBiotype(), Biotype.OTHER, pathogenTest.getBiotypeText());
		String variant = !biotype.isEmpty() ? specie + " - " + biotype : specie;
		return abbreviate(variant);
	}

	// for PCR, Target test is Species identification + test result positive, should show the target test + Specie
	private static String determinePcrVariant(PathogenTestDto pathogenTest) {
		if (PathogenTestResultType.POSITIVE != pathogenTest.getTestResult() || pathogenTest.getTargetTest() == null) {
			return abbreviateOrEmpty(pathogenTest.getTestResult());
		}
		String targetTest = abbreviateOtherAware(pathogenTest.getTargetTest(), TargetTest.OTHER, pathogenTest.getTargetTestText());
		String specie = abbreviateOtherAware(pathogenTest.getSpecie(), PathogenSpecie.OTHER, pathogenTest.getSpecieText());
		String variant = !specie.isEmpty() ? targetTest + " - " + specie : targetTest;
		return abbreviate(variant);
	}

	private static String abbreviate(String value) {
		return StringUtils.abbreviate(value, 125);
	}

	private static String abbreviateOrEmpty(Object value) {
		return StringUtils.abbreviate(value != null ? value.toString() : "", 125);
	}

	/**
	 * If {@code value} equals {@code otherValue} ("Other"), prefer the accompanying free-text field when it's filled in;
	 * otherwise (including when {@code value} isn't "Other") fall back to the enum value itself.
	 */
	private static <E extends Enum<E>> String abbreviateOtherAware(E value, E otherValue, String text) {
		if (value == otherValue && !DataHelper.isNullOrEmpty(text)) {
			return abbreviate(text);
		}
		return abbreviateOrEmpty(value);
	}
}
