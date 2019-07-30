package de.symeda.sormas.ui.caze;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;

@SuppressWarnings("serial")
public class MergeCasesView extends AbstractView {

	public static final String VIEW_NAME = CasesView.VIEW_NAME + "/merge";
	
	private CaseCriteria criteria;
	
	private MergeCasesGrid grid;
	
	public MergeCasesView() {
		super(VIEW_NAME);
		
		criteria = ViewModelProviders.of(MergeCasesView.class).get(CaseCriteria.class);

		grid = new MergeCasesGrid();
		grid.setCriteria(criteria);
		VerticalLayout gridLayout = new VerticalLayout();
		gridLayout.setMargin(true);
		gridLayout.setSizeFull();
		gridLayout.addComponent(grid);
		addComponent(gridLayout);
		
		Button btnBack = new Button(I18nProperties.getCaption(Captions.caseBackToDirectory));
		btnBack.setId("backToDirectory");
		btnBack.setIcon(VaadinIcons.ARROW_BACKWARD);
		btnBack.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnBack.addClickListener(e -> ControllerProvider.getCaseController().navigateToIndex());
		addHeaderComponent(btnBack);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		grid.reload();
	}
	
}
