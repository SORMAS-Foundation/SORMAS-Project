/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.docgeneration;

import com.vaadin.ui.CustomLayout;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;

import javax.validation.constraints.NotNull;

public class CaseDocumentsComponent extends AbstractDocumentGenerationComponent {

	public static final String QUARANTINE_LOC = "quarantine";

	public static void addComponentToLayout(@NotNull UserProvider currentUser, CustomLayout targetLayout, CaseDataDto caseDataDto) {
		addComponentToLayout(currentUser, targetLayout, caseDataDto.toReference());
	}

	public static void addComponentToLayout(@NotNull UserProvider currentUser, CustomLayout targetLayout, ContactDto contactDto) {
		addComponentToLayout(currentUser, targetLayout, contactDto.toReference());
	}

	public static void addComponentToLayout(@NotNull UserProvider currentUser, CustomLayout targetLayout, ReferenceDto referenceDto) {
		if (isQuarantineOrderAvailable(currentUser)) {
			CaseDocumentsComponent docgenerationComponent = new CaseDocumentsComponent(referenceDto);
			docgenerationComponent.addStyleName(CssStyles.SIDE_COMPONENT);
			targetLayout.addComponent(docgenerationComponent, QUARANTINE_LOC);
		}
	}

	private static boolean isQuarantineOrderAvailable(@NotNull UserProvider currentUser) {
		return currentUser.hasUserRight(UserRight.QUARANTINE_ORDER_CREATE);
	}

	public CaseDocumentsComponent(ReferenceDto referenceDto) {
		super();
		addDocumentBar(() -> new QuarantineOrderLayout(referenceDto), Captions.DocumentTemplate_QuarantineOrder);
	}
}
