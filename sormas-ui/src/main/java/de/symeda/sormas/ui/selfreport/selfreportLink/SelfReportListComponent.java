package de.symeda.sormas.ui.selfreport.selfreportLink;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.selfreport.SelfReportCriteria;
import de.symeda.sormas.api.selfreport.SelfReportIndexDto;
import de.symeda.sormas.api.selfreport.SelfReportType;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

public class SelfReportListComponent extends SideComponent {

	private SelfReportIndexDto selfReportDto;

	public SelfReportListComponent(SelfReportType selfReportType, SelfReportCriteria selfReportCriteria) {
		super(I18nProperties.getString(Strings.headingSelfReportSideComponent));
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		SelfReportList selfReportList = new SelfReportList(selfReportCriteria);
		addComponent(selfReportList);
		selfReportList.reload();

	}
}
