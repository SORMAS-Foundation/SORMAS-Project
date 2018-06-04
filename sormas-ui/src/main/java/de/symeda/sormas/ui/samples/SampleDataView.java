package de.symeda.sormas.ui.samples;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.HorizontalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.caze.CaseInfoLayout;

public class SampleDataView extends AbstractSampleView {
	
	private static final long serialVersionUID = 1L;
	
	public static final String VIEW_NAME = "samples/data";
	
	public SampleDataView() {
		super(VIEW_NAME);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		setHeightUndefined();
		
		SampleDto sampleDto = FacadeProvider.getSampleFacade().getSampleByUuid(getSampleRef().getUuid());
		CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(sampleDto.getAssociatedCase().getUuid());
		int caseSampleCount = FacadeProvider.getSampleFacade().getReceivedSampleCountByCase(new CaseReferenceDto(caseDto.getUuid()));
		
		HorizontalLayout layout = new HorizontalLayout();
    	layout.setSpacing(true);
    	layout.addComponent(ControllerProvider.getSampleController().getSampleEditComponent(sampleDto.getUuid()));
    	CaseInfoLayout caseInfoLayout = new CaseInfoLayout(caseDto);
    	caseInfoLayout.setMargin(new MarginInfo(true, false, false, true));
    	layout.addComponent(caseInfoLayout);
    	addComponent(layout);
    	
		SampleTestsComponent sampleTestsComponent = new SampleTestsComponent(getSampleRef(), sampleDto.isReceived(), caseSampleCount);
		sampleTestsComponent.setMargin(true);
		addComponent(sampleTestsComponent);
	}

}
