package de.symeda.sormas.ui.utils;

import com.vaadin.data.Item;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DoneListener;

public class VaadinUiUtil {

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
}
