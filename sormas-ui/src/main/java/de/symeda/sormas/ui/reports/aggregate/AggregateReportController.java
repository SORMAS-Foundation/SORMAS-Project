package de.symeda.sormas.ui.reports.aggregate;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * @author Christopher Riedel
 */
public class AggregateReportController {


	public AggregateReportController() {
	}

	public void openEditOrCreateWindow(Runnable onClose, boolean edit) {
		Window window = VaadinUiUtil.createPopupWindow();
		AggregateReportsView currentView = (AggregateReportsView) SormasUI.get().getNavigator().getCurrentView();
		AggregateReportsEditLayout createLayout = new AggregateReportsEditLayout(window, currentView.getCriteria(),
				edit);
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
}
