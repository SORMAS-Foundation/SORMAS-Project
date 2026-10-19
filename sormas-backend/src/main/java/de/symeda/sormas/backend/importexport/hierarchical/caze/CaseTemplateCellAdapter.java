package de.symeda.sormas.backend.importexport.hierarchical.caze;

import static de.symeda.sormas.backend.importexport.hierarchical.caze.CaseTemplateSchema.CASE;
import static de.symeda.sormas.backend.importexport.hierarchical.caze.CaseTemplateSchema.PERSON;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.ArrayUtils;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportErrorException;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.caze.caseimport.CaseTemplateCellWriter;
import de.symeda.sormas.backend.importexport.hierarchical.engine.CellWriteException;
import de.symeda.sormas.backend.importexport.hierarchical.engine.CellWriter;
import de.symeda.sormas.backend.importexport.hierarchical.engine.PropertyPaths;

@Stateless
@LocalBean
public class CaseTemplateCellAdapter implements CellWriter {

	@EJB
	private CaseTemplateCellWriter cellWriter;

	@Override
	public void write(Map<String, Object> targets, String target, String[] propertyPath, String header, String value) throws CellWriteException {
		try {
			if (writeDateTime(targets.get(target), propertyPath, header, value)) {
				return;
			}
			if (CASE.equals(target) || PERSON.equals(target)) {
				cellWriter.writeCaseCell((CaseDataDto) targets.get(CASE), (PersonDto) targets.get(PERSON), value, casePath(target, propertyPath));
			} else {
				cellWriter.writeRelatedCell(targets.get(target), value, propertyPath);
			}
		} catch (ImportErrorException e) {
			throw new CellWriteException(e.getMessage());
		} catch (InvalidColumnException e) {
			throw new CellWriteException(I18nProperties.getValidationError(Validations.importHierarchicalUnsupportedColumn, header));
		}
	}

	// the classic parser drops the time of day, nested files keep it
	private static boolean writeDateTime(Object root, String[] propertyPath, String header, String value) throws CellWriteException {
		Object owner = PropertyPaths.owner(root, propertyPath);
		if (owner == null) {
			return false;
		}
		PropertyDescriptor property = PropertyPaths.descriptor(owner.getClass(), propertyPath[propertyPath.length - 1]);
		if (property.getPropertyType() != Date.class) {
			return false;
		}
		try {
			property.getWriteMethod().invoke(owner, DateHelper.parseDateTimeWithException(value, I18nProperties.getUserLanguage()));
			return true;
		} catch (ParseException e) {
			throw new CellWriteException(
				I18nProperties.getValidationError(
					Validations.importInvalidDate,
					header,
					DateHelper.getAllowedDateFormats(I18nProperties.getUserLanguage().getDateFormat())));
		} catch (InvocationTargetException | IllegalAccessException e) {
			throw new CellWriteException(I18nProperties.getValidationError(Validations.importErrorInColumn, header));
		}
	}

	private static String[] casePath(String target, String[] propertyPath) {
		return PERSON.equals(target) ? ArrayUtils.insert(0, propertyPath, CaseDataDto.PERSON) : propertyPath;
	}
}
