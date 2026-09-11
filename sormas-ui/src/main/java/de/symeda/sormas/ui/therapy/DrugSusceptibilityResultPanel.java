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

import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.AMIKACIN_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.BEDAQUILINE_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.CAPREOMYCIN_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.CEFTRIAXONE_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.CIPROFLOXACIN_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.DELAMANID_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.ERYTHROMYCIN_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.ETHAMBUTOL_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.GATIFLOXACIN_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.ISONIAZID_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.KANAMYCIN_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.LEVOFLOXACIN_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.MOXIFLOXACIN_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.OFLOXACIN_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.PENICILLIN_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.RIFAMPICIN_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.STREPTOMYCIN_SUSCEPTIBILITY;
import static de.symeda.sormas.ui.utils.CssStyles.H3;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.therapy.Drug;
import de.symeda.sormas.api.therapy.DrugSusceptibilityDto;
import de.symeda.sormas.api.therapy.DrugSusceptibilityType;
import de.symeda.sormas.api.utils.AnnotationFieldHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class DrugSusceptibilityResultPanel extends CustomLayout {

	private static final long serialVersionUID = 8458057586239793721L;

	private static final String FORM_HEADING_LOC = "formHeadingLoc";
	private static final String CONTENT_LOC = "content";

	private PathogenTestDto pathogenTestDto;
	private VerticalLayout verticalLayout;

	private static final List<String> componentLocationsList = List.of(
		AMIKACIN_SUSCEPTIBILITY,
		BEDAQUILINE_SUSCEPTIBILITY,
		CAPREOMYCIN_SUSCEPTIBILITY,
		CIPROFLOXACIN_SUSCEPTIBILITY,
		DELAMANID_SUSCEPTIBILITY,
		ETHAMBUTOL_SUSCEPTIBILITY,
		GATIFLOXACIN_SUSCEPTIBILITY,
		ISONIAZID_SUSCEPTIBILITY,
		KANAMYCIN_SUSCEPTIBILITY,
		LEVOFLOXACIN_SUSCEPTIBILITY,
		MOXIFLOXACIN_SUSCEPTIBILITY,
		OFLOXACIN_SUSCEPTIBILITY,
		RIFAMPICIN_SUSCEPTIBILITY,
		STREPTOMYCIN_SUSCEPTIBILITY,
		CEFTRIAXONE_SUSCEPTIBILITY,
		PENICILLIN_SUSCEPTIBILITY,
		ERYTHROMYCIN_SUSCEPTIBILITY);

	public DrugSusceptibilityResultPanel(PathogenTestDto pathogenTestDto) {
		setWidth(100, Sizeable.Unit.PERCENTAGE);
		this.addStyleNames(CssStyles.VSPACE_TOP_3, CssStyles.VSPACE_3);
		String templateHtml = "<div location=\"" + FORM_HEADING_LOC + "\"></div>" + "<div location=\"" + CONTENT_LOC + "\"></div>";
		setTemplateContents(templateHtml);
		verticalLayout = new VerticalLayout();
		verticalLayout.setWidth(100, Unit.PERCENTAGE);
		verticalLayout.setSpacing(false);
		verticalLayout.setMargin(false);
		this.pathogenTestDto = pathogenTestDto;
		addFields();
		addComponent(verticalLayout, "content");
	}

	// to add the rows
	private HorizontalLayout currentRowLayout;

	private void addFields() {
		if (pathogenTestDto != null) {
			Label formHeadingLabel = new Label(I18nProperties.getString(Strings.headingDrugSusceptibility));
			formHeadingLabel.addStyleName(H3);
			addComponent(formHeadingLabel, FORM_HEADING_LOC);

			if (pathogenTestDto.getTestType() == PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY) {
				DrugSusceptibilityDto drugSusceptibilityDto = pathogenTestDto.getDrugSusceptibility();
				Disease testedDisease = pathogenTestDto.getTestedDisease();
				// Get fields applicable to current disease and test type beforehand
				List<String> applicableFieldIds = (testedDisease != null)
					? AnnotationFieldHelper
						.getFieldNamesWithMatchingDiseaseAndTestAnnotations(DrugSusceptibilityDto.class, testedDisease, pathogenTestDto.getTestType())
					: Collections.emptyList();

				Arrays.stream(drugSusceptibilityDto.getClass().getMethods())
					.filter(method -> method.getName().endsWith("Susceptibility"))
					.filter(method -> method.getParameterCount() == 0)
					.sorted(Comparator.comparingInt(method -> {
						// sort the drugs based on componentLocationsList
						String drugName = method.getName().substring(3, method.getName().length() - "Susceptibility".length());
						String fieldId = drugName.toUpperCase() + "SUSCEPTIBILITY";
						int index = IntStream.range(0, componentLocationsList.size())
							.filter(i -> componentLocationsList.get(i).equalsIgnoreCase(fieldId))
							.findFirst()
							.orElse(-1);
						return index != -1 ? index : Integer.MAX_VALUE;
					}))
					.forEach(method -> {
						try {
							DrugSusceptibilityType type = (DrugSusceptibilityType) method.invoke(drugSusceptibilityDto);
							// If DrugSusceptibilityType is null, don't need to display those drugs
							if (type == null) {
								return;
							}
							String drugName = method.getName().substring(3, method.getName().length() - "Susceptibility".length());
							// verify the field is visible or not.
							if (!applicableFieldIds.stream().anyMatch(e -> e != null && e.equalsIgnoreCase(drugName + "Susceptibility"))) {
								return;
							}
							String fieldId = drugName.toUpperCase() + "_SUSCEPTIBILITY";
							Drug drug = Drug.valueOf(drugName.toUpperCase());
							addResistanceResultField(fieldId, type, I18nProperties.getEnumCaption(drug));
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					});
			}
		}
	}

	private void addResistanceResultField(String fieldId, DrugSusceptibilityType drugSusceptibilityType, String caption) {
		if (drugSusceptibilityType == null) {
			return;
		}
		// 1. Build the individual Label + TextField container
		HorizontalLayout fieldLayout = new HorizontalLayout();
		fieldLayout.setWidth(80, Unit.PERCENTAGE);
		fieldLayout.setSpacing(true);
		fieldLayout.setMargin(false);

		Label label = new Label(caption);
		label.setWidth(120, Unit.PIXELS);

		TextField field = new TextField();
		field.setId(fieldId);
		field.setValue(I18nProperties.getEnumCaption(drugSusceptibilityType));
		field.setEnabled(false);
		field.setWidth(100, Unit.PERCENTAGE);
		CssStyles.style(field, ValoTheme.OPTIONGROUP_HORIZONTAL);

		fieldLayout.addComponents(label, field);
		fieldLayout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
		fieldLayout.setComponentAlignment(field, Alignment.MIDDLE_LEFT);
		fieldLayout.setExpandRatio(field, 1.0f);

		if (currentRowLayout == null || currentRowLayout.getComponentCount() == 2) {
			// first or odd field
			currentRowLayout = new HorizontalLayout();
			currentRowLayout.setWidth(50, Unit.PERCENTAGE); // Row spans ONLY 50% of parent width
			currentRowLayout.setSpacing(true);
			currentRowLayout.addComponent(fieldLayout);
			currentRowLayout.setExpandRatio(fieldLayout, 1.0f);
			verticalLayout.addComponent(currentRowLayout);
		} else {
			// adding the second field.
			currentRowLayout.setWidth(100, Unit.PERCENTAGE); // Expand row to 100% full width
			currentRowLayout.addComponent(fieldLayout);
			// Balance both fields so each occupies an equal 50% of the 100% width
			currentRowLayout.setExpandRatio(fieldLayout, 1.0f);
		}
	}

	public void updateFieldsVisibility(PathogenTestDto pathogenTestDto) {
		for (String locationId : componentLocationsList) {
			if (getComponent(locationId) != null)
				getComponent(locationId).setVisible(false);
		}

		if (pathogenTestDto != null) {
			Disease disease = pathogenTestDto.getTestedDisease();
			PathogenTestType pathogenTestType = pathogenTestDto.getTestType();

			if (disease != null && pathogenTestType != null) {
				List<String> applicableFieldIds =
					AnnotationFieldHelper.getFieldNamesWithMatchingDiseaseAndTestAnnotations(DrugSusceptibilityDto.class, disease, pathogenTestType);

				if (!applicableFieldIds.isEmpty()) {
					for (String applicableFieldId : applicableFieldIds) {
						Component component = getComponent(applicableFieldId);
						if (component != null) {
							component.setVisible(true);
						}
					}
				}
			}
		}
	}
}
