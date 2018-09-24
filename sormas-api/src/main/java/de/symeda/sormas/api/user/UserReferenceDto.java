package de.symeda.sormas.api.user;

import java.util.Set;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class UserReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -8558187171374254398L;

	public UserReferenceDto() {
		
	}
	
	public UserReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public UserReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}
	
	public UserReferenceDto(String uuid, String firstName, String lastName, Set<UserRole> userRoles) {
		setUuid(uuid);
		setCaption(buildCaption(firstName, lastName, userRoles));
	}
	
	public static String buildCaption(String firstName, String lastName, Set<UserRole> userRoles) {
		StringBuilder result = new StringBuilder();
		result.append(DataHelper.toStringNullable(firstName))
			.append(" ").append(DataHelper.toStringNullable(lastName).toUpperCase());
		boolean first = true;
		if (userRoles != null) {
			for (UserRole userRole : userRoles) {
				if (first) {
					result.append(" - ");
					first = false;
				} else {
					result.append(", ");
				}
				result.append(userRole.toShortString());
			}
		}
		return result.toString();
	}
}
