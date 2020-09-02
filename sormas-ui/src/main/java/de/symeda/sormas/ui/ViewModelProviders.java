/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.vaadin.navigator.View;
import com.vaadin.ui.UI;

public class ViewModelProviders {

	private Map<Class<?>, ViewModelProvider> viewModelProviders = new HashMap<Class<?>, ViewModelProvider>();

	public static <V extends View> ViewModelProvider of(Class<V> viewClass) {

		ViewModelProviders providers = getCurrent();
		if (!providers.viewModelProviders.containsKey(viewClass)) {
			providers.viewModelProviders.put(viewClass, new ViewModelProvider());
		}
		return providers.viewModelProviders.get(viewClass);
	}

	/**
	 * Gets the provider for the current UI belongs.
	 *
	 * @see UI#getCurrent()
	 *
	 * @return the current instance if available, otherwise <code>null</code>
	 */
	@NotNull
	private static ViewModelProviders getCurrent() {

		UI currentUI = UI.getCurrent();
		if (currentUI instanceof HasViewModelProviders) {
			return ((HasViewModelProviders) currentUI).getViewModelProviders();
		}
		throw new IllegalStateException("UI.getCurrent did not an instance that implements HasViewModelProviders");
	}

	public interface HasViewModelProviders {

		@NotNull
		ViewModelProviders getViewModelProviders();
	}
}
