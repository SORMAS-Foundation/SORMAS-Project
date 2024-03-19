package de.symeda.sormas.backend.util;

public class PasswordValidator {

    public static String  checkPasswordStrength(String password) {
        // Define your password strength rules here
        boolean strongPassword = isStrongPassword(password);
        boolean moderatePassword = isModeratePassword(password);

        if (strongPassword) {
            return "Password Strength is Strong";
        } else if (moderatePassword) {
            return "Password Strength is Moderate";
        } else {
            return "Password Strength is Weak";
        }
    }

    private static boolean isStrongPassword(String password) {
        // Define strong password criteria here
        return password.length() >= 10 && hasTwoDigits(password) && hasTwoSpecialCharacters(password);
    }

    private static boolean isModeratePassword(String password) {
        // Define moderate password criteria here
        return password.length() >= 8 && hasDigits(password) && hasSpecialCharacters(password);
    }

    private static boolean hasDigits(String password) {
        return password.matches(".*\\d.*");
    }

    private static boolean hasSpecialCharacters(String password) {
        return password.matches(".*[^a-zA-Z0-9 ].*");
    }

    private static boolean hasTwoDigits(String password) {
        return password.replaceAll("\\D", "").length() >= 2;
    }

    private static boolean hasTwoSpecialCharacters(String password) {
        return password.replaceAll("[\\w\\s]", "").length() >= 2;
    }
}
