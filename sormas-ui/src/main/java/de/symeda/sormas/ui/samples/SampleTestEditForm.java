package de.symeda.sormas.ui.samples;

import java.util.Arrays;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class SampleTestEditForm extends AbstractEditForm<SampleTestDto> {

	private static final String HTML_LAYOUT = 
			LayoutUtil.fluidRowLocs(SampleTestDto.TEST_TYPE, SampleTestDto.TEST_TYPE_TEXT) +
			LayoutUtil.fluidRowLocs(SampleTestDto.TEST_DATE_TIME, SampleTestDto.LAB) +
			LayoutUtil.fluidRowLocs(SampleTestDto.TEST_RESULT, SampleTestDto.TEST_RESULT_VERIFIED) +
			LayoutUtil.fluidRowLocs(SampleTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, "") +
			LayoutUtil.fluidRowLocs(SampleTestDto.TEST_RESULT_TEXT);

	private int caseSampleCount;
	
	public SampleTestEditForm(boolean create, UserRight editOrCreateUserRight, int caseSampleCount) {
		super(SampleTestDto.class, SampleTestDto.I18N_PREFIX, editOrCreateUserRight);
		
		this.caseSampleCount = caseSampleCount;
        setWidth(600, Unit.PIXELS);
        
        if (create) {
        	hideValidationUntilNextCommit();
        }
	}
	
	@Override
	protected void addFields() {
		ComboBox testType = addField(SampleTestDto.TEST_TYPE, ComboBox.class);
		addField(SampleTestDto.TEST_TYPE_TEXT, TextField.class);
		addField(SampleTestDto.TEST_DATE_TIME, DateTimeField.class);
		ComboBox lab = addField(SampleTestDto.LAB, ComboBox.class);
		lab.addItems(FacadeProvider.getFacilityFacade().getAllLaboratories(true));
		
		addField(SampleTestDto.TEST_RESULT, ComboBox.class);
		addField(SampleTestDto.TEST_RESULT_VERIFIED, CheckBox.class).addStyleName(CssStyles.FORCE_CAPTION);
		CheckBox fourFoldIncrease = addField(SampleTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, CheckBox.class);
		CssStyles.style(fourFoldIncrease, CssStyles.VSPACE_3, CssStyles.VSPACE_TOP_4);
		fourFoldIncrease.setVisible(false);
		fourFoldIncrease.setEnabled(false);
		addField(SampleTestDto.TEST_RESULT_TEXT, TextArea.class).setRows(3);

		FieldHelper.setVisibleWhen(getFieldGroup(), SampleTestDto.TEST_TYPE_TEXT, SampleTestDto.TEST_TYPE, Arrays.asList(SampleTestType.OTHER), true);
		FieldHelper.setRequiredWhen(getFieldGroup(), SampleTestDto.TEST_TYPE, Arrays.asList(SampleTestDto.TEST_TYPE_TEXT), Arrays.asList(SampleTestType.OTHER));
		
		testType.addValueChangeListener(e -> {
			if (e.getProperty().getValue() == SampleTestType.IGM_SERUM_ANTIBODY) {
				fourFoldIncrease.setVisible(true);
				fourFoldIncrease.setEnabled(caseSampleCount >= 2);
			} else {
				fourFoldIncrease.setVisible(false);
				fourFoldIncrease.setEnabled(false);
			}
		});
		
		setRequired(true, SampleTestDto.TEST_TYPE, SampleTestDto.TEST_DATE_TIME, SampleTestDto.LAB,
				SampleTestDto.TEST_RESULT);
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
}
