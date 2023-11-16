/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.docgeneration;

import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateEntities;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.EmailTemplateFacade;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.docgeneration.DocumentTemplateFacadeEjb.DocumentTemplateFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserFacadeEjb;

@Stateless(name = "EmailTemplateFacade")
public class EmailTemplateFacadeEjb implements EmailTemplateFacade {

	@EJB
	private DocumentTemplateFacadeEjbLocal documentTemplateFacade;
	@EJB
	private DocumentTemplateEntitiesBuilder entitiesBuilder;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private PersonFacadeEjbLocal personFacade;
	@EJB
	private UserFacadeEjb.UserFacadeEjbLocal userFacade;

	@Override
	public String generateCaseEmailContent(String templateName, CaseReferenceDto cazeRef, Properties extraProperties)
		throws DocumentTemplateException {
		CaseDataDto caze = caseFacade.getByUuid(cazeRef.getUuid());
		DocumentTemplateEntities entities = entitiesBuilder.resolveEntities(
			new RootEntities().addEntity(RootEntityType.ROOT_CASE, caze)
				.addReference(RootEntityType.ROOT_PERSON, caze.getPerson())
				.addEntity(RootEntityType.ROOT_USER, userFacade.getCurrentUser()));
		return generateEmailContent(DocumentWorkflow.CASE_EMAIL, entities, templateName, extraProperties);
	}

	private String generateEmailContent(
		DocumentWorkflow documentWorkflow,
		DocumentTemplateEntities entities,
		String templateName,
		Properties extraProperties)
		throws DocumentTemplateException {
		return documentTemplateFacade.generateDocumentTxtFromEntities(documentWorkflow, templateName, entities, extraProperties);
	}

	@LocalBean
	@Stateless
	public static class EmailTemplateFacadeEjbLocal extends EmailTemplateFacadeEjb {
	}
}
