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

package de.symeda.sormas.ui.selfreport.processing;

import static de.symeda.sormas.ui.utils.processing.ProcessingUiHelper.showPickOrCreateEntryWindow;
import static de.symeda.sormas.ui.utils.processing.ProcessingUiHelper.showPickOrCreatePersonWindow;

import java.util.List;

import javax.naming.CannotProceedException;

import org.apache.commons.collections4.CollectionUtils;

import com.vaadin.server.Sizeable;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.api.selfreport.processing.AbstractSelfReportProcessingFlow;
import de.symeda.sormas.api.selfreport.processing.SelfReportProcessingFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.dataprocessing.EntitySelection;
import de.symeda.sormas.api.utils.dataprocessing.HandlerCallback;
import de.symeda.sormas.api.utils.dataprocessing.PickOrCreateEntryResult;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.caze.CaseCreateForm;
import de.symeda.sormas.ui.contact.ContactCreateForm;
import de.symeda.sormas.ui.selfreport.SelfReportDataForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.processing.EntrySelectionField;

public class SelfReportProcessingFLow extends AbstractSelfReportProcessingFlow {

	public SelfReportProcessingFLow(SelfReportProcessingFacade selfReportProcessingFacade) {
		super(selfReportProcessingFacade, UiUtil.getUser());
	}

	@Override
	protected void handlePickOrCreatePerson(PersonDto person, HandlerCallback<EntitySelection<PersonDto>> callback) {
		showPickOrCreatePersonWindow(person, callback);
	}

	@Override
	protected void handlePickOrCreateCase(
		List<CaseSelectionDto> similarCases,
		SelfReportDto selfReport,
		HandlerCallback<PickOrCreateEntryResult> callback) {
		if (CollectionUtils.isNotEmpty(similarCases)) {
			EntrySelectionField.Options.Builder optionsBuilder = new EntrySelectionField.Options.Builder().addSelectCase(similarCases)
				.addCreateEntry(EntrySelectionField.OptionType.CREATE_CASE, FeatureType.CASE_SURVEILANCE, UserRight.CASE_CREATE, UserRight.CASE_EDIT);

			showPickOrCreateEntryWindow(new EntrySelectionComponentForSelfReport(optionsBuilder.build(), selfReport), callback);
		} else {
			PickOrCreateEntryResult result = new PickOrCreateEntryResult();
			result.setNewCase(true);
			callback.done(result);
		}
	}

	@Override
	protected void handlePickOrCreateContact(
		List<SimilarContactDto> similarContacts,
		SelfReportDto selfReport,
		HandlerCallback<PickOrCreateEntryResult> callback) {
		if (CollectionUtils.isNotEmpty(similarContacts)) {
			EntrySelectionField.Options.Builder optionsBuilder = new EntrySelectionField.Options.Builder().addSelectContact(similarContacts)
				.addCreateEntry(
					EntrySelectionField.OptionType.CREATE_CONTACT,
					FeatureType.CONTACT_TRACING,
					UserRight.CONTACT_CREATE,
					UserRight.CONTACT_EDIT);

			showPickOrCreateEntryWindow(new EntrySelectionComponentForSelfReport(optionsBuilder.build(), selfReport), callback);
		} else {
			PickOrCreateEntryResult result = new PickOrCreateEntryResult();
			result.setNewContact(true);
			callback.done(result);
		}
	}

	@Override
	protected void handleCreateCase(
		CaseDataDto caze,
		PersonDto person,
		boolean isNewPerson,
		SelfReportDto selfReport,
		HandlerCallback<CaseDataDto> callback) {
		Window window = VaadinUiUtil.createPopupWindow();

		CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent =
			ControllerProvider.getCaseController().getCaseCreateComponent(null, null, null, null, null, true);
		CaseCreateForm caseCreateForm = caseCreateComponent.getWrappedComponent();

		caseCreateComponent.addCommitListener(() -> callback.done(caseCreateForm.getValue()));
		caseCreateComponent.addDiscardListener(callback::cancel);

		caseCreateForm.setValue(caze);
		caseCreateForm.setSymptoms(caze.getSymptoms());
		caseCreateForm.setPerson(person, isNewPerson);

		showFormWithSelfReport(selfReport, caseCreateComponent, window, I18nProperties.getString(Strings.headingCreateNewCase), false);
	}

