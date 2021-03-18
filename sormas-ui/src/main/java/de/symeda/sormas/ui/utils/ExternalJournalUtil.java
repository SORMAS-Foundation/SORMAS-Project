package de.symeda.sormas.ui.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.externaljournal.ExternalJournalFacade;
import de.symeda.sormas.api.externaljournal.ExternalJournalValidation;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryResult;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonDto;

public class ExternalJournalUtil {

	private final static ExternalJournalFacade externalJournalFacade = FacadeProvider.getExternalJournalFacade();

	/**
	 * Creates a Button to be added wherever a patient needs to be managed by an external journal.
	 * If no external journal is enabled with the current settings, an empty optional is returned.
	 * If the person is not registered in the external journal, a create account/register button is returned
	 * If the person is registered, a button is returned which opens a popup with further options.
	 * 
	 * @param person
	 *            person to be managed by the external journal
	 * @return Optional containing appropriate Button
	 */
	public static Optional<Button> getExternalJournalUiButton(PersonDto person) {
		if (FacadeProvider.getConfigFacade().getSymptomJournalConfig().isActive()) {
			if (person.isEnrolledInExternalJournal()) {
				return Optional.of(createSymptomJournalOptionsButton(person));
			} else {
				return Optional.of(createSymptomJournalRegisterButton(person));
			}
		} else if (FacadeProvider.getConfigFacade().getPatientDiaryConfig().isActive()) {
			if (person.isEnrolledInExternalJournal()) {
				return Optional.of(createPatientDiaryOptionsButton(person));
			} else {
				return Optional.of(createPatientDiaryRegisterButton(person));
			}

		}
		return Optional.empty();
	}

	private static Button createSymptomJournalOptionsButton(PersonDto person) {
		VerticalLayout popupLayout = new VerticalLayout();
		popupLayout.setSpacing(true);
		popupLayout.setMargin(true);
		popupLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
		// TODO: implement cancel for PIA
		Button.ClickListener cancelListener = clickEvent -> {
		};
		Button.ClickListener openListener = clickEvent -> openSymptomJournalWindow(person);
		PopupButton ediaryButton =
			ButtonHelper.createPopupButton(I18nProperties.getCaption(Captions.symptomJournalOptionsButton), popupLayout, ValoTheme.BUTTON_PRIMARY);
		Button cancelButton =
			ButtonHelper.createButton(I18nProperties.getCaption(Captions.cancelExternalFollowUpButton), cancelListener, ValoTheme.BUTTON_PRIMARY);
		Button openButton =
			ButtonHelper.createButton(I18nProperties.getCaption(Captions.openInSymptomJournalButton), openListener, ValoTheme.BUTTON_PRIMARY);
		popupLayout.addComponent(cancelButton);
		popupLayout.addComponent(openButton);
		return ediaryButton;
	}

	private static Button createSymptomJournalRegisterButton(PersonDto person) {
		Button btnCreateSymptomJournalAccount = new Button(I18nProperties.getCaption(Captions.createSymptomJournalAccountButton));
		CssStyles.style(btnCreateSymptomJournalAccount, ValoTheme.BUTTON_PRIMARY);
		btnCreateSymptomJournalAccount.addClickListener(clickEvent -> openSymptomJournalWindow(person));
		return btnCreateSymptomJournalAccount;
	}

	private static Button createPatientDiaryOptionsButton(PersonDto person) {
		VerticalLayout popupLayout = new VerticalLayout();
		popupLayout.setSpacing(true);
		popupLayout.setMargin(true);
		popupLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
		Button.ClickListener cancelListener = clickEvent -> showCancelFollowupConfirmationPopup(person);
		Button.ClickListener openListener = clickEvent -> openPatientDiaryPage(person.getUuid());
		PopupButton ediaryButton =
			ButtonHelper.createPopupButton(I18nProperties.getCaption(Captions.patientDiaryOptionsButton), popupLayout, ValoTheme.BUTTON_PRIMARY);
		Button cancelButton =
			ButtonHelper.createButton(I18nProperties.getCaption(Captions.cancelExternalFollowUpButton), cancelListener, ValoTheme.BUTTON_PRIMARY);
		Button openButton =
			ButtonHelper.createButton(I18nProperties.getCaption(Captions.openInPatientDiaryButton), openListener, ValoTheme.BUTTON_PRIMARY);
		popupLayout.addComponent(cancelButton);
		popupLayout.addComponent(openButton);
		return ediaryButton;
	}

