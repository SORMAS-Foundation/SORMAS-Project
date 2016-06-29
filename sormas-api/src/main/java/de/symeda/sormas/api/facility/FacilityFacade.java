package de.symeda.sormas.api.facility;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.ReferenceDto;

@Remote
public interface FacilityFacade {

    public abstract List<ReferenceDto> getAllAsReference();
}
