package de.symeda.sormas.ui.statistics;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.DateField;

@SuppressWarnings("serial")
public class StatisticsFilterDateRangeElement extends StatisticsFilterElement {

	private DateField dateFromField;
	private DateField dateToField;
	
	public StatisticsFilterDateRangeElement() {
		setSpacing(true);
		
		dateFromField = new DateField("From");
		dateToField = new DateField("To");
		
		addComponent(dateFromField);
		addComponent(dateToField);
	}
	
	@Override
	List<TokenizableValue> getSelectedValues() {
		List<TokenizableValue> values = new ArrayList<>();
		TokenizableValue fromValue = new TokenizableValue(dateFromField.getValue(), 0);
		TokenizableValue toValue = new TokenizableValue(dateToField.getValue(), 1);
		values.add(fromValue);
		values.add(toValue);
		return values;
	}

}
