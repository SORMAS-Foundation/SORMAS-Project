package de.symeda.sormas.ui.samples;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.ShipmentStatus;
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
		
		SampleDto sampleDto = FacadeProvider.getSampleFacade().getSampleByUuid(getSampleRef().getUuid());
		setSubComponent(ControllerProvider.getSampleController().getSampleEditComponent(sampleDto.getUuid()));
		
		if (sampleDto.getShipmentStatus() != ShipmentStatus.NOT_SHIPPED && sampleDto.getShipmentStatus() != ShipmentStatus.SHIPPED) {
			SampleTestsComponent sampleTestsComponent = new SampleTestsComponent(getSampleRef());
			addComponent(sampleTestsComponent);
		}
	}

}
