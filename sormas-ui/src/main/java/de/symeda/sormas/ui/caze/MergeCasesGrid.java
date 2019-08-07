package de.symeda.sormas.ui.caze;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class MergeCasesGrid extends TreeGrid<CaseIndexDto> {

	public static final String COLUMN_DISEASE = Captions.columnDiseaseShort;
	public static final String COLUMN_ACTIONS = "actions";
	
	private CaseCriteria criteria;
	private List<CaseIndexDto> rootCases;
	private List<CaseIndexDto> childCases;

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
		}).setId(COLUMN_ACTIONS).setStyleGenerator(item -> "align-center");
		
		setColumns(CaseIndexDto.UUID, CaseIndexDto.EPID_NUMBER, COLUMN_DISEASE, CaseIndexDto.CASE_CLASSIFICATION, CaseIndexDto.OUTCOME,
				CaseIndexDto.INVESTIGATION_STATUS, CaseIndexDto.PERSON_FIRST_NAME, CaseIndexDto.PERSON_LAST_NAME, CaseIndexDto.AGE_AND_BIRTH_DATE,
				CaseIndexDto.SEX, CaseIndexDto.DISTRICT_NAME, CaseIndexDto.HEALTH_FACILITY_NAME, CaseIndexDto.POINT_OF_ENTRY_NAME,
				CaseIndexDto.REPORT_DATE, CaseIndexDto.CREATION_DATE, COLUMN_ACTIONS);

		((Column<CaseIndexDto, String>) getColumn(CaseIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<CaseIndexDto, Date>) getColumn(CaseIndexDto.REPORT_DATE)).setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat()));
		((Column<CaseIndexDto, Date>) getColumn(CaseIndexDto.CREATION_DATE)).setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat()));

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(
					CaseIndexDto.I18N_PREFIX, column.getId().toString(), column.getCaption()));
		}
		getColumn(COLUMN_ACTIONS).setCaption("");
		
		this.setStyleGenerator(new StyleGenerator<CaseIndexDto>() {
			@Override
			public String apply(CaseIndexDto item) {
				if (rootCases.indexOf(item) % 2 == 0 || childCases.indexOf(item) % 2 == 0) {
					return "v-treegrid-even-row";
				} else {
					return "v-treegrid-uneven-row";
				}
			}
		});
		
//		this.addSortListener(e -> {
//			TreeDataProvider<CaseIndexDto> dataProvider = (TreeDataProvider<CaseIndexDto>) getDataProvider();
//			dataProvider.refreshAll();
//			TreeData<CaseIndexDto> data = dataProvider.getTreeData();
//		});
	}
	
	@SuppressWarnings("unchecked")
	private HorizontalLayout buildButtonLayout(CaseIndexDto caze) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(false);
		Button btnMerge;
		Button btnPick;
		Button btnDismiss = null;
		Button btnCompare = null;
		
		if (rootCases.contains(caze)) {
			btnMerge = new Button(I18nProperties.getCaption(Captions.actionMerge));
			CssStyles.style(btnMerge, CssStyles.HSPACE_RIGHT_5, ValoTheme.BUTTON_PRIMARY);
			btnPick = new Button(I18nProperties.getCaption(Captions.actionPick));
			CssStyles.style(btnPick, CssStyles.HSPACE_RIGHT_5, ValoTheme.BUTTON_PRIMARY);
			btnDismiss = new Button(I18nProperties.getCaption(Captions.actionDismiss));
			btnDismiss.setIcon(VaadinIcons.CLOSE);
			btnDismiss.addClickListener(e -> {
				TreeDataProvider<CaseIndexDto> dataProvider = (TreeDataProvider<CaseIndexDto>) getDataProvider();
				dataProvider.getTreeData().removeItem(caze);
				dataProvider.refreshAll();
			});
		} else {
			btnMerge = new Button("");
			CssStyles.style(btnMerge, CssStyles.HSPACE_RIGHT_5);
			btnPick = new Button("");
			CssStyles.style(btnPick, CssStyles.HSPACE_RIGHT_5);
			btnCompare = new Button(I18nProperties.getCaption(Captions.actionCompare));
			btnCompare.setIcon(VaadinIcons.EXCHANGE);
			btnCompare.addClickListener(e -> {
				TreeDataProvider<CaseIndexDto> dataProvider = (TreeDataProvider<CaseIndexDto>) getDataProvider();
				buildAndOpenCaseComparisonWindow(dataProvider.getTreeData().getParent(caze).getUuid(), caze.getUuid());
			});
		}

		btnMerge.setIcon(VaadinIcons.COMPRESS_SQUARE);
		btnPick.setIcon(VaadinIcons.CHECK);
	
		layout.addComponents(btnMerge, btnPick);
		if (btnDismiss != null) {
			layout.addComponent(btnDismiss);
		}
		if (btnCompare != null) {
			layout.addComponent(btnCompare);
		}
		
		return layout;
	}

	private void buildAndOpenCaseComparisonWindow(String firstCaseUuid, String secondCaseUuid) {
		CaseComparisonLayout layout = new CaseComparisonLayout(firstCaseUuid, secondCaseUuid);
		layout.setWidth(1024, Unit.PIXELS);
		Window window = VaadinUiUtil.showPopupWindow(layout);
		window.setHeight(95, Unit.PERCENTAGE);
		window.setCaption(I18nProperties.getString(Strings.headingCaseComparison));
	}

	@SuppressWarnings("unchecked")
	public void reload() {
		rootCases = new ArrayList<>();
		childCases = new ArrayList<>();
		
		TreeDataProvider<CaseIndexDto> dataProvider = (TreeDataProvider<CaseIndexDto>) getDataProvider();
		TreeData<CaseIndexDto> data = dataProvider.getTreeData();
		data.clear();
		
		List<CaseIndexDto[]> casePairs = FacadeProvider.getCaseFacade().getCasesForDuplicateMerging(criteria, UserProvider.getCurrent().getUuid());
		for (CaseIndexDto[] casePair : casePairs) {
			data.addItem(null, casePair[0]);
			data.addItem(casePair[0], casePair[1]);
			expand(casePair[0]);
			
			rootCases.add(casePair[0]);
			childCases.add(casePair[1]);
		}
		
		dataProvider.refreshAll();
	}
	
	public void setCriteria(CaseCriteria criteria) {
		this.criteria = criteria;
	}

}
