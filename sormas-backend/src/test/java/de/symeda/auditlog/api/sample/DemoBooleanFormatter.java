package de.symeda.auditlog.api.sample;

import de.symeda.auditlog.api.value.format.ValueFormatter;

/**
 * Demo-Klasse zur beispielhaften Formatierung eines Boolean abweichend von {@link String#valueOf(boolean)}.
 * 
 * @author Oliver Milke
 * @since 08.04.2016
 */
public class DemoBooleanFormatter implements ValueFormatter<Boolean> {

	@Override
	public String format(Boolean value) {

		return "Das ist der Nachweis dafür, dass dieser Formatter auch tatsächlich genutzt wird, weil kein anderer Formatter eine so absurde Boolean -> String Konvertierung anwendet.";
	}
}