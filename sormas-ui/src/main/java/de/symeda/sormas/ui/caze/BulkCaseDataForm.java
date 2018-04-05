package de.symeda.sormas.ui.caze;

import java.util.Arrays;
import java.util.List;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OptionGroup;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class BulkCaseDataForm extends AbstractEditForm<CaseDataDto> {

	private static final String CLASSIFICATION_CHECKBOX = "classificationCheckbox";
	private static final String INVESTIGATION_STATUS_CHECKBOX = "investigationStatusCheckbox";
	private static final String OUTCOME_CHECKBOX = "outcomeCheckbox";
	private static final String SURVEILLANCE_OFFICER_CHECKBOX = "surveillanceOfficerCheckbox";
	
	private static final String HTML_LAYOUT =
			LayoutUtil.fluidRowLocsCss(CssStyles.VSPACE_4, CLASSIFICATION_CHECKBOX) +
			LayoutUtil.fluidRowLocs(CaseDataDto.CASE_CLASSIFICATION) +
			LayoutUtil.fluidRowLocsCss(CssStyles.VSPACE_4, INVESTIGATION_STATUS_CHECKBOX) +
			LayoutUtil.fluidRowLocs(CaseDataDto.INVESTIGATION_STATUS) +
			LayoutUtil.fluidRowLocsCss(CssStyles.VSPACE_4, OUTCOME_CHECKBOX) +
			LayoutUtil.fluidRowLocs(CaseDataDto.OUTCOME) +
			LayoutUtil.fluidRowLocsCss(CssStyles.VSPACE_4, SURVEILLANCE_OFFICER_CHECKBOX) +
			LayoutUtil.fluidRowLocs(CaseDataDto.SURVEILLANCE_OFFICER, "");
	
	private final DistrictReferenceDto singleSelectedDistrict;
	
	private boolean initialized = false;
	
	private CheckBox classificationCheckBox;
	private CheckBox investigationStatusCheckBox;
	private CheckBox outcomeCheckBox;
	private CheckBox surveillanceOfficerCheckBox;
	
	public BulkCaseDataForm(DistrictReferenceDto singleSelectedDistrict) {
		super(CaseDataDto.class, CaseDataDto.I18N_PREFIX, null);
		this.singleSelectedDistrict = singleSelectedDistrict;
		setWidth(680, Unit.PIXELS);
		hideValidationUntilNextCommit();
		initialized = true;
		addFields();
	}
	
	@Override
	protected void addFields() {
		if (!initialized) {
			return;
		}
		
		classificationCheckBox = new CheckBox("Change case classification");
		getContent().addComponent(classificationCheckBox, CLASSIFICATION_CHECKBOX);
		investigationStatusCheckBox = new CheckBox("Change investigation status");
		getContent().addComponent(investigationStatusCheckBox, INVESTIGATION_STATUS_CHECKBOX);
		outcomeCheckBox = new CheckBox("Change case outcome");
		getContent().addComponent(outcomeCheckBox, OUTCOME_CHECKBOX);
		OptionGroup caseClassification = addField(CaseDataDto.CASE_CLASSIFICATION, OptionGroup.class);
		caseClassification.setEnabled(false);
		OptionGroup investigationStatus = addField(CaseDataDto.INVESTIGATION_STATUS, OptionGroup.class);
		investigationStatus.setEnabled(false);
		OptionGroup outcome = addField(CaseDataDto.OUTCOME, OptionGroup.class);
		outcome.setEnabled(false);
		
		if (singleSelectedDistrict != null) {
			surveillanceOfficerCheckBox = new CheckBox("Change surveillance officer");
			getContent().addComponent(surveillanceOfficerCheckBox, SURVEILLANCE_OFFICER_CHECKBOX);
			ComboBox surveillanceOfficer = addField(CaseDataDto.SURVEILLANCE_OFFICER, ComboBox.class);
			surveillanceOfficer.setEnabled(false);
			FieldHelper.addSoftRequiredStyleWhen(getFieldGroup(), surveillanceOfficerCheckBox, Arrays.asList(CaseDataDto.SURVEILLANCE_OFFICER), Arrays.asList(true), null);
			List<UserReferenceDto> assignableSurveillanceOfficers = FacadeProvider.getUserFacade().getAssignableUsersByDistrict(singleSelectedDistrict, false, UserRole.SURVEILLANCE_OFFICER);
			FieldHelper.updateItems(surveillanceOfficer, assignableSurveillanceOfficers);
			
			surveillanceOfficerCheckBox.addValueChangeListener(e -> {
				surveillanceOfficer.setEnabled((boolean) e.getProperty().getValue());
			});
		}
		
		FieldHelper.setRequiredWhen(getFieldGroup(), classificationCheckBox, Arrays.asList(CaseDataDto.CASE_CLASSIFICATION), Arrays.asList(true));
		FieldHelper.setRequiredWhen(getFieldGroup(), investigationStatusCheckBox, Arrays.asList(CaseDataDto.INVESTIGATION_STATUS), Arrays.asList(true));
		FieldHelper.setRequiredWhen(getFieldGroup(), outcomeCheckBox, Arrays.asList(CaseDataDto.OUTCOME), Arrays.asList(true));
		
		classificationCheckBox.addValueChangeListener(e -> {
			caseClassification.setEnabled((boolean) e.getProperty().getValue());
		});
		investigationStatusCheckBox.addValueChangeListener(e -> {
			investigationStatus.setEnabled((boolean) e.getProperty().getValue());
		});
		outcomeCheckBox.addValueChangeListener(e -> {
			outcome.setEnabled((boolean) e.getProperty().getValue());
		});
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
	public CheckBox getClassificationCheckBox() {
		return classificationCheckBox;
	}

	public CheckBox getInvestigationStatusCheckBox() {
		return investigationStatusCheckBox;
	}

	public CheckBox getOutcomeCheckBox() {
		return outcomeCheckBox;
	}

	public CheckBox getSurveillanceOfficerCheckBox() {
		return surveillanceOfficerCheckBox;
	}
	
}
