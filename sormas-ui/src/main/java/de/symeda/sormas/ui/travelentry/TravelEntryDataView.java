package de.symeda.sormas.ui.travelentry;

import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.CaseInfoLayout;
import de.symeda.sormas.ui.docgeneration.QuarantineOrderDocumentsComponent;
import de.symeda.sormas.ui.document.DocumentListComponent;
import de.symeda.sormas.ui.email.ExternalEmailSideComponent;
import de.symeda.sormas.ui.task.TaskListComponent;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentLayout;

public class TravelEntryDataView extends AbstractTravelEntryView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";
	public static final String TRAVEL_ENTRY_LOC = "travelEntry";
	public static final String CASE_LOC = "case";
	public static final String DOCUMENTS_LOC = "documents";
	public static final String TASKS_LOC = "tasks";
	public static final String EXTERNAL_EMAILS_LOC = "externalEmails";

	private CommitDiscardWrapperComponent<TravelEntryDataForm> editComponent;

	public TravelEntryDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected String getRootViewName() {
		return TravelEntriesView.VIEW_NAME;
	}

	@Override
	protected void initView(String params) {
		setHeightUndefined();

		TravelEntryDto travelEntryDto = FacadeProvider.getTravelEntryFacade().getByUuid(getReference().getUuid());

		editComponent = ControllerProvider.getTravelEntryController().getTravelEntryDataEditComponent(getTravelEntryRef().getUuid());

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		container.setEnabled(true);

		LayoutWithSidePanel layout = new LayoutWithSidePanel(
			editComponent,
			CASE_LOC,
			DOCUMENTS_LOC,
			QuarantineOrderDocumentsComponent.QUARANTINE_LOC,
			TASKS_LOC,
			EXTERNAL_EMAILS_LOC);

		container.addComponent(layout);

		UserProvider currentUser = UserProvider.getCurrent();
		boolean caseButtonVisible = currentUser != null && UiUtil.permitted(FeatureType.CASE_SURVEILANCE, UserRight.CASE_CREATE);
		CaseReferenceDto resultingCase = travelEntryDto.getResultingCase();
		if (resultingCase == null && caseButtonVisible) {
			Button createCaseButton = ButtonHelper.createButton(Captions.travelEntryCreateCase, e -> showUnsavedChangesPopup(() -> {
				// Re-retrieve the travel entry from the database in case there were unsaved changes
				TravelEntryDto updatedTravelEntry = FacadeProvider.getTravelEntryFacade().getByUuid(travelEntryDto.getUuid());
				ControllerProvider.getCaseController().createFromTravelEntry(updatedTravelEntry);
			}), ValoTheme.BUTTON_PRIMARY, CssStyles.VSPACE_2);
			layout.addSidePanelComponent(createCaseButton, CASE_LOC);
		} else if (resultingCase != null) {
			layout.addSidePanelComponent(createCaseInfoLayout(resultingCase.getUuid()), CASE_LOC);
		}

		final String uuid = travelEntryDto.getUuid();
		final EditPermissionType travelEntryEditAllowed = FacadeProvider.getTravelEntryFacade().getEditPermissionType(uuid);
		boolean editAllowed = isEditAllowed();
		DocumentListComponent documentList = null;

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.DOCUMENTS)
			&& UserProvider.getCurrent().hasUserRight(UserRight.DOCUMENT_VIEW)) {
			documentList = new DocumentListComponent(
				DocumentRelatedEntityType.TRAVEL_ENTRY,
				getReference(),
				UserRight.TRAVEL_ENTRY_EDIT,
				travelEntryDto.isPseudonymized(),
				editAllowed,
				EditPermissionType.WITHOUT_OWNERSHIP.equals(travelEntryEditAllowed));
			layout.addSidePanelComponent(new SideComponentLayout(documentList), DOCUMENTS_LOC);
		}

		QuarantineOrderDocumentsComponent.addComponentToLayout(layout, travelEntryDto, documentList);

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.TASK_MANAGEMENT)
			&& UserProvider.getCurrent().hasUserRight(UserRight.TASK_VIEW)) {
			TaskListComponent taskList = new TaskListComponent(
				TaskContext.TRAVEL_ENTRY,
				getTravelEntryRef(),
				travelEntryDto.getDisease(),
				this::showUnsavedChangesPopup,
				editAllowed);
			taskList.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addSidePanelComponent(taskList, TASKS_LOC);
		}

		if (UiUtil.permitted(FeatureType.EXTERNAL_EMAILS, UserRight.EXTERNAL_EMAIL_SEND)) {
			ExternalEmailSideComponent externalEmailSideComponent = new ExternalEmailSideComponent(
				DocumentWorkflow.TRAVEL_ENTRY_EMAIL,
				RootEntityType.ROOT_TRAVEL_ENTRY,
                    DocumentRelatedEntityType.TRAVEL_ENTRY,
				travelEntryDto.toReference(),
				travelEntryDto.getPerson(),
				Strings.messageTravelEntryPersonHasNoEmail,
                    editAllowed,
				this::showUnsavedChangesPopup);
			layout.addSidePanelComponent(new SideComponentLayout(externalEmailSideComponent), EXTERNAL_EMAILS_LOC);
		}

		final boolean deleted = FacadeProvider.getTravelEntryFacade().isDeleted(uuid);
		layout.disableIfNecessary(deleted, travelEntryEditAllowed);
	}

	private CaseInfoLayout createCaseInfoLayout(String caseUuid) {

		CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
		CaseInfoLayout caseInfoLayout = new CaseInfoLayout(caseDto, true);
		caseInfoLayout.addStyleName(CssStyles.SIDE_COMPONENT);

		return caseInfoLayout;
	}
}
