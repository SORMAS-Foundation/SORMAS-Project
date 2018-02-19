package de.symeda.sormas.ui.caze;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;

import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.samples.SampleListComponent;
import de.symeda.sormas.ui.task.TaskListComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.ViewMode;

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
    	
    	if (getViewMode() == ViewMode.FULL) {
	    	CommitDiscardWrapperComponent<CaseDataForm> caseDataEditComponent = ControllerProvider.getCaseController().getCaseDataEditComponent(getCaseRef().getUuid(), getViewMode());
	    	setSubComponent(caseDataEditComponent);
    	} else {
    		CommitDiscardWrapperComponent<? extends Component> caseFullEditComponent = ControllerProvider.getCaseController().getCaseFullEditComponent(getCaseRef().getUuid(), getViewMode());
    		setSubComponent(caseFullEditComponent);
    	}
    	
    	TaskListComponent taskListComponent = new TaskListComponent(TaskContext.CASE, getCaseRef());
    	addComponent(taskListComponent);
    	taskListComponent.reload();
    	
    	SampleListComponent sampleListComponent = new SampleListComponent(getCaseRef());
    	addComponent(sampleListComponent);
    	sampleListComponent.reload();
    }
}
