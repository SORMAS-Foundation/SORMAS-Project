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
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.AbstractField;

import com.vaadin.v7.ui.TextField;

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

	private PathogenTestDto pathogenTestDto;

	//@formatter:off
    private static final String HTML_LAYOUT =
            loc(FORM_HEADING_LOC)
                    + fluidRowLocs(ISONIAZID_SUSCEPTIBILITY, OFLOXACIN_SUSCEPTIBILITY)
                    + fluidRowLocs(ETHAMBUTOL_SUSCEPTIBILITY, STREPTOMYCIN_SUSCEPTIBILITY)
                    + fluidRowLocs(LEVOFLOXACIN_SUSCEPTIBILITY, MOXIFLOXACIN_SUSCEPTIBILITY)
                    + fluidRowLocs(DELAMANID_SUSCEPTIBILITY, CAPREOMYCIN_SUSCEPTIBILITY)
                    + fluidRowLocs(KANAMYCIN_SUSCEPTIBILITY, GATIFLOXACIN_SUSCEPTIBILITY)
                    + fluidRowLocs(CIPROFLOXACIN_SUSCEPTIBILITY, RIFAMPICIN_SUSCEPTIBILITY)
					+ fluidRowLocs(CEFTRIAXONE_SUSCEPTIBILITY, PENICILLIN_SUSCEPTIBILITY)
					+ fluidRowLocs(BEDAQUILINE_SUSCEPTIBILITY,  "")
					+ fluidRowLocs(AMIKACIN_SUSCEPTIBILITY, "")
					+ fluidRowLocs(ERYTHROMYCIN_SUSCEPTIBILITY,  "")
			;
    //@formatter:on

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
		setTemplateContents(HTML_LAYOUT);

		this.pathogenTestDto = pathogenTestDto;
		addFields();
	}

	private void addFields() {
		if (pathogenTestDto != null) {
			Label formHeadingLabel = new Label(I18nProperties.getString(Strings.headingDrugSusceptibility));
			formHeadingLabel.addStyleName(H3);
			addComponent(formHeadingLabel, FORM_HEADING_LOC);

			if (pathogenTestDto.getTestType() == PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY) {
				DrugSusceptibilityDto drugSusceptibilityDto = pathogenTestDto.getDrugSusceptibility();

				addResistanceResultField(
					ISONIAZID_SUSCEPTIBILITY,
					drugSusceptibilityDto.getIsoniazidSusceptibility(),
					I18nProperties.getEnumCaption(Drug.ISONIAZID),
					pathogenTestDto.getTestedDisease());

				addResistanceResultField(
					RIFAMPICIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getRifampicinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.RIFAMPICIN),
					pathogenTestDto.getTestedDisease());

				addResistanceResultField(
					ETHAMBUTOL_SUSCEPTIBILITY,
					drugSusceptibilityDto.getEthambutolSusceptibility(),
					I18nProperties.getEnumCaption(Drug.ETHAMBUTOL),
					pathogenTestDto.getTestedDisease());

				addResistanceResultField(
					STREPTOMYCIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getStreptomycinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.STREPTOMYCIN),
					pathogenTestDto.getTestedDisease());

				addResistanceResultField(
					LEVOFLOXACIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getLevofloxacinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.LEVOFLOXACIN),
					pathogenTestDto.getTestedDisease());

				addResistanceResultField(
					MOXIFLOXACIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getMoxifloxacinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.MOXIFLOXACIN),
					pathogenTestDto.getTestedDisease());

				addResistanceResultField(
					BEDAQUILINE_SUSCEPTIBILITY,
					drugSusceptibilityDto.getBedaquilineSusceptibility(),
					I18nProperties.getEnumCaption(Drug.BEDAQUILINE),
					pathogenTestDto.getTestedDisease());

				addResistanceResultField(
					DELAMANID_SUSCEPTIBILITY,
					drugSusceptibilityDto.getDelamanidSusceptibility(),
					I18nProperties.getEnumCaption(Drug.DELAMANID),
					pathogenTestDto.getTestedDisease());

				addResistanceResultField(
					CAPREOMYCIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getCapreomycinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.CAPREOMYCIN),
					pathogenTestDto.getTestedDisease());

				addResistanceResultField(
					KANAMYCIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getKanamycinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.KANAMYCIN),
					pathogenTestDto.getTestedDisease());

				addResistanceResultField(
					CIPROFLOXACIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getCiprofloxacinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.CIPROFLOXACIN),
					pathogenTestDto.getTestedDisease());

				addResistanceResultField(
					OFLOXACIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getOfloxacinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.OFLOXACIN),
					pathogenTestDto.getTestedDisease());

				addResistanceResultField(
					GATIFLOXACIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getGatifloxacinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.GATIFLOXACIN),
					pathogenTestDto.getTestedDisease());

				addResistanceResultField(
					AMIKACIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getAmikacinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.AMIKACIN),
					pathogenTestDto.getTestedDisease());

				// CEFTRIAXONE
				addResistanceResultField(
					CEFTRIAXONE_SUSCEPTIBILITY,
					drugSusceptibilityDto.getCeftriaxoneSusceptibility(),
					I18nProperties.getEnumCaption(Drug.CEFTRIAXONE),
					pathogenTestDto.getTestedDisease());

				// PENICILLIN
				addResistanceResultField(
					PENICILLIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getPenicillinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.PENICILLIN),
					pathogenTestDto.getTestedDisease());

				// ERYTHROMYCIN
				addResistanceResultField(
					ERYTHROMYCIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getErythromycinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.ERYTHROMYCIN),
					pathogenTestDto.getTestedDisease());

				updateFieldsVisibility(pathogenTestDto);
			}
		}
	}

	private AbstractField addResistanceResultField(String fieldId, DrugSusceptibilityType drugSusceptibilityType, String caption, Disease disease) {
		HorizontalLayout fieldLayout = new HorizontalLayout();
		fieldLayout.setSpacing(true);
		fieldLayout.setMargin(false);

		TextField field = new TextField();
		field.setId(fieldId);
		field.setWidth(70, Unit.PERCENTAGE);
		field.setCaption(null);

		CssStyles.style(field, ValoTheme.OPTIONGROUP_HORIZONTAL);

		if (drugSusceptibilityType != null) {
			field.setValue(drugSusceptibilityType.name());
		}

		field.setEnabled(false);
		Label label = new Label(I18nProperties.getEnumCaption(Drug.valueOf(field.getId().replace("Susceptibility", "").toUpperCase())));
		label.setWidth(100, Unit.PIXELS);
		fieldLayout.addComponents(label, field);
		fieldLayout.setWidth(100, Unit.PERCENTAGE);
		fieldLayout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
		fieldLayout.setComponentAlignment(field, Alignment.MIDDLE_LEFT);
		fieldLayout.setExpandRatio(field, 1);
		addComponent(fieldLayout, fieldId);
		return field;
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
					// updating the visibility of custom layout components
					List<String> susceptibilities = applicableFieldIds.stream()
						.filter(e -> e.endsWith("Susceptibility"))
						.map(applicableFieldId -> "LAYOUT_" + applicableFieldId.replace("Susceptibility", "").toUpperCase())
						.collect(Collectors.toList());

					// Hide the custom layout components that are not in the valid susceptibilities list
					for (String layout : Arrays
						.asList("LAYOUT_CEFTRIAXONE", "LAYOUT_CIPROFLOXACIN", "LAYOUT_ERYTHROMYCIN", "LAYOUT_PENICILLIN", "LAYOUT_RIFAMPICIN")) {
						if (!susceptibilities.contains(layout) && getComponent(layout) != null) {
							getComponent(layout).setVisible(false);
						}
					}
				}
			}
		}
	}
}
