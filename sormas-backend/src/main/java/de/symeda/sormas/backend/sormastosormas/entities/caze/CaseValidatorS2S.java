package de.symeda.sormas.backend.sormastosormas.entities.caze;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasCasePreview;
import de.symeda.sormas.api.sormastosormas.validation.Sormas2SormasEntityValidator;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;

import de.symeda.sormas.backend.sormastosormas.data.Sormas2SormasCommonDataValidator;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;

import javax.ejb.EJB;

public class CaseValidatorS2S implements Sormas2SormasEntityValidator<CaseDataDto, SormasToSormasCasePreview> {

	@EJB
	private Sormas2SormasCommonDataValidator commonDataValidator;
	@EJB
	private InfrastructureValidator infraValidator;

	@Override
	public ValidationErrors validateInboundEntity(CaseDataDto caze) {
		ValidationErrors validationErrors = new ValidationErrors();

		infraValidator.validateResponsibleRegion(caze.getResponsibleRegion(), Captions.CaseData, validationErrors, caze::setResponsibleRegion);
		infraValidator.validateResponsibleDistrict(caze.getResponsibleDistrict(), Captions.CaseData, validationErrors, caze::setResponsibleDistrict);
		infraValidator
			.validateResponsibleCommunity(caze.getResponsibleCommunity(), Captions.CaseData, validationErrors, caze::setResponsibleCommunity);

		infraValidator.validateRegion(caze.getRegion(), Captions.CaseData, validationErrors, caze::setRegion);
		infraValidator.validateDistrit(caze.getDistrict(), Captions.CaseData, validationErrors, caze::setDistrict);
		infraValidator.validateCommunity(caze.getCommunity(), Captions.CaseData, validationErrors, caze::setCommunity);

		infraValidator.validateFacility(
			caze.getHealthFacility(),
			caze.getFacilityType(),
			caze.getHealthFacilityDetails(),
			Captions.CaseData,
			validationErrors,
			f -> {
				caze.setHealthFacility(f.getEntity());
				caze.setHealthFacilityDetails(f.getDetails());
			});

		infraValidator.validatePointOfEntry(caze.getPointOfEntry(), caze.getPointOfEntryDetails(), Captions.CaseData, validationErrors, p -> {
			caze.setPointOfEntry(p.getEntity());
			caze.setPointOfEntryDetails(p.getDetails());
		});

		final HospitalizationDto hospitalization = caze.getHospitalization();
		if (hospitalization != null) {
			hospitalization.getPreviousHospitalizations().forEach(ph -> commonDataValidator.validatePreviousHospitalization(validationErrors, ph));
		}

		final MaternalHistoryDto maternalHistory = caze.getMaternalHistory();
		if (maternalHistory != null) {
			commonDataValidator.validateMaternalHistory(validationErrors, maternalHistory);
		}

		commonDataValidator.validateEpiData(caze.getEpiData(), validationErrors);

		return validationErrors;
	}

	@Override
	public ValidationErrors validateInboundPreviewEntity(SormasToSormasCasePreview preview) {
		ValidationErrors validationErrors = new ValidationErrors();

		infraValidator.validateRegion(preview.getRegion(), Captions.CaseData, validationErrors, preview::setRegion);
		infraValidator.validateDistrit(preview.getDistrict(), Captions.CaseData, validationErrors, preview::setDistrict);
		infraValidator.validateCommunity(preview.getCommunity(), Captions.CaseData, validationErrors, preview::setCommunity);

		infraValidator.validateFacility(
			preview.getHealthFacility(),
			preview.getFacilityType(),
			preview.getHealthFacilityDetails(),
			Captions.CaseData,
			validationErrors,
			f -> {
				preview.setHealthFacility(f.getEntity());
				preview.setHealthFacilityDetails(f.getDetails());
			});

		infraValidator.validatePointOfEntry(preview.getPointOfEntry(), preview.getPointOfEntryDetails(), Captions.CaseData, validationErrors, p -> {
			preview.setPointOfEntry(p.getEntity());
			preview.setPointOfEntryDetails(p.getDetails());
		});
		return validationErrors;
	}

}
