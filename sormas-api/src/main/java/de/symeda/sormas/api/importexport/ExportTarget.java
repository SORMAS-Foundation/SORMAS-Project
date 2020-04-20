package de.symeda.sormas.api.importexport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.symeda.sormas.api.caze.CaseExportType;
import de.symeda.sormas.api.visit.VisitExportType;

/**
 * Defines which {@link CaseExportType}s the annotated field will be used for. If an export type is
 * chosen that is not in this list, the field will not appear in the result.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExportTarget {

	CaseExportType[] caseExportTypes() default CaseExportType.CASE_SURVEILLANCE;
	VisitExportType[] visitExportTypes() default VisitExportType.CONTACT_VISITS;

}
