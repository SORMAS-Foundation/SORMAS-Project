package de.symeda.sormas.ui.samples;

import java.util.Arrays;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class SampleTestEditForm extends AbstractEditForm<SampleTestDto> {

	private static final String HTML_LAYOUT = 
			LayoutUtil.fluidRowLocs(SampleTestDto.TEST_TYPE, SampleTestDto.TEST_TYPE_TEXT) +
			LayoutUtil.fluidRowLocs(SampleTestDto.TEST_DATE_TIME, SampleTestDto.LAB) +
			LayoutUtil.fluidRowLocs(SampleTestDto.TEST_RESULT, SampleTestDto.TEST_RESULT_VERIFIED) +
			LayoutUtil.fluidRowLocs(SampleTestDto.TEST_RESULT_TEXT);
	
	public SampleTestEditForm() {
		super(SampleTestDto.class, SampleTestDto.I18N_PREFIX);
	}
	
	@Override
	protected void addFields() {
		addField(SampleTestDto.TEST_TYPE, ComboBox.class);
		addField(SampleTestDto.TEST_TYPE_TEXT, TextField.class);
		DateField testDateTime = addField(SampleTestDto.TEST_DATE_TIME, DateField.class);
		testDateTime.setResolution(Resolution.MINUTE);
		testDateTime.setDateFormat(DateHelper.getTimeDateFormat().toPattern());
		addField(SampleTestDto.LAB, ComboBox.class);
		addField(SampleTestDto.TEST_RESULT, ComboBox.class);
		addField(SampleTestDto.TEST_RESULT_VERIFIED, CheckBox.class).addStyleName(CssStyles.FORCE_CAPTION);
		addField(SampleTestDto.TEST_RESULT_TEXT, TextArea.class).setRows(3);
		
		setReadOnly(true, SampleTestDto.LAB);

		FieldHelper.setVisibleWhen(getFieldGroup(), SampleTestDto.TEST_TYPE_TEXT, SampleTestDto.TEST_TYPE, Arrays.asList(SampleTestType.OTHER), true);
		FieldHelper.setRequiredWhen(getFieldGroup(), SampleTestDto.TEST_TYPE, Arrays.asList(SampleTestDto.TEST_TYPE_TEXT), Arrays.asList(SampleTestType.OTHER));
		
		setRequired(true, SampleTestDto.TEST_TYPE, SampleTestDto.TEST_DATE_TIME, SampleTestDto.LAB,
				SampleTestDto.TEST_RESULT, SampleTestDto.TEST_RESULT_TEXT);
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
}
