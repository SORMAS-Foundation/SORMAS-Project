/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.docgeneration;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

@Stateless
@LocalBean
public class DocumentTemplateService extends BaseAdoService<DocumentTemplate> {

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	protected DocumentTemplateService() {
		super(DocumentTemplate.class);
	}

	@Override
	public boolean deletePermanent(DocumentTemplate documentTemplate) throws DocumentTemplateException {
		String fileName = documentTemplate.getFileName();
		File templateFile = new File(getWorkflowTemplateDirPath(documentTemplate).resolve(fileName).toUri());
		if (templateFile.exists() && templateFile.isFile()) {
			return templateFile.delete();
		} else {
			throw new DocumentTemplateException(String.format(I18nProperties.getString(Strings.errorFileNotFound), fileName));
		}
		super.deletePermanent(documentTemplate);
	}

	private Path getWorkflowTemplateDirPath(DocumentTemplate documentTemplate) {
		Path path =
			Paths.get(configFacade.getCustomFilesPath()).resolve("docgeneration").resolve(documentTemplate.getWorkflow().getTemplateDirectory());

		if (documentTemplate.getDisease() != null) {
			path = path.resolve(documentTemplate.getDisease().name());
		}

		return path;
	}

}