	private static Button createPatientDiaryRegisterButton(PersonDto person) {
		Button btnPatientDiaryAccount = new Button(I18nProperties.getCaption(Captions.registerInPatientDiaryButton));
		CssStyles.style(btnPatientDiaryAccount, ValoTheme.BUTTON_PRIMARY);
		btnPatientDiaryAccount.addClickListener(clickEvent -> enrollPatientInPatientDiary(person));
		return btnPatientDiaryAccount;
	}

	/**
	 * Opens a window that contains an iFrame with the symptom journal website specified in the properties.
	 * The steps to build that iFrame are:
	 * 1. Request an authentication token based on the stored client ID and secret
	 * 2. Build an HTML page containing a form with the auth token and some personal details as parameters
	 * 3. The form is automatically submitted and replaced by the iFrame
	 */
	private static void openSymptomJournalWindow(PersonDto person) {
		String authToken = externalJournalFacade.getSymptomJournalAuthToken();
		BrowserFrame frame = new BrowserFrame(null, new StreamResource(() -> {
			String formUrl = FacadeProvider.getConfigFacade().getSymptomJournalConfig().getUrl();
			Map<String, String> parameters = new LinkedHashMap<>();
			parameters.put("token", authToken);
			parameters.put("uuid", person.getUuid());
			parameters.put("firstname", person.getFirstName());
			parameters.put("lastname", person.getLastName());
			parameters.put("email", person.getPrimaryEmailAddress());
			byte[] document = createSymptomJournalForm(formUrl, parameters);

			return new ByteArrayInputStream(document);
		}, "symptomJournal.html"));
		frame.setWidth("100%");
		frame.setHeight("100%");

		Window window = VaadinUiUtil.createPopupWindow();
		window.setContent(frame);
		window.setCaption(I18nProperties.getString(Strings.headingSymptomJournalAccountCreation));
		window.setWidth(80, Sizeable.Unit.PERCENTAGE);
		window.setHeight(80, Sizeable.Unit.PERCENTAGE);

		UI.getCurrent().addWindow(window);
	}

