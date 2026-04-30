package de.symeda.sormas.api.survey.alias;

import javax.ejb.Remote;

@Remote
public interface PathAliasFacade {

	/**
	 * Makes it more "readable" by shortening physical paths into aliases per example FieldIds.
	 * 
	 * @param path
	 *            that may (or may not) contain physical paths.
	 * @return shortened path.
	 */
	String fetchAliasPath(String path);
}
