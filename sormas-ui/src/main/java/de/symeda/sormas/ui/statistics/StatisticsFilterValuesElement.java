package de.symeda.sormas.ui.statistics;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import com.explicatis.ext_token_field.ExtTokenField;
import com.explicatis.ext_token_field.Tokenizable;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.IntegerRange;
import de.symeda.sormas.api.Month;
import de.symeda.sormas.api.MonthOfYear;
import de.symeda.sormas.api.QuarterOfYear;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
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

	private ExtTokenField createTokenField(String caption) {
		tokenField = new ExtTokenField();
		tokenField.setCaption(caption);
		tokenField.setWidth(100, Unit.PERCENTAGE);
		tokenField.setEnableDefaultDeleteTokenAction(true);

		addDropdown = new ComboBox("", getFilterValues());
		addDropdown.addStyleName(CssStyles.VSPACE_NONE);
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
		CssStyles.style(addAllButton, ValoTheme.BUTTON_LINK);//, CssStyles.BUTTON_FONT_SIZE_LARGE);
		addAllButton.addClickListener(e -> {
			for (TokenizableValue tokenizable : getFilterValues()) {
				tokenField.addTokenizable(tokenizable);
			}
		});

		Button removeAllButton = new Button("Clear", FontAwesome.TIMES_CIRCLE);
		CssStyles.style(removeAllButton, ValoTheme.BUTTON_LINK);//, CssStyles.BUTTON_FONT_SIZE_LARGE);
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
		// If a sub attribute is present, always use it
		if (subAttribute != null) {
			switch (subAttribute) {
			case YEAR:
			case QUARTER:
			case MONTH:
			case EPI_WEEK:
			case QUARTER_OF_YEAR:
			case MONTH_OF_YEAR:
			case EPI_WEEK_OF_YEAR:
				return getListOfDateValues();
			case REGION:
				return createTokens(FacadeProvider.getRegionFacade().getAllAsReference().toArray());
			case DISTRICT:
				if (relatedElement != null) {
					List<TokenizableValue> selectedRegionTokenizables = relatedElement.getSelectedValues();
					if (selectedRegionTokenizables != null && selectedRegionTokenizables.size() > 0) {
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
				return createTokens((Object[]) Sex.values());
			case AGE_INTERVAL_1_YEAR:
			case AGE_INTERVAL_5_YEARS:
			case AGE_INTERVAL_CHILDREN_COARSE:
			case AGE_INTERVAL_CHILDREN_FINE:
			case AGE_INTERVAL_CHILDREN_MEDIUM:
				return getListOfAgeIntervalValues();
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

	private List<TokenizableValue> getListOfDateValues() {
		Date oldestCaseDate = null;
		switch (attribute) {
		case ONSET_TIME:
			oldestCaseDate = FacadeProvider.getCaseFacade().getOldestCaseOnsetDate();
			break;
		case RECEPTION_TIME:
			oldestCaseDate = FacadeProvider.getCaseFacade().getOldestCaseReceptionDate();
			break;
		case REPORT_TIME:
			oldestCaseDate = FacadeProvider.getCaseFacade().getOldestCaseReportDate();
			break;
		default:
			return new ArrayList<>();
		}

		LocalDate earliest = oldestCaseDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate now = LocalDate.now();

		switch (subAttribute) {
		case YEAR:
			return createTokens(IntStream.rangeClosed(earliest.getYear(), now.getYear()).boxed().toArray());
		case QUARTER:
			List<TokenizableValue> quarterList = new ArrayList<>();
			for (int i = 1; i <= 4; i++) {
				quarterList.add(new TokenizableValue(i, "Q" + i, i));
			}
			return quarterList;
		case MONTH:
			return createTokens((Object[]) Month.values());
		case EPI_WEEK:
			List<TokenizableValue> epiWeekList = new ArrayList<>();
			for (int i = 1; i <= DateHelper.getMaximumEpiWeekNumber(); i++) {
				epiWeekList.add(new TokenizableValue(i, "Wk " + i, i));
			}
			return epiWeekList;
		case QUARTER_OF_YEAR:
			List<TokenizableValue> quarterOfYearList = new ArrayList<>();
			QuarterOfYear earliestQuarter = new QuarterOfYear(1, earliest.getYear());
			QuarterOfYear latestQuarter = new QuarterOfYear(4, now.getYear());
			int tokenId = 0;
			while (earliestQuarter.getYear() <= latestQuarter.getYear()) {
				QuarterOfYear newQuarter = new QuarterOfYear(earliestQuarter.getQuarter(), earliestQuarter.getYear());
				quarterOfYearList.add(new TokenizableValue(newQuarter, newQuarter.toString(), tokenId++));
				earliestQuarter.increaseQuarter();
			}
			return quarterOfYearList;
		case MONTH_OF_YEAR:
			List<TokenizableValue> monthOfYearList = new ArrayList<>();
			tokenId = 0;
			for (int year = earliest.getYear(); year <= now.getYear(); year++) {
				monthOfYearList.add(new TokenizableValue(new MonthOfYear(Month.JANUARY, year), tokenId++));
				monthOfYearList.add(new TokenizableValue(new MonthOfYear(Month.FEBRUARY, year), tokenId++));
				monthOfYearList.add(new TokenizableValue(new MonthOfYear(Month.MARCH, year), tokenId++));
				monthOfYearList.add(new TokenizableValue(new MonthOfYear(Month.APRIL, year), tokenId++));
				monthOfYearList.add(new TokenizableValue(new MonthOfYear(Month.MAY, year), tokenId++));
				monthOfYearList.add(new TokenizableValue(new MonthOfYear(Month.JUNE, year), tokenId++));
				monthOfYearList.add(new TokenizableValue(new MonthOfYear(Month.JULY, year), tokenId++));
				monthOfYearList.add(new TokenizableValue(new MonthOfYear(Month.AUGUST, year), tokenId++));
				monthOfYearList.add(new TokenizableValue(new MonthOfYear(Month.SEPTEMBER, year), tokenId++));
				monthOfYearList.add(new TokenizableValue(new MonthOfYear(Month.OCTOBER, year), tokenId++));
				monthOfYearList.add(new TokenizableValue(new MonthOfYear(Month.NOVEMBER, year), tokenId++));
				monthOfYearList.add(new TokenizableValue(new MonthOfYear(Month.DECEMBER, year), tokenId++));
			}
			return monthOfYearList;
		case EPI_WEEK_OF_YEAR:
			List<TokenizableValue> epiWeekOfYearList = new ArrayList<>();
			tokenId = 0;
			for (int year = earliest.getYear(); year <= now.getYear(); year++) {
				List<EpiWeek> epiWeeksOfYear = DateHelper.createEpiWeekList(year);
				for (EpiWeek epiWeekOfYear : epiWeeksOfYear) {
					epiWeekOfYearList.add(new TokenizableValue(epiWeekOfYear, tokenId++));
				}
			}
			return epiWeekOfYearList;
		default:
			return new ArrayList<>();
		}
	}

	private List<TokenizableValue> getListOfAgeIntervalValues() {
		List<TokenizableValue> ageIntervalList = new ArrayList<>();
		int tokenId = 0;

		switch (attribute) {
		case AGE_INTERVAL_1_YEAR:
			for (int i = 0; i < 80; i++) {
				ageIntervalList.add(new TokenizableValue(new IntegerRange(i, i), tokenId++));
			}
			break;
		case AGE_INTERVAL_5_YEARS:
			for (int i = 0; i < 80; i += 5) {
				ageIntervalList.add(new TokenizableValue(new IntegerRange(i, i + 4), tokenId++));
			}
			break;
		case AGE_INTERVAL_CHILDREN_COARSE:
			ageIntervalList.add(new TokenizableValue(new IntegerRange(0, 14), tokenId++));
			for (int i = 15; i < 30; i += 5) {
				ageIntervalList.add(new TokenizableValue(new IntegerRange(i, i + 4), tokenId++));
			}
			for (int i = 30; i < 80; i += 10) {
				ageIntervalList.add(new TokenizableValue(new IntegerRange(i, i + 9), tokenId++));
			}
			break;
		case AGE_INTERVAL_CHILDREN_FINE:
			for (int i = 0; i < 5; i++) {
				ageIntervalList.add(new TokenizableValue(new IntegerRange(i, i), tokenId++));
			}
			for (int i = 5; i < 30; i += 5) {
				ageIntervalList.add(new TokenizableValue(new IntegerRange(i, i + 4), tokenId++));
			}
			for (int i = 30; i < 80; i += 10) {
				ageIntervalList.add(new TokenizableValue(new IntegerRange(i, i + 9), tokenId++));
			}
			break;
		case AGE_INTERVAL_CHILDREN_MEDIUM:
			for (int i = 0; i < 30; i += 5) {
				ageIntervalList.add(new TokenizableValue(new IntegerRange(i, i + 4), tokenId++));
			}
			for (int i = 30; i < 80; i += 10) {
				ageIntervalList.add(new TokenizableValue(new IntegerRange(i, i + 9), tokenId++));
			}
			break;
		default:
			return ageIntervalList;
		}

		ageIntervalList.add(new TokenizableValue(new IntegerRange(80, null), tokenId++));
		ageIntervalList.add(new TokenizableValue(new IntegerRange(null, null), tokenId));
		return ageIntervalList;
	}

	private List<TokenizableValue> createTokens(Object ...values) {
		List<TokenizableValue> result = new ArrayList<>(values.length);
		for (int i = 0; i < values.length; i++) {
			result.add(new TokenizableValue(values[i], i));
		}

		return result;
	}

	public void setRelatedElement(StatisticsFilterValuesElement relatedElement) {
		this.relatedElement = relatedElement;
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

	@SuppressWarnings("unchecked")
	public List<TokenizableValue> getSelectedValues() {
		return (List<TokenizableValue>) tokenField.getValue();
	}

}
