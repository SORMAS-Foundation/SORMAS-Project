package de.symeda.sormas.ui.selfreport.selfreportLink;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.selfreport.SelfReportCriteria;
import de.symeda.sormas.api.selfreport.SelfReportListEntryDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.PaginationList;

import java.util.List;

public class SelfReportList extends PaginationList<SelfReportListEntryDto> {

	private static final int MAX_DISPLAYED_ENTRIES = 5;

	private final SelfReportCriteria selfReportCriteria;

	private final Label noSelfReportsLabel;

	public SelfReportList(SelfReportCriteria selfReportCriteria) {
		super(MAX_DISPLAYED_ENTRIES);
		this.selfReportCriteria = selfReportCriteria;
		this.noSelfReportsLabel = new Label("There are no self reports for this case");
	}

	@Override
	public void reload() {
		List<SelfReportListEntryDto> selfReportEntries =
			FacadeProvider.getSelfReportFacade().getEntriesList(selfReportCriteria, 0, maxDisplayedEntries * 20);

		setEntries(selfReportEntries);
		if (!selfReportEntries.isEmpty()) {
			showPage(1);
		} else {
			listLayout.removeAllComponents();
			updatePaginationLayout();
			listLayout.addComponent(noSelfReportsLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {
		for (SelfReportListEntryDto selfReportEntry : getDisplayedEntries()) {
			SelfReportListEntry listEntry = new SelfReportListEntry(selfReportEntry);

			String selfReportUuid = selfReportEntry.getUuid();
			if (UiUtil.permitted(UserRight.SELF_REPORT_EDIT)) {
				listEntry.addEditButton(
					"edit-selfReport" + selfReportUuid,
					(Button.ClickListener) event -> ControllerProvider.getSelfReportController().navigateToSelfReport(selfReportUuid));
			}else {
				listEntry.addEditButton(
						"view-selfReport" + selfReportUuid,
						(Button.ClickListener) event -> ControllerProvider.getSelfReportController().navigateToSelfReport(selfReportUuid));
			}

			listEntry.setEnabled(true);
			listLayout.addComponent(listEntry);
		}
	}
}
