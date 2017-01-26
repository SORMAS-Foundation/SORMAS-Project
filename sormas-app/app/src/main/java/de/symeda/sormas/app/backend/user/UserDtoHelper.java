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
    protected void fillInnerFromDto(User target, UserDto source) {

        target.setAktiv(source.isActive());
        target.setUserName(source.getUserName());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setUserEmail(source.getUserEmail());

        if (source.getUserRoles().size() > 0) {
            target.setUserRole(source.getUserRoles().iterator().next());
            if (source.getUserRoles().size() > 1) {
                Log.e(UserDtoHelper.class.getName(), "User should not have more than one role: " + source.toString());
            }
        }

        if (source.getRegion() != null) {
            target.setRegion(DatabaseHelper.getRegionDao().queryUuid(source.getRegion().getUuid()));
        } else {
            target.setRegion(null);
        }

        if (source.getDistrict() != null) {
            target.setDistrict(DatabaseHelper.getDistrictDao().queryUuid(source.getDistrict().getUuid()));
        } else {
            target.setDistrict(null);
        }
        if (source.getHealthFacility() != null) {
            target.setHealthFacility(DatabaseHelper.getFacilityDao().queryUuid(source.getHealthFacility().getUuid()));
        } else {
            target.setHealthFacility(null);
        }

        if (source.getAssociatedOfficer() != null) {
            target.setAssociatedOfficer(DatabaseHelper.getUserDao().queryUuid(source.getAssociatedOfficer().getUuid()));
        } else {
            target.setAssociatedOfficer(null);
        }

        target.setAddress(locationHelper.fillOrCreateFromDto(target.getAddress(), source.getAddress()));
        target.setPhone(source.getPhone());
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
