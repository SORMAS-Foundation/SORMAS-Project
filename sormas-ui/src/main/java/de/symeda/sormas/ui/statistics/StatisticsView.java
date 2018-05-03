package de.symeda.sormas.ui.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.explicatis.ext_token_field.ExtTokenField;
import com.explicatis.ext_token_field.Tokenizable;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.statistics.CasesStatisticField;
import de.symeda.sormas.api.statistics.StatisticSubField;
import de.symeda.sormas.api.statistics.StatisticsCaseCriteria;

public class StatisticsView extends AbstractStatisticsView {

	private static final long serialVersionUID = -4440568319850399685L;

	public static final String VIEW_NAME = "statistics";

	private StatisticsCaseCriteria caseCriteria = new StatisticsCaseCriteria();
	private Label resultLabel = new Label("", ContentMode.HTML);
	
	public StatisticsView() {
		super(VIEW_NAME);
		
		VerticalLayout statisticsLayout = new VerticalLayout();
		statisticsLayout.setMargin(true);
		statisticsLayout.setSpacing(true);
		
		// FILTERS
		VerticalLayout filtersLayout = new VerticalLayout();
		
		// TODO very hacky -> move to specific filter component(s)
		VerticalLayout filterLayout = new VerticalLayout();
		ComboBox filterFieldSelect = new ComboBox("Attribute", Arrays.asList(CasesStatisticField.values()));
		filterLayout.addComponent(filterFieldSelect);
		filterFieldSelect.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				CasesStatisticField casesStatisticField  = (CasesStatisticField)event.getProperty().getValue();
				filterLayout.removeAllComponents();
				filterLayout.addComponent(filterFieldSelect);
				if (casesStatisticField != null) {
					switch (casesStatisticField) {
					case ONSET_TIME:
					case RECEPTION_TIME:
					case REPORT_TIME:
					case PERSON_AGE_GROUP:
					case PERSON_SEX:
					case DISEASE:
					case CLASSIFICATION:
					case OUTCOME:
						// TODO for time we need a second field to select the time period 
						
						ExtTokenField defaultTokenField = new ExtTokenField();
						ComboBox addSelect = new ComboBox("Test", getFilterValues(casesStatisticField));
						defaultTokenField.setInputField(addSelect);
						addSelect.addValueChangeListener(new ValueChangeListener() {
							@Override
							public void valueChange(ValueChangeEvent event) {
								TokenizableValue token = (TokenizableValue)event.getProperty().getValue();
								if (token != null) {
									defaultTokenField.addTokenizable(token);
								}
								
							}
						});
						defaultTokenField.setWidth(300, Unit.PIXELS);
						filterLayout.addComponent(defaultTokenField);
						
						defaultTokenField.addValueChangeListener(new ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								List<TokenizableValue> tokens = (List<TokenizableValue>)event.getProperty().getValue();
								switch (casesStatisticField) {
								case ONSET_TIME:
									caseCriteria.onsetYears(tokens.stream().map(token -> (Integer)token.getValue()).collect(Collectors.toList()));
									break;
									// TODO
								}
							}
						});
						break;
						
					case PLACE:
						// TODO;
						break;
					}
				}
				
			}
		});
		filtersLayout.addComponent(filterLayout);
		
		statisticsLayout.addComponent(filtersLayout);
		
		
		NativeButton updateButton = new NativeButton("Update");
		updateButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				refreshData();
			}
		});
		statisticsLayout.addComponent(updateButton);
		
		statisticsLayout.addComponent(resultLabel);

		addComponent(statisticsLayout);
	}
	
	public void refreshData() {

		// TODO grouping should be defined using a component
		// TODO add second grouping
		List<Object[]> resultData = FacadeProvider.getCaseFacade().queryCaseCount(caseCriteria, CasesStatisticField.PLACE, StatisticSubField.REGION);
		
		String resultString = "";
		for (Object[] resultDataRow : resultData) {
			for (Object resultDataCell : resultDataRow) {
				resultString += resultDataCell.toString() + ", ";
			}
			resultString += "<br>";
		}
		resultLabel.setValue(resultString);
	}

	
	
	public List<TokenizableValue> getFilterValues(CasesStatisticField casesStatisticField) {
		
		// TODO add StatisticsSubField
		
		switch(casesStatisticField) {
		case ONSET_TIME:
			return createTokens((Integer)2017, (Integer)2018);

		case PERSON_SEX:
			return createTokens((Object[])Sex.values());
		case DISEASE:
			return createTokens((Object[])Disease.values());
		case CLASSIFICATION:
			return createTokens((Object[])CaseClassification.values());
		case OUTCOME:
			return createTokens((Object[])CaseOutcome.values());
			
		default:
			throw new IllegalArgumentException(this.toString()); 
		}
	}
	
	public List<TokenizableValue> createTokens(Object ...values) {
		List<TokenizableValue> result = new ArrayList<TokenizableValue>(values.length);
		for (int i=0; i<values.length; i++) {
			result.add(new TokenizableValue(values[i], i));
		}
		return result;
	}
	
	public static class TokenizableValue implements Tokenizable {

		private final Object value;
		private final long id;
		
		public TokenizableValue(Object value, long id) {
			this.value = value;
			this.id = id;
		}
		
		public Object getValue() {
			return value;
		}
		
		@Override
		public String getStringValue() {
			return value.toString();
		}

		@Override
		public long getIdentifier() {
			return id;
		}
		
		@Override
		public String toString() {
			return getStringValue();
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
	}
}
