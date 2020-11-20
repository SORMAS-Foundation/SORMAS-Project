package de.symeda.sormas.ui.docgeneration;

import com.vaadin.ui.CustomLayout;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;

public class CaseDocumentsComponent extends AbstractDocumentGenerationComponent {

	public static final String QUARANTINE_LOC = "quarantine";

	public static void addComponentToLayout(CustomLayout targetLayout, EntityDto entityDto) {
		if (isQuarantineOrderAvailable(entityDto)) {
			CaseDocumentsComponent docgenerationComponent = new CaseDocumentsComponent(entityDto);
			docgenerationComponent.addStyleName(CssStyles.SIDE_COMPONENT);
			targetLayout.addComponent(docgenerationComponent, QUARANTINE_LOC);
		}
	}

	private static boolean isQuarantineOrderAvailable(EntityDto entityDto) {
		QuarantineType quarantineType = (entityDto instanceof CaseDataDto)
			? ((CaseDataDto) entityDto).getQuarantine()
			: ((entityDto instanceof ContactDto) ? ((ContactDto) entityDto).getQuarantine() : null);
		UserProvider currentUser = UserProvider.getCurrent();
		return quarantineType != null
			&& QuarantineType.isQuarantineInEffect(quarantineType)
			&& currentUser != null
			&& currentUser.hasUserRight(UserRight.QUARANTINE_ORDER_CREATE);
	}

	public CaseDocumentsComponent(EntityDto entityDto) {
		super();
		ReferenceDto referenceDto = (entityDto instanceof CaseDataDto)
			? ((CaseDataDto) entityDto).toReference()
			: ((entityDto instanceof ContactDto) ? ((ContactDto) entityDto).toReference() : null);
		addDocumentBar(() -> new QuarantineOrderLayout(referenceDto), Captions.DocumentTemplate_QuarantineOrder);
	}

	@Override
	protected String getComponentLabel() {
		return Captions.caseDocuments;
	}
}
