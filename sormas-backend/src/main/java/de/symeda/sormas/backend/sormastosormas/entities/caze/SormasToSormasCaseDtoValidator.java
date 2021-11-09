package de.symeda.sormas.backend.sormastosormas.entities.caze;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasCasePreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
@LocalBean
public class SormasToSormasCaseDtoValidator extends SormasToSormasDtoValidator<CaseDataDto, SormasToSormasCaseDto, SormasToSormasCasePreview> {

	public SormasToSormasCaseDtoValidator() {
	}

	@Inject
	protected SormasToSormasCaseDtoValidator(InfrastructureValidator infraValidator) {
		super(infraValidator);
	}

	@Override
	public ValidationErrors validateIncoming(SormasToSormasCaseDto sharedData) {
		CaseDataDto caze = sharedData.getEntity();
		ValidationErrors validationErrors = new ValidationErrors();

		ValidationErrors personValidationErrors = validatePerson(sharedData.getPerson());
		validationErrors.addAll(personValidationErrors);

		final String groupNameTag = Captions.CaseData;
		infraValidator.validateResponsibleRegion(caze.getResponsibleRegion(), groupNameTag, validationErrors, caze::setResponsibleRegion);
		infraValidator.validateResponsibleDistrict(caze.getResponsibleDistrict(), groupNameTag, validationErrors, caze::setResponsibleDistrict);
		infraValidator.validateResponsibleCommunity(caze.getResponsibleCommunity(), groupNameTag, validationErrors, caze::setResponsibleCommunity);

		infraValidator.validateRegion(caze.getRegion(), groupNameTag, validationErrors, caze::setRegion);
		infraValidator.validateDistrit(caze.getDistrict(), groupNameTag, validationErrors, caze::setDistrict);
		infraValidator.validateCommunity(caze.getCommunity(), groupNameTag, validationErrors, caze::setCommunity);

		infraValidator.validateFacility(
			caze.getHealthFacility(),
			caze.getFacilityType(),
			caze.getHealthFacilityDetails(),
			groupNameTag,
			validationErrors,
			f -> {
				caze.setHealthFacility(f.getEntity());
				caze.setHealthFacilityDetails(f.getDetails());
			});

		infraValidator.validatePointOfEntry(caze.getPointOfEntry(), caze.getPointOfEntryDetails(), groupNameTag, validationErrors, p -> {
			caze.setPointOfEntry(p.getEntity());
			caze.setPointOfEntryDetails(p.getDetails());
		});

		final HospitalizationDto hospitalization = caze.getHospitalization();
		if (hospitalization != null) {
			hospitalization.getPreviousHospitalizations().forEach(ph -> validatePreviousHospitalization(validationErrors, ph));
		}

		final MaternalHistoryDto maternalHistory = caze.getMaternalHistory();
		if (maternalHistory != null) {
			validateMaternalHistory(validationErrors, maternalHistory);
		}

		validateEpiData(caze.getEpiData(), validationErrors);

		return validationErrors;
	}

	@Override
	public ValidationErrors validateIncomingPreview(SormasToSormasCasePreview preview) {
		ValidationErrors validationErrors = new ValidationErrors();
		validationErrors.addAll(validatePersonPreview(preview.getPerson()));

		final String groupNameTag = Captions.CaseData;
		infraValidator.validateRegion(preview.getRegion(), groupNameTag, validationErrors, preview::setRegion);
		infraValidator.validateDistrit(preview.getDistrict(), groupNameTag, validationErrors, preview::setDistrict);
		infraValidator.validateCommunity(preview.getCommunity(), groupNameTag, validationErrors, preview::setCommunity);
		infraValidator.validateFacility(
			preview.getHealthFacility(),
			preview.getFacilityType(),
			preview.getHealthFacilityDetails(),
			groupNameTag,
			validationErrors,
			f -> {
				preview.setHealthFacility(f.getEntity());
				preview.setHealthFacilityDetails(f.getDetails());
			});

		return validationErrors;
	}

	@Override
	public ValidationErrors validateOutgoing(SormasToSormasCaseDto sharedData) {
		return null;
	}

	@Override
	public ValidationErrors validateOutgoingPreview(SormasToSormasCasePreview preview) {
		return null;
	}

}
