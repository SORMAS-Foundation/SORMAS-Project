package de.symeda.sormas.ui.samples;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class SampleTestsComponent extends AbstractView {

	private SampleTestGrid grid;
	
	private VerticalLayout gridLayout;
	
	public SampleTestsComponent(SampleReferenceDto sampleRef) {
		setSizeFull();
		addStyleName("crud-view");
		
		grid = new SampleTestGrid(sampleRef);
		grid.setHeightMode(HeightMode.ROW);
		
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createTopBar(sampleRef));
		gridLayout.addComponent(grid);
		gridLayout.setMargin(new MarginInfo(true, false, false, false));
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");
		
		addComponent(gridLayout);
	}
	
	public HorizontalLayout createTopBar(SampleReferenceDto sampleRef) {
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		topLayout.setWidth(100, Unit.PERCENTAGE);
		topLayout.addStyleName(CssStyles.VSPACE3);
		
		Label header = new Label("Laboratory results");
		header.setSizeUndefined();
		CssStyles.style(header, CssStyles.H3, CssStyles.NO_MARGIN, CssStyles.SUBLIST_PADDING);
		topLayout.addComponent(header);
		
		Button createButton = new Button("New result");
		createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		createButton.setIcon(FontAwesome.PLUS_CIRCLE);
		createButton.addClickListener(e -> ControllerProvider.getSampleTestController().create(sampleRef, grid));
		topLayout.addComponent(createButton);
		topLayout.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
		topLayout.setExpandRatio(createButton, 1);
		
		return topLayout;
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		grid.reload();
	}
	
}
