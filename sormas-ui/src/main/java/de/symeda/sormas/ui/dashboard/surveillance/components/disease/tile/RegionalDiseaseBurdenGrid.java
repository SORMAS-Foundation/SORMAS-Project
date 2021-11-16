package de.symeda.sormas.ui.dashboard.diseasedetails;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.data.util.converter.StringToFloatConverter;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.renderers.HtmlRenderer;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.PercentageRenderer;

import java.util.Locale;

public class RegionalDiseaseBurdenGrid extends Grid {
	private static final String VIEW_DETAILS_BTN_ID = "viewDetails";

	DashboardDataProvider dashboardDataProvider;

	public RegionalDiseaseBurdenGrid(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;
//		setSizeFull();

//		getColumn(DiseaseBurdenDto.CASES_DIFFERENCE_PERCENTAGE)
//			.setHeaderCaption(I18nProperties.getPrefixCaption(DiseaseBurdenDto.I18N_PREFIX, DiseaseBurdenDto.CASES_DIFFERENCE));

		// format columns
//		getColumn(DiseaseBurdenDto.CASE_FATALITY_RATE).setRenderer(new PercentageRenderer());

		// format casesGrowth column with chevrons
		/*getColumn(DiseaseBurdenDto.CASES_DIFFERENCE_PERCENTAGE).setConverter(new StringToFloatConverter() {

			@Override
			public String convertToPresentation(Float value, Class<? extends String> targetType, Locale locale) throws ConversionException {

				String stringRepresentation = super.convertToPresentation(value, targetType, locale);
				String chevronType = "";
				String criticalLevel = "";

				if (value > 0) {
					chevronType = VaadinIcons.CHEVRON_UP.getHtml();
					criticalLevel = CssStyles.LABEL_CRITICAL;
				} else if (value < 0) {
					chevronType = VaadinIcons.CHEVRON_DOWN.getHtml();
					criticalLevel = CssStyles.LABEL_POSITIVE;
				} else {
					chevronType = VaadinIcons.CHEVRON_RIGHT.getHtml();
					criticalLevel = CssStyles.LABEL_IMPORTANT;
				}

				String strValue = "" + Math.abs(value);
				if (strValue.equals("100.0"))
					strValue = "100";
//				or use below to remove insignificant decimals
//				if (strValue.endsWith(".0"))
//					strValue = strValue.substring(0, strValue.length() - 3);

				//@formatter:off
                    stringRepresentation =
                            "<div style=\"width:100%\">"
                                    +	"<div class=\"\" style=\"display: inline-block;margin-top: 2px;width: 70%;text-align:left;\">" + strValue + "%" + "</div>"
                                    +	"<div class=\"v-label v-widget " + criticalLevel + " v-label-" + criticalLevel
                                    +		" align-center v-label-align-center bold v-label-bold large v-label-large v-has-width\" "
                                    +		" style=\"width: 15px;width: 30%;text-align: left;\">"
                                    +		"<span class=\"v-icon\" style=\"font-family: VaadinIcons;\">" + chevronType + "</span>"
                                    + 	"</div>"
                                    + "</div>";
                    //@formatter:on

				return stringRepresentation;
			}
		}).setRenderer(new HtmlRenderer());
*/


	}

	public void refresh(){
		BeanItemContainer<DiseaseBurdenDto> container = new BeanItemContainer<DiseaseBurdenDto>(DiseaseBurdenDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		setContainerDataSource(generatedContainer);

		setColumns(
				DiseaseBurdenDto.CASES_REGION,
				DiseaseBurdenDto.CASE_COUNT,
				DiseaseBurdenDto.CASES_ACTIVE_CASE,
				DiseaseBurdenDto.CASES_RECOVERED_CASES,
				DiseaseBurdenDto.CASE_DEATH_COUNT);

//		Language userLanguage = I18nProperties.getUserLanguage();
//		getColumn(DiseaseBurdenDto.CASES_RECOVERED_CASES);
//		getColumn(DiseaseBurdenDto.CASES_ACTIVE_CASE);
//		getColumn(DiseaseBurdenDto.CASE_DEATH_COUNT);
//		getColumn(DiseaseBurdenDto.CASE_COUNT).setCaption(I18nProperties.getPrefixCaption(CaseIndexDto.I18N_PREFIX, CaseIndexDto.UUID));
//		getColumn(COLUMN_COMPLETENESS).setCaption(I18nProperties.getPrefixCaption(CaseIndexDto.I18N_PREFIX, CaseIndexDto.COMPLETENESS));
//		getColumn(COLUMN_COMPLETENESS).setSortable(false);

		for (Grid.Column column : getColumns()) {
			if (column.getPropertyId().equals(VIEW_DETAILS_BTN_ID)) {
				column.setHeaderCaption("");
			} else {
				column.setHeaderCaption(
						I18nProperties.getPrefixCaption(DiseaseBurdenDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
			}
		}

		setSelectionMode(Grid.SelectionMode.NONE);
	}

/*	@SuppressWarnings("unchecked")
	public void reload() {

		DataProvider<DiseaseBurdenDto> dataProvider = (DataProvider<DiseaseBurdenDto>) getDataProvider();
		TreeData<CaseIndexDto> data = dataProvider.getTreeData();
		data.clear();

		if (hiddenUuidPairs == null) {
			hiddenUuidPairs = new ArrayList<>();
		}

		List<CaseIndexDto[]> casePairs = FacadeProvider.getCaseFacade().getCasesForDuplicateMerging(criteria, ignoreRegion);
		for (CaseIndexDto[] casePair : casePairs) {
			boolean uuidPairExists = false;
			for (String[] hiddenUuidPair : hiddenUuidPairs) {
				if (hiddenUuidPair[0].equals(casePair[0].getUuid()) && hiddenUuidPair[1].equals(casePair[1].getUuid())) {
					uuidPairExists = true;
				}
			}

			if (uuidPairExists) {
				continue;
			}

			data.addItem(null, casePair[0]);
			data.addItem(casePair[0], casePair[1]);
		}
		dataCount = casePairs.size();

		expandRecursively(data.getRootItems(), 0);
		dataProvider.refreshAll();
	}

	public void reload(boolean ignoreRegion) {
		this.ignoreRegion = ignoreRegion;
		reload();
	}*/
}
