package de.symeda.sormas.ui.caze.messaging;

import java.util.List;

import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.messaging.ManualMessageLogDto;
import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.ui.utils.PaginationList;

public class SmsList extends PaginationList<ManualMessageLogDto> {

	private CaseReferenceDto caseReferenceDto;
	private boolean hasPhoneNumber;

	public SmsList(CaseReferenceDto caseReferenceDto, boolean hasPhoneNumber) {
		super(5);
		this.caseReferenceDto = caseReferenceDto;
		this.hasPhoneNumber = hasPhoneNumber;
	}

	@Override
	public void reload() {

		List<ManualMessageLogDto> messageLogs = FacadeProvider.getCaseFacade().getMessageLog(caseReferenceDto.getUuid(), MessageType.SMS);

		if (!hasPhoneNumber){
			Label noPhoneNumberLabel = new Label(I18nProperties.getCaption(Captions.noPhoneNumberForCasePerson));
			listLayout.addComponent(noPhoneNumberLabel);
		}

		setEntries(messageLogs);
		if (!messageLogs.isEmpty()) {
			showPage(1);
		} else {
			updatePaginationLayout();
			Label noMessageLabel = new Label(I18nProperties.getCaption(Captions.noSmsSentForCase));
			listLayout.addComponent(noMessageLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {
		List<ManualMessageLogDto> displayedEntries = getDisplayedEntries();
		for (int i = 0, displayedEntriesSize = displayedEntries.size(); i < displayedEntriesSize; i++) {
			SmsListEntry listEntry = new SmsListEntry(displayedEntries.get(i));
			listLayout.addComponent(listEntry);
		}
	}
}
