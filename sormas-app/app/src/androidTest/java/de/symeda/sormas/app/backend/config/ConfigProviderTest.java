package de.symeda.sormas.app.backend.config;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ConfigProviderTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void setCurrentAppDownloadId() {
        ConfigProvider.setCurrentAppDownloadId(null);
        assertNull(ConfigProvider.getCurrentAppDownloadId());
        ConfigProvider.setCurrentAppDownloadId(2l);
        assertThat(ConfigProvider.getCurrentAppDownloadId(), is(2l));
    }

    @Test
    public void setUsernameAndPassword() {
        exceptionRule.expect(NullPointerException.class);
        ConfigProvider.setUsernameAndPassword(null, "pw");
        exceptionRule.expect(NullPointerException.class);
        ConfigProvider.setUsernameAndPassword("user", null);

        ConfigProvider.setUsernameAndPassword("user", "pw");
        assertThat(ConfigProvider.getUsername(), is("user"));
        assertThat(ConfigProvider.getPassword(), is("pw"));
    }

    @Test
    public void setPin() {
        exceptionRule.expect(NullPointerException.class);
        ConfigProvider.setPin(null);
        assertNull(ConfigProvider.getPin());
        ConfigProvider.setPin("");
        assertNull(ConfigProvider.getPin());
        ConfigProvider.setPin("122334");
        assertThat(ConfigProvider.getPin(), is("122334"));
    }

    @Test
    public void setServerRestUrl() {
        ConfigProvider.setServerRestUrl(null);
        assertNull(ConfigProvider.getServerRestUrl());
        ConfigProvider.setServerRestUrl("");
        assertNull(ConfigProvider.getServerRestUrl());
        ConfigProvider.setServerRestUrl("sormas-rest");
        assertThat(ConfigProvider.getServerRestUrl(), is("sormas-rest/"));
        ConfigProvider.setServerRestUrl(" rest ");
        assertThat(ConfigProvider.getServerRestUrl(), is("rest/"));
    }

    @Test
    public void setLastNotificationDate() {
        ConfigProvider.setLastNotificationDate(null);
        assertNull(ConfigProvider.getLastNotificationDate());
        Date date = new Date();
        ConfigProvider.setLastNotificationDate(date);
        assertThat(ConfigProvider.getLastNotificationDate(), is(date));
    }

    @Test
    public void setLastArchivedSyncDate() {
        ConfigProvider.setLastArchivedSyncDate(null);
        assertNull(ConfigProvider.getLastArchivedSyncDate());
        Date date = new Date();
        ConfigProvider.setLastArchivedSyncDate(date);
        assertThat(ConfigProvider.getLastArchivedSyncDate(), is(date));
    }

    @Test
    public void setLastDeletedSyncDate() {
        ConfigProvider.setLastDeletedSyncDate(null);
        assertNull(ConfigProvider.getLastDeletedSyncDate());
        Date date = new Date();
        ConfigProvider.setLastDeletedSyncDate(date);
        assertThat(ConfigProvider.getLastDeletedSyncDate(), is(date));
    }

    @Test
    public void setAccessGranted() {
        exceptionRule.expect(NullPointerException.class);
        ConfigProvider.setAccessGranted(null);
        assertNull(ConfigProvider.isAccessGranted());
        ConfigProvider.setAccessGranted(true);
        assertThat(ConfigProvider.isAccessGranted(), is(true));
    }

    @Test
    public void setServerLocale() {
        ConfigProvider.setServerLocale(null);
        assertNotNull(ConfigProvider.getServerLocale());
        ConfigProvider.setServerLocale("de");
        assertThat(ConfigProvider.getServerLocale(), is("de"));
        assertTrue(ConfigProvider.isGermanServer());
        ConfigProvider.setServerLocale("en");
        assertThat(ConfigProvider.getServerLocale(), is("en"));
        assertFalse(ConfigProvider.isGermanServer());
    }

    @Test
    public void setRepullNeeded() {
        assertThat(ConfigProvider.isRepullNeeded(), is(true));
        ConfigProvider.setRepullNeeded(false);
        assertThat(ConfigProvider.isRepullNeeded(), is(false));
        ConfigProvider.setRepullNeeded(true);
        assertThat(ConfigProvider.isRepullNeeded(), is(true));
    }

    @Test
    public void setInitialSyncRequired() {
        assertThat(ConfigProvider.isInitialSyncRequired(), is(true));
        ConfigProvider.setInitialSyncRequired(false);
        assertThat(ConfigProvider.isInitialSyncRequired(), is(false));
        ConfigProvider.setInitialSyncRequired(true);
        assertThat(ConfigProvider.isInitialSyncRequired(), is(true));
    }
}