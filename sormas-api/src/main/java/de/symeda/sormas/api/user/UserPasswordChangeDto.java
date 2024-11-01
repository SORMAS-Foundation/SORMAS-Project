package de.symeda.sormas.api.user;

import java.io.Serializable;

public class UserPasswordChangeDto implements Serializable {
    private static final long serialVersionUID = 6269655187128160377L;

    private String uuid;
    private String newPassword;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public UserPasswordChangeDto(String uuid, String newPassword) {
        this.uuid = uuid;
        this.newPassword = newPassword;
    }

    public UserPasswordChangeDto(){}
}
