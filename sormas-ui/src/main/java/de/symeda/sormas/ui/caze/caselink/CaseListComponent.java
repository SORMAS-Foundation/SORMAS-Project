package de.symeda.sormas.ui.caze.caselink;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.ui.utils.CssStyles;

public class CaseListComponent extends VerticalLayout {

    private CaseList list;

    public CaseListComponent(PersonReferenceDto personReferenceDto) {
        createCaseListComponent(new CaseList(personReferenceDto), I18nProperties.getString(Strings.entityCases));
    }

    private void createCaseListComponent(CaseList caseList, String heading) {
        setWidth(100, Sizeable.Unit.PERCENTAGE);
        setMargin(false);
        setSpacing(false);

        HorizontalLayout componentHeader = new HorizontalLayout();
        componentHeader.setMargin(false);
        componentHeader.setSpacing(false);
        componentHeader.setWidth(100, Sizeable.Unit.PERCENTAGE);
        addComponent(componentHeader);

        list = caseList;
        addComponent(list);
        list.reload();

        Label eventLabel = new Label(heading);
        eventLabel.addStyleName(CssStyles.H3);
        componentHeader.addComponent(eventLabel);
    }
}
