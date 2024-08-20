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
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        boolean isLengthValid = password.length() >= 8;
        boolean hasDigit = hasDigits(password);
        boolean hasCapitalLetter = hasCapitalLetter(password);
        boolean hasLowercaseLetter = hasLowercaseLetter(password);

        return isLengthValid && hasDigit && hasCapitalLetter && hasLowercaseLetter;
    }

    private static boolean hasDigits(String password) {
        return password.chars().anyMatch(Character::isDigit);
    }

    private static boolean hasCapitalLetter(String password) {
        return password.chars().anyMatch(Character::isUpperCase);
    }

    private static boolean hasLowercaseLetter(String password) {
        return password.chars().anyMatch(Character::isLowerCase);
    }

}
