package de.symeda.sormas.api.survey.alias;

import javax.ejb.Remote;

/**
 * Alias can be (but not exclusively) used for Field ids.
 */
@Remote
public interface PathAliasFacade {

	/**
	 * Makes it more "readable" by shortening physical paths into aliases per example FieldIds.
	 * 
	 * @param path
	 *            that may (or may not) contain physical paths.
	 * @return shortened path with aliases.
	 */
	String fetchAliasPath(String path);
}
