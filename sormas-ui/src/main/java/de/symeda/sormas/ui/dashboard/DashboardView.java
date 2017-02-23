package de.symeda.sormas.ui.dashboard;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.AbstractView;

@SuppressWarnings("serial")
public class DashboardView extends AbstractView {

	public static final String VIEW_NAME = "dashboard";
	
	private final ComboBox diseaseFilter;
	private final MapComponent mapComponent;

    
	public DashboardView() {
		setSizeFull();
		setSpacing(false);
		
		HorizontalLayout filterLayout = new HorizontalLayout();
		{
			filterLayout.setSizeUndefined();
			filterLayout.setSpacing(true);
			filterLayout.setMargin(true);

			diseaseFilter = new ComboBox();
	        diseaseFilter.setWidth(200, Unit.PIXELS);
	        diseaseFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE));
	        diseaseFilter.addItems((Object[])Disease.values());
	        diseaseFilter.addValueChangeListener(e -> refreshVisibleCases());
	        filterLayout.addComponent(diseaseFilter);
		}
        addComponent(filterLayout);
		
		mapComponent = new MapComponent();
        addComponent(mapComponent);
        setExpandRatio(mapComponent, 1);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		
		refreshVisibleCases();
	}
	
	private void refreshVisibleCases() {
    	List<CaseDataDto> cases = ControllerProvider.getCaseController().getCaseIndexList();

    	// TODO move into service layer
		Disease disease = (Disease) diseaseFilter.getValue(); 
		if (disease != null) {
			cases = cases.stream()
    		.filter(entry -> disease.equals(entry.getDisease()))
    		.collect(Collectors.toList());
		}
		
    	mapComponent.showCases(cases);
		
	}
}
