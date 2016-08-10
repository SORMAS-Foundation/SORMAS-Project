package de.symeda.sormas.app.backend.user;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;
import de.symeda.sormas.app.backend.region.Region;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class UserDtoHelper extends AdoDtoHelper<User, UserDto> {

    private LocationDtoHelper locationHelper = new LocationDtoHelper();

    @Override
    public User create() {
        return new User();
    }

    @Override
    public void fillInnerFromDto(User ado, UserDto dto) {

        ado.setAktiv(dto.isActive());
        ado.setUserName(dto.getUserName());
        ado.setFirstName(dto.getFirstName());
        ado.setLastName(dto.getLastName());
        ado.setUserEmail(dto.getUserEmail());

        if (dto.getUserRoles().size() > 0) {
            ado.setUserRole(dto.getUserRoles().iterator().next());
            if (dto.getUserRoles().size() > 1) {
                Log.e(UserDtoHelper.class.getName(), "User should not have more than one role: " + dto.toString());
            }
        }

        ado.setAddress(locationHelper.fillOrCreateFromDto(ado.getAddress(), dto.getAddress()));
        ado.setPhone(dto.getPhone());
    }
}
