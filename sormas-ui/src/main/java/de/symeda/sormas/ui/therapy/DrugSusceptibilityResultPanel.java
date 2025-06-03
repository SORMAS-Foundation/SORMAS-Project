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
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.CIPROFLOXACIN_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.DELAMANID_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.ETHAMBUTOL_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.GATIFLOXACIN_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.ISONIAZID_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.KANAMYCIN_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.LEVOFLOXACIN_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.MOXIFLOXACIN_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.OFLOXACIN_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.RIFAMPICIN_SUSCEPTIBILITY;
import static de.symeda.sormas.api.therapy.DrugSusceptibilityDto.STREPTOMYCIN_SUSCEPTIBILITY;
import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.List;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.therapy.Drug;
import de.symeda.sormas.api.therapy.DrugSusceptibilityDto;
import de.symeda.sormas.api.therapy.DrugSusceptibilityType;
import de.symeda.sormas.api.utils.AnnotationFieldHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.utils.CssStyles;

public class DrugSusceptibilityResultPanel extends CustomLayout {

	private static final long serialVersionUID = 8458057586239793721L;

	private static final String FORM_HEADING_LOC = "formHeadingLoc";

	private PathogenTestDto pathogenTestDto;

	//@formatter:off
    private static final String HTML_LAYOUT =
            loc(FORM_HEADING_LOC) +
                    fluidRow(
                            fluidColumn(4, 0,
                                    fluidRowLocs(AMIKACIN_SUSCEPTIBILITY)
                                            + fluidRowLocs(BEDAQUILINE_SUSCEPTIBILITY)
                                            + fluidRowLocs(CAPREOMYCIN_SUSCEPTIBILITY)
                                            + fluidRowLocs(CIPROFLOXACIN_SUSCEPTIBILITY)
                                            + fluidRowLocs(DELAMANID_SUSCEPTIBILITY)
                                            + fluidRowLocs(ETHAMBUTOL_SUSCEPTIBILITY)
                                            + fluidRowLocs(GATIFLOXACIN_SUSCEPTIBILITY)),
                            fluidColumn(4, 0,
                                    fluidRowLocs(ISONIAZID_SUSCEPTIBILITY)
                                            + fluidRowLocs(KANAMYCIN_SUSCEPTIBILITY)
                                            + fluidRowLocs(LEVOFLOXACIN_SUSCEPTIBILITY)
                                            + fluidRowLocs(MOXIFLOXACIN_SUSCEPTIBILITY)
                                            + fluidRowLocs(OFLOXACIN_SUSCEPTIBILITY)
                                            + fluidRowLocs(RIFAMPICIN_SUSCEPTIBILITY)
                                            + fluidRowLocs(STREPTOMYCIN_SUSCEPTIBILITY))
                    );
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
		STREPTOMYCIN_SUSCEPTIBILITY);

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
					AMIKACIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getAmikacinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.AMIKACIN));
				addResistanceResultField(
					BEDAQUILINE_SUSCEPTIBILITY,
					drugSusceptibilityDto.getBedaquilineSusceptibility(),
					I18nProperties.getEnumCaption(Drug.BEDAQUILINE));
				addResistanceResultField(
					CAPREOMYCIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getCapreomycinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.CAPREOMYCIN));
				addResistanceResultField(
					CIPROFLOXACIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getCiprofloxacinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.CIPROFLOXACIN));
				addResistanceResultField(
					DELAMANID_SUSCEPTIBILITY,
					drugSusceptibilityDto.getDelamanidSusceptibility(),
					I18nProperties.getEnumCaption(Drug.DELAMANID));
				addResistanceResultField(
					ETHAMBUTOL_SUSCEPTIBILITY,
					drugSusceptibilityDto.getEthambutolSusceptibility(),
					I18nProperties.getEnumCaption(Drug.ETHAMBUTOL));
				addResistanceResultField(
					GATIFLOXACIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getGatifloxacinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.GATIFLOXACIN));
				addResistanceResultField(
					ISONIAZID_SUSCEPTIBILITY,
					drugSusceptibilityDto.getIsoniazidSusceptibility(),
					I18nProperties.getEnumCaption(Drug.ISONIAZID));
				addResistanceResultField(
					KANAMYCIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getKanamycinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.KANAMYCIN));
				addResistanceResultField(
					LEVOFLOXACIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getLevofloxacinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.LEVOFLOXACIN));
				addResistanceResultField(
					MOXIFLOXACIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getMoxifloxacinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.MOXIFLOXACIN));
				addResistanceResultField(
					OFLOXACIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getOfloxacinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.OFLOXACIN));
				addResistanceResultField(
					RIFAMPICIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getRifampicinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.RIFAMPICIN));
				addResistanceResultField(
					STREPTOMYCIN_SUSCEPTIBILITY,
					drugSusceptibilityDto.getStreptomycinSusceptibility(),
					I18nProperties.getEnumCaption(Drug.STREPTOMYCIN));

				updateFieldsVisibility(pathogenTestDto);
			}
		}
	}

	private OptionGroup addResistanceResultField(String fieldId, DrugSusceptibilityType drugSusceptibilityType, String caption) {
		OptionGroup field = new OptionGroup();
		field.setId(fieldId);
		field.setCaption(caption);
		field.setWidth(100, Unit.PERCENTAGE);
		CssStyles.style(field, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_CAPTION_INLINE);
		field.addItems((Object[]) YesNoUnknown.values());

		if (drugSusceptibilityType != null) {
			field.setValue(drugSusceptibilityType.toYesNoUnknown());
		}

		field.setEnabled(false);
		addComponent(field, fieldId);
		return field;
	}

	public void updateFieldsVisibility(PathogenTestDto pathogenTestDto) {
		for (String locationId : componentLocationsList) {
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
