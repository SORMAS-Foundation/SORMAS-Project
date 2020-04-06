package de.symeda.sormas.ui.reports.aggregate;


import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.report.AggregateReportDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

/**
 * @author Christopher Riedel
 */
public class AggregateReportEditForm extends AbstractEditForm<AggregateReportDto> {

	private static final long serialVersionUID = 2224137772717110789L;

	static final String DISEASE_LOC = "diseaseLoc";

	private String disease;

	private boolean initialized = false;

	private static final String HTML_LAYOUT = LayoutUtil.fluidRow(LayoutUtil.oneOfTwoCol(DISEASE_LOC),
			LayoutUtil.oneOfSixCol(AggregateReportDto.NEW_CASES),
			LayoutUtil.oneOfSixCol(AggregateReportDto.LAB_CONFIRMATIONS),
			LayoutUtil.oneOfSixCol(AggregateReportDto.DEATHS));

	private TextField caseField;
	private TextField labField;
	private TextField deathField;

	public AggregateReportEditForm(String disease) {
		super(AggregateReportDto.class, AggregateReportDto.I18N_PREFIX, UserRight.AGGREGATE_REPORT_EDIT);

		this.disease = disease;

		initialized = true;
		addFields();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		if (!initialized) {
			// vars have to be set first
			return;
		}

		getContent().setWidth(520, Unit.PIXELS);

		Label diseaseLabel = new Label(disease);
		getContent().addComponent(diseaseLabel, DISEASE_LOC);
		caseField = addField(AggregateReportDto.NEW_CASES);
		caseField.setInputPrompt(I18nProperties.getCaption(Captions.aggregateReportNewCasesShort));
		caseField.setConversionError(
				I18nProperties.getValidationError(Validations.onlyNumbersAllowed, caseField.getCaption()));
		labField = addField(AggregateReportDto.LAB_CONFIRMATIONS);
		labField.setInputPrompt(I18nProperties.getCaption(Captions.aggregateReportLabConfirmationsShort));
		labField.setConversionError(
				I18nProperties.getValidationError(Validations.onlyNumbersAllowed, labField.getCaption()));
		deathField = addField(AggregateReportDto.DEATHS);
		deathField.setInputPrompt(I18nProperties.getCaption(Captions.aggregateReportDeathsShort));
		deathField.setConversionError(
				I18nProperties.getValidationError(Validations.onlyNumbersAllowed, deathField.getCaption()));
		CssStyles.style(CssStyles.CAPTION_HIDDEN, diseaseLabel, caseField, labField, deathField);
	}

	public boolean isValid() {
		return (caseField.getValue().isEmpty() || DataHelper.isParseableInt(caseField.getValue()))
				&& (labField.getValue().isEmpty() || DataHelper.isParseableInt(labField.getValue()))
				&& (deathField.getValue().isEmpty() || DataHelper.isParseableInt(deathField.getValue()));
	}

	public String getDisease() {
		return disease;
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
}
