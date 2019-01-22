package de.symeda.sormas.api;

import java.io.Serializable;

import de.symeda.sormas.api.utils.PojoUrlParamConverter;

@SuppressWarnings("serial")
public abstract class BaseCriteria implements Serializable {
	
	public String toUrlParams() {
		return PojoUrlParamConverter.toUrlParams(this);
	}
	
	public void fromUrlParams(String urlParams) {
		PojoUrlParamConverter.fromUrlParams(this, urlParams);
	}
}
