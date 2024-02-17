package de.symeda.sormas.backend.campaign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.campaign.CampaignCriteria;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignFacade;
import de.symeda.sormas.api.campaign.CampaignIndexDto;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.deletionconfiguration.DeletionInfoDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.campaign.diagram.CampaignDiagramDefinitionFacadeEjb;
import de.symeda.sormas.backend.campaign.form.CampaignFormMetaService;
import de.symeda.sormas.backend.common.AbstractCoreFacadeEjb;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserRoleFacadeEjb.UserRoleFacadeEjbLocal;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "CampaignFacade")
@RightsAllowed(UserRight._CAMPAIGN_VIEW)
public class CampaignFacadeEjb
	extends AbstractCoreFacadeEjb<Campaign, CampaignDto, CampaignIndexDto, CampaignReferenceDto, CampaignService, CampaignCriteria>
	implements CampaignFacade {

	@EJB
	private CampaignFormMetaService campaignFormMetaService;
	@EJB
	private UserRoleFacadeEjbLocal userRoleFacade;
	@EJB
	private CampaignDiagramDefinitionFacadeEjb.CampaignDiagramDefinitionFacadeEjbLocal campaignDiagramDefinitionFacade;

	public CampaignFacadeEjb() {
	}

	@Inject
	public CampaignFacadeEjb(CampaignService service) {
		super(Campaign.class, CampaignDto.class, service);
	}

	@Override
	public List<CampaignIndexDto> getIndexList(CampaignCriteria campaignCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CampaignIndexDto> cq = cb.createQuery(CampaignIndexDto.class);
		Root<Campaign> campaign = cq.from(Campaign.class);
		CampaignQueryContext queryContext = new CampaignQueryContext(cb, cq, campaign);

		cq.multiselect(campaign.get(Campaign.UUID), campaign.get(Campaign.NAME), campaign.get(Campaign.START_DATE), campaign.get(Campaign.END_DATE));

		Predicate filter = service.createUserFilter(queryContext);

		if (campaignCriteria != null) {
			Predicate criteriaFilter = service.buildCriteriaFilter(queryContext, campaignCriteria);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		cq.where(filter);

		if (sortProperties != null && !sortProperties.isEmpty()) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case CampaignIndexDto.UUID:
				case CampaignIndexDto.START_DATE:
				case CampaignIndexDto.END_DATE:
					expression = campaign.get(sortProperty.propertyName);
					break;
				case CampaignIndexDto.NAME:
					expression = cb.lower(campaign.get(sortProperty.propertyName));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(campaign.get(Campaign.CHANGE_DATE)));
		}

		return QueryHelper.getResultList(em, cq, first, max);
	}

	@Override
	public List<CampaignReferenceDto> getAllActiveCampaignsAsReference() {
		return service.getAll()
			.stream()
			.filter(c -> !c.isDeleted() && !c.isArchived())
			.map(CampaignFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	public CampaignReferenceDto getLastStartedCampaign() {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Campaign> query = cb.createQuery(Campaign.class);
		final Root<Campaign> from = query.from(Campaign.class);
		query.select(from);
		query.where(cb.and(service.createActiveCampaignsFilter(cb, from), cb.lessThanOrEqualTo(from.get(Campaign.START_DATE), new Date())));
		query.orderBy(cb.desc(from.get(Campaign.START_DATE)));

		final TypedQuery<Campaign> q = em.createQuery(query);
		final Campaign lastStartedCampaign = q.getResultList().stream().findFirst().orElse(null);

		return toReferenceDto(lastStartedCampaign);
	}

	@Override
	public long count(CampaignCriteria campaignCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Campaign> campaign = cq.from(Campaign.class);
		CampaignQueryContext queryContext = new CampaignQueryContext(cb, cq, campaign);

		Predicate filter = service.createUserFilter(queryContext);

		if (campaignCriteria != null) {
			Predicate criteriaFilter = service.buildCriteriaFilter(queryContext, campaignCriteria);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		cq.where(filter);
		cq.select(cb.count(campaign));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	@RightsAllowed(UserRight._CAMPAIGN_EDIT)
	public CampaignDto save(@Valid @NotNull CampaignDto dto) {
		validate(dto);
		Campaign existingCampaign = service.getByUuid(dto.getUuid());
		Campaign campaign = fillOrBuildEntity(dto, existingCampaign, true);
		if (!service.isEditAllowed(campaign)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorEntityNotEditable));
		}
		service.ensurePersisted(campaign);
		return toDto(campaign);
	}

	public Campaign fillOrBuildEntity(@NotNull CampaignDto source, Campaign target, boolean checkChangeDate) {

		target = DtoHelper.fillOrBuildEntity(source, target, Campaign::new, checkChangeDate);

		target.setCreatingUser(userService.getByReferenceDto(source.getCreatingUser()));
		target.setDescription(source.getDescription());
		target.setEndDate(source.getEndDate());
		target.setName(source.getName());
		target.setStartDate(source.getStartDate());
		final Set<CampaignFormMetaReferenceDto> campaignFormMetas = source.getCampaignFormMetas();
		if (!CollectionUtils.isEmpty(campaignFormMetas)) {
			target.setCampaignFormMetas(
				campaignFormMetas.stream()
					.map(campaignFormMetaReferenceDto -> campaignFormMetaService.getByUuid(campaignFormMetaReferenceDto.getUuid()))
					.collect(Collectors.toSet()));
		}
		target.setDashboardElements(source.getCampaignDashboardElements());

		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		return target;
	}

	public void validate(CampaignReferenceDto campaignReferenceDto) {
		validate(getByUuid(campaignReferenceDto.getUuid()));
	}

	public void validate(@Valid CampaignDto campaignDto) {
		final List<CampaignDashboardElement> campaignDashboardElements = campaignDto.getCampaignDashboardElements();
		if (campaignDashboardElements != null) {

			final Map<String, Boolean> oneSubTabIsNotNullOrEmptyMap = new HashMap<>();

			for (CampaignDashboardElement cde : campaignDashboardElements) {
				final String diagramId = cde.getDiagramId();
				if (diagramId == null) {
					throw new ValidationRuntimeException(
						I18nProperties.getValidationError(
							Validations.campaignDashboardChartValueNull,
							CampaignDashboardElement.DIAGRAM_ID,
							campaignDto.getName()));
				} else if (!campaignDiagramDefinitionFacade.exists(diagramId)) {
					throw new ValidationRuntimeException(
						I18nProperties.getValidationError(Validations.campaignDashboardChartIdDoesNotExist, diagramId, campaignDto.getName()));
				}

				if (cde.getTabId() == null) {
					throw new ValidationRuntimeException(
						I18nProperties
							.getValidationError(Validations.campaignDashboardChartValueNull, CampaignDashboardElement.TAB_ID, campaignDto.getName()));
				}

				if (cde.getSubTabId() == null || cde.getSubTabId().isEmpty()) {
					if (oneSubTabIsNotNullOrEmptyMap.containsKey(cde.getTabId()) && oneSubTabIsNotNullOrEmptyMap.get(cde.getTabId())) {
						throw new ValidationRuntimeException(
							I18nProperties.getValidationError(
								Validations.campaignDashboardChartValueNull,
								CampaignDashboardElement.SUB_TAB_ID,
								campaignDto.getName()));
					}
					oneSubTabIsNotNullOrEmptyMap.put(cde.getTabId(), false);
				} else {
					if (oneSubTabIsNotNullOrEmptyMap.containsKey(cde.getTabId()) && !oneSubTabIsNotNullOrEmptyMap.get(cde.getTabId())) {
						throw new ValidationRuntimeException(
							I18nProperties.getValidationError(
								Validations.campaignDashboardChartValueNull,
								CampaignDashboardElement.SUB_TAB_ID,
								campaignDto.getName()));
					}
					oneSubTabIsNotNullOrEmptyMap.put(cde.getTabId(), true);
				}

				if (cde.getOrder() == null) {
					throw new ValidationRuntimeException(
						I18nProperties
							.getValidationError(Validations.campaignDashboardChartValueNull, CampaignDashboardElement.ORDER, campaignDto.getName()));
				}

				if (cde.getHeight() == null) {
					throw new ValidationRuntimeException(
						I18nProperties
							.getValidationError(Validations.campaignDashboardChartValueNull, CampaignDashboardElement.HEIGHT, campaignDto.getName()));
				}

				if (cde.getWidth() == null) {
					throw new ValidationRuntimeException(
						I18nProperties
							.getValidationError(Validations.campaignDashboardChartValueNull, CampaignDashboardElement.WIDTH, campaignDto.getName()));
				}
			}

			campaignDto.getCampaignFormMetas().forEach(campaignFormMetaReferenceDto -> {
				if (campaignFormMetaReferenceDto == null || campaignFormMetaReferenceDto.getUuid() == null) {
					throw new ValidationRuntimeException(
						I18nProperties.getValidationError(
							Validations.campaignDashboardDataFormValueNull,
							CampaignDto.CAMPAIGN_FORM_METAS,
							campaignDto.getName()));
				}
			});
		}
	}

	public CampaignDto toDto(Campaign source) {

		if (source == null) {
			return null;
		}

		CampaignDto target = new CampaignDto();
		DtoHelper.fillDto(target, source);

		target.setCreatingUser(UserFacadeEjb.toReferenceDto(source.getCreatingUser()));
		target.setDescription(source.getDescription());
		target.setEndDate(source.getEndDate());
		target.setName(source.getName());
		target.setStartDate(source.getStartDate());
		target.setCampaignFormMetas(
			source.getCampaignFormMetas().stream().map(campaignFormMeta -> campaignFormMeta.toReference()).collect(Collectors.toSet()));

		target.setCampaignDashboardElements(source.getDashboardElements());

		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		return target;
	}

	@Override
	protected CampaignReferenceDto toRefDto(Campaign campaign) {
		return toReferenceDto(campaign);
	}

	@Override
	public List<CampaignDashboardElement> getCampaignDashboardElements(String campaignUuid) {
		final List<CampaignDashboardElement> result = new ArrayList<>();
		if (campaignUuid != null) {
			final Campaign campaign = service.getByUuid(campaignUuid);
			final List<CampaignDashboardElement> dashboardElements = campaign.getDashboardElements();
			if (dashboardElements != null) {
				result.addAll(dashboardElements);
			}
		} else {
			service.getAllActive().forEach(campaign -> {
				final List<CampaignDashboardElement> dashboardElements = campaign.getDashboardElements();
				if (dashboardElements != null) {
					result.addAll(dashboardElements);
				}
			});
		}
		result.forEach(cde -> {
			if (cde.getTabId() == null) {
				cde.setTabId(StringUtils.EMPTY);
			}
			if (cde.getSubTabId() == null) {
				cde.setSubTabId(StringUtils.EMPTY);
			}
			if (cde.getOrder() == null) {
				cde.setOrder(0);
			}
			if (cde.getHeight() == null) {
				cde.setHeight(50);
			}
			if (cde.getWidth() == null) {
				cde.setWidth(50);
			}
		});
		return result.stream().sorted(Comparator.comparingInt(CampaignDashboardElement::getOrder)).collect(Collectors.toList());
	}

	@Override
	@RightsAllowed(UserRight._CAMPAIGN_DELETE)
	public void delete(String campaignUuid, DeletionDetails deletionDetails) {

		service.delete(service.getByUuid(campaignUuid), deletionDetails);
	}

	@Override
	@RightsAllowed(UserRight._CAMPAIGN_DELETE)
	public List<ProcessedEntity> delete(List<String> uuids, DeletionDetails deletionDetails) {
		throw new NotImplementedException();
	}

	@Override
	@RightsAllowed(UserRight._CAMPAIGN_DELETE)
	public void restore(String uuid) {
		super.restore(uuid);
	}

	@Override
	@RightsAllowed(UserRight._CAMPAIGN_DELETE)
	public List<ProcessedEntity> restore(List<String> uuids) {
		throw new NotImplementedException();
	}

	@Override
	protected void pseudonymizeDto(Campaign source, CampaignDto dto, Pseudonymizer<CampaignDto> pseudonymizer, boolean inJurisdiction) {
		// No pseudonymization for Campaign entities
	}

	@Override
	protected void restorePseudonymizedDto(CampaignDto dto, CampaignDto existingDto, Campaign entity, Pseudonymizer<CampaignDto> pseudonymizer) {
		// No pseudonymization for Campaign entities
	}

	@Override
	public List<String> getAllActiveUuids() {
		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return service.getAllActiveUuids();
	}

	public static CampaignReferenceDto toReferenceDto(Campaign entity) {
		if (entity == null) {
			return null;
		}
		return new CampaignReferenceDto(entity.getUuid(), entity.getName());
	}

	@Override
	protected DeletableEntityType getDeletableEntityType() {
		return DeletableEntityType.CAMPAIGN;
	}

	@Override
	public DeletionInfoDto getAutomaticDeletionInfo(String uuid) {
		return null; // campaigns do not support automatic deletion yet
	}

	@Override
	@RightsAllowed(UserRight._CAMPAIGN_ARCHIVE)
	public ProcessedEntity archive(String entityUuid, Date endOfProcessingDate) {
		return super.archive(entityUuid, endOfProcessingDate);
	}

	@Override
	@RightsAllowed(UserRight._CAMPAIGN_ARCHIVE)
	public List<ProcessedEntity> archive(List<String> entityUuids) {
		return super.archive(entityUuids);
	}

	@Override
	@RightsAllowed(UserRight._CAMPAIGN_ARCHIVE)
	public List<ProcessedEntity> dearchive(List<String> entityUuids, String dearchiveReason) {
		return super.dearchive(entityUuids, dearchiveReason);
	}

	@Override
	public List<String> getArchivedUuidsSince(Date since) {
		throw new NotImplementedException();
	}

	@LocalBean
	@Stateless
	public static class CampaignFacadeEjbLocal extends CampaignFacadeEjb {

		public CampaignFacadeEjbLocal() {
		}

		@Inject
		public CampaignFacadeEjbLocal(CampaignService service) {
			super(service);
		}
	}
}
