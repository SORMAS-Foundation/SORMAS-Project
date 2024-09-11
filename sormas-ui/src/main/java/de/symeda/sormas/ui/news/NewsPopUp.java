package de.symeda.sormas.ui.news;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocsCss;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.news.NewsDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.InfrastructureFieldsHelper;

public class NewsPopUp extends AbstractEditForm<NewsDto> {

	private final static String I18N_PREFIX = "NewsPopUp";
	private final static String IFRAME_CONTAINER = "iframe_container";
	private final static String OPEN_LINK_BUTTON = "openLinkButton";
	private final static String NEWS_BROWSER_FRAME = "news-popup-iframe";
	private final static String TOP_ROW_NEWS_SELECT = "top-row-news-select";
	//@formatter:off
	private final String HTML_LAYOUT =
			fluidRowLocsCss(TOP_ROW_NEWS_SELECT, NewsDto.RISK_LEVEL, NewsDto.DISEASE,
					NewsDto.STATUS, NewsDto.REGION, NewsDto.DISTRICT, NewsDto.COMMUNITY, OPEN_LINK_BUTTON)
					+ fluidRowLocs(IFRAME_CONTAINER);
	//@formatter:on
	private final String newsLink;

	public NewsPopUp(String newsLink) {
		super(NewsDto.class, null, false);
		this.newsLink = newsLink;
		addFields();
	}

	@Override
	protected void addFields() {
		BrowserFrame browserFrame = new BrowserFrame("", new ExternalResource(newsLink));
		browserFrame.setId(NEWS_BROWSER_FRAME);
		getContent().addComponent(browserFrame, IFRAME_CONTAINER);
		addFields(NewsDto.RISK_LEVEL, NewsDto.DISEASE, NewsDto.STATUS);
		ComboBox regionCombo = addInfrastructureField(CaseDataDto.REGION);
		ComboBox districtCombo = addInfrastructureField(CaseDataDto.DISTRICT);
		ComboBox communityCombo = addInfrastructureField(CaseDataDto.COMMUNITY);
		InfrastructureFieldsHelper.initInfrastructureFields(regionCombo, districtCombo, communityCombo);
		Button openLinkButton = new Button(I18nProperties.getCaption(Captions.openLinkInTab));
		openLinkButton.addClickListener(event -> getUI().getPage().open(newsLink, "_blank"));
		getContent().addComponent(openLinkButton, OPEN_LINK_BUTTON);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
