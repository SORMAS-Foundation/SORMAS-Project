package de.symeda.sormas.ui.samples;

import java.util.Arrays;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.ShipmentStatus;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class SampleCreateForm extends AbstractEditForm<SampleDto> {
	
	private static final String HTML_LAYOUT =
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
							LayoutUtil.threeOfFourCol(LayoutUtil.loc(SampleDto.SHIPMENT_STATUS))
					),
					LayoutUtil.fluidRowCss(
							CssStyles.VSPACE4, 
							LayoutUtil.oneOfThreeCol(LayoutUtil.loc(SampleDto.LAB)), 
							LayoutUtil.oneOfThreeCol(LayoutUtil.loc(SampleDto.OTHER_LAB)),
							LayoutUtil.oneOfFourCol(LayoutUtil.loc(SampleDto.RECEIVED_DATE))
					),
					LayoutUtil.fluidRowCss(
							CssStyles.VSPACE4,
							LayoutUtil.oneOfThreeCol(LayoutUtil.loc(SampleDto.NO_TEST_POSSIBLE)),
							LayoutUtil.twoOfThreeCol(LayoutUtil.loc(SampleDto.NO_TEST_POSSIBLE_REASON))
					)
			);
	
	public SampleCreateForm() {
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
		addField(SampleDto.SHIPMENT_DATE, DateField.class);
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
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
