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
