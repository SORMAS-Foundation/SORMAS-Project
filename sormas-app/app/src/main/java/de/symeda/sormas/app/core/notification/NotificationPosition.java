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

package de.symeda.sormas.app.core.notification;

/**
 * Created by Orson on 02/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public abstract class NotificationPosition {

	private final int value;
	private final String displayName;

	public static final NotificationPosition TOP = new NotificationTop();
	public static final NotificationPosition BOTTOM = new NotificationBottom();

	protected NotificationPosition(int value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	private static class NotificationTop extends NotificationPosition {

		public NotificationTop() {
			super(0, "Top");
		}
	}

	private static class NotificationBottom extends NotificationPosition {

		public NotificationBottom() {
			super(1, "Bottom");
		}
	}

	// <editor-fold defaultstate="collapsed" desc="Overrides">

	@Override
	public int hashCode() {
		return value + 37 * value;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NotificationPosition)) {
			return false;
		}
		NotificationPosition other = (NotificationPosition) obj;
		return value == other.value;
	}

	@Override
	public String toString() {
		return displayName;
	}

	// </editor-fold>
}
