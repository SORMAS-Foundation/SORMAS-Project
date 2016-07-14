package de.symeda.sormas.api.user;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.ReferenceDto;

@Remote
public interface UserFacade {

    public abstract List<ReferenceDto> getListAsReference(UserRole userRole);
    
    public abstract List<UserDto> getAll(UserRole role);
    
    public abstract UserDto getByUuid(String uuid);
    
    public abstract UserDto saveUser(UserDto dto);
    
    
}
