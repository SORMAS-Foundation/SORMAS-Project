package de.symeda.sormas.backend.patch.alias;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.survey.alias.PathAliasFacade;

@Stateless(name = "PathAliasFacade")
public class PathAliasFacadeEjb implements PathAliasFacade {

	@Inject
	private PathAliasHelper pathAliasHelper;

	@Override
	public String toAliasPath(String path) {
		return pathAliasHelper.toAliasPath(path);
	}

	@LocalBean
	@Stateless
	public static class PathAliasFacadeEjbLocal extends PathAliasFacadeEjb {

	}
}