	@Override
	protected void handleCreateContact(
		ContactDto contact,
		PersonDto person,
		boolean isNewPerson,
		SelfReportDto selfReport,
		HandlerCallback<ContactDto> callback) {
		Window window = VaadinUiUtil.createPopupWindow();

		CommitDiscardWrapperComponent<ContactCreateForm> contactCreateComponent =
			ControllerProvider.getContactController().getContactCreateComponent(null, false, null, true);

		ContactCreateForm contactCreaForm = contactCreateComponent.getWrappedComponent();
		contactCreateComponent.addCommitListener(() -> {
			callback.done(contactCreaForm.getValue());
		});
		contactCreateComponent.addDiscardListener(callback::cancel);

		contactCreaForm.setValue(contact);
		contactCreaForm.setPerson(person, isNewPerson);

		showFormWithSelfReport(selfReport, contactCreateComponent, window, I18nProperties.getString(Strings.headingCreateNewContact), false);
	}

	public static void showFormWithSelfReport(
		SelfReportDto selfReport,
		CommitDiscardWrapperComponent<? extends Component> editComponent,
		Window window,
		String heading,
		boolean discardOnClose) {

		addProcessedInMeantimeCheck(editComponent, selfReport);
		SelfReportDataForm form = new SelfReportDataForm(selfReport.getDisease(), selfReport.isInJurisdiction(), selfReport.isPseudonymized());
		form.setWidth(550, Sizeable.Unit.PIXELS);

		form.addStyleName(CssStyles.VSPACE_TOP_3);
		editComponent.addStyleName(CssStyles.VSPACE_TOP_3);

		HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();
		horizontalSplitPanel.setFirstComponent(form);
		horizontalSplitPanel.setSecondComponent(editComponent);
		horizontalSplitPanel.setSplitPosition(569, Sizeable.Unit.PIXELS); // This is just the position it needs to avoid vertical scroll bars.
		horizontalSplitPanel.addStyleName("lab-message-processing");

		Panel panel = new Panel();
		panel.setHeightFull();
		panel.setContent(horizontalSplitPanel);

		HorizontalLayout layout = new HorizontalLayout(panel);
		layout.setHeightFull();
		layout.setMargin(new MarginInfo(false, true, true, true));

		window.setHeightFull();
		window.setContent(layout);
		window.setCaption(heading);
		UI.getCurrent().addWindow(window);

		form.setValue(selfReport);
		form.setEnabled(false);

		// discard on close without clicking discard/commit button
		Registration closeListener = window.addCloseListener(e -> {
			if (discardOnClose) {
				editComponent.discard();
			}
		});

		// close after clicking commit or discard button
		editComponent.addDoneListener(() -> {
			// prevent discard on close
			closeListener.remove();
			window.close();
		});
	}

	public static void addProcessedInMeantimeCheck(CommitDiscardWrapperComponent<? extends Component> createComponent, SelfReportDto selfReportDto) {
		createComponent.setPrimaryCommitListener(() -> {
			if (Boolean.TRUE.equals(FacadeProvider.getSelfReportFacade().isProcessed(selfReportDto.toReference()))) {
				createComponent.getCommitButton().setEnabled(false);
				showAlreadyProcessedPopup();
				throw new CannotProceedException("The self report was processed in the meantime");
			}
		});
	}

	public static void showAlreadyProcessedPopup() {
		VerticalLayout warningLayout = VaadinUiUtil.createWarningLayout();
		Window popupWindow = VaadinUiUtil.showPopupWindow(warningLayout);
		Label infoLabel = new Label(I18nProperties.getValidationError(Validations.selfReportAlreadyProcessedError));
		CssStyles.style(infoLabel, CssStyles.LABEL_LARGE, CssStyles.LABEL_WHITE_SPACE_NORMAL);
		warningLayout.addComponent(infoLabel);
		popupWindow.addCloseListener(e -> popupWindow.close());
		popupWindow.setWidth(400, Sizeable.Unit.PIXELS);
	}
}
