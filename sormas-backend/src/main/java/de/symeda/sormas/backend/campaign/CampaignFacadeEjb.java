package de.symeda.sormas.backend.campaign;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.campaign.CampaignCriteria;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignFacade;
import de.symeda.sormas.api.campaign.CampaignIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserRoleConfigFacadeEjb.UserRoleConfigFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "CampaignFacade")
public class CampaignFacadeEjb implements CampaignFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private CampaignService campaignService;
	@EJB
	private UserService userService;
	@EJB
	private UserRoleConfigFacadeEjbLocal userRoleConfigFacade;

	@Override
	public List<CampaignIndexDto> getIndexList(CampaignCriteria campaignCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CampaignIndexDto> cq = cb.createQuery(CampaignIndexDto.class);
		Root<Campaign> campaign = cq.from(Campaign.class);

		cq.multiselect(campaign.get(Campaign.UUID), campaign.get(Campaign.NAME), campaign.get(Campaign.START_DATE), campaign.get(Campaign.END_DATE));

		Predicate filter = campaignService.createUserFilter(cb, cq, campaign);

		if (campaignCriteria != null) {
			Predicate criteriaFilter = campaignService.buildCriteriaFilter(campaignCriteria, cb, campaign);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		}

		cq.where(filter);

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<Order>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case CampaignIndexDto.UUID:
				case CampaignIndexDto.NAME:
				case CampaignIndexDto.START_DATE:
				case CampaignIndexDto.END_DATE:
					expression = campaign.get(sortProperty.propertyName);
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

		if (first != null && max != null) {
			return em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			return em.createQuery(cq).getResultList();
		}
	}

	@Override
	public long count(CampaignCriteria campaignCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Campaign> campaign = cq.from(Campaign.class);

		Predicate filter = campaignService.createUserFilter(cb, cq, campaign);

		if (campaignCriteria != null) {
			Predicate criteriaFilter = campaignService.buildCriteriaFilter(campaignCriteria, cb, campaign);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		}

		cq.where(filter);
		cq.select(cb.count(campaign));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public CampaignDto saveCampaign(CampaignDto dto) {

		Campaign campaign = fromDto(dto);
		campaignService.ensurePersisted(campaign);
		return toDto(campaign);
	}

	public Campaign fromDto(@NotNull CampaignDto source) {

		Campaign target = campaignService.getByUuid(source.getUuid());
		if (target == null) {
			target = new Campaign();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setCreatingUser(userService.getByReferenceDto(source.getCreatingUser()));
		target.setDescription(source.getDescription());
		target.setEndDate(source.getEndDate());
		target.setName(source.getName());
		target.setStartDate(source.getStartDate());

		return target;
	}

	public static CampaignDto toDto(Campaign source) {

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

		return target;
	}

	@Override
	public CampaignDto getByUuid(String uuid) {
		return toDto(campaignService.getByUuid(uuid));
	}

	@Override
	public boolean isArchived(String uuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Campaign> from = cq.from(Campaign.class);

		// Workaround for probable bug in Eclipse Link/Postgre that throws a
		// NoResultException when trying to
		// query for a true Boolean result
		cq.where(cb.and(cb.equal(from.get(Campaign.ARCHIVED), true), cb.equal(from.get(AbstractDomainObject.UUID), uuid)));
		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	@Override
	public void deleteCampaign(String campaignUuid) {

		User user = userService.getCurrentUser();
		if (!userRoleConfigFacade.getEffectiveUserRights(user.getUserRoles().toArray(new UserRole[user.getUserRoles().size()]))
			.contains(UserRight.CAMPAIGN_DELETE)) {
			throw new UnsupportedOperationException(
				I18nProperties.getString(Strings.entityUser) + " " + user.getUuid() + " is not allowed to delete "
					+ I18nProperties.getString(Strings.entityCampaigns).toLowerCase() + ".");
		}

		campaignService.delete(campaignService.getByUuid(campaignUuid));
	}

	@Override
	public void archiveOrDearchiveCampaign(String campaignUuid, boolean archive) {

		Campaign campaign = campaignService.getByUuid(campaignUuid);
		campaign.setArchived(archive);
		campaignService.ensurePersisted(campaign);
	}

	@LocalBean
	@Stateless
	public static class CampaignFacadeEjbLocal extends CampaignFacadeEjb {

	}
}
