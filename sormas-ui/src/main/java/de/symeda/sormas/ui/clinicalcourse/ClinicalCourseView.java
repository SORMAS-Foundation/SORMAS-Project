package de.symeda.sormas.ui.clinicalcourse;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.caze.AbstractCaseView;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class ClinicalCourseView extends AbstractCaseView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/clinicalcourse";
	
	private ClinicalVisitCriteria clinicalVisitCriteria;
	private ClinicalVisitGrid clinicalVisitGrid;
	
	public ClinicalCourseView() {
		super(VIEW_NAME);
		
		clinicalVisitCriteria = ViewModelProviders.of(ClinicalCourseView.class).get(ClinicalVisitCriteria.class);
		
		VerticalLayout container = new VerticalLayout();
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		
		container.addComponent(createClinicalVisitsHeader());
		
		clinicalVisitGrid = new ClinicalVisitGrid(this);
		clinicalVisitGrid.setCriteria(clinicalVisitCriteria);
		clinicalVisitGrid.setHeightMode(HeightMode.ROW);
		CssStyles.style(clinicalVisitGrid, CssStyles.VSPACE_3);
		container.addComponent(clinicalVisitGrid);
		
		setSubComponent(container);
	}
	
	private VerticalLayout createClinicalVisitsHeader() {
		VerticalLayout clinicalVisitsHeader = new VerticalLayout();
		clinicalVisitsHeader.setWidth(100, Unit.PERCENTAGE);
		
		HorizontalLayout headlineRow = new HorizontalLayout();
		headlineRow.setSpacing(true);
		headlineRow.setWidth(100, Unit.PERCENTAGE);
		{
			Label clinicalVisitsLabel = new Label(I18nProperties.getString(Strings.entityClinicalVisits));
			CssStyles.style(clinicalVisitsLabel, CssStyles.H3);
			headlineRow.addComponent(clinicalVisitsLabel);
			headlineRow.setExpandRatio(clinicalVisitsLabel, 1);
			
			// Bulk operations
			if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
				MenuBar bulkOperationsDropdown = new MenuBar();	
				MenuItem bulkOperationsItem = bulkOperationsDropdown.addItem(I18nProperties.getCaption(Captions.bulkActions), null);

				Command deleteCommand = selectedItem -> {
					ControllerProvider.getClinicalCourseController().deleteAllSelectedClinicalVisits(clinicalVisitGrid.getSelectedRows(), new Runnable() {
						public void run() {
							clinicalVisitGrid.reload();
						}
					});
				};
				bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkDelete), FontAwesome.TRASH, deleteCommand);

				headlineRow.addComponent(bulkOperationsDropdown);
				headlineRow.setComponentAlignment(bulkOperationsDropdown, Alignment.MIDDLE_RIGHT);
			}
			
			Button newClinicalVisitButton = new Button(I18nProperties.getCaption(Captions.clinicalVisitNewClinicalVisit));
			CssStyles.style(newClinicalVisitButton, ValoTheme.BUTTON_PRIMARY);
			newClinicalVisitButton.addClickListener(e -> {
				ControllerProvider.getClinicalCourseController().openClinicalVisitCreateForm(clinicalVisitCriteria.getClinicalCourse(), getCaseRef().getUuid(), this::reloadClinicalVisitGrid);
			});
			headlineRow.addComponent(newClinicalVisitButton);
			
			headlineRow.setComponentAlignment(newClinicalVisitButton, Alignment.MIDDLE_RIGHT);
		}
		clinicalVisitsHeader.addComponent(headlineRow);
		
		return clinicalVisitsHeader;
	}
	
	private void update() {
		if (clinicalVisitCriteria.getClinicalCourse() == null) {
			CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(getCaseRef().getUuid());
			
			// TODO: Remove this once a proper ViewModel system has been introduced
			if (caze.getClinicalCourse() == null) {
				ClinicalCourseDto clinicalCourse = ClinicalCourseDto.build();
				caze.setClinicalCourse(clinicalCourse);
				caze = FacadeProvider.getCaseFacade().saveCase(caze);
			}
			
			clinicalVisitCriteria.clinicalCourse(caze.getClinicalCourse());
		}
	}
	
	public void reloadClinicalVisitGrid() {
		clinicalVisitGrid.reload();
		clinicalVisitGrid.setHeightByRows(Math.min(clinicalVisitGrid.getContainer().size(), 10));
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		clinicalVisitGrid.setCaseRef(getCaseRef());
		update();
		reloadClinicalVisitGrid();
	}
	
}
