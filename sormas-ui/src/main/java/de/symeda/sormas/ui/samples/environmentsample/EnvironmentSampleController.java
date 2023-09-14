/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.samples.environmentsample;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.deletionconfiguration.DeletionInfoDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleFacade;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleIndexDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleReferenceDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.samples.SamplesView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.DeleteRestoreHandlers;
import de.symeda.sormas.ui.utils.components.automaticdeletion.DeletionLabel;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;

public class EnvironmentSampleController {

	public EnvironmentSampleController() {
		// do nothing
	}

	public void deleteAllSelectedItems(
		Collection<EnvironmentSampleIndexDto> selectedRows,
		EnvironmentSampleGrid sampleGrid,
		Runnable noEntriesRemainingCallback) {

		ControllerProvider.getDeleteRestoreController()
			.deleteAllSelectedItems(
				selectedRows,
				null,
				null,
				DeleteRestoreHandlers.forEnvironmentSample(),
				bulkOperationCallback(sampleGrid, noEntriesRemainingCallback));

	}

	public void restoreSelectedSamples(
		Collection<EnvironmentSampleIndexDto> selectedRows,
		EnvironmentSampleGrid sampleGrid,
		Runnable noEntriesRemainingCallback) {

		ControllerProvider.getDeleteRestoreController()
			.restoreSelectedItems(
				selectedRows,
				DeleteRestoreHandlers.forEnvironmentSample(),
				bulkOperationCallback(sampleGrid, noEntriesRemainingCallback));
	}

	private Consumer<List<EnvironmentSampleIndexDto>> bulkOperationCallback(EnvironmentSampleGrid sampleGrid, Runnable noEntriesRemainingCallback) {
		return remainingSamples -> {
			sampleGrid.reload();
			if (CollectionUtils.isNotEmpty(remainingSamples)) {
				sampleGrid.asMultiSelect().selectItems(remainingSamples.toArray(new EnvironmentSampleIndexDto[0]));
			} else {
				noEntriesRemainingCallback.run();
			}
		};
	}

	public CommitDiscardWrapperComponent<EnvironmentSampleEditForm> getEditComponent(EnvironmentSampleDto sample) {

		EnvironmentSampleFacade environmentSampleFacade = FacadeProvider.getEnvironmentSampleFacade();
		String sampleUuid = sample.getUuid();
		DeletionInfoDto automaticDeletionInfoDto = environmentSampleFacade.getAutomaticDeletionInfo(sampleUuid);
		DeletionInfoDto manuallyDeletionInfoDto = environmentSampleFacade.getManuallyDeletionInfo(sampleUuid);

		EnvironmentSampleEditForm editForm = new EnvironmentSampleEditForm(sample.isPseudonymized());
		editForm.setValue(sample);
		editForm.setWidth(100, Sizeable.Unit.PERCENTAGE);

		CommitDiscardWrapperComponent<EnvironmentSampleEditForm> editComponent =
			new CommitDiscardWrapperComponent<>(editForm, editForm.getFieldGroup());
		editComponent.getButtonsPanel()
			.addComponentAsFirst(
				new DeletionLabel(automaticDeletionInfoDto, manuallyDeletionInfoDto, sample.isDeleted(), EventParticipantDto.I18N_PREFIX));

		editComponent.addCommitListener(() -> {
			if (!editForm.getFieldGroup().isModified()) {
				EnvironmentSampleDto editedSample = editForm.getValue();
				FacadeProvider.getEnvironmentSampleFacade().save(editedSample);

				Notification.show(I18nProperties.getString(Strings.messageEnvironmentSampleSaved), Notification.Type.WARNING_MESSAGE);
				SormasUI.refreshView();
			}
		});

		if (sample.isDeleted()) {
			editComponent.getWrappedComponent().getField(EnvironmentSampleDto.DELETION_REASON).setVisible(true);
			if (editComponent.getWrappedComponent().getField(EnvironmentSampleDto.DELETION_REASON).getValue() == DeletionReason.OTHER_REASON) {
				editComponent.getWrappedComponent().getField(EnvironmentSampleDto.OTHER_DELETION_REASON).setVisible(true);
			}
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.ENVIRONMENT_SAMPLE_DELETE)) {
			editComponent.addDeleteWithReasonOrRestoreListener(
				SamplesView.VIEW_NAME,
				null,
				I18nProperties.getString(Strings.entityEnvironmentSample),
				sampleUuid,
				FacadeProvider.getEnvironmentSampleFacade());
		}

		editComponent.restrictEditableComponentsOnEditView(
			UserRight.ENVIRONMENT_SAMPLE_EDIT,
			null,
			UserRight.ENVIRONMENT_SAMPLE_DELETE,
			null,
			FacadeProvider.getEnvironmentSampleFacade().getEditPermissionType(sampleUuid),
			sample.isInJurisdiction());

		return editComponent;
	}

	public Component getEditViewTitleLayout(String uuid) {
		EnvironmentSampleDto sample = FacadeProvider.getEnvironmentSampleFacade().getByUuid(uuid);

		TitleLayout titleLayout = new TitleLayout();
		titleLayout.addRow(DataHelper.getShortUuid(sample.getUuid()));
		titleLayout.addRow(DateFormatHelper.formatDate(sample.getSampleDateTime()));

		String mainRowText = EnvironmentSampleReferenceDto.buildCaption(sample.getSampleMaterial(), sample.getEnvironment().getUuid());
		titleLayout.addMainRow(mainRowText);

		return titleLayout;
	}

	public void navigateToSample(String uuid) {
		String navigationState = EnvironmentSampleDataView.VIEW_NAME + "/" + uuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}
}
