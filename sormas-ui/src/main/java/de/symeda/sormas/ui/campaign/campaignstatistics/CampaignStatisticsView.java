package de.symeda.sormas.ui.campaign.campaignstatistics;

import static de.symeda.sormas.ui.utils.FilteredGrid.EDIT_BTN_ID;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.campaign.AbstractCampaignView;
import de.symeda.sormas.ui.campaign.campaigndata.CampaignDataGrid;
import de.symeda.sormas.ui.campaign.components.CampaignSelector;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.GridExportStreamResource;

public class CampaignStatisticsView extends AbstractCampaignView {

	private final CampaignSelector campaignLayout;
	private final CampaignFormDataCriteria criteria;
	private final CampaignDataGrid grid;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/campaignstatistics";

	public CampaignStatisticsView() {
		super(VIEW_NAME);

		campaignLayout = new CampaignSelector();
		addHeaderComponent(campaignLayout);

		criteria = ViewModelProviders.of(getClass()).get(CampaignFormDataCriteria.class);
		grid = new CampaignDataGrid(criteria);

		if (UserProvider.getCurrent().hasUserRight(UserRight.CAMPAIGN_FORM_DATA_EXPORT)) {
			VerticalLayout exportLayout = new VerticalLayout();
			{
				exportLayout.setSpacing(true);
				exportLayout.setMargin(true);
				exportLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
				exportLayout.setWidth(250, Unit.PIXELS);
			}

			PopupButton exportPopupButton = ButtonHelper.createIconPopupButton(Captions.export, VaadinIcons.DOWNLOAD, exportLayout);
			addHeaderComponent(exportPopupButton);

			{
				StreamResource streamResource = GridExportStreamResource.createStreamResource(grid, ExportEntityName.CAMPAIGN_DATA, EDIT_BTN_ID);
				addExportButton(streamResource, exportPopupButton, exportLayout, VaadinIcons.TABLE, Captions.export, Strings.infoBasicExport);
			}
		}
	}
}
