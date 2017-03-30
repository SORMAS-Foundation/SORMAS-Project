package de.symeda.sormas.ui.dashboard;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class DashboardView extends AbstractView {

	public static final String VIEW_NAME = "dashboard";
	
	private final ComboBox diseaseFilter;
	private final MapComponent mapComponent;

    
	public DashboardView() {
		setSizeFull();
		setSpacing(false);

		diseaseFilter = new ComboBox();
        addComponent(createTopBar());
        
		mapComponent = new MapComponent();
		VerticalLayout layout = createContents();
        addComponent(layout);
        setExpandRatio(layout, 1);
	}
	
	private HorizontalLayout createTopBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
    	filterLayout.setWidth(100, Unit.PERCENTAGE);
		filterLayout.setSpacing(true);
		filterLayout.setMargin(true);

        diseaseFilter.setWidth(200, Unit.PIXELS);
        diseaseFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE));
        diseaseFilter.addItems((Object[])Disease.values());
        diseaseFilter.addValueChangeListener(e -> refreshVisibleCases());
        filterLayout.addComponent(diseaseFilter);
        
        HorizontalLayout keyLayout = new HorizontalLayout();
        keyLayout.setSpacing(true);
        keyLayout.setMargin(false);

        Image iconGrey = new Image(null, new ThemeResource("mapicons/grey-dot-small.png"));
        Image iconYellow = new Image(null, new ThemeResource("mapicons/yellow-dot-small.png"));
        Image iconOrange = new Image(null, new ThemeResource("mapicons/orange-dot-small.png"));
        Image iconRed = new Image(null, new ThemeResource("mapicons/red-dot-small.png"));
        
        keyLayout.addComponent(iconGrey);
        keyLayout.addComponent(new Label("Possible"));
        keyLayout.addComponent(iconYellow);
        keyLayout.addComponent(new Label("Suspect"));
        keyLayout.addComponent(iconOrange);
        keyLayout.addComponent(new Label("Probable"));
        keyLayout.addComponent(iconRed);
        keyLayout.addComponent(new Label("Confirmed"));
        CssStyles.stylePrimary(keyLayout, CssStyles.DASHBOARD_KEY);
        
        filterLayout.addComponent(keyLayout);
        filterLayout.setComponentAlignment(keyLayout, Alignment.MIDDLE_RIGHT);
        filterLayout.setExpandRatio(keyLayout, 1);
        
		return filterLayout;
	}
	
	private VerticalLayout createContents() {
		VerticalLayout layout = new VerticalLayout();
        
        mapComponent.setHeight(700, Unit.PIXELS);
        mapComponent.setWidth(100, Unit.PERCENTAGE);
        mapComponent.setMargin(new MarginInfo(false, true));
        layout.addComponent(mapComponent);
        layout.setExpandRatio(mapComponent, 1);
        
        HorizontalLayout imageLayout = new HorizontalLayout();
        Image previewImg1 = new Image(null, new ThemeResource("img/dashboard-preview1.png"));
        Image previewImg2 = new Image(null, new ThemeResource("img/dashboard-preview2.jpg"));
        imageLayout.addComponent(previewImg1);
        imageLayout.addComponent(previewImg2);
        imageLayout.setWidth(100, Unit.PERCENTAGE);
        imageLayout.setSpacing(true);
        imageLayout.setMargin(true);
        layout.addComponent(imageLayout);
        layout.setExpandRatio(imageLayout, 1);
        
        return layout;
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
		
    	mapComponent.showFacilities(cases);
	}
}
