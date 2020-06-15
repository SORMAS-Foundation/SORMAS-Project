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

package de.symeda.sormas.app.component.dialog;

import android.graphics.drawable.Drawable;

public class DialogViewConfig {

	private String heading;
	private String subHeading;
	private String positiveButtonText;
	private String negativeButtonText;
	private String deleteButtonText;
	private boolean hideHeadlineSeparator;
	private Drawable positiveButtonIcon;
	private Drawable negativeButtonIcon;

	public DialogViewConfig(String heading) {
		this.heading = heading;
	}

	public DialogViewConfig(
		String heading,
		String subHeading,
		String positiveButtonText,
		String negativeButtonText,
		String deleteButtonText,
		Drawable positiveButtonIcon,
		Drawable negativeButtonIcon) {
		this.heading = heading;
		this.subHeading = subHeading;
		this.positiveButtonText = positiveButtonText;
		this.negativeButtonText = negativeButtonText;
		this.deleteButtonText = deleteButtonText;
		this.positiveButtonIcon = positiveButtonIcon;
		this.negativeButtonIcon = negativeButtonIcon;
	}

	public String getHeading() {
		return heading;
	}

	public String getSubHeading() {
		return subHeading;
	}

	public void setSubHeading(String subHeading) {
		this.subHeading = subHeading;
	}

	public String getPositiveButtonText() {
		return positiveButtonText;
	}

	public void setPositiveButtonText(String positiveButtonText) {
		this.positiveButtonText = positiveButtonText;
	}

	public String getNegativeButtonText() {
		return negativeButtonText;
	}

	public void setNegativeButtonText(String negativeButtonText) {
		this.negativeButtonText = negativeButtonText;
	}

	public String getDeleteButtonText() {
		return deleteButtonText;
	}

	public boolean isHideHeadlineSeparator() {
		return hideHeadlineSeparator;
	}

	public void setHideHeadlineSeparator(boolean hideHeadlineSeparator) {
		this.hideHeadlineSeparator = hideHeadlineSeparator;
	}

	public Drawable getPositiveButtonIcon() {
		return positiveButtonIcon;
	}

	public Drawable getNegativeButtonIcon() {
		return negativeButtonIcon;
	}
}
