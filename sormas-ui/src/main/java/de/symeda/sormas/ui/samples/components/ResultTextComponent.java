package de.symeda.sormas.ui.samples.components;

import com.vaadin.ui.TextArea;

import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.ui.utils.FormComponent;

/**
 * Test result free-text field.
 * Extracted from TestResultComponent to allow correct field ordering
 * (result text appears after disease section in the original form layout).
 */
public class ResultTextComponent extends FormComponent<PathogenTestDto> {

	private static final long serialVersionUID = 1L;

	private TextArea resultText;

	public ResultTextComponent() {
		super(PathogenTestDto.class);
		buildLayout();
		bindFields();
	}

	private void buildLayout() {
		resultText = createTextArea(PathogenTestDto.TEST_RESULT_TEXT, PathogenTestDto.I18N_PREFIX);
		resultText.setRows(6);
		addFullWidthRow(resultText);
	}

	private void bindFields() {
		binder.forField(resultText).bind(PathogenTestDto::getTestResultText, PathogenTestDto::setTestResultText);
	}
}
