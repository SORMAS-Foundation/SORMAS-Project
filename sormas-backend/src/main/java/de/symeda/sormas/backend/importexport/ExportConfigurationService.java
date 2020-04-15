package de.symeda.sormas.backend.importexport;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.common.AbstractAdoService;

@Stateless
@LocalBean
public class ExportConfigurationService extends AbstractAdoService<ExportConfiguration> {

	public ExportConfigurationService() {
		super(ExportConfiguration.class);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<ExportConfiguration, ExportConfiguration> from) {
		// A user should not query for this
		throw new UnsupportedOperationException();
	}
	
}
