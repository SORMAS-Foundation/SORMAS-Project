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
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.therapy.Drug;
import de.symeda.sormas.api.therapy.DrugSusceptibilityDto;
import de.symeda.sormas.api.therapy.DrugSusceptibilityType;
import de.symeda.sormas.api.utils.AnnotationFieldHelper;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;

public class DrugSusceptibilityForm extends AbstractEditForm<DrugSusceptibilityDto> {

	private static final long serialVersionUID = -8824484702333610347L;

	private static final String FORM_HEADING_LOC = "formHeadingLoc";
	private static final String HEADER_DRUG_LOC = "headerDrugLoc";
	private static final String HEADER_METHOD_LOC = "headerMethodLoc";
	private static final String HEADER_VALUE_LOC = "headerValueLoc";
	private static final String HEADER_INTERPRETATION_LOC = "headerInterpretationLoc";
	private static final String AMIKACIN_LABEL_LOC = "amikacinLabelLoc";
	private static final String BEDAQUILINE_LABEL_LOC = "bedaquilineLabelLoc";
	private static final String CAPREOMYCIN_LABEL_LOC = "capreomycinLabelLoc";
	private static final String CIPROFLOXACIN_LABEL_LOC = "ciprofloxacinLabelLoc";
	private static final String DELAMANID_LABEL_LOC = "delamanidLabelLoc";
	private static final String ETHAMBUTOL_LABEL_LOC = "ethambutolLabelLoc";
	private static final String GATIFLOXACIN_LABEL_LOC = "gatifloxacinLabelLoc";
	private static final String ISONIAZID_LABEL_LOC = "isoniazidLabelLoc";
	private static final String KANAMYCIN_LABEL_LOC = "kanamycinLabelLoc";
	private static final String LEVOFLOXACIN_LABEL_LOC = "levofloxacinLabelLoc";
	private static final String MOXIFLOXACIN_LABEL_LOC = "moxifloxacinLabelLoc";
	private static final String OFLOXACIN_LABEL_LOC = "ofloxacinLabelLoc";
	private static final String RIFAMPICIN_LABEL_LOC = "rifampicinLabelLoc";
	private static final String STREPTOMYCIN_LABEL_LOC = "streptomycinLabelLoc";
	private static final String CEFTRIAXONE_LABEL_LOC = "ceftriaxoneLabelLoc";
	private static final String PENICILLIN_LABEL_LOC = "penicillinLabelLoc";
	private static final String ERYTHROMYCIN_LABEL_LOC = "erythromycinLabelLoc";
	private static final String AZITHROMYCIN_LABEL_LOC = "azithromycinLabelLoc";
	private static final String AMPICILLIN_LABEL_LOC = "ampicillinLabelLoc";
	private static final String CEFTAZIDIME_LABEL_LOC = "ceftazidimeLabelLoc";
	private static final String CEFOTAXIME_LABEL_LOC = "cefotaximeLabelLoc";
	private static final String TRIMETHOPRIM_SULFAMETHOXAZOLE_LABEL_LOC = "trimethoprimSulfamethoxazoleLabelLoc";

	private Label formHeadingLabel;

	/**
	 * Column-header labels (Method / Value / Interpretation), shown/hidden together with the form heading.
	 * Lazily initialised because {@link AbstractEditForm}'s constructor calls {@code addFields()} before
	 * subclass field initialisers run, so an inline initialiser would still be null at that point.
	 */
	private List<Label> columnHeaderLabels;

	/**
	 * Drug-name labels keyed by the drug's MIC field id, so their visibility tracks the drug's fields.
	 * Lazily initialised because {@link AbstractEditForm}'s constructor calls {@code addFields()} before
	 * subclass field initialisers run, so an inline initialiser would still be null at that point.
	 */
	private Map<String, Label> drugLabelsByMicFieldId;

