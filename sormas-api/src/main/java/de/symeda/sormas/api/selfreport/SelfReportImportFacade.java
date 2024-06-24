package de.symeda.sormas.api.selfreport;

import javax.ejb.Remote;

import de.symeda.sormas.api.importexport.ImportLineResultDto;

@Remote
public interface SelfReportImportFacade {

	ImportLineResultDto<SelfReportDto> importSelfReportData(
		String[] values,
		String[] entityClasses,
		String[] entityProperties,
		String[][] entityPropertyPaths,
		boolean ignoreEmptyEntries);

}
