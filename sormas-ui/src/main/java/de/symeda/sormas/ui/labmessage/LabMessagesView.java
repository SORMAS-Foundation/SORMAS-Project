package de.symeda.sormas.ui.labmessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.labmessage.LabMessageCriteria;
import de.symeda.sormas.api.labmessage.LabMessageFetchResult;
import de.symeda.sormas.api.labmessage.LabMessageStatus;
import de.symeda.sormas.api.labmessage.NewMessagesState;
import de.symeda.sormas.api.systemevents.SystemEventType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.samples.SamplesView;
import de.symeda.sormas.ui.samples.SamplesViewType;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FutureDateValidator;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.MenuBarHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class LabMessagesView extends AbstractView {

	public static final String VIEW_NAME = SamplesView.VIEW_NAME + "/labMessages";

	private final ViewConfiguration viewConfiguration;
	private LabMessageCriteria criteria;

	private Button btnEnterBulkEditMode;
	private Button btnLeaveBulkEditMode;
	private MenuBar bulkOperationsDropdown;

	private Map<Button, String> statusButtons;
	private Button activeStatusButton;

	private LabMessageGridFilterForm filterForm;
	private final LabMessageGrid grid;

	public LabMessagesView() {

		super(VIEW_NAME);

		viewConfiguration = ViewModelProviders.of(LabMessagesView.class).get(ViewConfiguration.class);

		criteria = ViewModelProviders.of(LabMessagesView.class).get(LabMessageCriteria.class);
		if (criteria == null) {
			// init default filter
			criteria = new LabMessageCriteria();
			ViewModelProviders.of(LabMessagesView.class).get(LabMessageCriteria.class, criteria);
		}

		OptionGroup samplesViewSwitcher = new OptionGroup();
		samplesViewSwitcher.setId("samplesViewSwitcher");
		CssStyles.style(samplesViewSwitcher, CssStyles.FORCE_CAPTION, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY);
		for (SamplesViewType type : SamplesViewType.values()) {
			samplesViewSwitcher.addItem(type);
			samplesViewSwitcher.setItemCaption(type, I18nProperties.getEnumCaption(type));
		}

		samplesViewSwitcher.setValue(SamplesViewType.LAB_MESSAGES);
		samplesViewSwitcher.addValueChangeListener(e -> SormasUI.get().getNavigator().navigateTo(SamplesView.VIEW_NAME));
		addHeaderComponent(samplesViewSwitcher);

		addHeaderComponent(ButtonHelper.createIconButton(Captions.labMessageFetch, VaadinIcons.REFRESH, e -> {
			checkForConcurrentEventsAndFetch();
		}, ValoTheme.BUTTON_PRIMARY));

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

		grid = new LabMessageGrid(criteria);
		grid.setDataProviderListener(e -> updateStatusButtons());
		grid.getDataProvider().addDataProviderListener(e -> updateStatusButtons());

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

		filterForm = new LabMessageGridFilterForm();
		filterForm.addValueChangeListener(e -> {
			if (!filterForm.hasFilter()) {
				this.navigateTo(null);
			}
		});
		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(LabMessagesView.class).remove(LabMessageCriteria.class);
			this.navigateTo(null, true);
		});
		filterForm.addApplyHandler(e -> {
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

		createAndAddStatusButton(LabMessageStatus.UNPROCESSED, statusFilterLayout);
		createAndAddStatusButton(LabMessageStatus.PROCESSED, statusFilterLayout);
		createAndAddStatusButton(LabMessageStatus.UNCLEAR, statusFilterLayout);
		createAndAddStatusButton(LabMessageStatus.FORWARDED, statusFilterLayout);

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

		menuBarItems.add(new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.bulkDelete), VaadinIcons.TRASH, mi -> {
			ControllerProvider.getLabMessageController().deleteAllSelectedItems(grid.asMultiSelect().getSelectedItems(), () -> navigateTo(criteria));
		}, true));
		menuBarItems.add(
			new MenuBarHelper.MenuBarItem(
				I18nProperties.getCaption(Captions.bulkEditAssignee),
				VaadinIcons.ELLIPSIS_H,
				mi -> ControllerProvider.getLabMessageController()
					.assignAllSelectedItems(grid.asMultiSelect().getSelectedItems(), () -> navigateTo(criteria)),
				true));

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
			if (b.getData() == criteria.getLabMessageStatus()) {
				activeStatusButton = b;
			}
		});
		if (activeStatusButton != null) {
			CssStyles.removeStyles(activeStatusButton, CssStyles.BUTTON_FILTER_LIGHT);
			activeStatusButton
				.setCaption(statusButtons.get(activeStatusButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getItemCount())));
		}

		LabMessageStatus activeStatus = (LabMessageStatus) activeStatusButton.getData();
		grid.updateProcessColumnVisibility(activeStatus == null || activeStatus.isProcessable());
	}

	private Button createAndAddStatusButton(@Nullable LabMessageStatus status, HorizontalLayout buttonLayout) {
		Button button = ButtonHelper.createButton(status == null ? I18nProperties.getCaption(Captions.all) : status.toString(), e -> {
			criteria.labMessageStatus(status);
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

	private void checkForConcurrentEventsAndFetch() {
		boolean fetchAlreadyStarted = FacadeProvider.getSystemEventFacade().existsStartedEvent(SystemEventType.FETCH_LAB_MESSAGES);
		if (fetchAlreadyStarted) {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingFetchLabMessages),
				new Label(I18nProperties.getString(Strings.confirmationFetchLabMessages)),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				480,
				confirmed -> {
					if (confirmed) {
						askForSinceDateAndFetch();
					}
				});
		} else {
			askForSinceDateAndFetch();
		}
	}

	private void askForSinceDateAndFetch() {
		boolean atLeastOneFetchExecuted = FacadeProvider.getSyncFacade().atLeastOneSuccessfullSyncOf(SystemEventType.FETCH_LAB_MESSAGES);
		if (atLeastOneFetchExecuted) {
			fetchLabMessages(null);
		} else {
			showSinceDateSelectionWindow(this::fetchLabMessages);
		}
	}

	private void fetchLabMessages(Date since) {
		LabMessageFetchResult fetchResult = FacadeProvider.getLabMessageFacade().fetchAndSaveExternalLabMessages(since);
		if (!fetchResult.isSuccess()) {
			VaadinUiUtil.showWarningPopup(fetchResult.getError());
		} else if (NewMessagesState.NO_NEW_MESSAGES.equals(fetchResult.getNewMessagesState())) {
			VaadinUiUtil.showWarningPopup(I18nProperties.getCaption(Captions.labMessageNoNewMessages));
		} else {
			grid.reload();
		}
	}

	private void showSinceDateSelectionWindow(Consumer<Date> dateConsumer) {
		VerticalLayout verticalLayout = new VerticalLayout();
		Label label = new Label(I18nProperties.getString(Strings.confirmationSinceLabMessages));
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
		noButton.addClickListener(event -> dateConsumer.accept(null));
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
		return UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS_LAB_MESSAGES);
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
