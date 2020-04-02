package de.symeda.sormas.backend.user;

/**
 * The class CurrentUser.
 */
public class CurrentUser {

    private User user;

    public CurrentUser() {
    }

    public CurrentUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
