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

import static de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventsDto.I18N_PREFIX;
import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.List;

import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.therapy.Drug;
import de.symeda.sormas.api.therapy.DrugSusceptibilityDto;
import de.symeda.sormas.api.utils.AnnotationFieldHelper;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;

public class DrugSusceptibilityForm extends AbstractEditForm<DrugSusceptibilityDto> {

	private static final long serialVersionUID = -8824484702333610347L;

	private static final String FORM_HEADING_LOC = "formHeadingLoc";

	//@formatter:off
    private static final String HTML_LAYOUT =
		loc(FORM_HEADING_LOC) +
			fluidRow(
				fluidColumn(6, 0,
			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, DrugSusceptibilityDto.AMIKACIN_MIC, DrugSusceptibilityDto.AMIKACIN_SUSCEPTIBILITY)
					+ fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, DrugSusceptibilityDto.BEDAQUILINE_MIC, DrugSusceptibilityDto.BEDAQUILINE_SUSCEPTIBILITY)
					+ fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, DrugSusceptibilityDto.CAPREOMYCIN_MIC, DrugSusceptibilityDto.CAPREOMYCIN_SUSCEPTIBILITY)
					+ fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, DrugSusceptibilityDto.CIPROFLOXACIN_MIC, DrugSusceptibilityDto.CIPROFLOXACIN_SUSCEPTIBILITY)
					+ fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, DrugSusceptibilityDto.DELAMANID_MIC, DrugSusceptibilityDto.DELAMANID_SUSCEPTIBILITY)
					+ fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, DrugSusceptibilityDto.ETHAMBUTOL_MIC, DrugSusceptibilityDto.ETHAMBUTOL_SUSCEPTIBILITY)
					+ fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, DrugSusceptibilityDto.GATIFLOXACIN_MIC, DrugSusceptibilityDto.GATIFLOXACIN_SUSCEPTIBILITY)),
				fluidColumn(6, 0,
			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, DrugSusceptibilityDto.ISONIAZID_MIC, DrugSusceptibilityDto.ISONIAZID_SUSCEPTIBILITY)
					+ fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, DrugSusceptibilityDto.KANAMYCIN_MIC, DrugSusceptibilityDto.KANAMYCIN_SUSCEPTIBILITY)
					+ fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, DrugSusceptibilityDto.LEVOFLOXACIN_MIC, DrugSusceptibilityDto.LEVOFLOXACIN_SUSCEPTIBILITY)
					+ fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, DrugSusceptibilityDto.MOXIFLOXACIN_MIC, DrugSusceptibilityDto.MOXIFLOXACIN_SUSCEPTIBILITY)
					+ fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, DrugSusceptibilityDto.OFLOXACIN_MIC, DrugSusceptibilityDto.OFLOXACIN_SUSCEPTIBILITY)
					+ fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, DrugSusceptibilityDto.RIFAMPICIN_MIC, DrugSusceptibilityDto.RIFAMPICIN_SUSCEPTIBILITY)
					+ fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, DrugSusceptibilityDto.STREPTOMYCIN_MIC, DrugSusceptibilityDto.STREPTOMYCIN_SUSCEPTIBILITY))
			);
    //@formatter:on

	public DrugSusceptibilityForm(FieldVisibilityCheckers fieldVisibilityCheckers, UiFieldAccessCheckers fieldAccessCheckers) {
		super(DrugSusceptibilityDto.class, I18N_PREFIX, true, fieldVisibilityCheckers, fieldAccessCheckers);
		this.addStyleNames(CssStyles.VIEW_SECTION, CssStyles.VSPACE_2);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		Label formHeadingLabel = new Label(I18nProperties.getString(Strings.headingDrugSusceptibility));
		formHeadingLabel.addStyleName(H3);
		getContent().addComponent(formHeadingLabel, FORM_HEADING_LOC);

		addMicField(DrugSusceptibilityDto.AMIKACIN_MIC, Drug.AMIKACIN).setInputPrompt(I18nProperties.getString(Strings.promptMicValue));
		addResistanceResultField(DrugSusceptibilityDto.AMIKACIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));

		addMicField(DrugSusceptibilityDto.BEDAQUILINE_MIC, Drug.BEDAQUILINE).setInputPrompt(I18nProperties.getString(Strings.promptMicValue));
		addResistanceResultField(DrugSusceptibilityDto.BEDAQUILINE_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));

		addMicField(DrugSusceptibilityDto.CAPREOMYCIN_MIC, Drug.CAPREOMYCIN).setInputPrompt(I18nProperties.getString(Strings.promptMicValue));
		addResistanceResultField(DrugSusceptibilityDto.CAPREOMYCIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));

		addMicField(DrugSusceptibilityDto.CIPROFLOXACIN_MIC, Drug.CIPROFLOXACIN).setInputPrompt(I18nProperties.getString(Strings.promptMicValue));
		addResistanceResultField(DrugSusceptibilityDto.CIPROFLOXACIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));

		addMicField(DrugSusceptibilityDto.DELAMANID_MIC, Drug.DELAMANID).setInputPrompt(I18nProperties.getString(Strings.promptMicValue));
		addResistanceResultField(DrugSusceptibilityDto.DELAMANID_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));

		addMicField(DrugSusceptibilityDto.ETHAMBUTOL_MIC, Drug.ETHAMBUTOL).setInputPrompt(I18nProperties.getString(Strings.promptMicValue));
		addResistanceResultField(DrugSusceptibilityDto.ETHAMBUTOL_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));

		addMicField(DrugSusceptibilityDto.GATIFLOXACIN_MIC, Drug.GATIFLOXACIN).setInputPrompt(I18nProperties.getString(Strings.promptMicValue));
		addResistanceResultField(DrugSusceptibilityDto.GATIFLOXACIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));

		addMicField(DrugSusceptibilityDto.ISONIAZID_MIC, Drug.ISONIAZID).setInputPrompt(I18nProperties.getString(Strings.promptMicValue));
		addResistanceResultField(DrugSusceptibilityDto.ISONIAZID_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));

		addMicField(DrugSusceptibilityDto.KANAMYCIN_MIC, Drug.KANAMYCIN).setInputPrompt(I18nProperties.getString(Strings.promptMicValue));
		addResistanceResultField(DrugSusceptibilityDto.KANAMYCIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));

		addMicField(DrugSusceptibilityDto.LEVOFLOXACIN_MIC, Drug.LEVOFLOXACIN).setInputPrompt(I18nProperties.getString(Strings.promptMicValue));
		addResistanceResultField(DrugSusceptibilityDto.LEVOFLOXACIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));

		addMicField(DrugSusceptibilityDto.MOXIFLOXACIN_MIC, Drug.MOXIFLOXACIN).setInputPrompt(I18nProperties.getString(Strings.promptMicValue));
		addResistanceResultField(DrugSusceptibilityDto.MOXIFLOXACIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));

		addMicField(DrugSusceptibilityDto.OFLOXACIN_MIC, Drug.OFLOXACIN).setInputPrompt(I18nProperties.getString(Strings.promptMicValue));
		addResistanceResultField(DrugSusceptibilityDto.OFLOXACIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));

		addMicField(DrugSusceptibilityDto.RIFAMPICIN_MIC, Drug.RIFAMPICIN).setInputPrompt(I18nProperties.getString(Strings.promptMicValue));
		addResistanceResultField(DrugSusceptibilityDto.RIFAMPICIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));

		addMicField(DrugSusceptibilityDto.STREPTOMYCIN_MIC, Drug.STREPTOMYCIN).setInputPrompt(I18nProperties.getString(Strings.promptMicValue));
		addResistanceResultField(DrugSusceptibilityDto.STREPTOMYCIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));

		FieldHelper.hideFieldsNotInList(getFieldGroup(), List.of(), true);
	}

	private TextField addMicField(String fieldId, Drug drug) {
		TextField field = addField(fieldId, TextField.class);
		field.setCaption(I18nProperties.getEnumCaption(drug));
		field.setDescription(I18nProperties.getString(Strings.promptMicValue));
		field.addStyleNames(CssStyles.TEXTFIELD_ROW, CssStyles.TEXTFIELD_CAPTION_INLINE);
		field.setWidth(80, Unit.PIXELS);
		return field;
	}

	private ComboBox addResistanceResultField(String fieldId) {
		ComboBox field = addField(fieldId, ComboBox.class);
		field.setCaption(null);
		field.setDescription(I18nProperties.getString(Strings.promptResistanceResult));
		field.setWidth(150, Unit.PIXELS);
		return field;
	}

	public void updateFieldsVisibility(Disease disease, PathogenTestType pathogenTestType) {
		FieldHelper.hideFieldsNotInList(getFieldGroup(), List.of(), true);

		if (disease != null && pathogenTestType != null) {
			List<String> applicableFieldIds =
				AnnotationFieldHelper.getFieldNamesWithMatchingDiseaseAndTestAnnotations(DrugSusceptibilityDto.class, disease, pathogenTestType);

			if (!applicableFieldIds.isEmpty()) {
				FieldHelper.showOnlyFields(getFieldGroup(), applicableFieldIds, true);
			}
		}
	}
}
