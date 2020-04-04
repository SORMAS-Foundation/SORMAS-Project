package de.symeda.sormas.app.backend.config;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ConfigProviderTest {

    @Test
    public void setCurrentAppDownloadId() {
        ConfigProvider.setCurrentAppDownloadId(null);
        assertNull(ConfigProvider.getCurrentAppDownloadId());
        ConfigProvider.setCurrentAppDownloadId(2l);
        assertThat(ConfigProvider.getCurrentAppDownloadId(), is(2l));
    }
}