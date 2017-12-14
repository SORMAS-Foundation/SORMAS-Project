package de.symeda.sormas.ui;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * View shown when trying to navigate to a view the user does not have access to using
 * {@link com.vaadin.navigator.Navigator}.
 * 
 * 
 */
@SuppressWarnings("serial")
public class AccessDeniedView extends VerticalLayout implements View {

    private Label explanation;

    public AccessDeniedView() {
        setMargin(true);
        setSpacing(true);

        Label header = new Label("Access denied");
        header.addStyleName(Reindeer.LABEL_H1);
        addComponent(header);
        addComponent(explanation = new Label());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        explanation.setValue("You do not have the required rights to view this page.");
    }
}
