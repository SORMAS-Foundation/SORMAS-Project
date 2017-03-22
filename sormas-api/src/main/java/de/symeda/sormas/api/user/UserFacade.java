package de.symeda.sormas.api.user;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;

@Remote
public interface UserFacade {

    List<UserDto> getAll(UserRole... roles);
    
    UserDto getByUuid(String uuid);
    
    UserDto saveUser(UserDto dto);

    boolean isLoginUnique(String uuid, String userName);
    
    String resetPassword(String uuid);

	List<UserDto> getAllAfter(Date date);

	UserDto getByUserName(String userName);

	UserReferenceDto getByUserNameAsReference(String userName);

	List<UserReferenceDto> getAllAfterAsReference(Date date);

	List<UserReferenceDto> getAssignableUsers(UserReferenceDto assigningUser, UserRole... assignableRoles);

	List<UserReferenceDto> getAssignableUsersByCase(CaseReferenceDto caze, UserRole... assignableRoles);
	
	List<UserReferenceDto> getAssignableUsersByContact(ContactReferenceDto contact, UserRole... assignableRoles);
	
	List<UserReferenceDto> getAssignableUsersByEvent(EventReferenceDto event, UserRole... assignableRoles);
}
