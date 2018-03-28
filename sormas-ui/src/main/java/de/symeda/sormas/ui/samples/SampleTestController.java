package de.symeda.sormas.ui.samples;

import java.util.Collection;
import java.util.List;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.sample.SampleTestFacade;
import de.symeda.sormas.api.sample.SampleTestReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DeleteListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class SampleTestController {
	
	private SampleTestFacade stf = FacadeProvider.getSampleTestFacade();
	
	public SampleTestController() { }

	public List<SampleTestDto> getSampleTestsBySample(SampleReferenceDto sampleRef) {
		return stf.getAllBySample(sampleRef);
	}
	
	public void create(SampleReferenceDto sampleRef, SampleTestGrid grid) {
		SampleTestEditForm createForm = new SampleTestEditForm(true, UserRight.SAMPLETEST_CREATE);
		createForm.setValue(createNewSampleTest(sampleRef));
		final CommitDiscardWrapperComponent<SampleTestEditForm> editView = new CommitDiscardWrapperComponent<SampleTestEditForm>(createForm, createForm.getFieldGroup());
	
		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!createForm.getFieldGroup().isModified()) {
					SampleTestDto dto = createForm.getValue();
					stf.saveSampleTest(dto);
					grid.reload();
				}
			}
		});
		
		VaadinUiUtil.showModalPopupWindow(editView, "Create new sample test result"); 
	}
	
	public void edit(SampleTestDto dto, SampleTestGrid grid) {
		// get fresh data
		SampleTestDto newDto = stf.getByUuid(dto.getUuid());
		
		SampleTestEditForm form = new SampleTestEditForm(false, UserRight.SAMPLETEST_EDIT);
		form.setValue(newDto);
		final CommitDiscardWrapperComponent<SampleTestEditForm> editView = new CommitDiscardWrapperComponent<SampleTestEditForm>(form, form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, "Edit sample test result");
		
		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!form.getFieldGroup().isModified()) {
					SampleTestDto dto = form.getValue();
					stf.saveSampleTest(dto);
					grid.reload();
				}
			}
		});

        if (LoginHelper.getCurrentUserRoles().contains(UserRole.ADMIN)) {
			editView.addDeleteListener(new DeleteListener() {
				@Override
				public void onDelete() {
					FacadeProvider.getSampleTestFacade().deleteSampleTest(dto.toReference(), LoginHelper.getCurrentUserAsReference().getUuid());
					UI.getCurrent().removeWindow(popupWindow);
					grid.reload();
				}
			}, I18nProperties.getFieldCaption("SampleTest"));
		}
	}
	
	private SampleTestDto createNewSampleTest(SampleReferenceDto sampleRef) {
		SampleTestDto sampleTest = new SampleTestDto();
		sampleTest.setUuid(DataHelper.createUuid());
		sampleTest.setSample(sampleRef);
		sampleTest.setLab(LoginHelper.getCurrentUser().getLaboratory());
		sampleTest.setLabUser(LoginHelper.getCurrentUserAsReference());
		return sampleTest;
	}
	
	public void deleteAllSelectedItems(Collection<Object> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification("No sample tests selected", "You have not selected any sample tests.", Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow("Are you sure you want to delete all " + selectedRows.size() + " selected sample tests?", new Runnable() {
				public void run() {
					for (Object selectedRow : selectedRows) {
						FacadeProvider.getSampleTestFacade().deleteSampleTest(new SampleTestReferenceDto(((SampleTestDto) selectedRow).getUuid()), LoginHelper.getCurrentUser().getUuid());
					}
					callback.run();
					new Notification("Sample tests deleted", "All selected sample tests have been deleted.", Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				}
			});
		}
	}
	
}
