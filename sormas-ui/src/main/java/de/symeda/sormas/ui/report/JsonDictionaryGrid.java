package de.symeda.sormas.ui.report;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.renderers.HtmlRenderer;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.community.CommunityCriteriaNew;
import de.symeda.sormas.api.report.JsonDictionaryReportModelDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.ui.utils.FilteredGrid;
import elemental.json.JsonValue;

public class JsonDictionaryGrid extends FilteredGrid<JsonDictionaryReportModelDto, BaseCriteria> {
	private static final long serialVersionUID = -1L;
	
	@SuppressWarnings("unchecked")
	public JsonDictionaryGrid(CampaignFormDataCriteria criteria) {
		super(JsonDictionaryReportModelDto.class);
		setSizeFull();
		
		setLazyDataProvider();
		setCriteria(criteria);
		
		setColumns(
				JsonDictionaryReportModelDto.ID, JsonDictionaryReportModelDto.CAPTION,
				JsonDictionaryReportModelDto.DATATYPE, JsonDictionaryReportModelDto.FORM_TYPE,
				JsonDictionaryReportModelDto.MODALITY
				);
		
		for (Column<?, ?> column : getColumns()) {
			column.setDescriptionGenerator(CommunityUserReportModelDto -> column.getCaption());
			//System.out.println(column.getId() +" dcolumn.getId() ");
			if(column.getId().toString().equals("community")) {
				column.setCaption(I18nProperties.getPrefixCaption(JsonDictionaryReportModelDto.I18N_PREFIX, column.getId().toString(),
						column.getCaption()));
			}else {
			column.setCaption(I18nProperties.getPrefixCaption(JsonDictionaryReportModelDto.I18N_PREFIX, column.getId().toString(),
					column.getCaption()));
			}
		}
	}
	public void setLazyDataProvider() {
		System.out.println("sdafasdfasddfgsdfhsdfg");
		
		DataProvider<JsonDictionaryReportModelDto, BaseCriteria> dataProvider = DataProvider
				.fromFilteringCallbacks(
						query -> FacadeProvider.getCampaignFormDataFacade().getByJsonFormDefinitonToCSV().stream(),
						query -> Integer.parseInt(FacadeProvider.getCampaignFormDataFacade()
								.getByJsonFormDefinitonToCSVCount())
								);
		
		System.out.println("sdafasdfasdfgasdgvasdfgsdfhsdfg "+dataProvider);
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}
	
	public void reload() {

		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		getDataProvider().refreshAll();
	}

	public static class ActiveRenderer extends HtmlRenderer {

		@Override
		public JsonValue encode(String value) {
			String iconValue = VaadinIcons.CHECK_SQUARE_O.getHtml();
			if (!Boolean.parseBoolean(value)) {
				iconValue = VaadinIcons.THIN_SQUARE.getHtml();
			}
			return super.encode(iconValue);
		}
	}
	
	
}
