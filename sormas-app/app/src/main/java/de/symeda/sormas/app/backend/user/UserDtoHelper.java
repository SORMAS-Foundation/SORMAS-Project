package de.symeda.sormas.app.backend.user;

import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class UserDtoHelper extends AdoDtoHelper<User, UserDto> {

    private LocationDtoHelper locationHelper = new LocationDtoHelper();

    @Override
    protected Class<User> getAdoClass() {
        return User.class;
    }

    @Override
    protected Class<UserDto> getDtoClass() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Call<List<UserDto>> pullAllSince(long since) {
        return RetroProvider.getUserFacade().pullAllSince(since);
    }

    @Override
    protected Call<Long> pushAll(List<UserDto> userDtos) {
        // TODO
        throw new UnsupportedOperationException("Can't change users in app");
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

        target.setRegion(DatabaseHelper.getRegionDao().getByReferenceDto(source.getRegion()));
        target.setDistrict(DatabaseHelper.getDistrictDao().getByReferenceDto(source.getDistrict()));
        target.setHealthFacility(DatabaseHelper.getFacilityDao().getByReferenceDto(source.getHealthFacility()));

        target.setAssociatedOfficer(DatabaseHelper.getUserDao().getByReferenceDto(source.getAssociatedOfficer()));

        target.setAddress(locationHelper.fillOrCreateFromDto(target.getAddress(), source.getAddress()));
        target.setPhone(source.getPhone());
    }

    @Override
    protected void fillInnerFromAdo(UserDto userDto, User user) {
        // TODO
        throw new UnsupportedOperationException("Can't change users in app");
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
