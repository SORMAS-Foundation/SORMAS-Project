package de.symeda.sormas.ui.samples;

import java.util.Date;
import java.util.List;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleFacade;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class SampleController {
	
	private SampleFacade sf = FacadeProvider.getSampleFacade();
	
	public SampleController() { }
	
	public List<SampleIndexDto> getAllSamples() {
		UserDto user = LoginHelper.getCurrentUser();
		return FacadeProvider.getSampleFacade().getIndexList(user.getUuid());
	}
	
	public List<SampleIndexDto> getSamplesByCase(CaseReferenceDto caseRef) {
		return FacadeProvider.getSampleFacade().getIndexListByCase(caseRef);
	}
	
	public void registerViews(Navigator navigator) {
		navigator.addView(SamplesView.VIEW_NAME, SamplesView.class);
		navigator.addView(SampleDataView.VIEW_NAME, SampleDataView.class);
	}
	
	public void navigateToData(String sampleUuid) {
		String navigationState = SampleDataView.VIEW_NAME + "/" + sampleUuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}
	
	public void create(CaseReferenceDto caseRef, SampleGrid grid) {
		SampleCreateForm createForm = new SampleCreateForm();
		createForm.setValue(createNewSample(caseRef));
		final CommitDiscardWrapperComponent<SampleCreateForm> editView = new CommitDiscardWrapperComponent<SampleCreateForm>(createForm, createForm.getFieldGroup());
		
		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if(createForm.getFieldGroup().isValid()) {
					SampleDto dto = createForm.getValue();
					FacadeProvider.getSampleFacade().saveSample(dto);
					grid.reload();
				}
			}
		});
		
		VaadinUiUtil.showModalPopupWindow(editView, "Create new sample");
	}
	
	public CommitDiscardWrapperComponent<SampleEditForm> getSampleEditComponent(final String sampleUuid) {
		SampleEditForm form = new SampleEditForm();
		form.setWidth(1024, Unit.PIXELS);
		SampleDto dto = sf.getSampleByUuid(sampleUuid);
		form.setValue(dto);
		final CommitDiscardWrapperComponent<SampleEditForm> editView = new CommitDiscardWrapperComponent<SampleEditForm>(form, form.getFieldGroup());
		
		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if(form.getFieldGroup().isValid()) {
					SampleDto dto = form.getValue();
					dto = sf.saveSample(dto);
					Notification.show("Sample data saved", Type.WARNING_MESSAGE);
					navigateToData(dto.getUuid());
				}
			}
		});
		
		return editView;
	}
	
	private SampleDto createNewSample(CaseReferenceDto caseRef) {
		SampleDto sample = new SampleDto();
		sample.setUuid(DataHelper.createUuid());
		sample.setAssociatedCase(caseRef);
		sample.setReportingUser(LoginHelper.getCurrentUserAsReference());
		sample.setReportDateTime(new Date());
		
		return sample;
		
	}

}
