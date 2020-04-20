package de.symeda.sormas.ui.clinicalcourse;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.shared.ui.grid.HeightMode;

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
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.ViewMode;

@SuppressWarnings("serial")
public class ClinicalCourseView extends AbstractCaseView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/clinicalcourse";

	private ClinicalVisitCriteria clinicalVisitCriteria;
	private ClinicalVisitGrid clinicalVisitGrid;

	public ClinicalCourseView() {
		super(VIEW_NAME);

		clinicalVisitCriteria = ViewModelProviders.of(ClinicalCourseView.class).get(ClinicalVisitCriteria.class);
	}

	private VerticalLayout createClinicalVisitsHeader() {
		VerticalLayout clinicalVisitsHeader = new VerticalLayout();
		clinicalVisitsHeader.setMargin(false);
		clinicalVisitsHeader.setSpacing(false);
		clinicalVisitsHeader.setWidth(100, Unit.PERCENTAGE);

		HorizontalLayout headlineRow = new HorizontalLayout();
		headlineRow.setMargin(false);
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
				bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkDelete), VaadinIcons.TRASH, deleteCommand);

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
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(getCaseRef().getUuid());

		clinicalVisitCriteria.clinicalCourse(caze.getClinicalCourse().toReference());
	}

	public void reloadClinicalVisitGrid() {
		clinicalVisitGrid.reload();
		clinicalVisitGrid.setHeightByRows(Math.max(1, Math.min(clinicalVisitGrid.getContainer().size(), 10)));
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);

		if (getViewMode() == ViewMode.SIMPLE) {
			ControllerProvider.getCaseController().navigateToCase(getCaseRef().getUuid());
			return;
		}
		
		// TODO: Remove this once a proper ViewModel system has been introduced
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(getCaseRef().getUuid());
		if (caze.getClinicalCourse() == null) {
			ClinicalCourseDto clinicalCourse = ClinicalCourseDto.build();
			caze.setClinicalCourse(clinicalCourse);
			caze = FacadeProvider.getCaseFacade().saveCase(caze);
		}

		VerticalLayout container = new VerticalLayout();
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);

		container.addComponent(createClinicalVisitsHeader());

		clinicalVisitGrid = new ClinicalVisitGrid(getCaseRef());
		clinicalVisitGrid.setCriteria(clinicalVisitCriteria);
		clinicalVisitGrid.setHeightMode(HeightMode.ROW);
		CssStyles.style(clinicalVisitGrid, CssStyles.VSPACE_3);
		container.addComponent(clinicalVisitGrid);
		
		CommitDiscardWrapperComponent<ClinicalCourseForm> clinicalCourseComponent = ControllerProvider.getCaseController().getClinicalCourseComponent(getCaseRef().getUuid(), getViewMode());
		clinicalCourseComponent.setMargin(false);
		container.addComponent(clinicalCourseComponent);

		setSubComponent(container);

		update();
		reloadClinicalVisitGrid();
	}

}
