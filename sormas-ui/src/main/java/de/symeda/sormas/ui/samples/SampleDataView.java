package de.symeda.sormas.ui.samples;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import de.symeda.sormas.ui.ControllerProvider;

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
		setSubComponent(ControllerProvider.getSampleController().getSampleEditComponent(getSampleRef().getUuid()));
		
		SampleTestsComponent sampleTestsComponent = new SampleTestsComponent(getSampleRef());
		addComponent(sampleTestsComponent);
	}

}
