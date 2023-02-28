package de.symeda.sormas.ui.report;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.campaign.data.CampaignFormDataIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.utils.FilteredGrid;
import elemental.json.JsonValue;

@SuppressWarnings("serial")
@Component
public class CompletionAnalysisGrid extends FilteredGrid<CampaignFormDataIndexDto, CampaignFormDataCriteria> {

	private static final long serialVersionUID = -1L;

	@SuppressWarnings("unchecked")
	public CompletionAnalysisGrid(CampaignFormDataCriteria criteria, FormAccess formacc) {
		super(CampaignFormDataIndexDto.class);
		setSizeFull();

		// To Do enable other loader
		setLazyDataProvider(formacc);
		setCriteria(criteria);

		setColumns(
				CampaignFormDataIndexDto.AREA, 
				CampaignFormDataIndexDto.REGION, CampaignFormDataIndexDto.DISTRICT,
				//CampaignFormDataIndexDto.COMMUNITY, 
				CampaignFormDataIndexDto.CCODE,
				CampaignFormDataIndexDto.COMMUNITYNUMBER,
				CampaignFormDataIndexDto.ANALYSIS_FIELD_A,
				CampaignFormDataIndexDto.ANALYSIS_FIELD_B,
				CampaignFormDataIndexDto.ANALYSIS_FIELD_C,
				CampaignFormDataIndexDto.ANALYSIS_FIELD_D
				);

//		((Column<CampaignFormDataIndexDto, Set<FormAccess>>) getColumn(CampaignFormDataIndexDto.REP_FORMACCESS)).setRenderer(new CollectionValueProvider<Set<FormAccess>>(), new HtmlRenderer());
//		((Column<CampaignFormDataIndexDto, String>) getColumn(CampaignFormDataIndexDto.REP_USERNAME)).setRenderer(value -> String.valueOf(value).replace("[", "").replace("]", "").replace("null,", "").replace("null", ""), new HtmlRenderer());
//			

		for (Column<?, ?> column : getColumns()) {
			column.setDescriptionGenerator(CampaignFormDataIndexDto -> column.getCaption());
			// System.out.println(column.getId() +" dcolumn.getId() ");
			if (column.getId().toString().equals("community")) {
				column.setCaption(I18nProperties.getPrefixCaption(CampaignFormDataIndexDto.I18N_PREFIX,
						column.getId().toString(), column.getCaption()));
			} 
			if(column.getCaption().equalsIgnoreCase("Analysis_a")) {
				column.setCaption("ICM Household Monitoring");
			}
			if(column.getCaption().equalsIgnoreCase("Analysis_b")) {
				column.setCaption("ICM Revisits");
			}
			if(column.getCaption().equalsIgnoreCase("Analysis_c")) {
				column.setCaption("ICM Supervisor Monitoring");
			}
			if(column.getCaption().equalsIgnoreCase("Analysis_d")) {
				column.setCaption("ICM Team Monitoring");
			}
			if(column.getCaption().equalsIgnoreCase("Clusternumber")) {
				column.setCaption("Cluster Number");
			}
			else {
				column.setCaption(I18nProperties.getPrefixCaption(CampaignFormDataIndexDto.I18N_PREFIX,
						column.getId().toString(), column.getCaption()));
			}
		}

	}

	public void setLazyDataProvider(FormAccess formacc) {
		System.out.println("sdafasdfasddfgsdfhsdfg");

		DataProvider<CampaignFormDataIndexDto, CampaignFormDataCriteria> dataProvider = DataProvider
				.fromFilteringCallbacks(
						query -> FacadeProvider.getCampaignFormDataFacade()
								.getByCompletionAnalysis(query.getFilter().orElse(null), query.getOffset(),
										query.getLimit(),
										query.getSortOrders().stream()
												.map(sortOrder -> new SortProperty(sortOrder.getSorted(),
														sortOrder.getDirection() == SortDirection.ASCENDING))
												.collect(Collectors.toList()), formacc)
								.stream(),
						query -> Integer.parseInt(FacadeProvider.getCampaignFormDataFacade()
								.getByCompletionAnalysisCount(query.getFilter().orElse(null), query.getOffset(),
										query.getLimit(),
										query.getSortOrders().stream()
												.map(sortOrder -> new SortProperty(sortOrder.getSorted(),
														sortOrder.getDirection() == SortDirection.ASCENDING))
												.collect(Collectors.toList()), formacc))
								);

		System.out.println("sdafasdfasdfgasdgvasdfgsdfhsdfg " + dataProvider);
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
	
	public void addCustomColumn(String property, String caption) {
		if (!property.toString().contains("readonly")) {

			Column<CampaignFormDataIndexDto, Object> newColumn = addColumn(
					e -> e.getForm().toString());// .getFormValues().stream().filter(v -> v.getId().equals(property)).findFirst().orElse(null));
			newColumn.setSortable(false);
			newColumn.setCaption(caption);
			newColumn.setId(property);
			newColumn.setWidth(240.0);
			newColumn.setDescriptionGenerator(CampaignFormDataIndexDto -> newColumn.getCaption());// set the
																									// description
																									// of default
																									// columns
																									// #94-iyanuu

		}

	}

}
