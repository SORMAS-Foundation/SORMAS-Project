package de.symeda.sormas.api;

public enum Disease {
	AVIAN_INFLUENCA,
	CHOLERA,
	CSM,
	DENGUE,
	EVD,
	LASSA,
	MEASLES,
	MONKEYPOX,
	PLAGUE,
	YELLOW_FEVER,
	OTHER
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
	
	public String toShortString() {
		return I18nProperties.getEnumCaption(this, "Short");
	}
	
	public String getName() {
		return this.name();
	}
	
	public boolean isSupportingOutbreakMode() {
		switch(this) {
		case CSM:
			return true;
		default:
			return false;		
		}
	}
}
