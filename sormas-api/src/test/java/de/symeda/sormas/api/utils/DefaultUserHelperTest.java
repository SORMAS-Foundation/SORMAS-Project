package de.symeda.sormas.api.utils;

import de.symeda.sormas.api.user.UserDto;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DefaultUserHelperTest {

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASS = "sadmin";
    private static final String DEFAUTL_SURV_SUP_USER_PASS = "SurvSup";
    private static final String DEFAUTL_CASE_SUP_USER_PASS = "CaseSup";
    private static final String DEFAUTL_CONT_SUP_USER_PASS = "ContSup";
    private static final String DEFAUTL_POE_SUP_USER_PASS = "PoeSup";
    private static final String DEFAUTL_LAB_OFF_USER_PASS = "LabOff";
    private static final String DEFAUTL_EVE_OFF_USER_PASS = "EveOff";
    private static final String DEFAUTL_NAT_USER_USER_PASS = "NatUser";
    private static final String DEFAUTL_NAT_CLIN_USER_PASS = "NatClin";
    private static final String DEFAUTL_SURV_OFF_USER_PASS = "SurvOff";
    private static final String DEFAUTL_HOSP_INF_USER_PASS = "HospInf";
    private static final String DEFAUTL_POE_INF_USER_PASS = "PoeInf";

    private static final int COUNT_OF_DEFAULT_ACCOUNTS = 12;

    private static final Map<String, String> DEFAULT_USERS  = new HashMap<>() {{
        put(DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASS);
        put(DEFAUTL_SURV_SUP_USER_PASS, DEFAUTL_SURV_SUP_USER_PASS);
        put(DEFAUTL_CASE_SUP_USER_PASS, DEFAUTL_CASE_SUP_USER_PASS);
        put(DEFAUTL_CONT_SUP_USER_PASS, DEFAUTL_CONT_SUP_USER_PASS);
        put(DEFAUTL_POE_SUP_USER_PASS, DEFAUTL_POE_SUP_USER_PASS);
        put(DEFAUTL_LAB_OFF_USER_PASS, DEFAUTL_LAB_OFF_USER_PASS);
        put(DEFAUTL_EVE_OFF_USER_PASS, DEFAUTL_EVE_OFF_USER_PASS);
        put(DEFAUTL_NAT_USER_USER_PASS, DEFAUTL_NAT_USER_USER_PASS);
        put(DEFAUTL_NAT_CLIN_USER_PASS, DEFAUTL_NAT_CLIN_USER_PASS);
        put(DEFAUTL_SURV_OFF_USER_PASS, DEFAUTL_SURV_OFF_USER_PASS);
        put(DEFAUTL_HOSP_INF_USER_PASS, DEFAUTL_HOSP_INF_USER_PASS);
        put(DEFAUTL_POE_INF_USER_PASS, DEFAUTL_POE_INF_USER_PASS);
    }};

    @Test
    public void isDefaultUser() {
        for (String defaultUser : DEFAULT_USERS.keySet()) {
            assertTrue(DefaultUserHelper.isDefaultUser(defaultUser));
        }
    }

    @Test
    public void getDefaultPassword() {
        for (String defaultUser : DEFAULT_USERS.keySet()) {
            assertEquals(DEFAULT_USERS.get(defaultUser), DefaultUserHelper.getDefaultPassword(defaultUser).orElse(""));
        }
    }

    private void testUsesDefaultPasswordHelper(String username, String defaultPassword) {
        String seed = UUID.randomUUID().toString();
        String randomPass = UUID.randomUUID().toString();
        assertTrue(DefaultUserHelper.usesDefaultPassword(username, PasswordHelper.encodePassword(defaultPassword, seed), seed));
        assertFalse(DefaultUserHelper.usesDefaultPassword(username, PasswordHelper.encodePassword(randomPass, seed), seed));
    }

    @Test
    public void usesDefaultPassword() {
        for (String defaultUser : DEFAULT_USERS.keySet()) {
            testUsesDefaultPasswordHelper(defaultUser, DEFAULT_USERS.get(defaultUser));
        }
    }

    @Test
    public void currentUserUsesDefaultPassword() {
        List<UserDto> defaultDtos = new ArrayList<>();
        UserDto admin = new UserDto();
        admin.setUserName("admin");
        defaultDtos.add(admin);
        UserDto randomUser = new UserDto();
        randomUser.setUserName(UUID.randomUUID().toString());

        assertTrue(DefaultUserHelper.currentUserUsesDefaultPassword(defaultDtos, admin));
        assertFalse(DefaultUserHelper.currentUserUsesDefaultPassword(defaultDtos, randomUser));
        defaultDtos.remove(admin);
        assertFalse(DefaultUserHelper.currentUserUsesDefaultPassword(defaultDtos, admin));
    }

    @Test
    public void otherUsersWithDefaultPassword() {
        List<UserDto> defaultDtos = new ArrayList<>();
        UserDto admin = new UserDto();
        admin.setUserName("admin");
        defaultDtos.add(admin);
        UserDto randomUser = new UserDto();
        randomUser.setUserName(UUID.randomUUID().toString());

        assertTrue(DefaultUserHelper.otherUsersWithDefaultPassword(defaultDtos, randomUser));
        assertFalse(DefaultUserHelper.otherUsersWithDefaultPassword(defaultDtos, admin));
        defaultDtos.add(randomUser);
        assertTrue(DefaultUserHelper.otherUsersWithDefaultPassword(defaultDtos, admin));
        assertTrue(DefaultUserHelper.otherUsersWithDefaultPassword(defaultDtos, randomUser));
        assertFalse(DefaultUserHelper.otherUsersWithDefaultPassword(new ArrayList<>(), admin));
    }

    @Test
    public void getDefaultUserNames() {
        assertEquals(COUNT_OF_DEFAULT_ACCOUNTS, DefaultUserHelper.getDefaultUserNames().size());
        Set<String> result = DefaultUserHelper.getDefaultUserNames();
        for (String defaultUser : DEFAULT_USERS.keySet()) {
            assertTrue(result.contains(defaultUser));
        }
    }
}