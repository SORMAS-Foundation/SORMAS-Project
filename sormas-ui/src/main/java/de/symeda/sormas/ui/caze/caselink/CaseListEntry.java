package de.symeda.sormas.ui.caze.caselink;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class CaseListEntry extends HorizontalLayout {

    private final CaseIndexDto caseIndexDto;
    private Button editButton;

    public CaseListEntry(CaseIndexDto caseIndexDto) {
        this.caseIndexDto = caseIndexDto;
        setSpacing(true);
        setWidth(100, Unit.PERCENTAGE);
        addStyleName(CssStyles.SORMAS_LIST_ENTRY);

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidth(100, Unit.PERCENTAGE);
        mainLayout.setMargin(false);
        mainLayout.setSpacing(false);
        addComponent(mainLayout);
        setExpandRatio(mainLayout, 1);

        VerticalLayout leftLayout = new VerticalLayout();
        leftLayout.setMargin(false);
        leftLayout.setSpacing(false);

        Label caseUuidLabel = new Label(DataHelper.toStringNullable(DataHelper.getShortUuid(caseIndexDto.getUuid())));
        caseUuidLabel.addStyleNames(CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
        caseUuidLabel.setDescription(caseIndexDto.getUuid());
        leftLayout.addComponent(caseUuidLabel);

        Label diseaseLabel = new Label(caseIndexDto.getDisease().toString());
        diseaseLabel.addStyleNames(CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
        diseaseLabel.setDescription(caseIndexDto.getDisease().toString());
        leftLayout.addComponent(diseaseLabel);

        mainLayout.addComponent(leftLayout);
    }

    public void addEditListener(int rowIndex, Button.ClickListener editClickListener) {
        if (editButton == null) {
            editButton = ButtonHelper.createIconButtonWithCaption(
                    "edit-participant-" + rowIndex,
                    null,
                    VaadinIcons.PENCIL,
                    null,
                    ValoTheme.BUTTON_LINK,
                    CssStyles.BUTTON_COMPACT);

            addComponent(editButton);
            setComponentAlignment(editButton, Alignment.MIDDLE_RIGHT);
            setExpandRatio(editButton, 0);
        }

        editButton.addClickListener(editClickListener);
    }

    public CaseIndexDto getCaseIndexDto() {
        return caseIndexDto;
    }
}
