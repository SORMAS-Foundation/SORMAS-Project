package de.symeda.sormas.ui.utils.components.sidecomponent;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.Serotype;
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

	/**
	 * To display the variant of the pathogen test in the side component, we need to check the disease and test type to determine which
	 * field contains the variant information, and if there are any "other" options that require us to show the free-text field instead.
	 * 
	 * @param pathogenTest
	 * @return
	 */
	public String determineSideComponentVariant(PathogenTestDto pathogenTest) {
		if (pathogenTest.getTestType() == null || pathogenTest.getTestedDisease() == null)
			return null;

		Map<Disease, List<PathogenTestType>> VARIANT_MAP = Collections.unmodifiableMap(new HashMap<>() {

			{
				put(
					Disease.MALARIA,
					Collections.unmodifiableList(
						Arrays.asList(
							PathogenTestType.THIN_BLOOD_SMEAR,
							PathogenTestType.RAPID_TEST,
							PathogenTestType.PCR_RT_PCR,
							PathogenTestType.Q_PCR,
							PathogenTestType.LAMP,
							PathogenTestType.INDIRECT_FLUORESCENT_ANTIBODY,
							PathogenTestType.OTHER_MOLECULAR_ASSAY,
							PathogenTestType.OTHER_SEROLOGICAL_TEST,
							PathogenTestType.OTHER_ANTIGEN_DETECTION_TEST,
							PathogenTestType.ENZYME_LINKED_IMMUNOSORBENT_ASSAY,
							PathogenTestType.ANTIGEN_DETECTION)));
				put(
					Disease.DENGUE,
					Collections.unmodifiableList(
						Arrays.asList(PathogenTestType.NAAT, PathogenTestType.NEUTRALIZING_ANTIBODIES, PathogenTestType.PCR_RT_PCR)));
				put(Disease.SHIGELLOSIS, Collections.unmodifiableList(Arrays.asList(PathogenTestType.SEROGROUPING, PathogenTestType.SEROTYPING)));
				put(Disease.MEASLES, Collections.unmodifiableList(Arrays.asList(PathogenTestType.GENOTYPING)));
				put(
					Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
					Collections.unmodifiableList(
						Arrays.asList(
							PathogenTestType.SEROGROUPING,
							PathogenTestType.MULTILOCUS_SEQUENCE_TYPING,
							PathogenTestType.SLIDE_AGGLUTINATION,
							PathogenTestType.WHOLE_GENOME_SEQUENCING,
							PathogenTestType.SEQUENCING)));
				put(
					Disease.TUBERCULOSIS,
					Collections.unmodifiableList(
						Arrays.asList(
							PathogenTestType.MICROSCOPY,
							PathogenTestType.BEIJINGGENOTYPING,
							PathogenTestType.SPOLIGOTYPING,
							PathogenTestType.MIRU_PATTERN_CODE)));
			}
		});

		String variant = null;
		if (pathogenTest.getTestedDisease() == Disease.TUBERCULOSIS
			&& VARIANT_MAP.get(pathogenTest.getTestedDisease()).contains(pathogenTest.getTestType())) {
			if (pathogenTest.getTestType() == PathogenTestType.MICROSCOPY) {
				variant = StringUtils.abbreviate((pathogenTest.getTestScale() != null ? pathogenTest.getTestScale().toString() : ""), 125);
			} else if (pathogenTest.getTestType() == PathogenTestType.BEIJINGGENOTYPING) {
				variant =
					StringUtils.abbreviate((pathogenTest.getStrainCallStatus() != null ? pathogenTest.getStrainCallStatus().toString() : ""), 125);
			} else if (pathogenTest.getTestType() == PathogenTestType.SPOLIGOTYPING) {
				variant = StringUtils.abbreviate((pathogenTest.getSpecie() != null ? pathogenTest.getSpecie().toString() : ""), 125);
			} else if (pathogenTest.getTestType() == PathogenTestType.MIRU_PATTERN_CODE) {
				variant = StringUtils.abbreviate(pathogenTest.getPatternProfile(), 125);
			}
		} else if (Arrays.asList(Disease.MALARIA, Disease.SHIGELLOSIS).contains(pathogenTest.getTestedDisease())
			&& VARIANT_MAP.get(pathogenTest.getTestedDisease()).stream().anyMatch(pathogenTest.getTestType()::equals)) {	// handling other specie
			if (pathogenTest.getSpecie() == PathogenSpecie.OTHER) {
				variant = StringUtils.abbreviate((pathogenTest.getSpecieText() != null ? pathogenTest.getSpecieText().toString() : ""), 125);
			} else {
				variant = StringUtils.abbreviate((pathogenTest.getSpecie() != null ? pathogenTest.getSpecie().toString() : ""), 125);
			}

		} else if (pathogenTest.getTestedDisease() == Disease.INVASIVE_PNEUMOCOCCAL_INFECTION
			&& VARIANT_MAP.get(pathogenTest.getTestedDisease()).stream().anyMatch(pathogenTest.getTestType()::equals)) {	// IPI serotyping stores the serogroup/serotype in the free-text field; show it instead of the plain result
			if (!DataHelper.isNullOrEmpty(pathogenTest.getSerotypeText())) {
				variant = StringUtils.abbreviate(pathogenTest.getSerotypeText(), 125);
			} else if (pathogenTest.getSerotype() != null) {
				variant = StringUtils.abbreviate(pathogenTest.getSerotype().toString(), 125);
			} else {
				variant = null;
			}

		} else if (pathogenTest.getTestedDisease() == Disease.DENGUE
			&& VARIANT_MAP.get(pathogenTest.getTestedDisease()).stream().anyMatch(pathogenTest.getTestType()::equals)) {
			// handling other serotypes
			if (pathogenTest.getSerotype() == Serotype.OTHER) {
				variant = StringUtils.abbreviate((pathogenTest.getSerotypeText() != null ? pathogenTest.getSerotypeText().toString() : ""), 125);
			} else {
				variant = StringUtils.abbreviate((pathogenTest.getSerotype() != null ? pathogenTest.getSerotype().toString() : ""), 125);
			}

		} else {
			variant = null;
		}
		return variant;
	}
}
