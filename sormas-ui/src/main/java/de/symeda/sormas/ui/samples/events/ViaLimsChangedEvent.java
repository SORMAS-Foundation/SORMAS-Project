package de.symeda.sormas.ui.samples.events;

public class ViaLimsChangedEvent {

	private final boolean viaLims;

	public ViaLimsChangedEvent(boolean viaLims) {
		this.viaLims = viaLims;
	}

	public boolean isViaLims() {
		return viaLims;
	}
}
