package de.symeda.sormas.backend.util;

public class PasswordValidator {

    private PasswordValidator() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String checkPasswordStrength(String password) {
        boolean strongPassword = isStrongPassword(password);

        if (strongPassword) {
            return "Password is Strong";
        } else {
            return "Password is Weak";
        }
    }

    public static boolean isStrongPassword(String password) {
        return password.length() >= 8 && hasDigits(password) && hasCapitalLetter(password);
    }

    private static boolean hasDigits(String password) {
        return password.chars().anyMatch(Character::isDigit);
    }

    private static boolean hasCapitalLetter(String password) {
        return password.chars().anyMatch(Character::isUpperCase);
    }

}
