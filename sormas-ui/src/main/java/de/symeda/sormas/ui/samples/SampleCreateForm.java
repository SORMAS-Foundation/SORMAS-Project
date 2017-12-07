package de.symeda.sormas.ui.samples;

import java.util.Arrays;
import java.util.Date;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class SampleCreateForm extends AbstractEditForm<SampleDto> {
	
	private static final String HTML_LAYOUT =
			LayoutUtil.div(
					LayoutUtil.div(
						LayoutUtil.fluidRowLocs(SampleDto.SAMPLE_DATE_TIME, SampleDto.SAMPLE_CODE),
						LayoutUtil.fluidRowLocs(SampleDto.SAMPLE_MATERIAL, SampleDto.SAMPLE_MATERIAL_TEXT),
						LayoutUtil.fluidRowLocs(SampleDto.SAMPLE_SOURCE, ""),
						LayoutUtil.fluidRowLocs(SampleDto.SUGGESTED_TYPE_OF_TEST, SampleDto.LAB)
					),
					LayoutUtil.locCss(CssStyles.VSPACE_TOP_3, SampleDto.SHIPPED),
					LayoutUtil.div(
							LayoutUtil.fluidRowLocs(SampleDto.SHIPMENT_DATE, SampleDto.SHIPMENT_DETAILS)
					),
					LayoutUtil.locCss(CssStyles.VSPACE_TOP_3, SampleDto.RECEIVED),
					LayoutUtil.div(
							LayoutUtil.fluidRowLocs(SampleDto.RECEIVED_DATE, SampleDto.LAB_SAMPLE_ID),
							LayoutUtil.fluidRowLocs(SampleDto.SPECIMEN_CONDITION, SampleDto.NO_TEST_POSSIBLE_REASON),
							LayoutUtil.fluidRowLocs(SampleDto.COMMENT)
					)
			);
	
	public SampleCreateForm() {
		super(SampleDto.class, SampleDto.I18N_PREFIX);
		
		hideValidationUntilNextCommit();
	}
	
	@Override
	protected void addFields() {
		addField(SampleDto.SAMPLE_CODE, TextField.class);
		addField(SampleDto.LAB_SAMPLE_ID, TextField.class);
		addField(SampleDto.SAMPLE_DATE_TIME, DateTimeField.class);
		addField(SampleDto.SAMPLE_MATERIAL, ComboBox.class);
		addField(SampleDto.SAMPLE_MATERIAL_TEXT, TextField.class);
		ComboBox sampleSource = addField(SampleDto.SAMPLE_SOURCE, ComboBox.class);
		DateField shipmentDate = addField(SampleDto.SHIPMENT_DATE, DateField.class);
		shipmentDate.setDateFormat(DateHelper.getShortDateFormat().toPattern());
		addField(SampleDto.SHIPMENT_DETAILS, TextField.class);
		DateField receivedDate = addField(SampleDto.RECEIVED_DATE, DateField.class);
		receivedDate.setDateFormat(DateHelper.getShortDateFormat().toPattern());
		addField(SampleDto.SUGGESTED_TYPE_OF_TEST, ComboBox.class);
		ComboBox lab = addField(SampleDto.LAB, ComboBox.class);
		lab.addItems(FacadeProvider.getFacilityFacade().getAllLaboratories());
		addField(SampleDto.SPECIMEN_CONDITION, ComboBox.class);
		addField(SampleDto.NO_TEST_POSSIBLE_REASON, TextField.class);
		addField(SampleDto.COMMENT, TextArea.class).setRows(2);
		CheckBox shipped = addField(SampleDto.SHIPPED, CheckBox.class);
		CheckBox received = addField(SampleDto.RECEIVED, CheckBox.class);
		
		FieldHelper.setVisibleWhen(getFieldGroup(), SampleDto.SAMPLE_MATERIAL_TEXT, SampleDto.SAMPLE_MATERIAL, Arrays.asList(SampleMaterial.OTHER), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), SampleDto.NO_TEST_POSSIBLE_REASON, SampleDto.SPECIMEN_CONDITION, Arrays.asList(SpecimenCondition.NOT_ADEQUATE), true);
		FieldHelper.setRequiredWhen(getFieldGroup(), SampleDto.SAMPLE_MATERIAL, Arrays.asList(SampleDto.SAMPLE_MATERIAL_TEXT), Arrays.asList(SampleMaterial.OTHER));
		FieldHelper.setRequiredWhen(getFieldGroup(), SampleDto.SPECIMEN_CONDITION, Arrays.asList(SampleDto.NO_TEST_POSSIBLE_REASON), Arrays.asList(SpecimenCondition.NOT_ADEQUATE));
		
		setRequired(true, SampleDto.SAMPLE_DATE_TIME, SampleDto.SAMPLE_MATERIAL, SampleDto.LAB);
		
		addValueChangeListener(e -> {
			CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(getValue().getAssociatedCase().getUuid());
			if (caze.getDisease() != Disease.AVIAN_INFLUENCA) {
				sampleSource.setVisible(false);
			}

			FieldHelper.setEnabledWhen(getFieldGroup(), shipped, Arrays.asList(true), Arrays.asList(SampleDto.SHIPMENT_DATE, SampleDto.SHIPMENT_DETAILS), true);
			FieldHelper.setRequiredWhen(getFieldGroup(), shipped, Arrays.asList(SampleDto.SHIPMENT_DATE), Arrays.asList(true));
			FieldHelper.setRequiredWhen(getFieldGroup(), received, Arrays.asList(SampleDto.RECEIVED_DATE, SampleDto.SPECIMEN_CONDITION), Arrays.asList(true));
			FieldHelper.setEnabledWhen(getFieldGroup(), received, Arrays.asList(true), Arrays.asList(SampleDto.RECEIVED_DATE, SampleDto.LAB_SAMPLE_ID, SampleDto.SPECIMEN_CONDITION, SampleDto.NO_TEST_POSSIBLE_REASON), true);
		});
		
		shipped.addValueChangeListener(event -> {
			if ((boolean) event.getProperty().getValue() == true) {
				if (shipmentDate.getValue() == null) {
					shipmentDate.setValue(new Date());
				}
			}
		});
		
		received.addValueChangeListener(event -> {
			if ((boolean) event.getProperty().getValue() == true) {
				if (receivedDate.getValue() == null) {
					receivedDate.setValue(new Date());
				}
			}
		});
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
