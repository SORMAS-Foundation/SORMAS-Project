package de.symeda.sormas.backend.importexport.hierarchical.lab;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportErrorException;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.common.EnumService;
import de.symeda.sormas.backend.importexport.ImportFacadeEjb.ImportFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.hierarchical.engine.CellWriteException;
import de.symeda.sormas.backend.importexport.hierarchical.engine.CellWriter;
import de.symeda.sormas.backend.importexport.hierarchical.engine.PropertyPaths;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;

@Stateless
@LocalBean
public class LabCellWriter implements CellWriter {

	@EJB
	private ImportFacadeEjbLocal importFacade;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private CommunityFacadeEjbLocal communityFacade;
	@EJB
	private FacilityFacadeEjbLocal facilityFacade;

	@Override
	public void write(Map<String, Object> targets, String target, String[] propertyPath, String header, String value) throws CellWriteException {
		Object owner = PropertyPaths.owner(targets.get(target), propertyPath);
		PropertyDescriptor property = PropertyPaths.descriptor(owner.getClass(), propertyPath[propertyPath.length - 1]);
		try {
			if (property.getPropertyType() == Date.class) {
				property.getWriteMethod().invoke(owner, DateHelper.parseDateTimeWithException(value, I18nProperties.getUserLanguage()));
			} else if (!importFacade.executeDefaultInvoke(property, owner, value, new String[] { header }, false)) {
				property.getWriteMethod().invoke(owner, resolveReference(property.getPropertyType(), targets, target, header, value));
			}
		} catch (ImportErrorException e) {
			throw new CellWriteException(e.getMessage());
		} catch (ParseException e) {
			throw new CellWriteException(
				I18nProperties.getValidationError(
					Validations.importInvalidDate,
					header,
					DateHelper.getAllowedDateFormats(I18nProperties.getUserLanguage().getDateFormat())));
		} catch (EnumService.InvalidEnumCaptionException | IllegalArgumentException e) {
			throw new CellWriteException(new ImportErrorException(value, header).getMessage());
		} catch (InvocationTargetException | IllegalAccessException e) {
			throw new CellWriteException(I18nProperties.getValidationError(Validations.importErrorInColumn, header));
		}
	}

	private Object resolveReference(Class<?> type, Map<String, Object> targets, String target, String header, String value)
		throws CellWriteException {
		CaseDataDto caze = (CaseDataDto) targets.get(LabImportSchema.CASE);
		if (type == DistrictReferenceDto.class) {
			return single(districtFacade.getByName(value, caze.getResponsibleRegion(), false), header, value);
		}
		if (type == CommunityReferenceDto.class) {
			return single(communityFacade.getByName(value, caze.getResponsibleDistrict(), false), header, value);
		}
		if (type == FacilityReferenceDto.class && LabImportSchema.CASE.equals(target)) {
			return single(
				facilityFacade.getByNameAndType(
					facilityName(value),
					caze.getResponsibleDistrict(),
					caze.getResponsibleCommunity(),
					caze.getFacilityType(),
					false),
				header,
				value);
		}
		if (type == FacilityReferenceDto.class) {
			return single(facilityFacade.getLaboratoriesByName(value, false), header, value);
		}
		throw new CellWriteException(I18nProperties.getValidationError(Validations.importHierarchicalUnsupportedColumn, header));
	}

	private static String facilityName(String value) {
		if (I18nProperties.getPrefixCaption(FacilityDto.I18N_PREFIX, FacilityDto.OTHER_FACILITY).equals(value)) {
			return FacilityDto.OTHER_FACILITY;
		}
		if (I18nProperties.getPrefixCaption(FacilityDto.I18N_PREFIX, FacilityDto.NO_FACILITY).equals(value)) {
			return FacilityDto.NO_FACILITY;
		}
		return value;
	}

	private static <T> T single(List<T> matches, String header, String value) throws CellWriteException {
		if (matches.isEmpty()) {
			throw new CellWriteException(I18nProperties.getValidationError(Validations.importEntryDoesNotExist, value, header));
		}
		if (matches.size() > 1) {
			throw new CellWriteException(I18nProperties.getValidationError(Validations.importHierarchicalNotUnique, value, header));
		}
		return matches.get(0);
	}
}
