package de.symeda.sormas.ui.reports.aggregate;

import de.symeda.sormas.api.i18n.Strings;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.CustomLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.report.AggregateReportDto;
import de.symeda.sormas.api.utils.AgeGroupUtils;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

/**
 * @author Christopher Riedel
 */
public class AggregateReportEditForm extends AbstractEditForm<AggregateReportDto> {

	static final String DISEASE_LOC = "diseaseLoc";
	static final String AGE_GROUP_LOC = "ageGroupLoc";
	private static final long serialVersionUID = 2224137772717110789L;
	private Disease disease;
	private String ageGroup;
	private boolean firstGroup;

	private boolean initialized = false;

	private TextField caseField;
	private TextField labField;
	private TextField deathField;
	private boolean expiredAgeGroup;

	public AggregateReportEditForm(Disease disease, String ageGroup, boolean firstGroup, boolean expiredAgeGroup) {
		super(AggregateReportDto.class, AggregateReportDto.I18N_PREFIX);

		this.disease = disease;
		this.ageGroup = ageGroup;
		this.firstGroup = firstGroup;
		this.expiredAgeGroup = expiredAgeGroup;

		initialized = true;
		addFields();
	}

	@Override
	protected String createHtmlLayout() {
		if (ageGroup == null) {
			return LayoutUtil.fluidRow(
				LayoutUtil.oneOfThreeCol(DISEASE_LOC),
				LayoutUtil.oneOfSixCol(AggregateReportDto.NEW_CASES),
				LayoutUtil.oneOfSixCol(AggregateReportDto.LAB_CONFIRMATIONS),
				LayoutUtil.oneOfSixCol(AggregateReportDto.DEATHS),
				LayoutUtil.oneOfSixCol(AggregateReportDto.EXPIRED_AGE_GROUP));
		} else {
			return this.firstGroup
				? LayoutUtil.fluidRow(LayoutUtil.oneOfTwoCol(DISEASE_LOC))
					+ StringUtils.EMPTY
					+ LayoutUtil.fluidRow(
						LayoutUtil.oneOfThreeCol(AGE_GROUP_LOC),
						LayoutUtil.oneOfSixCol(AggregateReportDto.NEW_CASES),
						LayoutUtil.oneOfSixCol(AggregateReportDto.LAB_CONFIRMATIONS),
						LayoutUtil.oneOfSixCol(AggregateReportDto.DEATHS),
						LayoutUtil.oneOfSixCol(AggregateReportDto.EXPIRED_AGE_GROUP))
				: StringUtils.EMPTY
					+ LayoutUtil.fluidRow(
						LayoutUtil.oneOfThreeCol(AGE_GROUP_LOC),
						LayoutUtil.oneOfSixCol(AggregateReportDto.NEW_CASES),
						LayoutUtil.oneOfSixCol(AggregateReportDto.LAB_CONFIRMATIONS),
						LayoutUtil.oneOfSixCol(AggregateReportDto.DEATHS),
						LayoutUtil.oneOfSixCol(AggregateReportDto.EXPIRED_AGE_GROUP));
		}
	}

	@Override
	protected void addFields() {
		if (!initialized) {
			// vars have to be set first
			return;
		}

		getContent().setWidth(520, Unit.PIXELS);

		if (ageGroup == null && !isExpiredAgeGroup()) {
			addDiseaseLabel();
		} else {
			if (firstGroup) {
				addDiseaseLabel();
			}
			Label ageGroupLabel = new Label(AgeGroupUtils.createCaption(ageGroup));
			getContent().addComponent(ageGroupLabel, AGE_GROUP_LOC);
			CssStyles.style(CssStyles.CAPTION_HIDDEN, ageGroupLabel);
		}

		caseField = addField(AggregateReportDto.NEW_CASES);
		caseField.setInputPrompt(I18nProperties.getCaption(Captions.aggregateReportNewCasesShort));
		caseField.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, caseField.getCaption()));
		labField = addField(AggregateReportDto.LAB_CONFIRMATIONS);
		labField.setInputPrompt(I18nProperties.getCaption(Captions.aggregateReportLabConfirmationsShort));
		labField.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, labField.getCaption()));
		deathField = addField(AggregateReportDto.DEATHS);
		deathField.setInputPrompt(I18nProperties.getCaption(Captions.aggregateReportDeathsShort));
		deathField.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, deathField.getCaption()));
		CssStyles.style(CssStyles.CAPTION_HIDDEN, caseField, labField, deathField);

		if (isExpiredAgeGroup()) {
			Label expiredAgeGroupLabel = new Label(I18nProperties.getCaption(Captions.aggregateReportExpiredAgeGroups));
			expiredAgeGroupLabel.addStyleName(CssStyles.LABEL_BOLD);
			getContent().addComponent(expiredAgeGroupLabel, AggregateReportDto.EXPIRED_AGE_GROUP);
		}
	}

	private void addDiseaseLabel() {
		Label diseaseLabel = new Label(disease.toString());
		diseaseLabel.addStyleName(CssStyles.LABEL_BOLD);
		getContent().addComponent(diseaseLabel, DISEASE_LOC);
		CssStyles.style(CssStyles.CAPTION_HIDDEN, diseaseLabel);
	}

	@Override
	public boolean isValid() {
		return (caseField.getValue().isEmpty() || DataHelper.isParseableInt(caseField.getValue()))
			&& (labField.getValue().isEmpty() || DataHelper.isParseableInt(labField.getValue()))
			&& (deathField.getValue().isEmpty() || DataHelper.isParseableInt(deathField.getValue()));
	}

	public Disease getDisease() {
		return disease;
	}

	public String getAgeGroup() {
		return ageGroup;
	}

	public void setNewCases(int cases) {
		caseField.setValue(String.valueOf(cases));
	}

	public void setLabConfirmations(int labs) {
		labField.setValue(String.valueOf(labs));
	}

	public void setDeaths(int deaths) {
		deathField.setValue(String.valueOf(deaths));
	}

	public boolean isFirstGroup() {
		return firstGroup;
	}

	public boolean isExpiredAgeGroup() {
		return expiredAgeGroup;
	}

	@Override
	protected CustomLayout getContent() {
		return (CustomLayout) super.getContent();
	}
}
