package de.symeda.sormas.ui.contact;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.caze.CaseDataForm;
import de.symeda.sormas.ui.caze.CaseInfoLayout;
import de.symeda.sormas.ui.task.TaskListComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

/**
 * CaseDataView for reading and editing the case data fields. Contains the
 * {@link CaseDataForm}.
 * 
 * @author Stefan Szczesny
 *
 */
public class ContactDataView extends AbstractContactView {

	private static final long serialVersionUID = -1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";

	public static final String EDIT_LOC = "edit";
	public static final String CASE_LOC = "case";
	public static final String TASKS_LOC = "tasks";

	public ContactDataView() {
		super(VIEW_NAME);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		setHeightUndefined();

		String htmlLayout = LayoutUtil.fluidRow(LayoutUtil.fluidColumnLoc(8, 0, 12, 0, EDIT_LOC),
				LayoutUtil.fluidColumnLoc(4, 0, 6, 0, CASE_LOC), LayoutUtil.fluidColumnLoc(4, 0, 6, 0, TASKS_LOC));

		VerticalLayout container = new VerticalLayout();
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		CustomLayout layout = new CustomLayout();
		layout.addStyleName(CssStyles.ROOT_COMPONENT);
		layout.setTemplateContents(htmlLayout);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeightUndefined();
		container.addComponent(layout);

		CommitDiscardWrapperComponent<?> editComponent = ControllerProvider.getContactController()
				.getContactDataEditComponent(getContactRef().getUuid());
		editComponent.setMargin(false);
		editComponent.setWidth(100, Unit.PERCENTAGE);
		editComponent.getWrappedComponent().setWidth(100, Unit.PERCENTAGE);
		editComponent.addStyleName(CssStyles.MAIN_COMPONENT);
		layout.addComponent(editComponent, EDIT_LOC);

		ContactDto contactDto = FacadeProvider.getContactFacade().getContactByUuid(getContactRef().getUuid());
		CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(contactDto.getCaze().getUuid());
		CaseInfoLayout caseInfoLayout = new CaseInfoLayout(caseDto);
		caseInfoLayout.addStyleName(CssStyles.SIDE_COMPONENT);
		layout.addComponent(caseInfoLayout, CASE_LOC);

		TaskListComponent taskList = new TaskListComponent(TaskContext.CONTACT, getContactRef());
		taskList.addStyleName(CssStyles.SIDE_COMPONENT);
		layout.addComponent(taskList, TASKS_LOC);
	}
}
