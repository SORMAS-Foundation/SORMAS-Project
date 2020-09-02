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
package de.symeda.sormas.ui.utils;

/**
 * Sets the app id on the server side if it has not yet been set in this request.
 * I would have liked to use CDI instead of ThreadLocal, but the ServiceLocator works static.
 * The BaseControllerProvider is held in the session!
 * So it is shared within the session between all instances of the application (tabs);
 * F5 does not reload it.
 */
public class BaseControllerProvider {

	private static ThreadLocal<BaseControllerProvider> controllerProviderThreadLocal = new ThreadLocal<>();

	protected static BaseControllerProvider get() {
		return controllerProviderThreadLocal.get();
	}

	/**
	 * Must be called if a new request was started by Vaadin.
	 * Could be controlled via the Vaadin session, for example.
	 */
	public static void requestStart(BaseControllerProvider controllerProvider) {
		controllerProviderThreadLocal.set(controllerProvider);
		controllerProvider.onRequestStart();
	}

	protected void onRequestStart() {

	}

	public static void requestEnd() {
		controllerProviderThreadLocal.remove();
	}
}
