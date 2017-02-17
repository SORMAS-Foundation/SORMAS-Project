package de.symeda.sormas.ui.samples;

import java.util.List;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.sample.SampleTestFacade;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class SampleTestController {
	
	private SampleTestFacade stf = FacadeProvider.getSampleTestFacade();
	
	public SampleTestController() { }

	public List<SampleTestDto> getSampleTestsBySample(SampleReferenceDto sampleRef) {
		return stf.getAllBySample(sampleRef);
	}
	
	public void create(SampleReferenceDto sampleRef, SampleTestGrid grid) {
		SampleTestEditForm createForm = new SampleTestEditForm();
		createForm.setValue(createNewSampleTest(sampleRef));
		final CommitDiscardWrapperComponent<SampleTestEditForm> editView = new CommitDiscardWrapperComponent<SampleTestEditForm>(createForm, createForm.getFieldGroup());
	
		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if(createForm.getFieldGroup().isValid()) {
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
		dto = stf.getByUuid(dto.getUuid());
		
		SampleTestEditForm form = new SampleTestEditForm();
		form.setValue(dto);
		final CommitDiscardWrapperComponent<SampleTestEditForm> editView = new CommitDiscardWrapperComponent<SampleTestEditForm>(form, form.getFieldGroup());
		
		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if(form.getFieldGroup().isValid()) {
					SampleTestDto dto = form.getValue();
					stf.saveSampleTest(dto);
					grid.reload();
				}
			}
		});
		
		VaadinUiUtil.showModalPopupWindow(editView, "Edit sample test result");
	}
	
	private SampleTestDto createNewSampleTest(SampleReferenceDto sampleRef) {
		SampleTestDto sampleTest = new SampleTestDto();
		sampleTest.setUuid(DataHelper.createUuid());
		sampleTest.setSample(sampleRef);
		sampleTest.setLab(LoginHelper.getCurrentUser().getLaboratory());
		sampleTest.setLabUser(LoginHelper.getCurrentUserAsReference());
		return sampleTest;
	}
}