	//@formatter:off
    private static final String HTML_LAYOUT =
		loc(FORM_HEADING_LOC) +
			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, HEADER_DRUG_LOC, HEADER_METHOD_LOC, HEADER_VALUE_LOC, HEADER_INTERPRETATION_LOC)
			+ 			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, AMIKACIN_LABEL_LOC, DrugSusceptibilityDto.AMIKACIN_METHOD, DrugSusceptibilityDto.AMIKACIN_MIC, DrugSusceptibilityDto.AMIKACIN_SUSCEPTIBILITY)
			+ 			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, BEDAQUILINE_LABEL_LOC, DrugSusceptibilityDto.BEDAQUILINE_METHOD, DrugSusceptibilityDto.BEDAQUILINE_MIC, DrugSusceptibilityDto.BEDAQUILINE_SUSCEPTIBILITY)
			+ 			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, CAPREOMYCIN_LABEL_LOC, DrugSusceptibilityDto.CAPREOMYCIN_METHOD, DrugSusceptibilityDto.CAPREOMYCIN_MIC, DrugSusceptibilityDto.CAPREOMYCIN_SUSCEPTIBILITY)
			+ 			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, CIPROFLOXACIN_LABEL_LOC, DrugSusceptibilityDto.CIPROFLOXACIN_METHOD, DrugSusceptibilityDto.CIPROFLOXACIN_MIC, DrugSusceptibilityDto.CIPROFLOXACIN_SUSCEPTIBILITY)
			+ 			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, DELAMANID_LABEL_LOC, DrugSusceptibilityDto.DELAMANID_METHOD, DrugSusceptibilityDto.DELAMANID_MIC, DrugSusceptibilityDto.DELAMANID_SUSCEPTIBILITY)
			+ 			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, ETHAMBUTOL_LABEL_LOC, DrugSusceptibilityDto.ETHAMBUTOL_METHOD, DrugSusceptibilityDto.ETHAMBUTOL_MIC, DrugSusceptibilityDto.ETHAMBUTOL_SUSCEPTIBILITY)
			+ 			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, GATIFLOXACIN_LABEL_LOC, DrugSusceptibilityDto.GATIFLOXACIN_METHOD, DrugSusceptibilityDto.GATIFLOXACIN_MIC, DrugSusceptibilityDto.GATIFLOXACIN_SUSCEPTIBILITY)
			+ 			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, AZITHROMYCIN_LABEL_LOC, DrugSusceptibilityDto.AZITHROMYCIN_METHOD, DrugSusceptibilityDto.AZITHROMYCIN_MIC, DrugSusceptibilityDto.AZITHROMYCIN_SUSCEPTIBILITY)
			+ 			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, AMPICILLIN_LABEL_LOC, DrugSusceptibilityDto.AMPICILLIN_METHOD, DrugSusceptibilityDto.AMPICILLIN_MIC, DrugSusceptibilityDto.AMPICILLIN_SUSCEPTIBILITY)
			+			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, ISONIAZID_LABEL_LOC, DrugSusceptibilityDto.ISONIAZID_METHOD, DrugSusceptibilityDto.ISONIAZID_MIC, DrugSusceptibilityDto.ISONIAZID_SUSCEPTIBILITY)
			+ 			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, KANAMYCIN_LABEL_LOC, DrugSusceptibilityDto.KANAMYCIN_METHOD, DrugSusceptibilityDto.KANAMYCIN_MIC, DrugSusceptibilityDto.KANAMYCIN_SUSCEPTIBILITY)
			+ 			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, LEVOFLOXACIN_LABEL_LOC, DrugSusceptibilityDto.LEVOFLOXACIN_METHOD, DrugSusceptibilityDto.LEVOFLOXACIN_MIC, DrugSusceptibilityDto.LEVOFLOXACIN_SUSCEPTIBILITY)
			+ 			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, MOXIFLOXACIN_LABEL_LOC, DrugSusceptibilityDto.MOXIFLOXACIN_METHOD, DrugSusceptibilityDto.MOXIFLOXACIN_MIC, DrugSusceptibilityDto.MOXIFLOXACIN_SUSCEPTIBILITY)
			+ 			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, OFLOXACIN_LABEL_LOC, DrugSusceptibilityDto.OFLOXACIN_METHOD, DrugSusceptibilityDto.OFLOXACIN_MIC, DrugSusceptibilityDto.OFLOXACIN_SUSCEPTIBILITY)
			+ 			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, RIFAMPICIN_LABEL_LOC, DrugSusceptibilityDto.RIFAMPICIN_METHOD, DrugSusceptibilityDto.RIFAMPICIN_MIC, DrugSusceptibilityDto.RIFAMPICIN_SUSCEPTIBILITY)
			+ 			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, STREPTOMYCIN_LABEL_LOC, DrugSusceptibilityDto.STREPTOMYCIN_METHOD, DrugSusceptibilityDto.STREPTOMYCIN_MIC, DrugSusceptibilityDto.STREPTOMYCIN_SUSCEPTIBILITY)
			+ 			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, CEFTRIAXONE_LABEL_LOC, DrugSusceptibilityDto.CEFTRIAXONE_METHOD, DrugSusceptibilityDto.CEFTRIAXONE_MIC, DrugSusceptibilityDto.CEFTRIAXONE_SUSCEPTIBILITY)
			+ 			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, PENICILLIN_LABEL_LOC, DrugSusceptibilityDto.PENICILLIN_METHOD, DrugSusceptibilityDto.PENICILLIN_MIC, DrugSusceptibilityDto.PENICILLIN_SUSCEPTIBILITY)
			+ 			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, CEFTAZIDIME_LABEL_LOC, DrugSusceptibilityDto.CEFTAZIDIME_METHOD, DrugSusceptibilityDto.CEFTAZIDIME_MIC, DrugSusceptibilityDto.CEFTAZIDIME_SUSCEPTIBILITY)
			+ 			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, CEFOTAXIME_LABEL_LOC, DrugSusceptibilityDto.CEFOTAXIME_METHOD, DrugSusceptibilityDto.CEFOTAXIME_MIC, DrugSusceptibilityDto.CEFOTAXIME_SUSCEPTIBILITY)
			+ 			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, ERYTHROMYCIN_LABEL_LOC, DrugSusceptibilityDto.ERYTHROMYCIN_METHOD, DrugSusceptibilityDto.ERYTHROMYCIN_MIC, DrugSusceptibilityDto.ERYTHROMYCIN_SUSCEPTIBILITY)
			+ 			fluidRowLocsCss(CssStyles.GRID_ROW_GAP_1, TRIMETHOPRIM_SULFAMETHOXAZOLE_LABEL_LOC, DrugSusceptibilityDto.TRIMETHOPRIM_SULFAMETHOXAZOLE_METHOD, DrugSusceptibilityDto.TRIMETHOPRIM_SULFAMETHOXAZOLE_MIC, DrugSusceptibilityDto.TRIMETHOPRIM_SULFAMETHOXAZOLE_SUSCEPTIBILITY);
    //@formatter:on

	public DrugSusceptibilityForm(FieldVisibilityCheckers fieldVisibilityCheckers, UiFieldAccessCheckers fieldAccessCheckers) {
		super(DrugSusceptibilityDto.class, I18N_PREFIX, true, fieldVisibilityCheckers, fieldAccessCheckers);
		//this.addStyleNames(CssStyles.VIEW_SECTION, CssStyles.VSPACE_2);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		formHeadingLabel = new Label(I18nProperties.getString(Strings.headingDrugSusceptibility));
		formHeadingLabel.addStyleName(H3);
		getContent().addComponent(formHeadingLabel, FORM_HEADING_LOC);
		formHeadingLabel.setVisible(false);

		// Column headers over the Method | Value | Interpretation columns (drug-name column left blank).
		addColumnHeader(HEADER_METHOD_LOC, I18nProperties.getString(Strings.headingAstMethod));
		addColumnHeader(HEADER_VALUE_LOC, I18nProperties.getString(Strings.headingAstValue));
		addColumnHeader(HEADER_INTERPRETATION_LOC, I18nProperties.getString(Strings.headingAstInterpretation));

		addDrugLabel(AMIKACIN_LABEL_LOC, Drug.AMIKACIN, DrugSusceptibilityDto.AMIKACIN_MIC);
		addMicField(DrugSusceptibilityDto.AMIKACIN_MIC);
		addResistanceResultField(DrugSusceptibilityDto.AMIKACIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.AMIKACIN_METHOD);

		addDrugLabel(BEDAQUILINE_LABEL_LOC, Drug.BEDAQUILINE, DrugSusceptibilityDto.BEDAQUILINE_MIC);
		addMicField(DrugSusceptibilityDto.BEDAQUILINE_MIC);
		addResistanceResultField(DrugSusceptibilityDto.BEDAQUILINE_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.BEDAQUILINE_METHOD);

		addDrugLabel(CAPREOMYCIN_LABEL_LOC, Drug.CAPREOMYCIN, DrugSusceptibilityDto.CAPREOMYCIN_MIC);
		addMicField(DrugSusceptibilityDto.CAPREOMYCIN_MIC);
		addResistanceResultField(DrugSusceptibilityDto.CAPREOMYCIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.CAPREOMYCIN_METHOD);

		addDrugLabel(CIPROFLOXACIN_LABEL_LOC, Drug.CIPROFLOXACIN, DrugSusceptibilityDto.CIPROFLOXACIN_MIC);
		addMicField(DrugSusceptibilityDto.CIPROFLOXACIN_MIC);
		addResistanceResultField(DrugSusceptibilityDto.CIPROFLOXACIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.CIPROFLOXACIN_METHOD);

		addDrugLabel(DELAMANID_LABEL_LOC, Drug.DELAMANID, DrugSusceptibilityDto.DELAMANID_MIC);
		addMicField(DrugSusceptibilityDto.DELAMANID_MIC);
		addResistanceResultField(DrugSusceptibilityDto.DELAMANID_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.DELAMANID_METHOD);

		addDrugLabel(ETHAMBUTOL_LABEL_LOC, Drug.ETHAMBUTOL, DrugSusceptibilityDto.ETHAMBUTOL_MIC);
		addMicField(DrugSusceptibilityDto.ETHAMBUTOL_MIC);
		addResistanceResultField(DrugSusceptibilityDto.ETHAMBUTOL_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.ETHAMBUTOL_METHOD);

		addDrugLabel(GATIFLOXACIN_LABEL_LOC, Drug.GATIFLOXACIN, DrugSusceptibilityDto.GATIFLOXACIN_MIC);
		addMicField(DrugSusceptibilityDto.GATIFLOXACIN_MIC);
		addResistanceResultField(DrugSusceptibilityDto.GATIFLOXACIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.GATIFLOXACIN_METHOD);

		addDrugLabel(ISONIAZID_LABEL_LOC, Drug.ISONIAZID, DrugSusceptibilityDto.ISONIAZID_MIC);
		addMicField(DrugSusceptibilityDto.ISONIAZID_MIC);
		addResistanceResultField(DrugSusceptibilityDto.ISONIAZID_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.ISONIAZID_METHOD);

		addDrugLabel(KANAMYCIN_LABEL_LOC, Drug.KANAMYCIN, DrugSusceptibilityDto.KANAMYCIN_MIC);
		addMicField(DrugSusceptibilityDto.KANAMYCIN_MIC);
		addResistanceResultField(DrugSusceptibilityDto.KANAMYCIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.KANAMYCIN_METHOD);

		addDrugLabel(LEVOFLOXACIN_LABEL_LOC, Drug.LEVOFLOXACIN, DrugSusceptibilityDto.LEVOFLOXACIN_MIC);
		addMicField(DrugSusceptibilityDto.LEVOFLOXACIN_MIC);
		addResistanceResultField(DrugSusceptibilityDto.LEVOFLOXACIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.LEVOFLOXACIN_METHOD);

		addDrugLabel(MOXIFLOXACIN_LABEL_LOC, Drug.MOXIFLOXACIN, DrugSusceptibilityDto.MOXIFLOXACIN_MIC);
		addMicField(DrugSusceptibilityDto.MOXIFLOXACIN_MIC);
		addResistanceResultField(DrugSusceptibilityDto.MOXIFLOXACIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.MOXIFLOXACIN_METHOD);

		addDrugLabel(OFLOXACIN_LABEL_LOC, Drug.OFLOXACIN, DrugSusceptibilityDto.OFLOXACIN_MIC);
		addMicField(DrugSusceptibilityDto.OFLOXACIN_MIC);
		addResistanceResultField(DrugSusceptibilityDto.OFLOXACIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.OFLOXACIN_METHOD);

		addDrugLabel(RIFAMPICIN_LABEL_LOC, Drug.RIFAMPICIN, DrugSusceptibilityDto.RIFAMPICIN_MIC);
		addMicField(DrugSusceptibilityDto.RIFAMPICIN_MIC);
		addResistanceResultField(DrugSusceptibilityDto.RIFAMPICIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.RIFAMPICIN_METHOD);

		addDrugLabel(STREPTOMYCIN_LABEL_LOC, Drug.STREPTOMYCIN, DrugSusceptibilityDto.STREPTOMYCIN_MIC);
		addMicField(DrugSusceptibilityDto.STREPTOMYCIN_MIC);
		addResistanceResultField(DrugSusceptibilityDto.STREPTOMYCIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.STREPTOMYCIN_METHOD);

		addDrugLabel(CEFTRIAXONE_LABEL_LOC, Drug.CEFTRIAXONE, DrugSusceptibilityDto.CEFTRIAXONE_MIC);
		addMicField(DrugSusceptibilityDto.CEFTRIAXONE_MIC);
		addResistanceResultField(DrugSusceptibilityDto.CEFTRIAXONE_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.CEFTRIAXONE_METHOD);
		addDrugLabel(PENICILLIN_LABEL_LOC, Drug.PENICILLIN, DrugSusceptibilityDto.PENICILLIN_MIC);
		addMicField(DrugSusceptibilityDto.PENICILLIN_MIC);
		addResistanceResultField(DrugSusceptibilityDto.PENICILLIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.PENICILLIN_METHOD);

		addDrugLabel(ERYTHROMYCIN_LABEL_LOC, Drug.ERYTHROMYCIN, DrugSusceptibilityDto.ERYTHROMYCIN_MIC);
		addMicField(DrugSusceptibilityDto.ERYTHROMYCIN_MIC);
		addResistanceResultField(DrugSusceptibilityDto.ERYTHROMYCIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.ERYTHROMYCIN_METHOD);

		addDrugLabel(AZITHROMYCIN_LABEL_LOC, Drug.AZITHROMYCIN, DrugSusceptibilityDto.AZITHROMYCIN_MIC);
		addMicField(DrugSusceptibilityDto.AZITHROMYCIN_MIC);
		addResistanceResultField(DrugSusceptibilityDto.AZITHROMYCIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.AZITHROMYCIN_METHOD);

		addDrugLabel(CEFTAZIDIME_LABEL_LOC, Drug.CEFTAZIDIME, DrugSusceptibilityDto.CEFTAZIDIME_MIC);
		addMicField(DrugSusceptibilityDto.CEFTAZIDIME_MIC);
		addResistanceResultField(DrugSusceptibilityDto.CEFTAZIDIME_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.CEFTAZIDIME_METHOD);

		addDrugLabel(CEFOTAXIME_LABEL_LOC, Drug.CEFOTAXIME, DrugSusceptibilityDto.CEFOTAXIME_MIC);
		addMicField(DrugSusceptibilityDto.CEFOTAXIME_MIC);
		addResistanceResultField(DrugSusceptibilityDto.CEFOTAXIME_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.CEFOTAXIME_METHOD);

		addDrugLabel(AMPICILLIN_LABEL_LOC, Drug.AMPICILLIN, DrugSusceptibilityDto.AMPICILLIN_MIC);
		addMicField(DrugSusceptibilityDto.AMPICILLIN_MIC);
		addResistanceResultField(DrugSusceptibilityDto.AMPICILLIN_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.AMPICILLIN_METHOD);

		addDrugLabel(
			TRIMETHOPRIM_SULFAMETHOXAZOLE_LABEL_LOC,
			Drug.TRIMETHOPRIM_SULFAMETHOXAZOLE,
			DrugSusceptibilityDto.TRIMETHOPRIM_SULFAMETHOXAZOLE_MIC);
		addMicField(DrugSusceptibilityDto.TRIMETHOPRIM_SULFAMETHOXAZOLE_MIC);
		addResistanceResultField(DrugSusceptibilityDto.TRIMETHOPRIM_SULFAMETHOXAZOLE_SUSCEPTIBILITY)
			.setInputPrompt(I18nProperties.getString(Strings.promptResistanceResult));
		addMethodField(DrugSusceptibilityDto.TRIMETHOPRIM_SULFAMETHOXAZOLE_METHOD);

		FieldHelper.hideFieldsNotInList(getFieldGroup(), List.of(), true);
	}

	private TextField addMicField(String fieldId) {
		// The drug name is rendered as a dedicated label column (addDrugLabel), so the value field itself
		// carries no inline caption — keeping the row aligned without the theme's fixed inline-caption
		// margin clipping longer drug names. Holds free text (e.g. "≤0.125 mg/L" or "mecA detected").
		TextField field = addField(fieldId, TextField.class);
		field.setCaption(null);
		field.setDescription(I18nProperties.getString(Strings.promptMicValue));
		field.setInputPrompt(I18nProperties.getString(Strings.promptMicValue));
		field.setWidth(100, Unit.PERCENTAGE);
		return field;
	}

	/** Adds an uppercase column header, tracked so it can be shown/hidden with the form heading. */
	private void addColumnHeader(String headerLoc, String caption) {
		if (columnHeaderLabels == null) {
			columnHeaderLabels = new java.util.ArrayList<>();
		}
		Label label = new Label(caption);
		label.addStyleName(CssStyles.LABEL_BOLD);
		label.setVisible(false);
		getContent().addComponent(label, headerLoc);
		columnHeaderLabels.add(label);
	}

	/**
	 * Adds the drug name as a standalone label at the row's leading column, registered against the
	 * drug's MIC field id so {@link #updateFieldsVisibility} can hide it together with the drug's fields.
	 */
	private void addDrugLabel(String labelLoc, Drug drug, String micFieldId) {
		if (drugLabelsByMicFieldId == null) {
			drugLabelsByMicFieldId = new java.util.HashMap<>();
		}
		Label label = new Label(I18nProperties.getEnumCaption(drug));
		label.addStyleName(CssStyles.LABEL_BOLD);
		getContent().addComponent(label, labelLoc);
		drugLabelsByMicFieldId.put(micFieldId, label);
	}

	/** Shows a drug's label iff its fields are applicable (its MIC field id is in {@code visibleFieldIds}). */
	private void updateDrugLabelsVisibility(List<String> visibleFieldIds) {
		if (drugLabelsByMicFieldId == null) {
			return;
		}
		drugLabelsByMicFieldId.forEach((micFieldId, label) -> label.setVisible(visibleFieldIds != null && visibleFieldIds.contains(micFieldId)));
	}

	private ComboBox addResistanceResultField(String fieldId) {
		ComboBox field = addField(fieldId, ComboBox.class);
		field.setCaption(null);
		field.setDescription(I18nProperties.getString(Strings.promptResistanceResult));
		field.setWidth(100, Unit.PERCENTAGE);
		return field;
	}

	private ComboBox addMethodField(String fieldId) {
		ComboBox field = addField(fieldId, ComboBox.class);
		field.setCaption(null);
		field.setInputPrompt(I18nProperties.getString(Strings.promptSusceptibilityMethod));
		field.setDescription(I18nProperties.getString(Strings.promptSusceptibilityMethod));
		field.setWidth(100, Unit.PERCENTAGE);
		return field;
	}

	@Override
	public void markAsDirty() {
		super.markAsDirty();
	}

	public void forceUpdateDrugSusceptibilityFields() {
		final DrugSusceptibilityDto drugSusceptibilityDto = getValue();
		if (drugSusceptibilityDto == null) {
			return;
		}

		final Map<String, Optional<DrugSusceptibilityType>> applicableFieldIds = Collections.unmodifiableMap(
			Map.ofEntries(
				Map.entry(DrugSusceptibilityDto.AMIKACIN_SUSCEPTIBILITY, Optional.ofNullable(drugSusceptibilityDto.getAmikacinSusceptibility())),
				Map.entry(
					DrugSusceptibilityDto.BEDAQUILINE_SUSCEPTIBILITY,
					Optional.ofNullable(drugSusceptibilityDto.getBedaquilineSusceptibility())),
				Map.entry(
					DrugSusceptibilityDto.CAPREOMYCIN_SUSCEPTIBILITY,
					Optional.ofNullable(drugSusceptibilityDto.getCapreomycinSusceptibility())),
				Map.entry(
					DrugSusceptibilityDto.CIPROFLOXACIN_SUSCEPTIBILITY,
					Optional.ofNullable(drugSusceptibilityDto.getCiprofloxacinSusceptibility())),
				Map.entry(DrugSusceptibilityDto.DELAMANID_SUSCEPTIBILITY, Optional.ofNullable(drugSusceptibilityDto.getDelamanidSusceptibility())),
				Map.entry(DrugSusceptibilityDto.ETHAMBUTOL_SUSCEPTIBILITY, Optional.ofNullable(drugSusceptibilityDto.getEthambutolSusceptibility())),
				Map.entry(
					DrugSusceptibilityDto.GATIFLOXACIN_SUSCEPTIBILITY,
					Optional.ofNullable(drugSusceptibilityDto.getGatifloxacinSusceptibility())),
				Map.entry(
					DrugSusceptibilityDto.CEFTRIAXONE_SUSCEPTIBILITY,
					Optional.ofNullable(drugSusceptibilityDto.getCeftriaxoneSusceptibility())),
				Map.entry(
					DrugSusceptibilityDto.ERYTHROMYCIN_SUSCEPTIBILITY,
					Optional.ofNullable(drugSusceptibilityDto.getErythromycinSusceptibility())),
				Map.entry(DrugSusceptibilityDto.ISONIAZID_SUSCEPTIBILITY, Optional.ofNullable(drugSusceptibilityDto.getIsoniazidSusceptibility())),
				Map.entry(DrugSusceptibilityDto.KANAMYCIN_SUSCEPTIBILITY, Optional.ofNullable(drugSusceptibilityDto.getKanamycinSusceptibility())),
				Map.entry(
					DrugSusceptibilityDto.LEVOFLOXACIN_SUSCEPTIBILITY,
					Optional.ofNullable(drugSusceptibilityDto.getLevofloxacinSusceptibility())),
				Map.entry(
					DrugSusceptibilityDto.MOXIFLOXACIN_SUSCEPTIBILITY,
					Optional.ofNullable(drugSusceptibilityDto.getMoxifloxacinSusceptibility())),
				Map.entry(DrugSusceptibilityDto.OFLOXACIN_SUSCEPTIBILITY, Optional.ofNullable(drugSusceptibilityDto.getOfloxacinSusceptibility())),
				Map.entry(DrugSusceptibilityDto.RIFAMPICIN_SUSCEPTIBILITY, Optional.ofNullable(drugSusceptibilityDto.getRifampicinSusceptibility())),
				Map.entry(
					DrugSusceptibilityDto.STREPTOMYCIN_SUSCEPTIBILITY,
					Optional.ofNullable(drugSusceptibilityDto.getStreptomycinSusceptibility())),
				Map.entry(DrugSusceptibilityDto.PENICILLIN_SUSCEPTIBILITY, Optional.ofNullable(drugSusceptibilityDto.getPenicillinSusceptibility())),
				Map.entry(
					DrugSusceptibilityDto.AZITHROMYCIN_SUSCEPTIBILITY,
					Optional.ofNullable(drugSusceptibilityDto.getAzithromycinSusceptibility())),
				Map.entry(
					DrugSusceptibilityDto.CEFTAZIDIME_SUSCEPTIBILITY,
					Optional.ofNullable(drugSusceptibilityDto.getCeftazidimeSusceptibility())),
				Map.entry(DrugSusceptibilityDto.CEFOTAXIME_SUSCEPTIBILITY, Optional.ofNullable(drugSusceptibilityDto.getCefotaximeSusceptibility())),
				Map.entry(DrugSusceptibilityDto.AMPICILLIN_SUSCEPTIBILITY, Optional.ofNullable(drugSusceptibilityDto.getAmpicillinSusceptibility())),
				Map.entry(
					DrugSusceptibilityDto.TRIMETHOPRIM_SULFAMETHOXAZOLE_SUSCEPTIBILITY,
					Optional.ofNullable(drugSusceptibilityDto.getTrimethoprimSulfamethoxazoleSusceptibility()))));

		applicableFieldIds.forEach(this::forceUpdateDrugSusceptibilityField);

		final Map<String, Optional<String>> drugSusceptibilityMic = Collections.unmodifiableMap(
			Map.ofEntries(
				Map.entry(DrugSusceptibilityDto.AMIKACIN_MIC, Optional.ofNullable(drugSusceptibilityDto.getAmikacinMic())),
				Map.entry(DrugSusceptibilityDto.BEDAQUILINE_MIC, Optional.ofNullable(drugSusceptibilityDto.getBedaquilineMic())),
				Map.entry(DrugSusceptibilityDto.CAPREOMYCIN_MIC, Optional.ofNullable(drugSusceptibilityDto.getCapreomycinMic())),
				Map.entry(DrugSusceptibilityDto.CIPROFLOXACIN_MIC, Optional.ofNullable(drugSusceptibilityDto.getCiprofloxacinMic())),
				Map.entry(DrugSusceptibilityDto.DELAMANID_MIC, Optional.ofNullable(drugSusceptibilityDto.getDelamanidMic())),
				Map.entry(DrugSusceptibilityDto.ETHAMBUTOL_MIC, Optional.ofNullable(drugSusceptibilityDto.getEthambutolMic())),
				Map.entry(DrugSusceptibilityDto.GATIFLOXACIN_MIC, Optional.ofNullable(drugSusceptibilityDto.getGatifloxacinMic())),
				Map.entry(DrugSusceptibilityDto.CEFTRIAXONE_MIC, Optional.ofNullable(drugSusceptibilityDto.getCeftriaxoneMic())),
				Map.entry(DrugSusceptibilityDto.ERYTHROMYCIN_MIC, Optional.ofNullable(drugSusceptibilityDto.getErythromycinMic())),
				Map.entry(DrugSusceptibilityDto.ISONIAZID_MIC, Optional.ofNullable(drugSusceptibilityDto.getIsoniazidMic())),
				Map.entry(DrugSusceptibilityDto.KANAMYCIN_MIC, Optional.ofNullable(drugSusceptibilityDto.getKanamycinMic())),
				Map.entry(DrugSusceptibilityDto.LEVOFLOXACIN_MIC, Optional.ofNullable(drugSusceptibilityDto.getLevofloxacinMic())),
				Map.entry(DrugSusceptibilityDto.MOXIFLOXACIN_MIC, Optional.ofNullable(drugSusceptibilityDto.getMoxifloxacinMic())),
				Map.entry(DrugSusceptibilityDto.OFLOXACIN_MIC, Optional.ofNullable(drugSusceptibilityDto.getOfloxacinMic())),
				Map.entry(DrugSusceptibilityDto.RIFAMPICIN_MIC, Optional.ofNullable(drugSusceptibilityDto.getRifampicinMic())),
				Map.entry(DrugSusceptibilityDto.STREPTOMYCIN_MIC, Optional.ofNullable(drugSusceptibilityDto.getStreptomycinMic())),
				Map.entry(DrugSusceptibilityDto.PENICILLIN_MIC, Optional.ofNullable(drugSusceptibilityDto.getPenicillinMic())),
				Map.entry(DrugSusceptibilityDto.AZITHROMYCIN_MIC, Optional.ofNullable(drugSusceptibilityDto.getAzithromycinMic())),
				Map.entry(DrugSusceptibilityDto.CEFTAZIDIME_MIC, Optional.ofNullable(drugSusceptibilityDto.getCeftazidimeMic())),
				Map.entry(DrugSusceptibilityDto.CEFOTAXIME_MIC, Optional.ofNullable(drugSusceptibilityDto.getCefotaximeMic())),
				Map.entry(DrugSusceptibilityDto.AMPICILLIN_MIC, Optional.ofNullable(drugSusceptibilityDto.getAmpicillinMic())),
				Map.entry(
					DrugSusceptibilityDto.TRIMETHOPRIM_SULFAMETHOXAZOLE_MIC,
					Optional.ofNullable(drugSusceptibilityDto.getTrimethoprimSulfamethoxazoleMic()))));

		drugSusceptibilityMic.forEach(this::forceUpdateDrugSusceptibilityMicField);

	}

	private void forceUpdateDrugSusceptibilityField(String fieldId, Optional<DrugSusceptibilityType> drugSusceptibilityType) {
		final ComboBox field = getField(fieldId);
		if (field == null) {
			return;
		}
		if (field.isReadOnly()) {
			return;
		}
		if (drugSusceptibilityType.isEmpty()) {
			field.clear();
			return;
		}
		if (!field.containsId(drugSusceptibilityType.get())) {
			field.addItem(drugSusceptibilityType.get());
		}

		field.setValue(drugSusceptibilityType.get());
	}

	private void forceUpdateDrugSusceptibilityMicField(String fieldId, Optional<String> drugSusceptibilityMic) {
		final TextField field = getField(fieldId);
		if (field == null) {
			return;
		}
		if (field.isReadOnly()) {
			return;
		}
		if (drugSusceptibilityMic.isEmpty()) {
			field.clear();
			return;
		}

		field.setValue(drugSusceptibilityMic.get());
	}

	/**
	 * @return true if the form has visible fields after the update
	 */
	public boolean updateFieldsVisibility(Disease disease, PathogenTestType pathogenTestType) {
		setHeaderVisible(false);

		// Null disease or test type means we don't know yet (e.g. during init before
		// all fields are populated). Hide fields but preserve values.
		if (disease == null || pathogenTestType == null) {
			FieldHelper.hideFieldsNotInList(getFieldGroup(), List.of(), false);
			updateDrugLabelsVisibility(List.of());
			return false;
		}

		// Actively switching to a non-applicable test type — clear stale values
		if (pathogenTestType != PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY) {
			FieldHelper.hideFieldsNotInList(getFieldGroup(), List.of(), true);
			updateDrugLabelsVisibility(List.of());
			return false;
		}

		// Non-Luxembourg TB/Latent TB exclusion — clear stale values
		if (!FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)
			&& (disease == Disease.TUBERCULOSIS || disease == Disease.LATENT_TUBERCULOSIS)) {
			FieldHelper.hideFieldsNotInList(getFieldGroup(), List.of(), true);
			updateDrugLabelsVisibility(List.of());
			return false;
		}

		// Show applicable fields, hide non-applicable without clearing
		List<String> applicableFieldIds =
			AnnotationFieldHelper.getFieldNamesWithMatchingDiseaseAndTestAnnotations(DrugSusceptibilityDto.class, disease, pathogenTestType);

		boolean hasVisibleFields = !applicableFieldIds.isEmpty();
		setHeaderVisible(hasVisibleFields);

		if (hasVisibleFields) {
			FieldHelper.showOnlyFields(getFieldGroup(), applicableFieldIds, false);
		}
		// A drug's label shows only when its own fields are applicable for this disease/test type.
		updateDrugLabelsVisibility(applicableFieldIds);

		return hasVisibleFields;
	}

	/** Toggles the form heading and the column headers together. */
	private void setHeaderVisible(boolean visible) {
		formHeadingLabel.setVisible(visible);
		if (columnHeaderLabels != null) {
			columnHeaderLabels.forEach(label -> label.setVisible(visible));
		}
	}
}
