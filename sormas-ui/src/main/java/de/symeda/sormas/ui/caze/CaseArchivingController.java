package de.symeda.sormas.ui.caze;

import java.util.Date;
import java.util.List;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.CheckBox;

import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.ArchivingController;

public class CaseArchivingController extends ArchivingController<CaseFacade> {

	private CheckBox archiveWithContacts;

	@Override
	protected void doArchive(CaseFacade entityFacade, String uuid, Date endOfProcessingDate) {
		entityFacade.archive(uuid, endOfProcessingDate, archiveWithContacts.getValue());
	}

	protected void doArchive(CaseFacade entityFacade, List<String> entityUuids) {
		entityFacade.archive(entityUuids, archiveWithContacts.getValue());
	}

	@Override
	protected void addAdditionalArchiveFields(VerticalLayout verticalLayout) {
		archiveWithContacts = new CheckBox();
		archiveWithContacts.setCaption(I18nProperties.getString(Strings.confirmationArchiveCaseWithContacts));
		archiveWithContacts.setValue(false);
		verticalLayout.addComponent(archiveWithContacts);
	}

	@Override
	protected void doDearchive(CaseFacade entityFacade, List<String> uuidList, String dearchiveReason) {
		entityFacade.dearchive(uuidList, dearchiveReason, archiveWithContacts.getValue());
	}

	@Override
	protected void addAdditionalDearchiveFields(VerticalLayout verticalLayout) {
		archiveWithContacts = new CheckBox();
		archiveWithContacts.setCaption(I18nProperties.getString(Strings.confirmationDearchiveCaseWithContacts));
		archiveWithContacts.setValue(false);
		verticalLayout.addComponent(archiveWithContacts);
	}
}
