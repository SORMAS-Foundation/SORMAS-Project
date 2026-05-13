package de.symeda.sormas.ui.externalmessage;

import java.util.*;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Validator;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.externalmessage.ExternalMessageCriteria;
import de.symeda.sormas.api.externalmessage.ExternalMessageFetchResult;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.NewMessagesState;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.systemevents.SystemEventType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.*;
import de.symeda.sormas.ui.utils.DateTimeField;

public class ExternalMessagesView extends AbstractView {

	private static final String SURVEY_FETCH_BUTTON_ENABLED = "SURVEY_FETCH_BUTTON_ENABLED";

	public static final String VIEW_NAME = "messages";

	private final ViewConfiguration viewConfiguration;
	private ExternalMessageCriteria criteria;

	private Button btnEnterBulkEditMode;
	private Button btnLeaveBulkEditMode;
	private MenuBar bulkOperationsDropdown;

	private Map<Button, String> statusButtons;
	private Button activeStatusButton;

	private ExternalMessageGridFilterForm filterForm;
	private final ExternalMessageGrid grid;

	public ExternalMessagesView() {

		super(VIEW_NAME);

		viewConfiguration = ViewModelProviders.of(ExternalMessagesView.class).get(ViewConfiguration.class);

		criteria = ViewModelProviders.of(ExternalMessagesView.class).get(ExternalMessageCriteria.class);
		if (criteria == null) {
			// init default filter
			criteria = new ExternalMessageCriteria();
			ViewModelProviders.of(ExternalMessagesView.class).get(ExternalMessageCriteria.class, criteria);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isPropertyValueTrue(FeatureType.EXTERNAL_MESSAGES, FeatureTypeProperty.FETCH_MODE)) {
			addHeaderComponent(ButtonHelper.createIconButton(Captions.externalMessageFetch, VaadinIcons.REFRESH, e -> {
				checkForConcurrentEventsAndFetch(false);
			}, ValoTheme.BUTTON_PRIMARY));
		}

		if (FacadeProvider.getFeatureConfigurationFacade()
			.isPropertyValueTrue(FeatureType.EXTERNAL_MESSAGES, FeatureTypeProperty.SURVEY_FETCH_ENABLED)) {
			addHeaderComponent(
				ButtonHelper.createIconButton(
					Captions.surveyFetch,
					VaadinIcons.REFRESH,
					e -> checkForConcurrentEventsAndFetch(true),
					ValoTheme.BUTTON_PRIMARY));
		}

		if (isBulkEditAllowed()) {
			btnEnterBulkEditMode = ButtonHelper.createIconButton(Captions.actionEnterBulkEditMode, VaadinIcons.CHECK_SQUARE_O, e -> {
				enterBulkEditMode();
			}, ValoTheme.BUTTON_PRIMARY);
			btnEnterBulkEditMode.setVisible(!viewConfiguration.isInEagerMode());
			addHeaderComponent(btnEnterBulkEditMode);

			btnLeaveBulkEditMode = ButtonHelper.createIconButton(Captions.actionLeaveBulkEditMode, VaadinIcons.CLOSE, e -> {
				leaveBulkEditMode();
			}, ValoTheme.BUTTON_PRIMARY);
			btnLeaveBulkEditMode.setVisible(viewConfiguration.isInEagerMode());
			addHeaderComponent(btnLeaveBulkEditMode);
		}

		VerticalLayout gridLayout = new VerticalLayout();
		addComponent(gridLayout);

		gridLayout.addComponent(createFilterBar());

		gridLayout.addComponent(createStatusFilterBar());

		grid = new ExternalMessageGrid(criteria);
		grid.addDataSizeChangeListener(e -> updateStatusButtons());

		gridLayout.addComponent(grid);

		gridLayout.setMargin(true);
		styleGridLayout(gridLayout);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}
		updateFilterComponents();
		grid.reload();
	}

	public HorizontalLayout createFilterBar() {

		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();
		filterLayout.addStyleName("wrap");

		filterForm = new ExternalMessageGridFilterForm();
		filterForm.addValueChangeListener(e -> {
			if (!filterForm.hasFilter()) {
				this.navigateTo(null);
			}
		});
		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(ExternalMessagesView.class).remove(ExternalMessageCriteria.class);
			this.navigateTo(null, true);
		});
		filterForm.addApplyHandler(e -> {
			SormasUI.get().getNavigator().navigateTo(ExternalMessagesView.VIEW_NAME);
			grid.reload();
		});
		filterLayout.addComponent(filterForm);

		return filterLayout;
	}

	public HorizontalLayout createStatusFilterBar() {
		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setWidthFull();
		statusFilterLayout.setMargin(false);
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		statusButtons = new HashMap<>();

		activeStatusButton = createAndAddStatusButton(null, statusFilterLayout);

		createAndAddStatusButton(ExternalMessageStatus.UNPROCESSED, statusFilterLayout);
		createAndAddStatusButton(ExternalMessageStatus.PROCESSED, statusFilterLayout);
		if (!FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
			createAndAddStatusButton(ExternalMessageStatus.UNCLEAR, statusFilterLayout);
			createAndAddStatusButton(ExternalMessageStatus.FORWARDED, statusFilterLayout);
		}

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);
		statusFilterLayout.addComponent(actionButtonsLayout);
		statusFilterLayout.setComponentAlignment(actionButtonsLayout, Alignment.TOP_RIGHT);
		statusFilterLayout.setExpandRatio(actionButtonsLayout, 1);

		bulkOperationsDropdown = createBulkOperationsDropdown();
		actionButtonsLayout.addComponent(bulkOperationsDropdown);

		return statusFilterLayout;
	}

	private MenuBar createBulkOperationsDropdown() {
		final List<MenuBarHelper.MenuBarItem> menuBarItems = new ArrayList<>();

		if (UiUtil.permitted(UserRight.EXTERNAL_MESSAGE_LABORATORY_DELETE)
			|| UiUtil.permitted(UserRight.EXTERNAL_MESSAGE_DOCTOR_DECLARATION_DELETE)) {
			menuBarItems.add(new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.bulkDelete), VaadinIcons.TRASH, mi -> {
				ControllerProvider.getExternalMessageController()
					.deleteAllSelectedItems(grid.asMultiSelect().getSelectedItems(), grid, () -> navigateTo(criteria));
			}, true));
		}

		if (UiUtil.permitted(UserRight.EXTERNAL_MESSAGE_LABORATORY_PROCESS)
			|| UiUtil.permitted(UserRight.EXTERNAL_MESSAGE_DOCTOR_DECLARATION_PROCESS)) {
			menuBarItems.add(
				new MenuBarHelper.MenuBarItem(
					I18nProperties.getCaption(Captions.bulkEditAssignee),
					VaadinIcons.ELLIPSIS_H,
					mi -> ControllerProvider.getExternalMessageController()
						.assignAllSelectedItems(grid.asMultiSelect().getSelectedItems(), () -> navigateTo(criteria)),
					true));
		}

		MenuBar bulkOperationsDropdown = MenuBarHelper.createDropDown(Captions.bulkActions, menuBarItems);
		bulkOperationsDropdown.setVisible(viewConfiguration.isInEagerMode());

		return bulkOperationsDropdown;
	}

	private void styleGridLayout(VerticalLayout gridLayout) {
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");
	}

	private void updateFilterComponents() {
		setApplyingCriteria(true);
		updateStatusButtons();
		filterForm.setValue(criteria);
		setApplyingCriteria(false);
	}

	private void updateStatusButtons() {
		statusButtons.keySet().forEach(b -> {
			CssStyles.style(b, CssStyles.BUTTON_FILTER_LIGHT);
			b.setCaption(statusButtons.get(b));
			if (b.getData() == criteria.getExternalMessageStatus()) {
				activeStatusButton = b;
			}
		});
		ExternalMessageStatus activeStatus = null;
		if (activeStatusButton != null) {
			CssStyles.removeStyles(activeStatusButton, CssStyles.BUTTON_FILTER_LIGHT);
			activeStatusButton
				.setCaption(statusButtons.get(activeStatusButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getDataSize())));
			activeStatus = (ExternalMessageStatus) activeStatusButton.getData();
		}

		boolean processingPossible = UiUtil.permitted(FeatureType.CASE_SURVEILANCE, UserRight.CASE_CREATE, UserRight.CASE_EDIT)
			|| UiUtil.permitted(FeatureType.CONTACT_TRACING, UserRight.CONTACT_CREATE, UserRight.CONTACT_EDIT)
			|| UiUtil.permitted(FeatureType.EVENT_SURVEILLANCE, UserRight.EVENTPARTICIPANT_CREATE, UserRight.EVENTPARTICIPANT_EDIT);
		grid.updateProcessColumnVisibility((activeStatus == null || activeStatus.isProcessable()) && processingPossible);
	}

	private Button createAndAddStatusButton(@Nullable ExternalMessageStatus status, HorizontalLayout buttonLayout) {
		Button button = ButtonHelper.createButton(status == null ? I18nProperties.getCaption(Captions.all) : status.toString(), e -> {
			criteria.externalMessageStatus(status);
			navigateTo(criteria);
		}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);

		if (status != null) {
			button.setData(status);
		}

		button.setCaptionAsHtml(true);

		buttonLayout.addComponent(button);
		statusButtons.put(button, button.getCaption());

		return button;
	}

	private void checkForConcurrentEventsAndFetch(boolean surveyMode) {
		boolean fetchAlreadyStarted = FacadeProvider.getSystemEventFacade().existsStartedEvent(SystemEventType.FETCH_EXTERNAL_MESSAGES);
		if (fetchAlreadyStarted) {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingFetchExternalMessages),
				new Label(I18nProperties.getString(Strings.confirmationFetchExternalMessages)),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				480,
				confirmed -> {
					if (confirmed) {
						askForSinceDateAndFetch(surveyMode);
					}
				});
		} else {
			askForSinceDateAndFetch(surveyMode);
		}
	}

	private void askForSinceDateAndFetch(boolean surveyMode) {
		boolean atLeastOneFetchExecuted = FacadeProvider.getSyncFacade().hasAtLeastOneSuccessfullSyncOf(SystemEventType.FETCH_EXTERNAL_MESSAGES);
		if (atLeastOneFetchExecuted) {
			if (surveyMode) {
				fetchSurveyMessages(null);
			} else {
				fetchExternalMessages(null);
			}
		} else {
			showSinceDateSelectionWindow(since -> {
				if (surveyMode) {
					fetchSurveyMessages(since);
				} else {
					fetchExternalMessages(since);
				}
			});
		}
	}

	private void fetchExternalMessages(Date since) {
		ExternalMessageFetchResult fetchResult = FacadeProvider.getExternalMessageFacade().fetchAndSaveExternalMessages(since);
		if (!fetchResult.isSuccess()) {
			VaadinUiUtil.showWarningPopup(fetchResult.getError());
		} else if (NewMessagesState.NO_NEW_MESSAGES.equals(fetchResult.getNewMessagesState())) {
			VaadinUiUtil.showWarningPopup(I18nProperties.getCaption(Captions.externalMessageNoNewMessages));
		} else {
			grid.reload();
		}
	}

	private void fetchSurveyMessages(Date since) {
		FacadeProvider.getExternalMessageFacade().saveAndProcessSurveyResponses(since);
		grid.reload();
	}

	private void showSinceDateSelectionWindow(Consumer<Date> dateConsumer) {
		VerticalLayout verticalLayout = new VerticalLayout();
		Label label = new Label(I18nProperties.getString(Strings.confirmationSinceExternalMessages));
		verticalLayout.addComponent(label);

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		Button yesButton = ButtonHelper.createButton(Captions.actionYes);
		Button noButton = ButtonHelper.createButton(Captions.actionNo);
		Button cancelButton = ButtonHelper.createButton(Captions.actionCancel);
		cancelButton.setStyleName(ValoTheme.BUTTON_PRIMARY);

		horizontalLayout.addComponent(yesButton);
		horizontalLayout.addComponent(noButton);
		horizontalLayout.addComponent(cancelButton);
		horizontalLayout.setStyleName(CssStyles.FLOAT_RIGHT);
		verticalLayout.addComponent(horizontalLayout);

		Window window = VaadinUiUtil.showPopupWindow(verticalLayout);

		cancelButton.addClickListener(event -> window.close());
		noButton.addClickListener(event -> {
			dateConsumer.accept(null);
			window.close();
		});
		yesButton.addClickListener(yesEvent -> {
			horizontalLayout.removeComponent(yesButton);
			horizontalLayout.removeComponent(noButton);
			horizontalLayout.removeComponent(cancelButton);

			Button confirmButton = ButtonHelper.createButton(Captions.actionConfirm);
			confirmButton.setStyleName(ValoTheme.BUTTON_PRIMARY);

			DateTimeField dateTimeField = new DateTimeField();
			dateTimeField.addValidator(date -> {
				if (date == null) {
					throw new Validator.InvalidValueException("Since date has to be set");
				}
			});
			dateTimeField.addValidator(new FutureDateValidator(dateTimeField, 0, null));

			horizontalLayout.addComponent(dateTimeField);
			horizontalLayout.addComponent(confirmButton);

			confirmButton.addClickListener(confirmEvent -> {
				if (dateTimeField.isValid()) {
					dateConsumer.accept(dateTimeField.getValue());
					window.close();
				} else {
					new Notification(I18nProperties.getString(Strings.messageCheckInputData), null, Notification.Type.ERROR_MESSAGE, true)
						.show(Page.getCurrent());
				}
			});
		});
	}

	private boolean isBulkEditAllowed() {
		return UiUtil.permitted(UserRight.PERFORM_BULK_OPERATIONS);
	}

	private void enterBulkEditMode() {
		bulkOperationsDropdown.setVisible(true);
		viewConfiguration.setInEagerMode(true);
		btnEnterBulkEditMode.setVisible(false);
		btnLeaveBulkEditMode.setVisible(true);

		navigateTo(criteria);
	}

	private void leaveBulkEditMode() {
		bulkOperationsDropdown.setVisible(false);
		viewConfiguration.setInEagerMode(false);
		btnLeaveBulkEditMode.setVisible(false);
		btnEnterBulkEditMode.setVisible(true);

		navigateTo(criteria);
	}
}
