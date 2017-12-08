package de.symeda.sormas.api.sample;

import java.io.Serializable;

public class DashboardSampleDto implements Serializable {

	private static final long serialVersionUID = -6982667922992479551L;
	
	public static final String SHIPPED = "shipped";
	public static final String RECEIVED = "received";
	
	private boolean shipped;
	private boolean received;

	public DashboardSampleDto(boolean shipped, boolean received) {
		this.shipped = shipped;
		this.received = received;
	}
	
	public boolean isShipped() {
		return shipped;
	}

	public void setShipped(boolean shipped) {
		this.shipped = shipped;
	}

	public boolean isReceived() {
		return received;
	}

	public void setReceived(boolean received) {
		this.received = received;
	}
}
