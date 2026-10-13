package de.symeda.sormas.ui.news;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.news.NewsDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;

public class NewsDataForm extends AbstractEditForm<NewsDto> {

	private static final String NEWS_DATA_HEADING_LOC = "NewsDataHeading";
	//@formatter:off
	private static final String HTML_LAYOUT = loc(NEWS_DATA_HEADING_LOC)
		+ fluidRowLocs(NewsDto.NEWS_DATE)
		+ fluidRowLocs(NewsDto.TITLE)
		+ fluidRowLocs(NewsDto.LINK)
		+ fluidRowLocs(NewsDto.RISK_LEVEL, NewsDto.DISEASE, NewsDto.STATUS)
		+ fluidRowLocs(NewsDto.REGION, NewsDto.DISTRICT, NewsDto.COMMUNITY)
		+ fluidRowLocs(NewsDto.DESCRIPTION);
	//@formatter:on
	private ComboBox region;
	private ComboBox district;
	private ComboBox community;
	private final Boolean isCreateForm;

	public NewsDataForm(boolean create) {
		super(NewsDto.class, NewsDto.I18N_PREFIX, false);
		this.isCreateForm = create;
		addFields();
	}

	@Override
	protected void addFields() {
		Label newsDataHeading = new Label(I18nProperties.getString(Strings.newsData));
		newsDataHeading.addStyleName(H3);
		getContent().addComponent(newsDataHeading, NEWS_DATA_HEADING_LOC);

		addFields(NewsDto.TITLE, NewsDto.LINK, NewsDto.RISK_LEVEL, NewsDto.DISEASE, NewsDto.STATUS);
		region = addInfrastructureField(NewsDto.REGION);
		district = addInfrastructureField(NewsDto.DISTRICT);
		community = addInfrastructureField(NewsDto.COMMUNITY);
		addField(NewsDto.DESCRIPTION, TextArea.class);

		setRequired(true, NewsDto.TITLE, NewsDto.LINK);
		region.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper
				.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});
		district.addValueChangeListener(e -> {
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(
				community,
				districtDto != null ? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid()) : null);
		});
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
