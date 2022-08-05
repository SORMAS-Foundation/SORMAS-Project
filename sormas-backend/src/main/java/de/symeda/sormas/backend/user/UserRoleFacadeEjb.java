/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.user;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleCriteria;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.api.user.UserRoleFacade;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.XssfHelper;

@Stateless(name = "UserRoleFacade")
public class UserRoleFacadeEjb implements UserRoleFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private UserRoleService userRoleService;
	@EJB
	private UserService userService;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	@Override
	public List<UserRoleDto> getAllAfter(Date since) {
		return userRoleService.getAllAfter(since).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<UserRoleDto> getAll() {
		return userRoleService.getAll().stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return userRoleService.getAllUuids();
	}

	@Override
	public List<String> getDeletedUuids(Date since) {
		return userRoleService.getDeletedUuids(since);
	}

	@Override
	public UserRoleDto getByUuid(String uuid) {
		return toDto(userRoleService.getByUuid(uuid));
	}

	@Override
	public UserRoleReferenceDto getReferenceByUuid(String uuid) {
		return toReferenceDto(userRoleService.getByUuid(uuid));
	}

	@Override
	public UserRoleDto saveUserRole(@Valid UserRoleDto dto) {

		validate(dto);

		UserRole entity = fromDto(dto, true);

		userRoleService.ensurePersisted(entity);
		return toDto(entity);
	}

	private void validate(UserRoleDto source) {
		if (StringUtils.isBlank(source.getCaption())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.specifyCaption));
		}
		if (Objects.isNull(source.getJurisdictionLevel())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.specifyJurisdictionLevel));
		}
		if (!userRoleService.isCaptionUnique(source.getUuid(), source.getCaption())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.captionNotUnique));
		}

		Set<UserRight> userRights = source.getUserRights();
		Set<UserRight> requiredUserRights = UserRight.getRequiredUserRights(userRights);
		if (!userRights.containsAll(requiredUserRights)) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(
					Validations.missingRequiredUserRights,
					requiredUserRights.stream().filter(r -> !userRights.contains(r)).map(UserRight::toString).collect(Collectors.joining(", "))));
		}

		User currentUser = userService.getCurrentUser();
		if (currentUser != null && currentUser.getUserRoles().stream().anyMatch(r -> DataHelper.isSame(r, source))) {
			Set<UserRole> currentUserRoles = currentUser.getUserRoles();
			Set<UserRight> currentUserRights = UserRole.getUserRights(currentUserRoles);
			Set<UserRight> newUserRights = UserRoleDto
				// replace old user role with the one being edited
				.getUserRights(currentUserRoles.stream().map(r -> DataHelper.isSame(r, source) ? source : toDto(r)).collect(Collectors.toList()));

			if (currentUserRights.contains(UserRight.USER_ROLE_EDIT) && !newUserRights.contains(UserRight.USER_ROLE_EDIT)) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.removeUserRightEditRightFromOwnUser));
			} else if (currentUserRights.contains(UserRight.USER_EDIT) && !newUserRights.contains(UserRight.USER_EDIT)) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.removeUserEditRightFromOwnUser));
			}
		}
	}

	@Override
	public void deleteUserRole(UserRoleReferenceDto dto) {

		UserRole entity = userRoleService.getByUuid(dto.getUuid());
		userRoleService.deletePermanent(entity);
	}

	@Override
	public boolean hasUserRight(Collection<UserRoleDto> userRoles, UserRight userRight) {

		return hasAnyUserRight(userRoles, Collections.singleton(userRight));
	}

	@Override
	public boolean hasAnyUserRight(Collection<UserRoleDto> userRoles, Collection<UserRight> userRights) {

		for (UserRoleDto userRole : userRoles) {
			for (UserRight userRight : userRights) {
				if (userRole.getUserRights().contains(userRight)) {
					return true;
				}
			}
		}
		return false;
	}

	public UserRole fromDto(UserRoleDto source, boolean checkChangeDate) {

		if (source == null) {
			return null;
		}

		UserRole target = DtoHelper.fillOrBuildEntity(source, userRoleService.getByUuid(source.getUuid()), UserRole::new, checkChangeDate);

		Set<UserRight> userRights = Optional.of(target).map(UserRole::getUserRights).orElseGet(HashSet::new);
		target.setUserRights(userRights);
		userRights.clear();
		userRights.addAll(source.getUserRights());
		target.setEnabled(source.isEnabled());
		target.setCaption(source.getCaption());
		target.setDescription(source.getDescription());
		target.setHasOptionalHealthFacility(source.getHasOptionalHealthFacility());
		target.setHasAssociatedDistrictUser(source.getHasAssociatedDistrictUser());
		target.setPortHealthUser(source.isPortHealthUser());
		target.setEmailNotificationTypes(source.getEmailNotificationTypes());
		target.setSmsNotificationTypes(source.getSmsNotificationTypes());
		target.setJurisdictionLevel(source.getJurisdictionLevel());

		return target;
	}

	public static UserRoleDto toDto(UserRole source) {

		if (source == null) {
			return null;
		}

		UserRoleDto target = new UserRoleDto();
		DtoHelper.fillDto(target, source);

		target.setUserRights(new HashSet<>(source.getUserRights()));
		target.setEnabled(source.isEnabled());
		target.setCaption(source.getCaption());
		target.setDescription(source.getDescription());
		target.setHasOptionalHealthFacility(source.getHasOptionalHealthFacility());
		target.setHasAssociatedDistrictUser(source.getHasAssociatedDistrictUser());
		target.setPortHealthUser(source.isPortHealthUser());
		target.setEmailNotificationTypes(new HashSet<>(source.getEmailNotificationTypes()));
		target.setSmsNotificationTypes(new HashSet<>(source.getSmsNotificationTypes()));
		target.setJurisdictionLevel(source.getJurisdictionLevel());

		return target;
	}

	@Override
	public Set<UserRoleReferenceDto> getAllAsReference() {
		List<UserRoleDto> all = getAll();
		return all != null ? all.stream().map(userRole -> userRole.toReference()).collect(Collectors.toSet()) : null;
	}

	@Override
	public List<UserRoleReferenceDto> getAllActiveAsReference() {
		return userRoleService.getAllActive().stream().map(UserRoleFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	public boolean isPortHealthUser(Set<UserRoleDto> userRoles) {
		return userRoles.stream().filter(userRoleDto -> userRoleDto.isPortHealthUser()).findFirst().orElse(null) != null;
	}

	@Override
	public boolean hasAssociatedDistrictUser(Set<UserRoleDto> userRoles) {
		return userRoles.stream().filter(UserRoleDto::getHasAssociatedDistrictUser).findFirst().orElse(null) != null;
	}

	@Override
	public boolean hasOptionalHealthFacility(Set<UserRoleDto> userRoles) {
		return userRoles.stream().filter(UserRoleDto::getHasOptionalHealthFacility).findFirst().orElse(null) != null;
	}

	public static UserRoleReferenceDto toReferenceDto(UserRole entity) {

		if (entity == null) {
			return null;
		}

		return new UserRoleReferenceDto(entity.getUuid(), entity.getCaption());
	}

	@Override
	public JurisdictionLevel getJurisdictionLevel(Collection<UserRoleDto> roles) {

		boolean laboratoryJurisdictionPresent = false;
		for (UserRoleDto role : roles) {
			final JurisdictionLevel jurisdictionLevel = role.getJurisdictionLevel();
			if (roles.size() == 1 || (jurisdictionLevel != JurisdictionLevel.NONE && jurisdictionLevel != JurisdictionLevel.LABORATORY)) {
				return jurisdictionLevel;
			} else if (jurisdictionLevel == JurisdictionLevel.LABORATORY) {
				laboratoryJurisdictionPresent = true;
			}
		}

		return laboratoryJurisdictionPresent ? JurisdictionLevel.LABORATORY : JurisdictionLevel.NONE;
	}

	@Override
	public void validateUserRoleCombination(Collection<UserRoleDto> roles) throws UserRoleDto.UserRoleValidationException {
		UserRoleDto previousCheckedRole = null;
		for (UserRoleDto userRole : roles) {
			final JurisdictionLevel jurisdictionLevel = userRole.getJurisdictionLevel();
			if (jurisdictionLevel != JurisdictionLevel.NONE && jurisdictionLevel != JurisdictionLevel.LABORATORY) {
				if (previousCheckedRole != null && previousCheckedRole.getJurisdictionLevel() != jurisdictionLevel) {
					throw new UserRoleDto.UserRoleValidationException(userRole, previousCheckedRole);
				} else {
					previousCheckedRole = userRole;
				}
			}
		}
	}

	@Override
	public UserRoleReferenceDto getReferenceById(long id) {
		return toReferenceDto(userRoleService.getById(id));
	}

	@Override
	public Map<UserRoleDto, Set<UserRight>> getUserRoleRights() {
		HashMap map = new HashMap<>();

		getAll().forEach(c -> map.put(c, c.getUserRights()));

		return map;
	}

	@Override
	public long count(UserRoleCriteria userRoleCriteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<UserRole> root = cq.from(UserRole.class);
        Join<UserRole, UserRight> userRightsJoin = root.join(UserRole.USER_RIGHTS, JoinType.LEFT);

		Predicate filter = null;

		if (userRoleCriteria != null) {
			filter = userRoleService.buildCriteriaFilter(userRoleCriteria, cb, root, userRightsJoin);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(root));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<UserRoleDto> getIndexList(UserRoleCriteria userRoleCriteria, int first, int max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UserRole> cq = cb.createQuery(UserRole.class);
		Root<UserRole> userRole = cq.from(UserRole.class);
		Join<UserRole, UserRight> userRightsJoin = userRole.join(UserRole.USER_RIGHTS, JoinType.LEFT);

		Predicate filter = null;

		if (userRoleCriteria != null) {
			filter = userRoleService.buildCriteriaFilter(userRoleCriteria, cb, userRole, userRightsJoin);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.distinct(true);
		if (sortProperties != null && !sortProperties.isEmpty()) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case UserRoleDto.UUID:
				case UserRoleDto.CAPTION:
				case UserRoleDto.JURISDICTION_LEVEL:
				case UserRoleDto.DESCRIPTION:
					expression = userRole.get(sortProperty.propertyName);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.asc(userRole.get(UserRole.CAPTION)));
		}

		cq.select(userRole);

		return QueryHelper.getResultList(em, cq, null, null, UserRoleFacadeEjb::toDto);
	}

	@Override
	public String generateUserRolesDocument() throws IOException {
		Path documentPath = generateUserRolesDocumentTempPath();

		if (Files.exists(documentPath)) {
			throw new IOException("File already exists: " + documentPath);
		}

		try (OutputStream fos = Files.newOutputStream(documentPath)) {
			TreeMap<UserRoleDto, Set<UserRight>> userRolesRights = getSortedUserRolesRights();
			generateUserRolesDocument(userRolesRights, fos);
		} catch (IOException e) {
			Files.deleteIfExists(documentPath);
			throw e;
		}

		return documentPath.toString();
	}

	private void generateUserRolesDocument(Map<UserRoleDto, Set<UserRight>> userRoleRights, OutputStream outStream) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();

		// Create User Role sheet
		String safeName = WorkbookUtil.createSafeSheetName(I18nProperties.getCaption(Captions.UserRole));
		XSSFSheet sheet = workbook.createSheet(safeName);

		// Define colors
		final XSSFColor green = XssfHelper.createColor(0, 153, 0);
		final XSSFColor red = XssfHelper.createColor(255, 0, 0);
		final XSSFColor black = XssfHelper.createColor(0, 0, 0);

		// Initialize cell styles
		// Authorized style
		XSSFCellStyle authorizedStyle = workbook.createCellStyle();
		authorizedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		authorizedStyle.setFillForegroundColor(green);
		authorizedStyle.setBorderBottom(BorderStyle.THIN);
		authorizedStyle.setBorderLeft(BorderStyle.THIN);
		authorizedStyle.setBorderTop(BorderStyle.THIN);
		authorizedStyle.setBorderRight(BorderStyle.THIN);
		authorizedStyle.setBorderColor(XSSFCellBorder.BorderSide.BOTTOM, black);
		authorizedStyle.setBorderColor(XSSFCellBorder.BorderSide.LEFT, black);
		authorizedStyle.setBorderColor(XSSFCellBorder.BorderSide.TOP, black);
		authorizedStyle.setBorderColor(XSSFCellBorder.BorderSide.RIGHT, black);

		// Unauthorized style
		XSSFCellStyle unauthorizedStyle = workbook.createCellStyle();
		unauthorizedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		unauthorizedStyle.setFillForegroundColor(red);
		unauthorizedStyle.setBorderBottom(BorderStyle.THIN);
		unauthorizedStyle.setBorderLeft(BorderStyle.THIN);
		unauthorizedStyle.setBorderTop(BorderStyle.THIN);
		unauthorizedStyle.setBorderRight(BorderStyle.THIN);
		unauthorizedStyle.setBorderColor(XSSFCellBorder.BorderSide.BOTTOM, black);
		unauthorizedStyle.setBorderColor(XSSFCellBorder.BorderSide.LEFT, black);
		unauthorizedStyle.setBorderColor(XSSFCellBorder.BorderSide.TOP, black);
		unauthorizedStyle.setBorderColor(XSSFCellBorder.BorderSide.RIGHT, black);

		// Bold style
		XSSFFont boldFont = workbook.createFont();
		boldFont.setBold(true);
		XSSFCellStyle boldStyle = workbook.createCellStyle();
		boldStyle.setFont(boldFont);

		int rowCounter = 0;

		// Header
		Row headerRow = sheet.createRow(rowCounter++);

		Cell userRightHeadlineCell = headerRow.createCell(0);
		userRightHeadlineCell.setCellValue(I18nProperties.getCaption(Captions.UserRole));
		userRightHeadlineCell.setCellStyle(boldStyle);

		Cell captionHeadlineCell = headerRow.createCell(1);
		captionHeadlineCell.setCellValue(I18nProperties.getCaption(Captions.UserRole_jurisdictionLevel));
		captionHeadlineCell.setCellStyle(boldStyle);

		Cell descHeadlineCell = headerRow.createCell(2);
		descHeadlineCell.setCellValue(I18nProperties.getCaption(Captions.UserRole_description));
		descHeadlineCell.setCellStyle(boldStyle);

		sheet.setColumnWidth(0, 256 * 35);
		sheet.setColumnWidth(1, 256 * 50);
		sheet.setColumnWidth(2, 256 * 50);
		sheet.createFreezePane(2, 2, 2, 2);

		int columnIndex = 3;

		for (UserRight userRight : UserRight.values()) {
			String columnCaption = userRight.name();
			Cell headerCell = headerRow.createCell(columnIndex);
			headerCell.setCellValue(columnCaption);
			headerCell.setCellStyle(boldStyle);
			sheet.setColumnWidth(columnIndex, 256 * 14);
			columnIndex++;
		}

		Cell uuidHeadlineCell = headerRow.createCell(columnIndex++);
		uuidHeadlineCell.setCellValue(I18nProperties.getCaption(Captions.UserRole_uuid));
		uuidHeadlineCell.setCellStyle(boldStyle);

		Cell portHealthUserHeadlineCell = headerRow.createCell(columnIndex++);
		portHealthUserHeadlineCell.setCellValue(I18nProperties.getCaption(Captions.UserRole_portHealthUser));
		portHealthUserHeadlineCell.setCellStyle(boldStyle);

		Cell hasAssociatedDistrictUserHeadlineCell = headerRow.createCell(columnIndex++);
		hasAssociatedDistrictUserHeadlineCell.setCellValue(I18nProperties.getCaption(Captions.UserRole_hasAssociatedDistrictUser));
		hasAssociatedDistrictUserHeadlineCell.setCellStyle(boldStyle);

		Cell hasOptionalHealthFacilityHeadlineCell = headerRow.createCell(columnIndex++);
		hasOptionalHealthFacilityHeadlineCell.setCellValue(I18nProperties.getCaption(Captions.UserRole_hasOptionalHealthFacility));
		hasOptionalHealthFacilityHeadlineCell.setCellStyle(boldStyle);

		Cell enabledHeadlineCell = headerRow.createCell(columnIndex++);
		enabledHeadlineCell.setCellValue(I18nProperties.getCaption(Captions.UserRole_enabled));
		enabledHeadlineCell.setCellStyle(boldStyle);

		//User roles rows
		for (UserRoleDto userRole : userRoleRights.keySet()) {
			Row row = sheet.createRow(rowCounter++);

			Cell nameCell = row.createCell(0);
			nameCell.setCellValue(userRole.getCaption());
			nameCell.setCellStyle(boldStyle);

			Cell captionCell = row.createCell(1);
			captionCell.setCellValue(userRole.getJurisdictionLevel().toString());

			Cell descCell = row.createCell(2);
			descCell.setCellValue(userRole.getDescription());

			columnIndex = 3;
			for (UserRight userRight : UserRight.values()) {
				Cell roleRightCell = row.createCell(columnIndex);

				if (hasUserRight(Collections.singletonList(userRole), userRight)) {
					roleRightCell.setCellStyle(authorizedStyle);
					roleRightCell.setCellValue(I18nProperties.getString(Strings.yes));
				} else {
					roleRightCell.setCellStyle(unauthorizedStyle);
					roleRightCell.setCellValue(I18nProperties.getString(Strings.no));
				}
				columnIndex++;
			}

			Cell uuidCell = row.createCell(columnIndex++);
			uuidCell.setCellValue(userRole.getUuid());

			Cell portHealthUserCell = row.createCell(columnIndex++);
			portHealthUserCell.setCellValue(getTranslationForBoolean(userRole.isPortHealthUser()));

			Cell hasAssociatedDistrictUserCell = row.createCell(columnIndex++);
			hasAssociatedDistrictUserCell.setCellValue(getTranslationForBoolean(userRole.getHasAssociatedDistrictUser()));

			Cell hasOptionalHealthFacilityCell = row.createCell(columnIndex++);
			hasOptionalHealthFacilityCell.setCellValue(getTranslationForBoolean(userRole.getHasOptionalHealthFacility()));

			Cell enabledCell = row.createCell(columnIndex++);
			enabledCell.setCellValue(getTranslationForBoolean(userRole.isEnabled()));
		}

		XssfHelper.addAboutSheet(workbook);

		workbook.write(outStream);
		workbook.close();
	}

	private TreeMap<UserRoleDto, Set<UserRight>> getSortedUserRolesRights() {
		Map<UserRoleDto, Set<UserRight>> userRoleRights = getUserRoleRights();
		TreeMap<UserRoleDto, Set<UserRight>> sortedMap = new TreeMap<>(Comparator.comparing(UserRoleDto::getCaption));
		sortedMap.putAll(userRoleRights);

		return sortedMap;
	}

	private String getTranslationForBoolean(boolean value) {
		return value ? I18nProperties.getString(Strings.yes) : I18nProperties.getString(Strings.no);
	}

	private Path generateUserRolesDocumentTempPath() {

		Path path = Paths.get(configFacade.getTempFilesPath());
		String fileName = ImportExportUtils.TEMP_FILE_PREFIX + "_userroles_" + DateHelper.formatDateForExport(new Date()) + "_"
			+ new Random().nextInt(Integer.MAX_VALUE) + ".xlsx";

		return path.resolve(fileName);
	}

	@Override
	public Set<UserRoleDto> getDefaultUserRolesAsDto() {
		return Stream.of(DefaultUserRole.values()).map(DefaultUserRole::toUserRole).collect(Collectors.toSet());
	}

	@Override
	public Collection<UserRoleDto> getByReferences(Set<UserRoleReferenceDto> references) {
		if (CollectionUtils.isEmpty(references)) {
			return Collections.emptyList();
		}

		return userRoleService.getByUuids(references.stream().map(UserRoleReferenceDto::getUuid).collect(Collectors.toList()))
			.stream()
			.map(UserRoleFacadeEjb::toDto)
			.collect(Collectors.toList());
	}

	@LocalBean
	@Stateless
	public static class UserRoleFacadeEjbLocal extends UserRoleFacadeEjb {

		public UserRoleFacadeEjbLocal() {
		}
	}
}