	/**
	 * @return An HTML page containing a form that is automatically submitted in order to display the symptom journal iFrame
	 */
	private static byte[] createSymptomJournalForm(String formUrl, Map<String, String> inputs) {
		Document document;
		try (InputStream in = ExternalJournalUtil.class.getResourceAsStream("/symptomJournal.html")) {
			document = Jsoup.parse(in, StandardCharsets.UTF_8.name(), formUrl);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		Element form = document.getElementById("form");
		form.attr("action", formUrl);
		Element parametersElement = form.getElementById("parameters");

		inputs.forEach((k, v) -> parametersElement.appendChild(new Element("input").attr("type", "hidden").attr("name", k).attr("value", v)));
		return document.toString().getBytes(StandardCharsets.UTF_8);
	}

	private static void showCancelFollowupConfirmationPopup(PersonDto personDto) {
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.cancelExternalFollowUpPopupTitle),
			new Label(I18nProperties.getString(Strings.confirmationCancelExternalFollowUpPopup)),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			600,
			confirmed -> {
				if (confirmed)
					cancelPatientDiaryFollowUp(personDto);
			});
	}

	private static void cancelPatientDiaryFollowUp(PersonDto personDto) {
		PatientDiaryResult result = externalJournalFacade.cancelPatientDiaryFollowUp(personDto);
		showPatientDiaryResultPopup(result, Captions.patientDiaryCancelError);
	}

	private static void openPatientDiaryPage(String personUuid) {
		String url = FacadeProvider.getConfigFacade().getPatientDiaryConfig().getUrl();
		String authToken = externalJournalFacade.getPatientDiaryAuthToken();
		url += "/data?q=" + personUuid + "&queryKey=sicFieldIdentifier" + "&token=" + authToken;
		UI.getCurrent().getPage().open(url, "_blank");
	}

	private static void enrollPatientInPatientDiary(PersonDto person) {
		ExternalJournalValidation validationResult = externalJournalFacade.validatePatientDiaryPerson(person);
		if (!validationResult.isValid()) {
			showPatientDiaryWarningPopup(validationResult.getMessage());
		} else {
			PatientDiaryResult registerResult = externalJournalFacade.registerPatientDiaryPerson(person);
			showPatientDiaryResultPopup(registerResult, Captions.patientDiaryRegistrationError);
		}
	}

	private static void showPatientDiaryWarningPopup(String message) {
		VerticalLayout warningLayout = new VerticalLayout();
		warningLayout.setMargin(true);
		Image warningIcon = new Image(null, new ThemeResource("img/warning-icon.png"));
		warningIcon.setHeight(35, Sizeable.Unit.PIXELS);
		warningIcon.setWidth(35, Sizeable.Unit.PIXELS);
		warningLayout.addComponentAsFirst(warningIcon);
		Window popupWindow = VaadinUiUtil.showPopupWindow(warningLayout);
		Label messageLabel = new Label(I18nProperties.getValidationError(Validations.externalJournalPersonValidationError));
		CssStyles.style(messageLabel, CssStyles.LABEL_LARGE, CssStyles.LABEL_WHITE_SPACE_NORMAL);
		warningLayout.addComponent(messageLabel);
		Label infoLabel = new Label(message);
		CssStyles.style(infoLabel, CssStyles.LABEL_LARGE, CssStyles.LABEL_WHITE_SPACE_NORMAL);
		warningLayout.addComponent(infoLabel);
		CssStyles.style(warningLayout, CssStyles.ALIGN_CENTER);
		popupWindow.addCloseListener(e -> popupWindow.close());
		popupWindow.setWidth(400, Sizeable.Unit.PIXELS);
	}

	private static void showPatientDiaryResultPopup(PatientDiaryResult result, String errorMessage) {
		VerticalLayout resultLayout = new VerticalLayout();
		resultLayout.setMargin(true);
		Image errorIcon = new Image(null, new ThemeResource("img/error-icon.png"));
		errorIcon.setHeight(35, Sizeable.Unit.PIXELS);
		errorIcon.setWidth(35, Sizeable.Unit.PIXELS);
		Image successIcon = new Image(null, new ThemeResource("img/success-icon.png"));
		successIcon.setHeight(35, Sizeable.Unit.PIXELS);
		successIcon.setWidth(35, Sizeable.Unit.PIXELS);
		CssStyles.style(resultLayout, CssStyles.ALIGN_CENTER);
		if (result.isSuccess()) {
			resultLayout.removeComponent(errorIcon);
			resultLayout.addComponentAsFirst(successIcon);
		} else {
			resultLayout.removeComponent(successIcon);
			resultLayout.addComponentAsFirst(errorIcon);
			Label infoLabel = new Label();
			CssStyles.style(infoLabel, CssStyles.LABEL_LARGE, CssStyles.LABEL_WHITE_SPACE_NORMAL);
			resultLayout.addComponent(infoLabel);
			infoLabel.setValue(errorMessage);
		}
		Label messageLabel = new Label();
		CssStyles.style(messageLabel, CssStyles.LABEL_LARGE, CssStyles.LABEL_WHITE_SPACE_NORMAL);
		resultLayout.addComponent(messageLabel);
		messageLabel.setValue(result.getMessage());
		Window popupWindow = VaadinUiUtil.showPopupWindow(resultLayout);
		popupWindow.addCloseListener(e -> popupWindow.close());
		popupWindow.setWidth(400, Sizeable.Unit.PIXELS);
	}
}
