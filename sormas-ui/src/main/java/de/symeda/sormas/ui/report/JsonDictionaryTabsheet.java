package de.symeda.sormas.ui.report;

import java.net.URI;
import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.user.UserGrid;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.V7GridExportStreamResource;

public class JsonDictionaryTabsheet extends VerticalLayout implements View {
	private static final long serialVersionUID = -3533557348144005469L;
	private JsonDictionaryGrid grid;
	private CampaignFormDataCriteria criteria;

	public JsonDictionaryTabsheet() {
		criteria = new CampaignFormDataCriteria();
		grid = new JsonDictionaryGrid(criteria);
		this.addComponent(grid);
		this.setHeightFull();
		this.setMargin(false);
		this.setSpacing(false);
		this.setSizeFull();
		this.setExpandRatio(grid, 1);
		this.setStyleName("crud-main-layout");
		grid.setVisible(false);

		Button exportButton = ButtonHelper.createIconButton(Captions.exportJsonDictionary, VaadinIcons.TABLE, null,
				ValoTheme.BUTTON_PRIMARY);
		exportButton.setDescription(I18nProperties.getDescription(Descriptions.descExportButton));
		this.addComponent(exportButton);
		
		StreamResource streamResource = GridExportStreamResource.createStreamResource("", "", grid,
				ExportEntityName.JSON_DICTIONARY, UserGrid.EDIT_BTN_ID);
		FileDownloader fileDownloader = new FileDownloader(streamResource);
		fileDownloader.extend(exportButton);

	}

	public static StreamResource createGridExportStreamResourcsse(List<String> lst, String fln) {

		return new V7GridExportStreamResource(lst, fln);
	}

//@Override
	public void extractUrl() {
		URI location = Page.getCurrent().getLocation();
		String uri = location.toString();

		String params = uri.trim();
		if (params.startsWith("?")) {
			params = params.substring(1);

		}

	}

	public boolean navigateTo(BaseCriteria criteria) {
		return navigateTo(criteria, true);
	}

	public boolean navigateTo(BaseCriteria criteria, boolean force) {

		Navigator navigator = SormasUI.get().getNavigator();

		String state = navigator.getState();
		String newState = buildNavigationState(state, criteria);

		boolean didNavigate = false;
		if (!newState.equals(state) || force) {
			navigator.navigateTo(newState);

			didNavigate = true;
		}

		return didNavigate;
	}

	public static String buildNavigationState(String currentState, BaseCriteria criteria) {

		String newState = currentState;
		int paramsIndex = newState.lastIndexOf('?');
		if (paramsIndex >= 0) {
			newState = newState.substring(0, paramsIndex);
		}

		if (criteria != null) {
			String params = criteria.toUrlParams();
			if (!DataHelper.isNullOrEmpty(params)) {
				if (newState.charAt(newState.length() - 1) != '/') {
					newState += "/";
				}

				newState += "?" + params;
			}
		}

		return newState;
	}

}
