package de.symeda.sormas.ui.utils;

import com.vaadin.server.Resource;
import com.vaadin.ui.MenuBar;
import de.symeda.sormas.api.i18n.I18nProperties;

public class MenuBarHelper {
	public static MenuBar createDropDown(String captionKey, MenuBarItem... items){
		MenuBar menuBar = new MenuBar();
		menuBar.setId(captionKey);

		MenuBar.MenuItem mainItem = menuBar.addItem(I18nProperties.getCaption(captionKey), null);

		for (MenuBarItem item : items) {
			MenuBar.MenuItem dropDownItem = mainItem.addItem(item.caption, item.icon, item.command);
			dropDownItem.setVisible(item.visible);
		}

		return menuBar;
	}

	public static class MenuBarItem {
		private String caption;
		private Resource icon;
		private MenuBar.Command command;
		private boolean visible = true;

		public MenuBarItem(String caption, Resource icon, MenuBar.Command command) {
			this.caption = caption;
			this.icon = icon;
			this.command = command;
		}

		public MenuBarItem(String caption, Resource icon, MenuBar.Command command, Boolean visible) {
			this.caption = caption;
			this.icon = icon;
			this.command = command;
			this.visible = visible;
		}
	}
}
