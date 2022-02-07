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
import de.symeda.sormas.backend.sormastosormas.data.validation.ValidationDirection;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildCaseValidationGroupName;

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
	public ValidationErrors validate(SormasToSormasCaseDto sharedData, ValidationDirection direction) {
		CaseDataDto caze = sharedData.getEntity();
		ValidationErrors validationErrors = new ValidationErrors(buildCaseValidationGroupName(caze));

		ValidationErrors personValidationErrors = validatePerson(sharedData.getPerson(), direction);
		validationErrors.addAll(personValidationErrors);

		final String groupNameTag = Captions.CaseData;
		infraValidator.validateResponsibleRegion(caze.getResponsibleRegion(), groupNameTag, validationErrors, caze::setResponsibleRegion, direction);
		infraValidator
			.validateResponsibleDistrict(caze.getResponsibleDistrict(), groupNameTag, validationErrors, caze::setResponsibleDistrict, direction);
		infraValidator
			.validateResponsibleCommunity(caze.getResponsibleCommunity(), groupNameTag, validationErrors, caze::setResponsibleCommunity, direction);

		infraValidator.validateRegion(caze.getRegion(), groupNameTag, validationErrors, caze::setRegion, direction);
		infraValidator.validateDistrict(caze.getDistrict(), groupNameTag, validationErrors, caze::setDistrict, direction);
		infraValidator.validateCommunity(caze.getCommunity(), groupNameTag, validationErrors, caze::setCommunity, direction);

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
			hospitalization.getPreviousHospitalizations().forEach(ph -> validatePreviousHospitalization(validationErrors, ph, direction));
		}

		final MaternalHistoryDto maternalHistory = caze.getMaternalHistory();
		if (maternalHistory != null) {
			validateMaternalHistory(validationErrors, maternalHistory, direction);
		}

		validateEpiData(caze.getEpiData(), validationErrors, direction);

		return validationErrors;
	}

	@Override
	public ValidationErrors validatePreview(SormasToSormasCasePreview preview, ValidationDirection direction) {
		ValidationErrors validationErrors = new ValidationErrors(buildCaseValidationGroupName(preview));
		validationErrors.addAll(validatePersonPreview(preview.getPerson(), direction));

		final String groupNameTag = Captions.CaseData;
		infraValidator.validateRegion(preview.getRegion(), groupNameTag, validationErrors, preview::setRegion, direction);
		infraValidator.validateDistrict(preview.getDistrict(), groupNameTag, validationErrors, preview::setDistrict, direction);
		infraValidator.validateCommunity(preview.getCommunity(), groupNameTag, validationErrors, preview::setCommunity, direction);
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

}
