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

package de.symeda.sormas.app.core;

import android.content.res.Resources;

/**
 * Created by Orson on 03/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class BoolResult {

	private final int mValue;
	private final boolean mSuccess;
	private final String mMessage;
	private final int mMessageResId;

	public static final BoolResult TRUE = new BoolResult(true, "");
	public static final BoolResult FALSE = new BoolResult(false, "");

	public BoolResult(boolean success, String message) {
		this.mValue = (success) ? 1 : 0;
		this.mSuccess = success;
		this.mMessage = message;
		this.mMessageResId = -1;
	}

	public BoolResult(boolean success, int messageResId) {
		this.mValue = (success) ? 1 : 0;
		this.mSuccess = success;
		this.mMessage = "";
		this.mMessageResId = messageResId;
	}

	public boolean isSuccess() {
		return mSuccess;
	}

	public boolean isFailed() {
		return !mSuccess;
	}

	public String getMessage() {
		return mMessage;
	}

	public String getMessage(Resources resources) {
		return resources.getString(mMessageResId);
	}

	/*
	 * public void setStatus(BoolResult status) {
	 * this.mSuccess = status.isSuccess();
	 * this.mMessage = status.getMessage();
	 * }
	 */

	// <editor-fold defaultstate="collapsed" desc="Overrides">

	@Override
	public int hashCode() {
		return mValue + 37 * mValue;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BoolResult)) {
			return false;
		}
		BoolResult other = (BoolResult) obj;
		return mValue == other.mValue;
	}

	@Override
	public String toString() {
		return mMessage;
	}

	// </editor-fold>
}
