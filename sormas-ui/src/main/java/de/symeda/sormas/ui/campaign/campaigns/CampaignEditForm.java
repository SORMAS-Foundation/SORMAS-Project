/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.campaign.campaigns;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserType;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.configuration.infrastructure.InfrastructureImportLayout;
import org.apache.commons.collections.CollectionUtils;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.MultiSelectionModel;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignTreeGridDto;
import de.symeda.sormas.api.campaign.CampaignTreeGridDtoImpl;
import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.api.infrastructure.area.AreaDto;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class CampaignEditForm extends AbstractEditForm<CampaignDto> {

	private static final long serialVersionUID = 7762204114905664597L;

	private static final String STATUS_CHANGE = "statusChange";
	private static final String CAMPAIGN_BASIC_HEADING_LOC = "campaignBasicHeadingLoc";
	private static final String USAGE_INFO = "usageInfo";
	private static final String ROUND_COMPONETS = "roundComponet";
	private static final String CAMPAIGN_TYPE_LOC = "typeLocation";
	private static final String CAMPAIGN_TYPE_SEARCH = "typeLocationSearch";
	private static final String CAMPAIGN_DATA_LOC = "campaignDataLoc";
	private static final String CAMPAIGN_DASHBOARD_LOC = "campaignDashboardLoc";
	private static final String SPACE_LOC = "spaceLoc";
	private static final String SPACE_LOCX = "spaceLocx";

	private static final String PRE_CAMPAIGN = "pre-campaign";
	private static final String INTRA_CAMPAIGN = "intra-campaign";
	private static final String POST_CAMPAIGN = "post-campaign";

	private static final String ASSOCIATE_CAMPAIGN = "Associate Campaign";
	private static final String POPULATION_CAMPAIGN = "Campaign Population";

	private OptionGroup clusterfieldx;

	private static final String HTML_LAYOUT = loc(CAMPAIGN_BASIC_HEADING_LOC)
			+ fluidRowLocs(CampaignDto.UUID, CampaignDto.CREATING_USER_NAME, CampaignDto.CAMPAIGN_YEAR)
			+ fluidRowLocs(CampaignDto.NAME, CampaignDto.ROUND) + fluidRowLocs(CampaignDto.CAMPAIGN_AREAS)
			+ fluidRowLocs(CampaignDto.START_DATE, CampaignDto.END_DATE) + fluidRowLocs(CampaignDto.DESCRIPTION)
			+ fluidRowLocs(SPACE_LOCX) + fluidRowLocs(CampaignDto.CAMPAIGN_TYPES) + fluidRowLocs(USAGE_INFO)
			+ fluidRowLocs(CAMPAIGN_TYPE_LOC) + fluidRowLocs(CAMPAIGN_TYPE_SEARCH) + fluidRowLocs(ROUND_COMPONETS)
			+ fluidRowLocs(CAMPAIGN_DATA_LOC) + fluidRowLocs(CAMPAIGN_DASHBOARD_LOC) + fluidRowLocs(SPACE_LOC);

	private final VerticalLayout statusChangeLayout;
	private Boolean isCreateForm = null;
	private CampaignDto campaignDto;
	//private PopulationDataDto popopulationDataDto = new PopulationDataDto();

	private Set<AreaReferenceDto> areass = new HashSet<>();;
	private Set<RegionReferenceDto> region = new HashSet<>();
	private Set<DistrictReferenceDto> districts = new HashSet<>();
	private Set<CommunityReferenceDto> community = new HashSet<>();
	private Set<PopulationDataDto> popopulationDataDtoSet = new HashSet<>();

	private CampaignFormsGridComponent campaignFormsGridComponent;
	private CampaignFormsGridComponent campaignFormsGridComponent_1;
	private CampaignFormsGridComponent campaignFormsGridComponent_2;

	private CampaignDashboardElementsGridComponent campaignDashboardGridComponent;
	private CampaignDashboardElementsGridComponent campaignDashboardGridComponent_1;
	private CampaignDashboardElementsGridComponent campaignDashboardGridComponent_2;

	private TreeGrid<CampaignTreeGridDto> treeGrid = new TreeGrid<>();
	//private Tree<? super InfrastructureDataReferenceDto> tree = new Tree<>();
	//private TreeData<? super InfrastructureDataReferenceDto> treeData = new TreeData<>();

	public CampaignEditForm(CampaignDto campaignDto) {

		super(CampaignDto.class, CampaignDto.I18N_PREFIX);
		setWidth(1280, Unit.PIXELS);

		this.campaignDto = campaignDto;
		isCreateForm = campaignDto == null;
		if (isCreateForm) {
			hideValidationUntilNextCommit();
		}
		statusChangeLayout = new VerticalLayout();
		statusChangeLayout.setSpacing(false);
		statusChangeLayout.setMargin(false);
		getContent().addComponent(statusChangeLayout, STATUS_CHANGE);

		//treeGrid.setSelectionMode(SelectionMode.MULTI);
		addFields();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void addFields() {

		if (isCreateForm == null) {
			System.out.print("siCreateForm=null");
			return;
		}
		Label campaignBasicHeadingLabel = new Label(I18nProperties.getString(Strings.headingCampaignBasics));
		campaignBasicHeadingLabel.addStyleName(H3);
		getContent().addComponent(campaignBasicHeadingLabel, CAMPAIGN_BASIC_HEADING_LOC);

		addField(CampaignDto.UUID);
		addField(CampaignDto.CREATING_USER_NAME);

		DateField startDate = addField(CampaignDto.START_DATE, DateField.class);
		startDate.removeAllValidators();

		DateField endDate = addField(CampaignDto.END_DATE, DateField.class);
		endDate.removeAllValidators();

		TextField textField = addField(CampaignDto.CAMPAIGN_YEAR);
		textField.setReadOnly(true);

		startDate.addValueChangeListener(e -> {
			textField.setReadOnly(false);
			textField.setValue(DateGetYear(startDate.getValue()) + " ");
			textField.setReadOnly(true);
		});

		startDate.addValidator(new DateComparisonValidator(startDate, endDate, true, true, I18nProperties
				.getValidationError(Validations.beforeDate, startDate.getCaption(), endDate.getCaption())));
		endDate.addValidator(new DateComparisonValidator(endDate, startDate, false, true, I18nProperties
				.getValidationError(Validations.afterDate, endDate.getCaption(), startDate.getCaption())));

		addField(CampaignDto.NAME);

		ComboBox clusterfield = addField(CampaignDto.ROUND, ComboBox.class);
		// This can be optimised by putting into an enum class and adding here
		clusterfield.addItem("NID");
		clusterfield.addItem("SNID");
		clusterfield.addItem("Case Respond");
		clusterfield.addItem("Mopping-Up");

		TextArea description = addField(CampaignDto.DESCRIPTION, TextArea.class);
		description.setRows(6);

		final Label spacerx = new Label();
		spacerx.setHeight("10%");
		getContent().addComponent(spacerx, SPACE_LOCX);

		setReadOnly(true, CampaignDto.UUID, CampaignDto.CREATING_USER_NAME);
		// setVisible(!isCreateForm, CampaignDto.UUID, CampaignDto.CREATING_USER);

		setRequired(true, CampaignDto.UUID, CampaignDto.NAME, CampaignDto.CREATING_USER_NAME, CampaignDto.START_DATE,
				CampaignDto.END_DATE, CampaignDto.ROUND, CampaignDto.CAMPAIGN_YEAR);

		FieldHelper.addSoftRequiredStyle(description);
		final HorizontalLayout usageLayout = new HorizontalLayout();
		usageLayout.setWidthFull();
		Label usageLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " "
				+ I18nProperties.getString(Strings.infoUsageOfEditableCampaignGrids), ContentMode.HTML);
		usageLabel.setWidthFull();
		usageLayout.addComponent(usageLabel);
		usageLayout.setSpacing(true);
		usageLayout.setMargin(new MarginInfo(true, false, true, false));
		getContent().addComponent(usageLayout, USAGE_INFO);

		final HorizontalLayout layoutParent = new HorizontalLayout();
		layoutParent.setWidthFull();

		TabSheet tabsheetParent = new TabSheet();
		layoutParent.addComponent(tabsheetParent);

		VerticalLayout parentTab1 = new VerticalLayout();
		final HorizontalLayout layout = new HorizontalLayout();
		layout.setWidthFull();

		TabSheet tabsheet = new TabSheet();
		layout.addComponent(tabsheet);

		// Create the first tab
		VerticalLayout tab1 = new VerticalLayout();

		campaignFormsGridComponent = new CampaignFormsGridComponent(
				this.campaignDto == null ? Collections.EMPTY_LIST
						: new ArrayList<>(campaignDto.getCampaignFormMetas("pre-campaign")),
				FacadeProvider.getCampaignFormMetaFacade().getAllCampaignFormMetasAsReferencesByRound(PRE_CAMPAIGN));
		getContent().addComponent(campaignFormsGridComponent, CAMPAIGN_DATA_LOC);
		tab1.addComponent(campaignFormsGridComponent);
		tab1.setCaption("Pre Campaign Forms");
		tabsheet.addTab(tab1);

		// campaignFormsGridComponent.ListnerCampaignFilter(event);

		// This tab gets its caption from the component caption
		VerticalLayout tab2 = new VerticalLayout();

		// To Do: Check why set this to nulll in the first place
		final List<CampaignDashboardElement> campaignDashboardElements = FacadeProvider.getCampaignFacade()
				.getCampaignDashboardElements(null, PRE_CAMPAIGN);
		campaignDashboardGridComponent = new CampaignDashboardElementsGridComponent(this.campaignDto == null
				? Collections.EMPTY_LIST
				: FacadeProvider.getCampaignFacade().getCampaignDashboardElements(campaignDto.getUuid(), PRE_CAMPAIGN),
				campaignDashboardElements);
		getContent().addComponent(campaignDashboardGridComponent, CAMPAIGN_DASHBOARD_LOC);
		tab2.addComponent(campaignDashboardGridComponent);
		tab2.setCaption("Pre Campaign Dashboard");
		tabsheet.addTab(tab2);

		getContent().addComponent(layout, ROUND_COMPONETS);

		parentTab1.addComponent(layout);
		parentTab1.setCaption("Pre-Campaign Phase");
		if (UserProvider.getCurrent().hasUserType(UserType.WHO_USER) || UserProvider.getCurrent().hasUserType(UserType.EOC_USER)) {
			tabsheetParent.addTab(parentTab1);
		}

		// start of a child campaign

		VerticalLayout parentTab3 = new VerticalLayout();

		final HorizontalLayout layoutPost = new HorizontalLayout();
		layoutPost.setWidthFull();

		TabSheet tabsheetPost = new TabSheet();
		layoutPost.addComponent(tabsheetPost);

		// Create the first tab
		VerticalLayout tab1Post = new VerticalLayout();

		campaignFormsGridComponent_1 = new CampaignFormsGridComponent(
				this.campaignDto == null ? Collections.EMPTY_LIST
						: new ArrayList<>(campaignDto.getCampaignFormMetas("intra-campaign")),
				FacadeProvider.getCampaignFormMetaFacade().getAllCampaignFormMetasAsReferencesByRound(INTRA_CAMPAIGN));
		getContent().addComponent(campaignFormsGridComponent_1, CAMPAIGN_DATA_LOC);
		tab1Post.addComponent(campaignFormsGridComponent_1);
		tab1Post.setCaption("Intra Campaign Forms");
		tabsheetPost.addTab(tab1Post);

		// This tab gets its caption from the component caption
		VerticalLayout tab2Post = new VerticalLayout();
		final List<CampaignDashboardElement> campaignDashboardElementsxx = FacadeProvider.getCampaignFacade()
				.getCampaignDashboardElements(null, INTRA_CAMPAIGN);
		campaignDashboardGridComponent_1 = new CampaignDashboardElementsGridComponent(
				this.campaignDto == null ? Collections.EMPTY_LIST
						: FacadeProvider.getCampaignFacade().getCampaignDashboardElements(campaignDto.getUuid(),
								INTRA_CAMPAIGN),
				campaignDashboardElementsxx);
		getContent().addComponent(campaignDashboardGridComponent_1, CAMPAIGN_DASHBOARD_LOC);
		tab2Post.addComponent(campaignDashboardGridComponent_1);
		tab2Post.setCaption("Intra Campaign Dashboard");
		tabsheetPost.addTab(tab2Post);

		tabsheetPost.addSelectedTabChangeListener(event -> campaignFormsGridComponent.ListnerCampaignFilter(event));

		getContent().addComponent(layoutPost, ROUND_COMPONETS);

		parentTab3.addComponent(layoutPost);
		parentTab3.setCaption("Intra-Campaign Phase");

		if (UserProvider.getCurrent().hasUserType(UserType.EOC_USER)
				|| UserProvider.getCurrent().hasUserType(UserType.WHO_USER)
				|| UserProvider.getCurrent().hasUserType(UserType.COMMON_USER)) {
			tabsheetParent.addTab(parentTab3);
		}
		// tabsheetParent.addTab(parentTab3);
		tabsheetParent.addSelectedTabChangeListener(event -> campaignFormsGridComponent.ListnerCampaignFilter(event));
		// stop

		// start of a child campaign

		VerticalLayout parentTab2 = new VerticalLayout();

		final HorizontalLayout layoutIntra = new HorizontalLayout();
		layoutIntra.setWidthFull();

		TabSheet tabsheetIntra = new TabSheet();
		layoutIntra.addComponent(tabsheetIntra);

		// Create the first tab
		VerticalLayout tab1Intra = new VerticalLayout();

		campaignFormsGridComponent_2 = new CampaignFormsGridComponent(
				this.campaignDto == null ? Collections.EMPTY_LIST
						: new ArrayList<>(campaignDto.getCampaignFormMetas("post-campaign")),
				FacadeProvider.getCampaignFormMetaFacade().getAllCampaignFormMetasAsReferencesByRound(POST_CAMPAIGN));
		// FacadeProvider.getCampaignFormMetaFacade().getAllCampaignFormMetasAsReferences());
		getContent().addComponent(campaignFormsGridComponent_2, CAMPAIGN_DATA_LOC);
		tab1Intra.addComponent(campaignFormsGridComponent_2);
		tab1Intra.setCaption("Post Campaign Forms");
		tabsheetIntra.addTab(tab1Intra);

		// This tab gets its caption from the component caption
		VerticalLayout tab2Intra = new VerticalLayout();
		final List<CampaignDashboardElement> campaignDashboardElementsx = FacadeProvider.getCampaignFacade()
				.getCampaignDashboardElements(null, POST_CAMPAIGN);
		campaignDashboardGridComponent_2 = new CampaignDashboardElementsGridComponent(this.campaignDto == null
				? Collections.EMPTY_LIST
				: FacadeProvider.getCampaignFacade().getCampaignDashboardElements(campaignDto.getUuid(), POST_CAMPAIGN),
				campaignDashboardElementsx);
		getContent().addComponent(campaignDashboardGridComponent_2, CAMPAIGN_DASHBOARD_LOC);
		tab2Intra.addComponent(campaignDashboardGridComponent_2);
		tab2Intra.setCaption("Post Campaign Dashboard");
		tabsheetIntra.addTab(tab2Intra);

		tabsheetIntra.addSelectedTabChangeListener(event -> campaignFormsGridComponent.ListnerCampaignFilter(event));

		getContent().addComponent(layoutIntra, ROUND_COMPONETS);

		parentTab2.addComponent(layoutIntra);
		parentTab2.setCaption("Post-Campaign Phase");
		if (UserProvider.getCurrent().hasUserType(UserType.WHO_USER)) {
			tabsheetParent.addTab(parentTab2);
		}
		if(campaignDto != null) {
		VerticalLayout parentTab4 = new VerticalLayout();
	//	final HorizontalLayout layout4 = new HorizontalLayout();
	//	layout4.setWidthFull();

		
		
		
		treeGrid.setWidthFull();
		treeGrid.setHeightFull();
		

		List<AreaDto> areas = FacadeProvider.getAreaFacade().getAllActiveAsReferenceAndPopulation(campaignDto);
		//List<RegionDto> regions_ = FacadeProvider.getRegionFacade().getAllActiveAsReferenceAndPopulation());
		
		treeGrid.setItems(generateTreeGridData(), CampaignTreeGridDto::getRegionData);
		
		
		treeGrid.addColumn(CampaignTreeGridDto::getName).setCaption("Location");
	//  treeGrid.addColumn(CampaignTreeGridDto::getUuid).setCaption("uuid");
	   // treeGrid.addColumn(CampaignTreeGridDto::getParentUuid).setCaption("parentuuid");
	    treeGrid.addColumn(CampaignTreeGridDto::getPopulationData).setCaption("Population");
	   // treeGrid.addColumn(CampaignTreeGridDto::getSavedData).setCaption("Saved?");
	    
	  //  treeGrid.getColumn("Saved?").setRenderer(value -> String.valueOf(value), new ActiveRenderer());
	    
	    
	    MultiSelectionModel<CampaignTreeGridDto> selectionModel
	      = (MultiSelectionModel<CampaignTreeGridDto>) treeGrid.setSelectionMode(SelectionMode.MULTI);

	    
	 //   System.out.println("area: "+campaignDto.getAreas().size() +"====== region: "+campaignDto.getRegion().size()+"   ====   district:"+campaignDto.getRegion().size());

	
		for (AreaReferenceDto root : campaignDto.getAreas()) {

			for (CampaignTreeGridDto areax : treeGrid.getTreeData().getRootItems()) {

				if (areax.getUuid().equals(root.getUuid())) {
					
					treeGrid.select(areax);
				}
				
				for (RegionReferenceDto region_root : campaignDto.getRegion()) {

					for (CampaignTreeGridDto regionx : treeGrid.getTreeData().getChildren(areax)) {

						if (regionx.getUuid().equals(region_root.getUuid())) {
							treeGrid.select(regionx);
						}
						
						for (DistrictReferenceDto district_root : campaignDto.getDistricts()) {

							for (CampaignTreeGridDto districtx : treeGrid.getTreeData().getChildren(regionx)) {

								if (districtx.getUuid().equals(district_root.getUuid())) {
									treeGrid.select(districtx);
								}
							}
						}
					}
				}
			}
		}
		
		
	
	    
		for (int i = 0; i < treeGrid.getTreeData().getRootItems().size(); i++) {

			// ftg.setIsClicked(777L);
		}

		treeGrid.addItemClickListener(e -> {

			boolean isSelectedClicked = false;
			// we set 777 to the clicked and selected items and check for it.

			if (e.getItem().getIsClicked() != null) {
				
				if (e.getItem().getIsClicked() == 777L) {
					
					// deselect this item
					treeGrid.deselect(e.getItem());

					// deselect its children
					treeGrid.getTreeData().getChildren(e.getItem())
							.forEach(ee -> treeGrid.deselect((CampaignTreeGridDto) ee));

					// deselect its grandchildren
					for (CampaignTreeGridDto firstChildren : treeGrid.getTreeData().getChildren(e.getItem())) {

						treeGrid.getTreeData().getChildren(firstChildren)
								.forEach(ee -> treeGrid.deselect((CampaignTreeGridDto) ee));
					}

					if (!e.getItem().getParentUuid().equals("Area")) {
						// treeGrid.deselect(treeGrid.getTreeData().getParent(e.getItem()));
					}

					e.getItem().setIsClicked(7L);
					
				} else {
					
					treeGrid.select(e.getItem());
					e.getItem().setIsClicked(777L);
					
					treeGrid.getTreeData().getChildren(e.getItem())
							.forEach(ee -> treeGrid.select((CampaignTreeGridDto) ee));
					
					for (CampaignTreeGridDto firstChildren : treeGrid.getTreeData().getChildren(e.getItem())) {

						treeGrid.getTreeData().getChildren(firstChildren)
								.forEach(ee -> treeGrid.select((CampaignTreeGridDto) ee));
					}
					
					
				}
			} else {
				
				treeGrid.select(e.getItem());
				e.getItem().setIsClicked(777L);
				treeGrid.getTreeData().getChildren(e.getItem())
						.forEach(ee -> treeGrid.select((CampaignTreeGridDto) ee));
				
				for (CampaignTreeGridDto firstChildren : treeGrid.getTreeData().getChildren(e.getItem())) {

					treeGrid.getTreeData().getChildren(firstChildren)
							.forEach(ee -> treeGrid.select((CampaignTreeGridDto) ee));
				}
				
			}

			for (CampaignTreeGridDto ftg : treeGrid.getSelectionModel().getSelectedItems()) {
				ftg.setIsClicked(777L);
			}
		});
	    
	    
		
		
		

		// check class of selection and cast to the appropriate class v-tree8-expander collapsed .v-tree .v-tree .sormas .v-tree-expander
		treeGrid.addSelectionListener(event -> {
			areass.clear();
			region.clear();
			districts.clear();
			community.clear();
			popopulationDataDtoSet.clear();
			
			for (int i = 0; i < event.getAllSelectedItems().size(); i++) {
				
				
				if (((CampaignTreeGridDto) event.getAllSelectedItems().toArray()[i]).getLevelAssessed() == "area") {
					AreaReferenceDto selectedArea = FacadeProvider.getAreaFacade().getAreaReferenceByUuid(
							((CampaignTreeGridDto) event.getAllSelectedItems().toArray()[i]).getUuid());
					areass.add(selectedArea);
				}
				if (((CampaignTreeGridDto) event.getAllSelectedItems().toArray()[i]).getLevelAssessed() == "region") {
					RegionReferenceDto selectedRegion = FacadeProvider.getRegionFacade().getRegionReferenceByUuid(
							((CampaignTreeGridDto) event.getAllSelectedItems().toArray()[i]).getUuid());
					region.add(selectedRegion);
				}
				if (((CampaignTreeGridDto) event.getAllSelectedItems().toArray()[i]).getLevelAssessed() == "district") {
					DistrictReferenceDto selectedDistrict = FacadeProvider.getDistrictFacade()
							.getDistrictReferenceByUuid(
									((CampaignTreeGridDto) event.getAllSelectedItems().toArray()[i]).getUuid());
					districts.add(selectedDistrict);
				}
				
				if (((CampaignTreeGridDto) event.getAllSelectedItems().toArray()[i]).getLevelAssessed() == "district") {
					
					PopulationDataDto popopulationDataDto = new PopulationDataDto();
					
					popopulationDataDto.setCampaign(FacadeProvider.getCampaignFacade().getReferenceByUuid(campaignDto.getUuid()));
					popopulationDataDto.setDistrict(FacadeProvider.getDistrictFacade()
							.getDistrictReferenceByUuid(
									((CampaignTreeGridDto) event.getAllSelectedItems().toArray()[i]).getUuid()));
					popopulationDataDtoSet.add(popopulationDataDto);
				}

			}
			if (campaignDto != null) {
				campaignDto.setAreas((Set<AreaReferenceDto>) areass);
				campaignDto.setRegion((Set<RegionReferenceDto>) region);
				campaignDto.setDistricts((Set<DistrictReferenceDto>) districts);
				//System.out.println("==================== "+popopulationDataDtoSet.size());
				campaignDto.setPopulationdata((Set<PopulationDataDto>) popopulationDataDtoSet);
				campaignDto.setCommunity((Set<CommunityReferenceDto>) community);
			}
		});
		
		parentTab4.addComponent(treeGrid);
		parentTab4.setCaption(ASSOCIATE_CAMPAIGN);
		tabsheetParent.addTab(parentTab4);
		
		//tab 5
		
		if (UserProvider.getCurrent().hasUserRight(UserRight.POPULATION_MANAGE)) {
			
			
			VerticalLayout parentTab5 = new VerticalLayout();
			
			
			VerticalLayout poplayout = new VerticalLayout();
			poplayout.setSpacing(false);
			CssStyles.style(poplayout, CssStyles.VSPACE_TOP_1);

			Label lblIntroduction = new Label(I18nProperties.getString(Strings.infoPopulationDataView));
			CssStyles.style(lblIntroduction, CssStyles.VSPACE_2, "v-wrapper-apmis");
			poplayout.addComponent(lblIntroduction);
			
			poplayout.setComponentAlignment(lblIntroduction, Alignment.MIDDLE_CENTER);

			Button btnImport = ButtonHelper.createIconButton(Captions.actionImport, VaadinIcons.UPLOAD, e -> {
				Window window = VaadinUiUtil.showPopupWindow(new InfrastructureImportLayout(InfrastructureType.POPULATION_DATA, campaignDto));
				window.setCaption(I18nProperties.getString(Strings.headingImportPopulationData));
			}, CssStyles.VSPACE_4, ValoTheme.BUTTON_PRIMARY);
			
			
			poplayout.addComponent(btnImport);
			poplayout.setComponentAlignment(btnImport, Alignment.MIDDLE_CENTER);

			Button btnExport = ButtonHelper.createIconButton(Captions.export, VaadinIcons.DOWNLOAD, null, ValoTheme.BUTTON_PRIMARY);
			poplayout.addComponent(btnExport);
			poplayout.setComponentAlignment(btnExport, Alignment.MIDDLE_CENTER);

			StreamResource populationDataExportResource = DownloadUtil.createPopulationDataExportResource();
			new FileDownloader(populationDataExportResource).extend(btnExport);

			parentTab5.addComponent(poplayout);
			

		//	parentTab5.addComponent(treeGrid);
			parentTab5.setCaption(POPULATION_CAMPAIGN);
			tabsheetParent.addTab(parentTab5);
			
			
		}
		}
	
		
		layout.setMargin(true);
		layout.setSpacing(true);
		// style todo
		tabsheetParent.setPrimaryStyleName("view-header");

		getContent().addComponent(layoutParent, CAMPAIGN_TYPE_LOC);

		final Label spacer = new Label();
		getContent().addComponent(spacer, SPACE_LOC);
		

		tabsheetParent.addSelectedTabChangeListener(new SelectedTabChangeListener() {
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				int position = tabsheetParent.getTabPosition(tabsheetParent.getTab(tabsheetParent.getSelectedTab()));
				VaadinService.getCurrentRequest().getWrappedSession().setAttribute("indexTab", position);
			}
		});
		
		
		if (VaadinService.getCurrentRequest().getWrappedSession().getAttribute("indexTab") != null) {
			tabsheetParent.setSelectedTab(
					(int) VaadinService.getCurrentRequest().getWrappedSession().getAttribute("indexTab"));
		}
		
		
	}

	@Override
	public CampaignDto getValue() {
		final CampaignDto campaignDto = super.getValue();
		HashSet<CampaignFormMetaReferenceDto> set = new HashSet<>();
		set.addAll(campaignFormsGridComponent.getItems());
		set.addAll(campaignFormsGridComponent_1.getItems());
		set.addAll(campaignFormsGridComponent_2.getItems());

		campaignDto.setCampaignFormMetas(new HashSet<>(set));

		List<CampaignDashboardElement> setDashboard = new ArrayList<>();
		setDashboard.addAll(campaignDashboardGridComponent.getItems());
		setDashboard.addAll(campaignDashboardGridComponent_1.getItems());
		setDashboard.addAll(campaignDashboardGridComponent_2.getItems());

		campaignDto.setCampaignDashboardElements(setDashboard);
		return campaignDto;
	}

	@Override
	public void setValue(CampaignDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {

		super.setValue(newFieldValue);
		campaignFormsGridComponent.setSavedItems(newFieldValue.getCampaignFormMetas() != null
				? new ArrayList<>(newFieldValue.getCampaignFormMetas("pre-campaign"))
				: new ArrayList<>());

		campaignFormsGridComponent_1.setSavedItems(newFieldValue.getCampaignFormMetas() != null
				? new ArrayList<>(newFieldValue.getCampaignFormMetas("intra-campaign"))
				: new ArrayList<>());

		campaignFormsGridComponent_2.setSavedItems(newFieldValue.getCampaignFormMetas() != null
				? new ArrayList<>(newFieldValue.getCampaignFormMetas("post-campaign"))
				: new ArrayList<>());

		if (CollectionUtils.isNotEmpty(newFieldValue.getCampaignDashboardElements())) {

			campaignDashboardGridComponent.setSavedItems(newFieldValue.getCampaignDashboardElements().stream()
					.filter(e -> e.getPhase().equals("pre-campaign")).collect(Collectors.toList()).stream()
					.sorted(Comparator.comparingInt(CampaignDashboardElement::getOrder)).collect(Collectors.toList()));

			campaignDashboardGridComponent_1.setSavedItems(newFieldValue.getCampaignDashboardElements().stream()
					.filter(e -> e.getPhase().equals("intra-campaign")).collect(Collectors.toList()).stream()
					.sorted(Comparator.comparingInt(CampaignDashboardElement::getOrder)).collect(Collectors.toList()));

			campaignDashboardGridComponent_2.setSavedItems(newFieldValue.getCampaignDashboardElements().stream()
					.filter(e -> e.getPhase().equals("post-campaign")).collect(Collectors.toList()));
		}

	}

	@Override
	public void discard() throws SourceException {
		super.discard();
		campaignFormsGridComponent.discardGrid();
		campaignFormsGridComponent_1.discardGrid();
		campaignFormsGridComponent_2.discardGrid();

		campaignDashboardGridComponent.discardGrid();
		campaignDashboardGridComponent_1.discardGrid();
		campaignDashboardGridComponent_2.discardGrid();
		SormasUI.refreshView();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	public String DateGetYear(Date dates) {

		SimpleDateFormat getYearFormat = new SimpleDateFormat("yyyy");
		String currentYear = getYearFormat.format(dates);
		return currentYear;
	}
	
	
	private List<CampaignTreeGridDto> generateTreeGridData() {
        List<CampaignTreeGridDto> gridData = new ArrayList<>();
        List<AreaDto> areas = FacadeProvider.getAreaFacade().getAllActiveAsReferenceAndPopulation(campaignDto);
		
        for (AreaDto area_ : areas) {
        	CampaignTreeGridDto areaData = new CampaignTreeGridDto(area_.getName(), area_.getAreaid(), "Area", area_.getUuid_(), "area");
        	List<RegionDto> regions_ = FacadeProvider.getRegionFacade().getAllActiveAsReferenceAndPopulation(area_.getAreaid(), campaignDto.getUuid());
        	 for (RegionDto regions_x : regions_) {
        		 CampaignTreeGridDto regionData = new CampaignTreeGridDto(regions_x.getName(), regions_x.getRegionId(), regions_x.getAreaUuid_(), regions_x.getUuid_(), "region");
        		 List<DistrictDto> district_ = FacadeProvider.getDistrictFacade().getAllActiveAsReferenceAndPopulation(regions_x.getRegionId(), campaignDto);
        		 ArrayList arr = new ArrayList<>();
        		 for (DistrictDto district_x : district_) {
        			 arr.add(new CampaignTreeGridDtoImpl(district_x.getName(), district_x.getPopulationData(), district_x.getRegionId(),
        					 district_x.getRegionUuid_(), district_x.getUuid_(), "district", district_x.getSelectedPopulationData()));
         		};
        		 
        		 regionData.setRegionData(arr);
        		 
        		 areaData.addRegionData(regionData);
            }
        	
        	 gridData.add(areaData);
        }
        return gridData;
    }

}
