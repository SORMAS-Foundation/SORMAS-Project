package de.symeda.sormas.api.error.templates;

import de.symeda.sormas.api.error.implementations.ExceptionDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ExceptionsTemplate {
    public final static String TOKEN_IS_NOT_VALID_MSG = "Token is not valid";
    public final static String RECORD_NOT_FOUND_MSG = "Record not found";
    public final static String RECORD_ALREADY_EXISTS = "Record already exists";
    public final static String PASS_NOT_MATCHING = "Password not match";

    public final static String USER_UNAUTHORISED_EXCEPTION = "User %s is unauthorized to access this profile!";
    public final static String USER_HAS_NO_ACCESS_TO_CHANGE_DETAILS = "This user doesn't have access to change details about this user.";

    public final static String USER_PASSWORDS_MISMATCH_OLD_CURRENT = "Old password doesn't match with current password!";
    public final static String USERNAME_NOT_FOUND = "No such email found in the db";

    public final static String USER_PASSWORDS_MISMATCH_CURRENT_CONFIRM = "Password doesn't match with confirm password!";
    public final static String USERNAME_NOT_FOUND_OR_NO_LONGER_ACTIVE = "Username not found or no longer active";

    public final static String BAD_CREDENTIALS = "Wrong Password!";

    public final static String SUPER_ADMIN_USER_NOT_ALLOWED_TO_CHANGE_USER_ACCESS = "You are not allowed to change this users access";

    public final static String ROLE_WAS_NOT_FOUND = "Role was not found";
    public final static String ROLE_HAS_USERS_ASSIGNED_TO_IT = "Role has users assigned to it!";

    public static Map<String, ExceptionDto> errorMap;


    static {
        Map<String, ExceptionDto> error = new HashMap<>();
        error.put( TOKEN_IS_NOT_VALID_MSG, new ExceptionDto( 1, new ArrayList<>() ) );
        error.put( RECORD_NOT_FOUND_MSG, new ExceptionDto( 2, new ArrayList<>() ) );
        error.put( RECORD_ALREADY_EXISTS, new ExceptionDto( 3, new ArrayList<>() ) );
        error.put( PASS_NOT_MATCHING, new ExceptionDto( 4, new ArrayList<>() ) );

        error.put( USER_UNAUTHORISED_EXCEPTION, new ExceptionDto( 5, new ArrayList<>() ) );//1 parameter
        error.put( USER_HAS_NO_ACCESS_TO_CHANGE_DETAILS, new ExceptionDto( 6, new ArrayList<>() ) );

        error.put( USER_PASSWORDS_MISMATCH_OLD_CURRENT, new ExceptionDto( 7, new ArrayList<>() ) );
        error.put( USERNAME_NOT_FOUND, new ExceptionDto( 8, new ArrayList<>() ) );

        error.put( USER_PASSWORDS_MISMATCH_CURRENT_CONFIRM, new ExceptionDto( 9, new ArrayList<>() ) );
        error.put( USERNAME_NOT_FOUND_OR_NO_LONGER_ACTIVE, new ExceptionDto( 10, new ArrayList<>() ) );

        error.put( BAD_CREDENTIALS, new ExceptionDto( 12, new ArrayList<>() ) );
        error.put( SUPER_ADMIN_USER_NOT_ALLOWED_TO_CHANGE_USER_ACCESS, new ExceptionDto( 15, new ArrayList<>() ) );

        error.put( ROLE_WAS_NOT_FOUND, new ExceptionDto( 16, new ArrayList<>() ) );
        error.put( ROLE_HAS_USERS_ASSIGNED_TO_IT, new ExceptionDto( 17, new ArrayList<>() ) );

        errorMap = Collections.unmodifiableMap(error);
    }
}
