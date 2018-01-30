package de.symeda.sormas.ui.configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.outbreak.OutbreakDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.StringToAnythingConverter;

@SuppressWarnings("serial")
public class OutbreakOverviewGrid extends Grid implements ItemClickListener {

	private static final String REGION = "State";

	private UserDto user;

	public OutbreakOverviewGrid() {
		super();
		setSizeFull();
		setSelectionMode(SelectionMode.NONE);		

		user = LoginHelper.getCurrentUser();

		addColumn(REGION, RegionReferenceDto.class);
		for (Disease disease : Disease.values()) {
			addColumn(disease, DiseaseOutbreakInformation.class);
			getColumn(disease).setHeaderCaption(disease.toShortString());
			getColumn(disease).setConverter(new StringToAnythingConverter<DiseaseOutbreakInformation>(DiseaseOutbreakInformation.class));
			getColumn(disease).setRenderer(new HtmlRenderer());
		}

		setCellDescriptionGenerator(cell -> getCellDescription(cell));
		addItemClickListener(this);
	}
	
	private String getCellDescription(CellReference cell) {
		Item item = cell.getItem();
		
		if (cell.getPropertyId() == REGION) {
			return "";
		}
		
		Set<DistrictReferenceDto> affectedDistricts = ((DiseaseOutbreakInformation) item.getItemProperty((Disease) cell.getPropertyId()).getValue()).getAffectedDistricts();
		
		if (affectedDistricts.isEmpty()) {
			return "No outbreak";
		}

		StringBuilder affectedDistrictsStringBuilder = new StringBuilder();
		affectedDistrictsStringBuilder.append("Affected districts: ");
		
		int index = 0;
		for (DistrictReferenceDto affectedDistrict : affectedDistricts) {
			affectedDistrictsStringBuilder.append(affectedDistrict.toString());
			if (index < affectedDistricts.size() - 1) {
				affectedDistrictsStringBuilder.append(", ");
			}
			index++;
		}
		
		return affectedDistrictsStringBuilder.toString();
	}

	public void reload() {
		Container.Indexed container = getContainerDataSource();
		container.removeAllItems();

		// Initially set all columns to their default value
		for (RegionReferenceDto region : FacadeProvider.getRegionFacade().getAllAsReference()) {
			addItem(region);
		}

		// Alter cells with regions and diseases that actually have an outbreak
		List<OutbreakDto> activeOutbreaks = FacadeProvider.getOutbreakFacade().getAllAfter(null);

		for (OutbreakDto outbreak : activeOutbreaks) {
			DistrictReferenceDto outbreakDistrict = outbreak.getDistrict();
			RegionReferenceDto outbreakRegion = FacadeProvider.getDistrictFacade().getDistrictByUuid(outbreakDistrict.getUuid()).getRegion();
			Disease outbreakDisease = outbreak.getDisease();
			
			((DiseaseOutbreakInformation) container.getItem(outbreakRegion).getItemProperty(outbreakDisease).getValue()).getAffectedDistricts().add(outbreakDistrict);
		}
	}

	@SuppressWarnings("unchecked")
	private void addItem(RegionReferenceDto region) {
		int totalDistricts = FacadeProvider.getDistrictFacade().getCountByRegion(region.getUuid());
		Item item = getContainerDataSource().addItem(region);
		item.getItemProperty(REGION).setValue(region);
		for (Disease disease : Disease.values()) {
			item.getItemProperty(disease).setValue(new DiseaseOutbreakInformation(totalDistricts, region, new HashSet<>()));
		}
	}

	@Override
	public void itemClick(ItemClickEvent event) {
		Item clickedItem = event.getItem();
		
		if (event.getPropertyId() == REGION) {
			return;
		}

		// Open the outbreak configuration window for the clicked row when
		// a) the user is allowed to configure all existing outbreaks or
		// b) the user is allowed to configure outbreaks in his assigned region and has clicked the respective row
		if (LoginHelper.hasUserRight(UserRight.OUTBREAK_CONFIGURE_ALL)) {
			ControllerProvider.getOutbreakController().openOutbreakConfigurationWindow((Disease) event.getPropertyId(), (DiseaseOutbreakInformation) clickedItem.getItemProperty((Disease) event.getPropertyId()).getValue());
		} else if (LoginHelper.hasUserRight(UserRight.OUTBREAK_CONFIGURE_RESTRICTED)) {
			if (user.getRegion().equals(clickedItem.getItemProperty(REGION).getValue())) {
				ControllerProvider.getOutbreakController().openOutbreakConfigurationWindow((Disease) event.getPropertyId(), (DiseaseOutbreakInformation) clickedItem.getItemProperty((Disease) event.getPropertyId()).getValue());
			}
		} else {
			return;
		}
	}

	

}
