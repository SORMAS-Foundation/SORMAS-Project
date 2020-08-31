package de.symeda.sormas.ui.statistics;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.TextField;

import de.symeda.sormas.ui.utils.CssStyles;

public class StatisticsFilterSimpleTextElement extends StatisticsFilterElement {

	private TextField textField;

	public StatisticsFilterSimpleTextElement(String caption, int rowIndex) {

		setSpacing(true);
		addStyleName(CssStyles.LAYOUT_MINIMAL);
		setWidth(100, Unit.PERCENTAGE);

		textField = new TextField(caption);
		textField.setId(caption + "-" + rowIndex);

		addComponent(textField);
	}

	@Override
	public List<TokenizableValue> getSelectedValues() {
		List<TokenizableValue> values = new ArrayList<>();
		TokenizableValue textValue = new TokenizableValue(textField.getValue(), 0);
		values.add(textValue);
		return values;
	}
}
