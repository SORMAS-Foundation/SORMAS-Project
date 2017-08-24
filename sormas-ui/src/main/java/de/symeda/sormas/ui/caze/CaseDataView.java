package de.symeda.sormas.ui.caze;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.samples.SampleListComponent;
import de.symeda.sormas.ui.task.TaskListComponent;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * CaseDataView for reading and editing the case data fields.
 * Contains the {@link CaseDataForm}.
 * @author Stefan Szczesny
 *
 */
public class CaseDataView extends AbstractCaseView {

	private static final long serialVersionUID = -1L;
	
	public static final String VIEW_NAME = "cases/data";

    public CaseDataView() {
    	super(VIEW_NAME);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	super.enter(event);
    	setHeightUndefined();
    	setSubComponent(ControllerProvider.getCaseController().getCaseDataEditComponent(getCaseRef().getUuid()));
    	
    	TaskListComponent taskListComponent = new TaskListComponent(TaskContext.CASE, getCaseRef());
    	taskListComponent.addStyleName(CssStyles.SUBLIST_MARGIN);
    	addComponent(taskListComponent);
    	taskListComponent.enter(event);
    	
    	SampleListComponent sampleListComponent = new SampleListComponent(getCaseRef());
    	sampleListComponent.addStyleName(CssStyles.SUBLIST_MARGIN_SMALL);
    	addComponent(sampleListComponent);
    	sampleListComponent.enter(event);
    }
}
