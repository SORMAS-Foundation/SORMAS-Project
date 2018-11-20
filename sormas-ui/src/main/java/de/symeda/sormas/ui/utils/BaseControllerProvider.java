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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.utils;

/**
 * Setzt die App-Id serverseitig, falls sie in diesem Request noch nicht gesetzt wurde.
 * Ich hätte gerne CDI statt ThreadLocal benutzt, aber der ServiceLocator funktioniert static.
 * Der BaseControllerProvider wird in der Session gehalten!
 * Er wird also innerhalb der Session zwischen allen Instanzen der Applikation (Tabs) geteilt;
 * Durch F5 wird er nicht neu geladen.
 * 
 * @author HReise, Martin Wahnschaffe
 */
public class BaseControllerProvider {

	private static ThreadLocal<BaseControllerProvider> controllerProviderThreadLocal = new ThreadLocal<>();


	protected static BaseControllerProvider get() {
		return controllerProviderThreadLocal.get();
	}

	/**
	 * Muss aufgerufen werden, wenn von Vaadin ein neuer Request gestartet wurde
	 * XXX Könnte z. B. über die Vaadin Session geregelt werden
	 * 
	 * @param appId
	 *            die id der Vaadin Applikation
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
