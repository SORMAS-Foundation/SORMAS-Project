package de.symeda.sormas.ui;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import org.junit.jupiter.api.AfterEach;

import com.vaadin.ui.UI;

import de.symeda.sormas.api.user.PseudonymizableDataAccessLevel;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public abstract class AbstractUiBeanTest extends AbstractBeanTest {

    @Override
    public void init() {
        super.init();

        MockProducer.mockProperty(ConfigFacadeEjb.CSV_SEPARATOR, ",");
        MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_EPID_PREFIX, "ng");

        FacadeProviderMock.MockFacadeProvider(this);
    }

    // forms read the user's data access level from UiUtil, which needs a current UI
    protected void mockCurrentUiWithFullDataAccess() {
        UserProvider userProvider = mock(UserProvider.class);
        when(userProvider.getPseudonymizableDataAccessLevel(anyBoolean())).thenReturn(PseudonymizableDataAccessLevel.ALL);
        UI ui = mock(UI.class, withSettings().extraInterfaces(UserProvider.HasUserProvider.class));
        when(((UserProvider.HasUserProvider) ui).getUserProvider()).thenReturn(userProvider);
        UI.setCurrent(ui);
    }

    @AfterEach
    void resetCurrentUi() {
        UI.setCurrent(null);
    }
}
