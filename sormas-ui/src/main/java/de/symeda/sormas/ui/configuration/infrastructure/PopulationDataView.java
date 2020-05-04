package de.symeda.sormas.ui.configuration.infrastructure;

import java.util.Date;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class PopulationDataView extends AbstractConfigurationView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/populationdata";

	public PopulationDataView() {
		super(VIEW_NAME);

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(false);
		CssStyles.style(layout, CssStyles.VSPACE_TOP_1);

		Label lblIntroduction = new Label(I18nProperties.getString(Strings.infoPopulationDataView));
		CssStyles.style(lblIntroduction, CssStyles.VSPACE_2);
		layout.addComponent(lblIntroduction);
		layout.setComponentAlignment(lblIntroduction, Alignment.MIDDLE_CENTER);

		Button btnImport = ButtonHelper.createIconButton(Captions.actionImport, VaadinIcons.UPLOAD, e -> {
			Window window = VaadinUiUtil.showPopupWindow(new InfrastructureImportLayout(InfrastructureType.POPULATION_DATA));
			window.setCaption(I18nProperties.getString(Strings.headingImportPopulationData));
		}, CssStyles.VSPACE_4, ValoTheme.BUTTON_PRIMARY);
		layout.addComponent(btnImport);
		layout.setComponentAlignment(btnImport, Alignment.MIDDLE_CENTER);

		Button btnExport = ButtonHelper.createIconButton(Captions.export, VaadinIcons.DOWNLOAD, null, ValoTheme.BUTTON_PRIMARY);
		layout.addComponent(btnExport);
		layout.setComponentAlignment(btnExport, Alignment.MIDDLE_CENTER);

		StreamResource populationDataExportResource = DownloadUtil.createPopulationDataExportResource("sormas_population_data_"
				+ DateHelper.formatDateForExport(new Date()) + ".csv");
		new FileDownloader(populationDataExportResource).extend(btnExport);

		addComponent(layout);
	}

}
