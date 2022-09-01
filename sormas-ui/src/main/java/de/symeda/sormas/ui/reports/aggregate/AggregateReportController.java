package de.symeda.sormas.ui.reports.aggregate;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.report.AggregateReportDto;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * @author Christopher Riedel
 */
public class AggregateReportController {

	public AggregateReportController() {

	}

	public void openEditOrCreateWindow(Runnable onClose, boolean edit, AggregateReportDto aggregateReportDto) {
		Window window = VaadinUiUtil.createPopupWindow();
		AggregateReportsEditLayout createLayout = new AggregateReportsEditLayout(window, edit, aggregateReportDto);
		window.setHeight(90, Unit.PERCENTAGE);
		window.setWidth(createLayout.getWidth() + 64 + 20, Unit.PIXELS);
		if (edit) {
			window.setCaption(I18nProperties.getString(Strings.headingEditAggregateReport));
		} else {
			window.setCaption(I18nProperties.getString(Strings.headingCreateNewAggregateReport));
		}
		window.setContent(createLayout);
		window.addCloseListener(e -> onClose.run());
		UI.getCurrent().addWindow(window);
	}

	public void deleteAggregateReport(String uuid, Runnable onClose) {

		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setMargin(false);
		Label contentLabel = new Label(I18nProperties.getString(Strings.messageAggregateReportDelete));
		verticalLayout.addComponent(contentLabel);

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.headingDeleteConfirmation),
			verticalLayout,
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			640,
			confirmed -> {
				if (Boolean.TRUE.equals(confirmed)) {
					FacadeProvider.getAggregateReportFacade().deleteReport(uuid);
					onClose.run();
				}
				return true;
			});
	}
}
