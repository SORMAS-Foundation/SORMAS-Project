package de.symeda.sormas.ui.travelentry;

import com.vaadin.ui.CustomLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.ViewMode;

public class TravelEntryDataView extends AbstractTravelEntryView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";
	public static final String TRAVEL_ENTRY_LOC = "travelEntry";

	private CommitDiscardWrapperComponent<TravelEntryDataForm> editComponent;

	public TravelEntryDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected String getRootViewName() {
		return TravelEntriesView.VIEW_NAME;
	}

	@Override
	protected void initView(String params) {
		setHeightUndefined();

		String htmlLayout = LayoutUtil.fluidRow(LayoutUtil.fluidColumnLoc(8, 0, 12, 0, TRAVEL_ENTRY_LOC));

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		CustomLayout layout = new CustomLayout();
		layout.addStyleName(CssStyles.ROOT_COMPONENT);
		layout.setTemplateContents(htmlLayout);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeightUndefined();
		container.addComponent(layout);

		TravelEntryDto travelEntryDto = FacadeProvider.getTravelEntryFacade().getByUuid(getReference().getUuid());

		editComponent = ControllerProvider.getTravelEntryController()
			.getTravelEntryDataEditComponent(getTravelEntryRef().getUuid(), ViewMode.NORMAL, travelEntryDto.isPseudonymized());
		editComponent.setMargin(false);
		editComponent.setWidth(100, Unit.PERCENTAGE);
		editComponent.getWrappedComponent().setWidth(100, Unit.PERCENTAGE);
		editComponent.addStyleName(CssStyles.MAIN_COMPONENT);
		layout.addComponent(editComponent, TRAVEL_ENTRY_LOC);

		setTravelEntryEditPermission(container);
	}
}
