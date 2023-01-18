package de.symeda.sormas.ui.report;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.community.CommunityCriteriaNew;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.report.CommunityUserReportModelDto;
import de.symeda.sormas.api.report.UserReportModelDto;
import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.api.user.UserCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.user.UserGrid.ActiveRenderer;
import de.symeda.sormas.ui.utils.CollectionValueProvider;
import de.symeda.sormas.ui.utils.FilteredGrid;
import elemental.json.JsonValue;

@SuppressWarnings("serial")
@Component
public class UserReportGrid extends FilteredGrid<CommunityUserReportModelDto, CommunityCriteriaNew> {

	private static final long serialVersionUID = -1L;

	@SuppressWarnings("unchecked")
	public UserReportGrid(CommunityCriteriaNew criteria) {
		super(CommunityUserReportModelDto.class);
		setSizeFull();
		
		//To Do enable other loader
		setLazyDataProvider();
		setCriteria(criteria);
	
		setColumns(
				CommunityUserReportModelDto.AREA,
				CommunityUserReportModelDto.REGION,
				CommunityUserReportModelDto.DISTRICT,
				CommunityUserReportModelDto.REP_FORMACCESS,
				CommunityUserReportModelDto.REP_CLUSTERNO,
				CommunityUserReportModelDto.REP_USERNAME,
				CommunityUserReportModelDto.REP_MESSAGE
					);
		
		((Column<CommunityUserReportModelDto, Set<FormAccess>>) getColumn(CommunityUserReportModelDto.REP_FORMACCESS)).setRenderer(new CollectionValueProvider<Set<FormAccess>>(), new HtmlRenderer());
		((Column<CommunityUserReportModelDto, String>) getColumn(CommunityUserReportModelDto.REP_USERNAME)).setRenderer(value -> String.valueOf(value).replace("[", "").replace("]", "").replace("null,", "").replace("null", ""), new HtmlRenderer());
			

		for (Column<?, ?> column : getColumns()) {
			//System.out.println(column.getId() +" dcolumn.getId() ");
			if(column.getId().toString().equals("community")) {
				column.setCaption(I18nProperties.getPrefixCaption(UserReportModelDto.I18N_PREFIX, column.getId().toString(),
						column.getCaption()));
			}else {
			column.setCaption(I18nProperties.getPrefixCaption(UserReportModelDto.I18N_PREFIX, column.getId().toString(),
					column.getCaption()));
			}
		}
		
		
	}

	public void setLazyDataProvider() {
		System.out.println("sdafasdfasddfgsdfhsdfg");
		
		DataProvider<CommunityUserReportModelDto, CommunityCriteriaNew> dataProvider = DataProvider.fromFilteringCallbacks(
				query -> FacadeProvider.getCommunityFacade()
					.getAllActiveCommunitytoRerence(
						query.getFilter().orElse(null),
						query.getOffset(),
						query.getLimit(),
						query.getSortOrders()
							.stream()
							.map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
							.collect(Collectors.toList()), false)
					.stream(),
				query -> {
					return (int) FacadeProvider.getCommunityFacade().getAllActiveCommunitytoRerence(
							query.getFilter().orElse(null),
							query.getOffset(),
							query.getLimit(),
							null,
							true).size();
				});
		
	//	List<CommunityUserReportModelDto> reportLists = FacadeProvider.getCommunityFacade().getAllActiveCommunitytoRerence(getCriteria());
		
	//	reportLists.removeIf(e -> e.getMessage().equals("Correctly assigned"));
		
	//	DataProvider<CommunityUserReportModelDto, UserCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
	//		query -> reportLists.stream(),
	//		query -> {
	//			return (int) reportLists.size();
	//		});
		System.out.println("sdafasdfasdfgasdgvasdfgsdfhsdfg "+dataProvider);
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}
	
	public void reload() {

		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		// ViewConfiguration viewConfiguration =
		// ViewModelProviders.of(UsersView.class).get(ViewConfiguration.class);
//			if (viewConfiguration.isInEagerMode()) {
//				setEagerDataProvider();
//			}

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
