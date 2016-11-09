package de.symeda.sormas.app.backend.user;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
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
    public UserDto createDto() {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void preparePulledResult(List<UserDto> result) {
        Collections.sort(result, new Comparator<UserDto>() {
            @Override
            public int compare(UserDto lhs, UserDto rhs) {
                if (lhs.getAssociatedOfficer() == null && rhs.getAssociatedOfficer() != null) {
                    return -1;
                } else if (lhs.getAssociatedOfficer() != null && rhs.getAssociatedOfficer() == null) {
                    return 1;
                }
                return 0;
            }
        });
    }

    @Override
    protected void fillInnerFromDto(User ado, UserDto dto) {

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


        if (dto.getRegion() != null) {
            ado.setRegion(DatabaseHelper.getRegionDao().queryUuid(dto.getRegion().getUuid()));
        } else {
            ado.setRegion(null);
        }

        if (dto.getDistrict() != null) {
            ado.setDistrict(DatabaseHelper.getDistrictDao().queryUuid(dto.getDistrict().getUuid()));
        } else {
            ado.setDistrict(null);
        }


        if (dto.getAssociatedOfficer() != null) {
            ado.setAssociatedOfficer(DatabaseHelper.getUserDao().queryUuid(dto.getAssociatedOfficer().getUuid()));
        } else {
            ado.setAssociatedOfficer(null);
        }

        ado.setAddress(locationHelper.fillOrCreateFromDto(ado.getAddress(), dto.getAddress()));
        ado.setPhone(dto.getPhone());
    }

    @Override
    protected void fillInnerFromAdo(UserDto userDto, User user) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public static UserReferenceDto toReferenceDto(User ado) {
        if (ado == null) {
            return null;
        }
        UserReferenceDto dto = new UserReferenceDto();
        fillReferenceDto(dto, ado);
        return dto;
    }
}
