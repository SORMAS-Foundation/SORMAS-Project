package de.symeda.sormas.ui.news;

import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;

public class NewsDataView extends AbstractNewsView {

	public static final String VIEW_NAME = NewsView.VIEW_NAME + "/data";
	private CommitDiscardWrapperComponent<?> editComponent;

	public NewsDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected void initView(String params) {
		setHeightUndefined();

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		container.setEnabled(true);
		editComponent = ControllerProvider.getNewsController().getNewsEditComponent(getReference().getUuid(), isEditAllowed());

		LayoutWithSidePanel layout = new LayoutWithSidePanel(editComponent);
		container.addComponent(layout);
	}

}
