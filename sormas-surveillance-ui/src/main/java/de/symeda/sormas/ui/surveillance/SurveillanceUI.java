package de.symeda.sormas.ui.surveillance;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Viewport;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.ui.surveillance.caze.CaseDataView;
import de.symeda.sormas.ui.surveillance.caze.CasesView;
import de.symeda.sormas.ui.surveillance.caze.PatientInformationView;
import de.symeda.sormas.ui.utils.authentication.AccessControl;
import de.symeda.sormas.ui.utils.authentication.BasicAccessControl;
import de.symeda.sormas.ui.utils.authentication.LoginScreen;
import de.symeda.sormas.ui.utils.authentication.LoginScreen.LoginListener;

/**
 * Main UI class of the application that shows either the login screen or the
 * main view of the application depending on whether a user is signed in.
 *
 * The @Viewport annotation configures the viewport meta tags appropriately on
 * mobile devices. Instead of device based scaling (default), using responsive
 * layouts.
 */
@SuppressWarnings("serial")
@Viewport("user-scalable=no,initial-scale=1.0")
@Theme("sormastheme")
@Widgetset("de.symeda.sormas.SormasWidgetset")
public class SurveillanceUI extends UI {

    private AccessControl accessControl = new BasicAccessControl();
    

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Responsive.makeResponsive(this);
        setLocale(vaadinRequest.getLocale());
        getPage().setTitle("Surveillance");
        if (!accessControl.isUserSignedIn()) {
            setContent(new LoginScreen(accessControl, new LoginListener() {
                @Override
                public void loginSuccessful() {
                    showMainView();
                }
            }));
        } else {
            showMainView();
        }
    }

    protected void showMainView() {
        addStyleName(ValoTheme.UI_WITH_MENU);
        setContent(new MainScreen(SurveillanceUI.this));
        getNavigator().navigateTo(getNavigator().getState());
    }

    public static SurveillanceUI get() {
        return (SurveillanceUI) UI.getCurrent();
    }

    public AccessControl getAccessControl() {
        return accessControl;
    }

    @WebServlet(urlPatterns = "/*", name = "SurveillanceUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = SurveillanceUI.class, productionMode = false)
    public static class SurveillanceUIServlet extends VaadinServlet {

    }
}
