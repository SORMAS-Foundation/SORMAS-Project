package de.symeda.sormas.backend.importexport;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.backend.common.BaseAdoService;

@Stateless
@LocalBean
public class ExportConfigurationService extends BaseAdoService<ExportConfiguration> {

	public ExportConfigurationService() {
		super(ExportConfiguration.class);
	}

}
