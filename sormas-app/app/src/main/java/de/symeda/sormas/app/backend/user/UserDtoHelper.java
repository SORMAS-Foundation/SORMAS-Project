package de.symeda.sormas.app.backend.user;

import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.region.Region;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class UserDtoHelper extends AdoDtoHelper<User, UserDto> {

    @Override
    public User create() {
        return new User();
    }

    @Override
    public void fillInnerFromDto(User ado, UserDto dto) {

        ado.setFirstName(dto.getFirstName());
        ado.setLastName(dto.getLastName());
        ado.setUserName(dto.getUserName());
        ado.setUserEmail(dto.getUserEmail());
        // TODO ...
    }
}
