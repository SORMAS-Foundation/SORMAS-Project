/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples;

import java.util.Collection;
import java.util.List;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.sample.SampleTestFacade;
import de.symeda.sormas.api.sample.SampleTestReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.CurrentUser;
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
	
	public void create(SampleReferenceDto sampleRef, int caseSampleCount, Runnable callback) {
		SampleTestEditForm createForm = new SampleTestEditForm(FacadeProvider.getSampleFacade().getSampleByUuid(sampleRef.getUuid()), true, UserRight.SAMPLETEST_CREATE, caseSampleCount);
		createForm.setValue(createNewSampleTest(sampleRef));
		final CommitDiscardWrapperComponent<SampleTestEditForm> editView = new CommitDiscardWrapperComponent<SampleTestEditForm>(createForm, createForm.getFieldGroup());
	
		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!createForm.getFieldGroup().isModified()) {
					saveSampleTest(createForm.getValue());
					callback.run();
				}
			}
		});
		
		VaadinUiUtil.showModalPopupWindow(editView, "Create new sample test result"); 
	}
	
	public void edit(SampleTestDto dto, int caseSampleCount, Runnable callback) {
		// get fresh data
		SampleTestDto newDto = stf.getByUuid(dto.getUuid());
		
		SampleTestEditForm form = new SampleTestEditForm(FacadeProvider.getSampleFacade().getSampleByUuid(dto.getSample().getUuid()), false, UserRight.SAMPLETEST_EDIT, caseSampleCount);
		form.setValue(newDto);
		final CommitDiscardWrapperComponent<SampleTestEditForm> editView = new CommitDiscardWrapperComponent<SampleTestEditForm>(form, form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, "Edit sample test result");
		
		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!form.getFieldGroup().isModified()) {
					saveSampleTest(form.getValue());
					callback.run();
				}
			}
		});

        if (CurrentUser.getCurrent().hasUserRole(UserRole.ADMIN)) {
			editView.addDeleteListener(new DeleteListener() {
				@Override
				public void onDelete() {
					FacadeProvider.getSampleTestFacade().deleteSampleTest(dto.toReference(), CurrentUser.getCurrent().getUserReference().getUuid());
					UI.getCurrent().removeWindow(popupWindow);
					callback.run();
				}
			}, I18nProperties.getCaption("SampleTest"));
		}
	}
	
	private void saveSampleTest(SampleTestDto dto) {
		SampleDto sample = FacadeProvider.getSampleFacade().getSampleByUuid(dto.getSample().getUuid());
		CaseDataDto existingCaseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(sample.getAssociatedCase().getUuid());
		stf.saveSampleTest(dto);
		CaseDataDto newCaseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(sample.getAssociatedCase().getUuid());
	
		if (existingCaseDto.getCaseClassification() != newCaseDto.getCaseClassification() &&
				newCaseDto.getClassificationUser() == null) {
			Notification notification = new Notification("Sample test saved. The classification of its associated case was automatically changed to " + newCaseDto.getCaseClassification().toString() + ".", Type.WARNING_MESSAGE);
			notification.setDelayMsec(-1);
			notification.show(Page.getCurrent());
		} else {
			Notification.show("Sample test saved", Type.WARNING_MESSAGE);
		}
	}
	
	private SampleTestDto createNewSampleTest(SampleReferenceDto sampleRef) {
		SampleTestDto sampleTest = new SampleTestDto();
		sampleTest.setUuid(DataHelper.createUuid());
		sampleTest.setSample(sampleRef);
		sampleTest.setLab(CurrentUser.getCurrent().getUser().getLaboratory());
		sampleTest.setLabUser(CurrentUser.getCurrent().getUserReference());
		return sampleTest;
	}
	
	public void deleteAllSelectedItems(Collection<Object> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification("No sample tests selected", "You have not selected any sample tests.", Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow("Are you sure you want to delete all " + selectedRows.size() + " selected sample tests?", new Runnable() {
				public void run() {
					for (Object selectedRow : selectedRows) {
						FacadeProvider.getSampleTestFacade().deleteSampleTest(new SampleTestReferenceDto(((SampleTestDto) selectedRow).getUuid()), CurrentUser.getCurrent().getUuid());
					}
					callback.run();
					new Notification("Sample tests deleted", "All selected sample tests have been deleted.", Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				}
			});
		}
	}
	
}
