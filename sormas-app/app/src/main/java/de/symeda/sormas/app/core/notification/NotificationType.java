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

import de.symeda.sormas.app.R;

/**
 * Created by Orson on 25/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public abstract class NotificationType {

	private final int value;
	private final String displayName;

	public static final NotificationType INFO = new InfoNotification();
	public static final NotificationType SUCCESS = new SuccessNotification();
	public static final NotificationType ERROR = new ErrorNotification();
	public static final NotificationType WARNING = new WarningNotification();

	protected NotificationType(int value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	public abstract int getBackgroundColor();

	public abstract int getTextColor();

	public abstract int getInverseBackgroundColor();

	public abstract int getInverseTextColor();

	private static class InfoNotification extends NotificationType {

		public InfoNotification() {
			super(0, "Info");
		}

		@Override
		public int getInverseBackgroundColor() {
			return R.color.infoBackground;
		}

		@Override
		public int getInverseTextColor() {
			return R.color.infoTextColor;
		}

		@Override
		public int getBackgroundColor() {
			//return R.color.infoBackground;
			return R.color.lightestBlue;
		}

		@Override
		public int getTextColor() {
			//return R.color.infoTextColor;
			return R.color.infoBackground;
		}
	}

	private static class SuccessNotification extends NotificationType {

		public SuccessNotification() {
			super(1, "Success");
		}

		@Override
		public int getInverseBackgroundColor() {
			return R.color.lightestBlue;
		}

		@Override
		public int getInverseTextColor() {
			return R.color.successBackground;
		}

		@Override
		public int getBackgroundColor() {
			return R.color.successBackground;
		}

		@Override
		public int getTextColor() {
			return R.color.successTextColor;
		}
	}

	private static class ErrorNotification extends NotificationType {

		public ErrorNotification() {
			super(2, "Error");
		}

		@Override
		public int getInverseBackgroundColor() {
			return R.color.lightestBlue;
		}

		@Override
		public int getInverseTextColor() {
			return R.color.errorBackground;
		}

		@Override
		public int getBackgroundColor() {
			return R.color.errorBackground;
		}

		@Override
		public int getTextColor() {
			return R.color.errorTextColor;
		}
	}

	private static class WarningNotification extends NotificationType {

		public WarningNotification() {
			super(3, "Warning");
		}

		@Override
		public int getInverseBackgroundColor() {
			return R.color.lightestBlue;
		}

		@Override
		public int getInverseTextColor() {
			return R.color.warningBackground;
		}

		@Override
		public int getBackgroundColor() {
			return R.color.warningBackground;
		}

		@Override
		public int getTextColor() {
			return R.color.warningTextColor;
		}
	}

	// <editor-fold defaultstate="collapsed" desc="Overrides">

	@Override
	public int hashCode() {
		return value + 37 * value;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NotificationType)) {
			return false;
		}
		NotificationType other = (NotificationType) obj;
		return value == other.value;
	}

	@Override
	public String toString() {
		return displayName;
	}

	// </editor-fold>
}
