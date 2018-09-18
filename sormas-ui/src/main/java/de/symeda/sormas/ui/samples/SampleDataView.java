package de.symeda.sormas.ui.samples;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.caze.CaseInfoLayout;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class SampleDataView extends AbstractSampleView {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";

	public static final String EDIT_LOC = "edit";
	public static final String CASE_LOC = "case";
	public static final String SAMPLE_TESTS_LOC = "sampleTests";

	public SampleDataView() {
		super(VIEW_NAME);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		setHeightUndefined();

		String htmlLayout = LayoutUtil.fluidRow(LayoutUtil.fluidColumnLoc(8, 0, 12, 0, EDIT_LOC),
				LayoutUtil.fluidColumnLoc(4, 0, 6, 0, CASE_LOC),
				LayoutUtil.fluidColumnLoc(4, 0, 6, 0, SAMPLE_TESTS_LOC));

		VerticalLayout container = new VerticalLayout();
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		CustomLayout layout = new CustomLayout();
		layout.addStyleName(CssStyles.ROOT_COMPONENT);
		layout.setTemplateContents(htmlLayout);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeightUndefined();
		container.addComponent(layout);

		CommitDiscardWrapperComponent<?> editComponent = ControllerProvider.getSampleController()
				.getSampleEditComponent(getSampleRef().getUuid());
		editComponent.setMargin(false);
		editComponent.setWidth(100, Unit.PERCENTAGE);
		editComponent.getWrappedComponent().setWidth(100, Unit.PERCENTAGE);
		editComponent.addStyleName(CssStyles.MAIN_COMPONENT);
		layout.addComponent(editComponent, EDIT_LOC);

		SampleDto sampleDto = FacadeProvider.getSampleFacade().getSampleByUuid(getSampleRef().getUuid());
		CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(sampleDto.getAssociatedCase().getUuid());
		CaseInfoLayout caseInfoLayout = new CaseInfoLayout(caseDto);
		caseInfoLayout.addStyleName(CssStyles.SIDE_COMPONENT);
		layout.addComponent(caseInfoLayout, CASE_LOC);

		SampleTestListComponent sampleTestList = new SampleTestListComponent(getSampleRef());
		sampleTestList.addStyleName(CssStyles.SIDE_COMPONENT);
		layout.addComponent(sampleTestList, SAMPLE_TESTS_LOC);
	}
}
