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

package de.symeda.sormas.app.component;

import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by Orson on 25/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class OnViewGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

	private final static int maxHeight = 96;
	private View view;

	public OnViewGlobalLayoutListener(View view) {
		this.view = view;
	}

	@Override
	public void onGlobalLayout() {
		if (view.getHeight() > maxHeight)
			view.getLayoutParams().height = maxHeight;
	}
}
