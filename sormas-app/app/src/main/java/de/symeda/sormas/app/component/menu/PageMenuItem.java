/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.component.menu;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.enumeration.StatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;

public class PageMenuItem {

	private int position;
//    private int notificationCount;
	private int iconResourceId;
	private String title;
	private String description;
	private boolean active;

	public static List<PageMenuItem> fromEnum(Enum[] values, Context context) {
		List<PageMenuItem> menuItems = new ArrayList<>();
		int position = 0;

		for (Enum value : values) {
			if (value == null) {
				menuItems.add(
					new PageMenuItem(
						position++,
						context.getResources().getString(R.string.all),
						context.getResources().getString(R.string.all),
						R.drawable.ic_view_comfy_black_24dp,
						false));
			} else {
				StatusElaborator elaborator = StatusElaboratorFactory.getElaborator(value);
				menuItems
					.add(new PageMenuItem(position++, elaborator.getFriendlyName(context), value.toString(), elaborator.getIconResourceId(), false));
			}
		}

		return menuItems;
	}

//    public PageMenuItem(int position, String title, String description, int iconResourceId, int notificationCount, boolean active) {
//        this.position = position;
//        this.notificationCount = notificationCount;
//        this.iconResourceId = iconResourceId;
//        this.title = title;
//        this.description = description;
//        this.active = active;
//    }

	public PageMenuItem(int position, String title, String description, int iconResourceId, boolean active) {
		this.position = position;
//        this.notificationCount = 0;
		this.iconResourceId = iconResourceId;
		this.title = title;
		this.description = description;
		this.active = active;
	}

//    public void setNotificationCount(int notificationCount) {
//        this.notificationCount = notificationCount;
//    }

	public void setIcon(int iconResourceId) {
		this.iconResourceId = iconResourceId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getIconResourceId() {
		return this.iconResourceId;
	}

	public String getTitle() {
		return this.title;
	}

	public String getDescription() {
		return this.description;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
