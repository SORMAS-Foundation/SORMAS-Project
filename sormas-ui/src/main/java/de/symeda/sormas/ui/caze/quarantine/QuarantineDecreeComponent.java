package de.symeda.sormas.ui.caze.quarantine;


import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.caze.CaseReferenceDto;

public class QuarantineDecreeComponent extends VerticalLayout {

    private Button createButton;

    public QuarantineDecreeComponent(CaseReferenceDto caseReferenceDto) {
        super();
        HorizontalLayout componentHeader = new HorizontalLayout();
        componentHeader.setMargin(false);
        componentHeader.setSpacing(false);
        componentHeader.setWidth(100, Unit.PERCENTAGE);
        addComponent(componentHeader);


        Label tasksHeader = new Label("Awesome"); // new Label(I18nProperties.getString(Strings.entitySamples));
        tasksHeader.addStyleName(CssStyles.H3);
        componentHeader.addComponent(tasksHeader);


        createButton = new Button("Click me!"); // new Button(I18nProperties.getCaption(Captions.sampleNewSample));
        createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        createButton.setIcon(VaadinIcons.PLUS_CIRCLE);
        // createButton.addClickListener(clickListener);
        componentHeader.addComponent(createButton);
        componentHeader.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);

    }
}