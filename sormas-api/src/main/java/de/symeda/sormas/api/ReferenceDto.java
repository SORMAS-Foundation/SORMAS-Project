package de.symeda.sormas.api;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class ReferenceDto implements Serializable, HasUuid {

	public static final String CAPTION = "caption";
	
	private String uuid;
	private String caption;
	
	public ReferenceDto() { }
	
	public ReferenceDto(String uuid) {
		this.uuid = uuid;
	}
	
	public ReferenceDto(String uuid, String caption) {
		this.uuid = uuid;
		this.caption = caption;
	}
	
	@Override
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	@Override
	public String toString() {
		return getCaption();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}

		if (getUuid() != null && o instanceof HasUuid
				&& ((HasUuid) o).getUuid() != null) {
			// this works, because we are using UUIDs
			HasUuid ado = (HasUuid) o;
			return getUuid().equals(ado.getUuid());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		if (getUuid() != null) {
			return getUuid().hashCode();
		}
		return 0;
	}

}
