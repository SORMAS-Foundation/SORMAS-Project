package de.symeda.sormas.ui.samples;

import java.util.Arrays;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.ShipmentStatus;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class SampleEditForm extends AbstractEditForm<SampleDto> {

	private static final String CASE_INFO = "caseInfo";
	
	private static final String HTML_LAYOUT = 
			LayoutUtil.h3(CssStyles.VSPACE3, "Laboratory sample") +
			LayoutUtil.div(
					LayoutUtil.fluidRowCss(CssStyles.VSPACE4,
							LayoutUtil.fluidColumn(9, 0,
									LayoutUtil.div(
										LayoutUtil.fluidRowCss(
												CssStyles.VSPACE4,
												LayoutUtil.oneOfThreeCol(LayoutUtil.loc(SampleDto.UUID)),
												LayoutUtil.oneOfThreeCol(LayoutUtil.loc(SampleDto.SAMPLE_CODE))
										),
										LayoutUtil.fluidRowCss(
												CssStyles.VSPACE4,
												LayoutUtil.oneOfThreeCol(LayoutUtil.loc(SampleDto.SAMPLE_DATE_TIME)),
												LayoutUtil.oneOfThreeCol(LayoutUtil.loc(SampleDto.REPORT_DATE_TIME)),
												LayoutUtil.oneOfThreeCol(LayoutUtil.loc(SampleDto.REPORTING_USER))
										),
										LayoutUtil.fluidRowCss(
												CssStyles.VSPACE4, 
												LayoutUtil.oneOfThreeCol(LayoutUtil.loc(SampleDto.SAMPLE_MATERIAL)),
												LayoutUtil.oneOfThreeCol(LayoutUtil.loc(SampleDto.SAMPLE_MATERIAL_TEXT))
										),
										LayoutUtil.fluidRowCss(
												CssStyles.VSPACE4, 
												LayoutUtil.oneOfThreeCol(LayoutUtil.loc(SampleDto.SHIPMENT_DATE)),
												LayoutUtil.twoOfThreeCol(LayoutUtil.loc(SampleDto.SHIPMENT_DETAILS))
										),
										LayoutUtil.fluidRowCss(
												CssStyles.VSPACE4,
												LayoutUtil.loc(SampleDto.SHIPMENT_STATUS)
										),
										LayoutUtil.fluidRowCss(
												CssStyles.VSPACE4, 
												LayoutUtil.oneOfTwoCol(LayoutUtil.loc(SampleDto.LAB)), 
												LayoutUtil.oneOfTwoCol(LayoutUtil.locs(SampleDto.OTHER_LAB, SampleDto.RECEIVED_DATE))
										),
										LayoutUtil.fluidRowCss(
												CssStyles.VSPACE4,
												LayoutUtil.oneOfThreeCol(LayoutUtil.loc(SampleDto.NO_TEST_POSSIBLE)),
												LayoutUtil.twoOfThreeCol(LayoutUtil.loc(SampleDto.NO_TEST_POSSIBLE_REASON))
										)
									)
							),
							LayoutUtil.fluidColumnLoc(3, 0, CASE_INFO)
						)
			);
	
	private VerticalLayout caseInfoLayout;
	
	public SampleEditForm() {
		super(SampleDto.class, SampleDto.I18N_PREFIX);
	}
	
	@Override
	protected void addFields() {
		addField(SampleDto.UUID, TextField.class);
		addField(SampleDto.SAMPLE_CODE, TextField.class);
		
		DateField sampleDateTime = addField(SampleDto.SAMPLE_DATE_TIME, DateField.class);
		DateField reportDateTime = addField(SampleDto.REPORT_DATE_TIME, DateField.class);
		sampleDateTime.setResolution(Resolution.MINUTE);
		sampleDateTime.setDateFormat(DateHelper.getTimeDateFormat().toPattern());
		reportDateTime.setResolution(Resolution.MINUTE);
		reportDateTime.setDateFormat(DateHelper.getTimeDateFormat().toPattern());
		
		addField(SampleDto.REPORTING_USER, ComboBox.class);
		addField(SampleDto.SAMPLE_MATERIAL, ComboBox.class);
		addField(SampleDto.SAMPLE_MATERIAL_TEXT, TextField.class);
		addField(SampleDto.SHIPMENT_DATE, DateField.class).setDateFormat(DateHelper.getShortDateFormat().toPattern());
		addField(SampleDto.SHIPMENT_DETAILS, TextField.class);
		addField(SampleDto.SHIPMENT_STATUS, OptionGroup.class);
		addField(SampleDto.RECEIVED_DATE, DateField.class).setDateFormat(DateHelper.getShortDateFormat().toPattern());
		ComboBox lab = addField(SampleDto.LAB, ComboBox.class);
		ComboBox otherLab = addField(SampleDto.OTHER_LAB, ComboBox.class);
		addField(SampleDto.NO_TEST_POSSIBLE, CheckBox.class);
		addField(SampleDto.NO_TEST_POSSIBLE_REASON, TextField.class);
		
		lab.addItems(FacadeProvider.getFacilityFacade().getAll());
		otherLab.addItems(FacadeProvider.getFacilityFacade().getAll());
		
		setReadOnly(true, SampleDto.UUID, SampleDto.REPORT_DATE_TIME, SampleDto.REPORTING_USER);
		
		FieldHelper.setVisibleWhen(getFieldGroup(), SampleDto.SAMPLE_MATERIAL_TEXT, SampleDto.SAMPLE_MATERIAL, Arrays.asList(SampleMaterial.OTHER), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), SampleDto.RECEIVED_DATE, SampleDto.SHIPMENT_STATUS, Arrays.asList(ShipmentStatus.RECEIVED), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), SampleDto.OTHER_LAB, SampleDto.SHIPMENT_STATUS, Arrays.asList(ShipmentStatus.REFERRED_OTHER_LAB), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), SampleDto.NO_TEST_POSSIBLE_REASON, SampleDto.NO_TEST_POSSIBLE, Arrays.asList(true), true);
		FieldHelper.setRequiredWhen(getFieldGroup(), SampleDto.SAMPLE_MATERIAL, Arrays.asList(SampleDto.SAMPLE_MATERIAL_TEXT), Arrays.asList(SampleMaterial.OTHER));
		FieldHelper.setRequiredWhen(getFieldGroup(), SampleDto.SHIPMENT_STATUS, Arrays.asList(SampleDto.RECEIVED_DATE), Arrays.asList(ShipmentStatus.RECEIVED));
		FieldHelper.setRequiredWhen(getFieldGroup(), SampleDto.SHIPMENT_STATUS, Arrays.asList(SampleDto.OTHER_LAB), Arrays.asList(ShipmentStatus.REFERRED_OTHER_LAB));
		FieldHelper.setRequiredWhen(getFieldGroup(), SampleDto.NO_TEST_POSSIBLE, Arrays.asList(SampleDto.NO_TEST_POSSIBLE_REASON), Arrays.asList(true));
		
		setRequired(true, SampleDto.UUID, SampleDto.SAMPLE_DATE_TIME, SampleDto.REPORT_DATE_TIME,
				SampleDto.REPORTING_USER, SampleDto.SAMPLE_MATERIAL, SampleDto.LAB, SampleDto.SHIPMENT_STATUS,
				SampleDto.SHIPMENT_DATE);
		
		caseInfoLayout = new VerticalLayout();
		caseInfoLayout.setSpacing(true);
		getContent().addComponent(caseInfoLayout, CASE_INFO);
		addValueChangeListener(e -> {
			updateCaseInfo();
		});
	}
	
	private void updateCaseInfo() {
		caseInfoLayout.removeAllComponents();
		
		SampleDto sampleDto = getValue();
		if(sampleDto != null) {
			CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(sampleDto.getAssociatedCase().getUuid());
			PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(caseDto.getPerson().getUuid());
		
			addDescLabel(caseInfoLayout, DataHelper.getShortUuid(caseDto.getUuid()),
					I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.UUID))
				.setDescription(caseDto.getUuid());
			addDescLabel(caseInfoLayout, caseDto.getPerson(),
					I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.PERSON));
			
	    	HorizontalLayout ageSexLayout = new HorizontalLayout();
	    	ageSexLayout.setSpacing(true);
			addDescLabel(ageSexLayout, ApproximateAgeHelper.formatApproximateAge(
						personDto.getApproximateAge(),personDto.getApproximateAgeType()),
					I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, PersonDto.APPROXIMATE_AGE));
			addDescLabel(ageSexLayout, personDto.getSex(),
					I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, PersonDto.SEX));
	    	caseInfoLayout.addComponent(ageSexLayout);
	    	
			addDescLabel(caseInfoLayout, caseDto.getDisease(),
					I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE));
			addDescLabel(caseInfoLayout, caseDto.getCaseClassification(),
					I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.CASE_CLASSIFICATION));
			addDescLabel(caseInfoLayout, DateHelper.formatDMY(caseDto.getSymptoms().getOnsetDate()),
					I18nProperties.getPrefixFieldCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE));
		}
	}
	
	private static Label addDescLabel(AbstractLayout layout, Object content, String caption) {
		String contentString = content != null ? content.toString() : "";
		Label label = new Label(contentString);
		label.setCaption(caption);
		layout.addComponent(label);
		return label;
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
}
