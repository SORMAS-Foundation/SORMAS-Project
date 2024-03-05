/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.specialcaseaccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.specialcaseaccess.SpecialCaseAccessDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.caze.AbstractCaseGrid;
import de.symeda.sormas.ui.utils.BulkOperationHandler;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class SpecialCaseAccessController {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public SpecialCaseAccessController() {
	}

	public void create(CaseReferenceDto caze, Runnable callback) {
		SpecialCaseAccessDto specialCaseAccess = SpecialCaseAccessDto.build(caze, UiUtil.getUserReference());

		openEditWindow(
			specialCaseAccess,
			Strings.headingCreateSpecailCaseAccess,
			true,
			false,
			FacadeProvider.getSpecialCaseAccessFacade()::save,
			callback);
	}

	public void createForSelectedCases(Set<CaseIndexDto> cases, AbstractCaseGrid<?> caseGrid) {

		if (CollectionUtils.isEmpty(cases)) {
			new Notification(
				I18nProperties.getString(Strings.headingNoCasesSelected),
				I18nProperties.getString(Strings.messageNoCasesSelected),
				Notification.Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());

			return;
		}

		SpecialCaseAccessDto specialCaseAccess = SpecialCaseAccessDto.build(null, UiUtil.getUserReference());
		openEditWindow(specialCaseAccess, Strings.headingCreateSpecailCaseAccess, true, true, editedAccess -> {
			boolean anyAssignedToUser = FacadeProvider.getSpecialCaseAccessFacade()
				.isAnyAssignedToUser(cases.stream().map(CaseIndexDto::toReference).collect(Collectors.toList()), editedAccess.getAssignedTo());

			if (anyAssignedToUser) {
				VaadinUiUtil.showConfirmationPopup(
					I18nProperties.getString(Strings.headingConfirmBulkGrantSpecialAccess),
					new Label(I18nProperties.getString(Strings.confirmationBulkGrantSpecialAccess)),
					I18nProperties.getString(Strings.yes),
					I18nProperties.getString(Strings.no),
					640,
					confirmed -> {
						if (Boolean.TRUE.equals(confirmed)) {
							saveBulkSpecialAccesses(new ArrayList<>(cases), editedAccess, caseGrid);
						}
					});
			} else {
				saveBulkSpecialAccesses(new ArrayList<>(cases), editedAccess, caseGrid);
			}
		}, () -> {
		});
	}

	public void edit(SpecialCaseAccessDto specialCaseAccess, Runnable callback) {
		openEditWindow(
			specialCaseAccess,
			Strings.headingEditSpecailCaseAccess,
			false,
			false,
			FacadeProvider.getSpecialCaseAccessFacade()::save,
			callback);
	}

	private static void openEditWindow(
		SpecialCaseAccessDto specialCaseAccess,
		String titleTag,
		boolean isCreate,
		boolean isBulkCreate,
		Consumer<SpecialCaseAccessDto> handleSave,
		Runnable callback) {
		SpecialCaseAccessForm editForm = new SpecialCaseAccessForm(isCreate);
		if (isBulkCreate) {
			editForm.hideCaseField();
		}
		editForm.setValue(specialCaseAccess);

		CommitDiscardWrapperComponent<SpecialCaseAccessForm> editComponent = new CommitDiscardWrapperComponent<>(editForm, editForm.getFieldGroup());

		Window window = VaadinUiUtil.showModalPopupWindow(editComponent, I18nProperties.getString(titleTag));
		window.setWidth(600, Unit.PIXELS);

		editComponent.addCommitListener(() -> {
			if (!editForm.getFieldGroup().isModified()) {
				SpecialCaseAccessDto dto = editForm.getValue();

				handleSave.accept(dto);
				callback.run();
			}
		});

		if (!isCreate) {
			editComponent.addDeleteListener(() -> {
				FacadeProvider.getSpecialCaseAccessFacade().delete(specialCaseAccess.getUuid());

				window.close();
				callback.run();
			}, I18nProperties.getCaption(SpecialCaseAccessDto.I18N_PREFIX));
		}
	}

	private void saveBulkSpecialAccesses(List<CaseIndexDto> cases, SpecialCaseAccessDto accessTemplate, AbstractCaseGrid<?> caseGrid) {
		new BulkOperationHandler<CaseIndexDto>(
			Strings.messageBulkSpecialCaseAccessAllProcessed,
			null,
			Strings.headingBulkSpecialCaseAccessSomeNotProcessed,
			Strings.headingBulkSpecialCaseAccessNoneProcessed,
			Strings.messageCountAccessesNotGrantedDueToError,
			null,
			null,
			null,
			null,
			Strings.infoBulkProcessFinishedWithSkips,
			Strings.infoBulkProcessFinishedWithoutSuccess).doBulkOperation(batch -> {
				List<ProcessedEntity> processedCases = new ArrayList<>();

				try {
					List<SpecialCaseAccessDto> specialAccesses =
						batch.stream().map(c -> accessTemplate.withCase(c.toReference())).collect(Collectors.toList());
					FacadeProvider.getSpecialCaseAccessFacade().saveAll(specialAccesses);
					processedCases.addAll(batch.stream().map(ProcessedEntity::successful).collect(Collectors.toList()));
				} catch (Exception e) {
					processedCases.addAll(batch.stream().map(ProcessedEntity::failedInternally).collect(Collectors.toList()));
					logger.error("Failed to save special case accesses", e);
				}
				return processedCases;
			}, new ArrayList<>(cases), ControllerProvider.getCaseController().bulkOperationCallback(caseGrid, null));
	}
}
