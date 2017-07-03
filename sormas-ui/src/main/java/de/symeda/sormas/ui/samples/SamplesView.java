package de.symeda.sormas.ui.samples;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import de.symeda.sormas.ui.utils.AbstractView;

@SuppressWarnings("serial")
public class SamplesView extends AbstractView {

	public static final String VIEW_NAME = "samples";	
	
	private final SampleListComponent sampleListComponent;
	
	public SamplesView() {
		sampleListComponent = new SampleListComponent();
		setSizeFull();
		addComponent(sampleListComponent);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		sampleListComponent.enter(event);
	}

}
