package de.symeda.sormas.api.region;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.ReferenceDto;

@Remote
public interface RegionFacade {

    public abstract List<ReferenceDto> getAllAsReference();
}
