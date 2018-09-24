package de.symeda.sormas.ui.utils;

import java.util.function.Consumer;

import com.vaadin.data.Item;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DoneListener;

public class VaadinUiUtil {

	public static Window createPopupWindow() {
		Window window = new Window(null);
		window.setModal(true);
		window.setSizeUndefined();
		window.setResizable(false);
		window.center();

		return window;
	}

	public static Window showSimplePopupWindow(String caption, String contentText) {
		Window window = new Window(null);
		window.setModal(true);
		window.setSizeUndefined();
		window.setResizable(false);
		window.center();

		VerticalLayout popupLayout = new VerticalLayout();
		popupLayout.setMargin(true);
		popupLayout.setSpacing(true);
		popupLayout.setSizeUndefined();
		Label contentLabel = new Label(contentText);
		contentLabel.setWidth(100, Unit.PERCENTAGE);
		popupLayout.addComponent(contentLabel);
		Button okayButton = new Button("Okay");
		okayButton.addClickListener(e -> {
			window.close();
		});
		CssStyles.style(okayButton, ValoTheme.BUTTON_PRIMARY);
		popupLayout.addComponent(okayButton);
		popupLayout.setComponentAlignment(okayButton, Alignment.BOTTOM_RIGHT);

		window.setCaption(caption);
		window.setContent(popupLayout);

		UI.getCurrent().addWindow(window);

		return window;
	}

	public static Window showPopupWindow(Component content) {

		Window window = new Window(null);
		window.setModal(true);
		window.setSizeUndefined();
		window.setResizable(false);
		window.center();
		window.setContent(content);

		UI.getCurrent().addWindow(window);

		return window;
	}

	public static Window showModalPopupWindow(CommitDiscardWrapperComponent<?> content, String caption) {

		final Window popupWindow = VaadinUiUtil.showPopupWindow(content);
		popupWindow.setCaption(caption);
		content.setMargin(true);

		content.addDoneListener(new DoneListener() {
			public void onDone() {
				popupWindow.close();
			}
		});

		return popupWindow;
	}

	@SuppressWarnings("serial")
	public static void addIconColumn(GeneratedPropertyContainer container, String iconPropertyId, FontAwesome fontAwesomeIcon) {
		container.addGeneratedProperty(iconPropertyId, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				return fontAwesomeIcon.getHtml();
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
		});
	}

	public static Window showConfirmationPopup(String caption, Component content, String confirmCaption, String cancelCaption, Integer width, Consumer<Boolean> resultConsumer) {
		Window popupWindow = VaadinUiUtil.createPopupWindow();
		if (width != null) {
			popupWindow.setWidth(width, Unit.PIXELS);
		} else {
			popupWindow.setWidthUndefined();
		}
		popupWindow.setCaption(caption);

		VerticalLayout layout = new VerticalLayout();	
		layout.setMargin(true);
		layout.addComponent(content);

		ConfirmationComponent confirmationComponent = new ConfirmationComponent(false) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onConfirm() {
				popupWindow.close();
				resultConsumer.accept(true);
			}
			@Override
			protected void onCancel() {
				popupWindow.close();
				resultConsumer.accept(false);
			}
		};
		confirmationComponent.getConfirmButton().setCaption(confirmCaption);
		confirmationComponent.getCancelButton().setCaption(cancelCaption);

		popupWindow.addCloseListener(new CloseListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void windowClose(CloseEvent e) {
				confirmationComponent.getCancelButton().click();
			}
		});

		layout.addComponent(confirmationComponent);
		layout.setComponentAlignment(confirmationComponent, Alignment.BOTTOM_RIGHT);
		layout.setSizeUndefined();
		layout.setSpacing(true);

		UI.getCurrent().addWindow(popupWindow);
		return popupWindow;
	}
	
	public static Window showDeleteConfirmationWindow(String content, Runnable callback) {
		Window popupWindow = VaadinUiUtil.createPopupWindow();	
		
		VerticalLayout deleteLayout = new VerticalLayout();
		deleteLayout.setMargin(true);
		deleteLayout.setSizeUndefined();
		deleteLayout.setSpacing(true);
	
		Label description = new Label(content);
		description.setWidth(100, Unit.PERCENTAGE);
		deleteLayout.addComponent(description);
		
		ConfirmationComponent deleteConfirmationComponent = new ConfirmationComponent(false) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onConfirm() {
				popupWindow.close();
				onDone();
				callback.run();
			}

			@Override
			protected void onCancel() {
				popupWindow.close();
			}
		};
		deleteConfirmationComponent.getConfirmButton().setCaption("Yes");
		deleteConfirmationComponent.getCancelButton().setCaption("No");
		deleteLayout.addComponent(deleteConfirmationComponent);
		deleteLayout.setComponentAlignment(deleteConfirmationComponent, Alignment.BOTTOM_RIGHT);
		
		popupWindow.setCaption("Confirm Deletion");
		popupWindow.setContent(deleteLayout);
		UI.getCurrent().addWindow(popupWindow);
		
		return popupWindow;
	}

}
