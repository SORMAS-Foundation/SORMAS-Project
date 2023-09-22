package de.symeda.sormas.api.environment;

import javax.ejb.Remote;

import de.symeda.sormas.api.CoreFacade;

import java.util.List;

@Remote
public interface EnvironmentFacade extends CoreFacade<EnvironmentDto, EnvironmentIndexDto, EnvironmentReferenceDto, EnvironmentCriteria> {

    List<String> getAllActiveUuids();
}
