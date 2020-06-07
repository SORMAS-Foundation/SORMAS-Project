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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ViewModelProvider {

	private Map<Class<?>, Object> viewModels = new HashMap<Class<?>, Object>();

	public <M extends Object> M get(Class<M> modelClass) {
		return get(modelClass, null);
	}

	@SuppressWarnings("unchecked")
	public <M extends Object> M get(Class<M> modelClass, M defaultModel) {

		if (!viewModels.containsKey(modelClass)) {
			try {
				if (defaultModel != null) {
					viewModels.put(modelClass, defaultModel);
				} else {
					viewModels.put(modelClass, modelClass.newInstance());
				}
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return (M) viewModels.get(modelClass);
	}

	public <M extends Object> void remove(Class<M> modelClass) {

		if (viewModels.containsKey(modelClass)) {
			viewModels.remove(modelClass);
		}
	}

	public <M extends Object> boolean has(Class<M> modelClass) {
		return viewModels.containsKey(modelClass);
	}

	public Collection<Object> getAll() {
		return viewModels.values();
	}
}
