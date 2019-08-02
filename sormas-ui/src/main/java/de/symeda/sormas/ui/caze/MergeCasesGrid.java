package de.symeda.sormas.ui.caze;

import java.util.Date;
import java.util.List;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class MergeCasesGrid extends TreeGrid<CaseIndexDto> {

	public static final String COLUMN_DISEASE = Captions.columnDiseaseShort;
	public static final String COLUMN_ACTIONS = "actions";
	public static final String COLUMN_UUID = "uuidLink";
	
	private CaseCriteria criteria;

	@SuppressWarnings("unchecked")
	public MergeCasesGrid() {
		super(CaseIndexDto.class);
		setSizeFull();
		setSelectionMode(SelectionMode.NONE);

		Column<CaseIndexDto, String> diseaseColumn = addColumn(caze -> 
			DiseaseHelper.toString(caze.getDisease(), caze.getDiseaseDetails()));
		diseaseColumn.setId(COLUMN_DISEASE);
		
		addComponentColumn(indexDto -> {
			return buildButtonLayout(indexDto);
		}).setId(COLUMN_ACTIONS);
		
		addComponentColumn(indexDto -> {
			Link link = new Link(DataHelper.getShortUuid(indexDto.getUuid()), new ExternalResource(SormasUI.get().getPage().getLocation().getRawPath() + "#!" + CaseDataView.VIEW_NAME + "/" + indexDto.getUuid()));
			link.setTargetName("_blank");
			return link;
		}).setId(COLUMN_UUID);
		
		setColumns(COLUMN_UUID, CaseIndexDto.EPID_NUMBER, COLUMN_DISEASE, CaseIndexDto.CASE_CLASSIFICATION, CaseIndexDto.OUTCOME,
				CaseIndexDto.INVESTIGATION_STATUS, CaseIndexDto.PERSON_FIRST_NAME, CaseIndexDto.PERSON_LAST_NAME, CaseIndexDto.AGE_AND_BIRTH_DATE,
				CaseIndexDto.SEX, CaseIndexDto.DISTRICT_NAME, CaseIndexDto.HEALTH_FACILITY_NAME, CaseIndexDto.POINT_OF_ENTRY_NAME,
				CaseIndexDto.REPORT_DATE, CaseIndexDto.CREATION_DATE, COLUMN_ACTIONS);

		((Column<CaseIndexDto, Date>) getColumn(CaseIndexDto.REPORT_DATE)).setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat()));
		((Column<CaseIndexDto, Date>) getColumn(CaseIndexDto.CREATION_DATE)).setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat()));

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(
					CaseIndexDto.I18N_PREFIX, column.getId().toString(), column.getCaption()));
		}
		getColumn(COLUMN_ACTIONS).setCaption("");
		getColumn(COLUMN_UUID).setCaption(I18nProperties.getPrefixCaption(CaseIndexDto.I18N_PREFIX, CaseIndexDto.UUID));
		
		this.setStyleGenerator(new StyleGenerator<CaseIndexDto>() {
			@Override
			public String apply(CaseIndexDto item) {
				TreeDataProvider<CaseIndexDto> dataProvider = (TreeDataProvider<CaseIndexDto>) getDataProvider();
				TreeData<CaseIndexDto> data = dataProvider.getTreeData();
				
				if (data.getRootItems().contains(item)) {
					return "v-treegrid-parent-row";
				} else {
					return "v-treegrid-child-row";
				}
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private HorizontalLayout buildButtonLayout(CaseIndexDto caze) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(false);
		Button btnMerge = new Button(I18nProperties.getCaption(Captions.actionMerge));
		btnMerge.setIcon(VaadinIcons.COMPRESS_SQUARE);
		Button btnPick = new Button(I18nProperties.getCaption(Captions.actionPick));
		btnPick.setIcon(VaadinIcons.CHECK);
		Button btnDismiss = null;
		
		TreeDataProvider<CaseIndexDto> dataProvider = (TreeDataProvider<CaseIndexDto>) getDataProvider();
		TreeData<CaseIndexDto> data = dataProvider.getTreeData();
		
		if (data.getParent(caze) == null) {
			CssStyles.style(btnMerge, CssStyles.HSPACE_RIGHT_5, ValoTheme.BUTTON_PRIMARY);
			CssStyles.style(btnPick, CssStyles.HSPACE_RIGHT_5, ValoTheme.BUTTON_PRIMARY);
			btnDismiss = new Button(I18nProperties.getCaption(Captions.actionDismiss));
			btnDismiss.setIcon(VaadinIcons.CLOSE);
			btnDismiss.addClickListener(e -> {
				dataProvider.getTreeData().removeItem(caze);
				dataProvider.refreshAll();
			});
		} else {
			CssStyles.style(btnMerge, ValoTheme.BUTTON_LINK);
			CssStyles.style(btnPick, ValoTheme.BUTTON_LINK);
		}
	
		layout.addComponents(btnMerge, btnPick);
		if (btnDismiss != null) {
			layout.addComponent(btnDismiss);
		}
		
		return layout;
	}
	
	@SuppressWarnings("unchecked")
	public void reload() {		
		TreeDataProvider<CaseIndexDto> dataProvider = (TreeDataProvider<CaseIndexDto>) getDataProvider();
		TreeData<CaseIndexDto> data = dataProvider.getTreeData();
		data.clear();
		
		List<CaseIndexDto[]> casePairs = FacadeProvider.getCaseFacade().getCasesForDuplicateMerging(criteria, UserProvider.getCurrent().getUuid());
		for (CaseIndexDto[] casePair : casePairs) {
			data.addItem(null, casePair[0]);
			data.addItem(casePair[0], casePair[1]);
			expand(casePair[0]);
		}
		
		dataProvider.refreshAll();
	}
	
	public void setCriteria(CaseCriteria criteria) {
		this.criteria = criteria;
	}

}
