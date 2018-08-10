package de.symeda.sormas.ui.configuration;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

/**
 * @author Christopher Riedel
 */
public class FacilityCreateForm extends AbstractEditForm<FacilityDto> {

	private static final long serialVersionUID = 1952619382018965255L;

	private static final String HTML_LAYOUT = LayoutUtil.fluidRowLocs(FacilityDto.NAME, FacilityDto.REGION)
			+ LayoutUtil.fluidRowLocs(FacilityDto.DISTRICT, FacilityDto.COMMUNITY)
			+ LayoutUtil.fluidRowLocs(FacilityDto.CITY)
			+ LayoutUtil.fluidRowLocs(FacilityDto.LATITUDE, FacilityDto.LONGITUDE);

	private Boolean createLaboratory;

	public FacilityCreateForm(UserRight editOrCreateUserRight, boolean createLaboratory) {
		super(FacilityDto.class, FacilityDto.I18N_PREFIX, editOrCreateUserRight);

		setWidth(540, Unit.PIXELS);

		this.createLaboratory = createLaboratory;
		addFields();
	}

	@Override
	protected void addFields() {
		if (createLaboratory == null) {
			return;
		}

		TextField name = addField(FacilityDto.NAME, TextField.class);
		ComboBox region = addField(FacilityDto.REGION, ComboBox.class);
		ComboBox district = addField(FacilityDto.DISTRICT, ComboBox.class);
		ComboBox community = addField(FacilityDto.COMMUNITY, ComboBox.class);
		@SuppressWarnings("unused")
		TextField city = addField(FacilityDto.CITY, TextField.class);
		@SuppressWarnings("unused")
		TextField latitude = addField(FacilityDto.LATITUDE, TextField.class);
		@SuppressWarnings("unused")
		TextField longitude = addField(FacilityDto.LONGITUDE, TextField.class);

		name.setRequired(true);
		region.setRequired(true);
		if (!createLaboratory) {
			district.setRequired(true);
			community.setRequired(true);
		}

		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(district,
					regionDto != null ? FacadeProvider.getDistrictFacade().getAllByRegion(regionDto.getUuid()) : null);
		});

		district.addValueChangeListener(e -> {
			FieldHelper.removeItems(community);
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(community,
					districtDto != null ? FacadeProvider.getCommunityFacade().getAllByDistrict(districtDto.getUuid())
							: null);
		});

		community.addValueChangeListener(e -> {
			@SuppressWarnings("unused")
			CommunityReferenceDto communityDto = (CommunityReferenceDto) e.getProperty().getValue();
		});
		region.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
