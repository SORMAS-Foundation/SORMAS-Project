package de.symeda.sormas.ui.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.Page;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DoneListener;

public class VaadinUiUtil {

    public enum NotificationType {
        HUMANIZED_MESSAGE, WARNING_MESSAGE, ERROR_MESSAGE, TRAY_NOTIFICATION;
    }
    
	/**
	 * Zeigt die Ã¼bergebene Komponente in einen Popup-Window an.
	 * @param content
	 * @return
	 */
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
	
	/**
	 * Zeigt die Ã¼bergebene Komponente in einen Popup-Window an.
	 * @param content
	 * @return
	 */
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
	
	//Diese Methoden sollen statt Notification#show() benutz werden.

    public static Notification showNotification(String caption) {
        Notification notification = new Notification(caption, null, Notification.Type.HUMANIZED_MESSAGE);
        notification.setDelayMsec(2000); //2s
		notification.show(Page.getCurrent());
		return notification;
    }

    public static Notification showNotification(String caption, NotificationType type) {
        Notification notification = new Notification(caption, null, convert(type));
        if (type == NotificationType.HUMANIZED_MESSAGE) {
            notification.setDelayMsec(2000); //2s
        }
		notification.show(Page.getCurrent());
		return notification;
    }

	public static Notification showNotification(String caption, String description, NotificationType type) {
        Notification notification = new Notification(caption, description, convert(type));
        if (type == NotificationType.HUMANIZED_MESSAGE) {
            notification.setDelayMsec(2000); //2s
        }
		notification.show(Page.getCurrent());
		return notification;
    }

	public static Notification showWarning(String caption, String description) {
        return showNotification(caption, description, NotificationType.WARNING_MESSAGE);
    }

	public static Notification showError(String caption, String description) {
        return showNotification(caption, description, NotificationType.ERROR_MESSAGE);
    }

    private static Type convert(NotificationType type) {
    	
    	if (type == null)
    		return null;
    	
    	switch (type) {
    	case HUMANIZED_MESSAGE: 
    		return Notification.Type.HUMANIZED_MESSAGE;
    	case WARNING_MESSAGE: 
    		return Notification.Type.WARNING_MESSAGE;
    	case ERROR_MESSAGE: 
    		return Notification.Type.ERROR_MESSAGE; 
    	case TRAY_NOTIFICATION: 
    		return Notification.Type.TRAY_NOTIFICATION;
    	default:
    		throw new RuntimeException("Unknown type " + type);
    	}
	}



	/**
	 * Setzt an Feldern ggf. den Input-Prompt.<br/>
	 * Verschedene Felder haben diese FnktionalitÃ¤t, 
	 * aber leider gibt es dafÃ¼r kein Interface...<br/>
	 * Betrifft z.Zt. 
	 * <ul>
	 * <li>AbstractTextField</li>
	 * <li>PopupDateField</li>
	 * <li>ComboBox</li>
	 * <li>AssigningListField</li>
	 * <li>TokenField</li>
	 * </ul>
	 * @return ob es geklappt hat
	 */
	public static boolean trySetInputPrompt(Field<?> field, String prompt) {
		try {
			Method ipMethod = field.getClass().getMethod("setInputPrompt", String.class);
			if ((ipMethod.getModifiers() | Method.PUBLIC) != 0) {
				ipMethod.invoke(field, prompt);
				return true;
			}
			return false;
			
		} catch (NoSuchMethodException | SecurityException e) {
			return false;
			
		} catch (IllegalAccessException  | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

//	/**
//	 * Setzt den escapeten descriptionText als Description.<br/> 
//	 * AbstractComponent.description wird als HTML angezeigt, damit kann es schnell zu HTML-Injection-Problemen kommen.
//	 * 
//	 * @param c
//	 * @param description
//	 */
//	public static void setDescriptionText(AbstractComponent c, String descriptionText) {
//		if (c == null) {
//			return;
//		}
//		if (descriptionText == null) {
//			c.setDescription(null);
//		} else {
//			c.setDescription(SafeHtmlUtils.htmlEscape(descriptionText));
//		}
//	}

	/**
	 * Setzt den Ã¼bergebenen {@code value} per {@link Converter} auf das {@link Field}.<br />
	 * Diese Methode ignoriert das nicht prÃ¼fbare Generic von {@code field}.
	 * 
	 * @param field
	 *            <ul>
	 *            <li>Darf nicht {@code null} sein.</li>
	 *            <li>Muss ein {@link AbstractField} sein, weil {@link AbstractField#setConvertedValue(Object)} benÃ¶tigt wird.</li>
	 *            </ul>
	 * @param value
	 */
	public static void setConvertedValue(Field<?> field, Object value) {

		if (field == null) {
			throw new IllegalArgumentException("field is null");
		} else if (field instanceof AbstractField<?>) {

			@SuppressWarnings("rawtypes")
			AbstractField rawTypeField = (AbstractField) field;
			rawTypeField.setConvertedValue(value);
		} else {
			throw new IllegalArgumentException("No AbstractField: " + field.getClass());
		}
	}

	/**
	 * Setzt den Ã¼bergebenen {@code value} auf das {@link Field}.<br />
	 * Diese Methode ignoriert das nicht prÃ¼fbare Generic von {@code field}.
	 * 
	 * @param property
	 *            Darf nicht {@code null} sein.
	 * @param value
	 */
	public static void setUnconvertedValue(Field<?> field, Object value) {

		setValue((Property<?>) field, value);
	}

	/**
	 * Setzt den Ã¼bergebenen {@code value} auf das {@link Field}.<br />
	 * Diese Methode ignoriert das nicht prÃ¼fbare Generic von {@code field}.
	 * 
	 * @deprecated FÃ¼r {@link Field}s besser bewusst {@link #setConvertedValue(Field, Object)} oder
	 *             {@link #setUnconvertedValue(Field, Object)} benutzen.
	 * @param property
	 *            Darf nicht {@code null} sein.
	 * @param value
	 */
	@Deprecated
	public static void setValue(Field<?> field, Object value) {

		setValue((Property<?>) field, value);
	}

	/**
	 * Setzt den Ã¼bergebenen {@code value} auf das {@link Property}.<br />
	 * Diese Methode ignoriert das nicht prÃ¼fbare Generic von {@code property}.<br />
	 * FÃ¼r {@link Field}s besser {@link #setConvertedValue(Field, Object)} benutzen.
	 * 
	 * @param property
	 *            Darf nicht {@code null} sein.
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public static void setValue(Property<?> property, Object value) {

		@SuppressWarnings("rawtypes")
		Property rawTypeProperty = property;
		rawTypeProperty.setValue(value);
	}
}
