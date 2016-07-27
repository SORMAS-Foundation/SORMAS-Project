package de.symeda.sormas.app.backend.user;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.user.UserRole;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
@Entity(name="userroles")
public class UserRoleToUser {

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @DatabaseField(foreign = true)
    private User user;

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return getUserRole().toString();
    }
}
