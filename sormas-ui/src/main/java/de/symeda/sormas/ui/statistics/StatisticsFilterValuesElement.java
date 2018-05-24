package de.symeda.sormas.ui.statistics;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.explicatis.ext_token_field.ExtTokenField;
import com.explicatis.ext_token_field.Tokenizable;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.api.statistics.StatisticsHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsFilterValuesElement extends StatisticsFilterElement {

	private final StatisticsCaseAttribute attribute;
	private final StatisticsCaseSubAttribute subAttribute;
	
	private StatisticsFilterValuesElement relatedElement;
	private ValueChangeListener valueChangeListener;
	private ExtTokenField tokenField;
	private ComboBox addDropdown;

	public StatisticsFilterValuesElement(String caption, StatisticsCaseAttribute attribute, StatisticsCaseSubAttribute subAttribute) {
		setSpacing(true);
		addStyleName(CssStyles.LAYOUT_MINIMAL);
		setWidth(100, Unit.PERCENTAGE);

		this.attribute = attribute;
		this.subAttribute = subAttribute;

		ExtTokenField tokenField = createTokenField(caption);
		VerticalLayout utilityButtonsLayout = createUtilityButtonsLayout();
		addComponent(tokenField);
		addComponent(utilityButtonsLayout);
		setExpandRatio(tokenField, 1);
		setExpandRatio(utilityButtonsLayout, 0);
		setComponentAlignment(utilityButtonsLayout, Alignment.MIDDLE_RIGHT);
	}

	public void updateRelatedElementOnValueChange(boolean update) {
		if (!update && valueChangeListener != null) {
			tokenField.removeValueChangeListener(valueChangeListener);
			valueChangeListener = null;
		} else if (update) {
			valueChangeListener = new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					relatedElement.updateDropdownContent();					
				}
			};
			tokenField.addValueChangeListener(valueChangeListener);
		}
	}

	public void updateDropdownContent() {
		addDropdown.removeAllItems();
		addDropdown.addItems(getFilterValues());
	}

	private ExtTokenField createTokenField(String caption) {
		tokenField = new ExtTokenField();
		tokenField.setCaption(caption);
		tokenField.setWidth(100, Unit.PERCENTAGE);
		tokenField.setEnableDefaultDeleteTokenAction(true);

		addDropdown = new ComboBox("", getFilterValues());
		addDropdown.addStyleName(CssStyles.VSPACE_NONE);
		addDropdown.setFilteringMode(FilteringMode.CONTAINS);
		addDropdown.setInputPrompt("Type here to add...");
		tokenField.setInputField(addDropdown);
		addDropdown.addValueChangeListener(e -> {
			TokenizableValue token = (TokenizableValue) e.getProperty().getValue();
			if (token != null) {
				tokenField.addTokenizable(token);
				addDropdown.setValue(null);
			}
		});

		return tokenField;
	}

	private VerticalLayout createUtilityButtonsLayout() {
		VerticalLayout utilityButtonsLayout = new VerticalLayout();
		utilityButtonsLayout.setSizeUndefined();

		Button addAllButton = new Button("All", FontAwesome.PLUS_CIRCLE);
		CssStyles.style(addAllButton, ValoTheme.BUTTON_LINK);
		addAllButton.addClickListener(e -> {
			for (TokenizableValue tokenizable : getFilterValues()) {
				tokenField.addTokenizable(tokenizable);
			}
		});

		Button removeAllButton = new Button("Clear", FontAwesome.TIMES_CIRCLE);
		CssStyles.style(removeAllButton, ValoTheme.BUTTON_LINK);
		removeAllButton.addClickListener(e -> {
			for (Tokenizable tokenizable : tokenField.getValue()) {
				tokenField.removeTokenizable(tokenizable);
			}
		});

		utilityButtonsLayout.addComponent(addAllButton);
		utilityButtonsLayout.addComponent(removeAllButton);

		return utilityButtonsLayout;
	}

	private List<TokenizableValue> getFilterValues() {
		if (subAttribute != null) {
			switch (subAttribute) {
			case YEAR:
			case QUARTER:
			case MONTH:
			case EPI_WEEK:
			case QUARTER_OF_YEAR:
			case MONTH_OF_YEAR:
			case EPI_WEEK_OF_YEAR:
				List<Object> dateValues = StatisticsHelper.getListOfDateValues(attribute, subAttribute);
				return createTokens(dateValues.toArray());
			case REGION:
				return createTokens(FacadeProvider.getRegionFacade().getAllAsReference().toArray());
			case DISTRICT:
				if (relatedElement != null) {
					List<TokenizableValue> selectedRegionTokenizables = relatedElement.getSelectedValues();
					if (CollectionUtils.isNotEmpty(selectedRegionTokenizables)) {
						List<DistrictReferenceDto> districts = new ArrayList<>();
						for (TokenizableValue selectedRegionTokenizable : selectedRegionTokenizables) {
							RegionReferenceDto selectedRegion = (RegionReferenceDto) selectedRegionTokenizable.getValue();
							districts.addAll(FacadeProvider.getDistrictFacade().getAllByRegion(selectedRegion.getUuid()));
						}
						return createTokens(districts.toArray());
					} else {
						return createTokens(FacadeProvider.getDistrictFacade().getAllAsReference().toArray());
					}
				} else {
					return createTokens(FacadeProvider.getDistrictFacade().getAllAsReference().toArray());
				}
			default:
				throw new IllegalArgumentException(this.toString());
			}
		} else {
			switch (attribute) {
			case SEX:
				List<TokenizableValue> tokens = createTokens((Object[]) Sex.values());
				tokens.add(new TokenizableValue("Unknown", tokens.size()));
				return tokens;
			case AGE_INTERVAL_1_YEAR:
			case AGE_INTERVAL_5_YEARS:
			case AGE_INTERVAL_CHILDREN_COARSE:
			case AGE_INTERVAL_CHILDREN_FINE:
			case AGE_INTERVAL_CHILDREN_MEDIUM:
			case AGE_INTERVAL_BASIC:
				List<Object> ageIntervalValues = StatisticsHelper.getListOfAgeIntervalValues(attribute);
				return createTokens(ageIntervalValues.toArray());
			case DISEASE:
				return createTokens((Object[]) Disease.values());
			case CLASSIFICATION:
				return createTokens((Object[]) CaseClassification.values());
			case OUTCOME:
				return createTokens((Object[]) CaseOutcome.values());
			default:
				throw new IllegalArgumentException(this.toString());
			}
		}
	}

	public void setRelatedElement(StatisticsFilterValuesElement relatedElement) {
		this.relatedElement = relatedElement;
	}
	
	@SuppressWarnings("unchecked")
	public List<TokenizableValue> getSelectedValues() {
		return (List<TokenizableValue>) tokenField.getValue();
	}
	

}
