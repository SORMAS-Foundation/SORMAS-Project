/*
 *  SORMAS® - Surveillance Outbreak Response Management & Analysis System
 *  Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

select distinct new de.symeda.sormas.api.caze.CaseExportDto(generatedAlias0.id, generatedAlias1.id, generatedAlias2.id, generatedAlias3.id, generatedAlias4.id, generatedAlias5.id, generatedAlias6.id, generatedAlias0.uuid, generatedAlias0.epidNumber, generatedAlias0.disease, generatedAlias0.diseaseVariant, generatedAlias0.diseaseDetails, generatedAlias0.diseaseVariantDetails, generatedAlias1.uuid, generatedAlias1.firstName, generatedAlias1.lastName, generatedAlias1.salutation, generatedAlias1.otherSalutation, generatedAlias1.sex, generatedAlias0.pregnant, generatedAlias1.approximateAge, generatedAlias1.approximateAgeType, generatedAlias1.birthdateDD, generatedAlias1.birthdateMM, generatedAlias1.birthdateYYYY, generatedAlias0.reportDate, generatedAlias7.name, generatedAlias8.name, generatedAlias9.name, generatedAlias0.facilityType, generatedAlias10.name, generatedAlias10.uuid, generatedAlias0.healthFacilityDetails, generatedAlias11.name, generatedAlias11.uuid, generatedAlias0.pointOfEntryDetails, generatedAlias0.caseClassification, generatedAlias0.clinicalConfirmation, generatedAlias0.epidemiologicalConfirmation, generatedAlias0.laboratoryDiagnosticConfirmation, generatedAlias0.notACaseReasonNegativeTest, generatedAlias0.notACaseReasonPhysicianInformation, generatedAlias0.notACaseReasonDifferentPathogen, generatedAlias0.notACaseReasonOther, generatedAlias0.notACaseReasonDetails, generatedAlias0.investigationStatus, generatedAlias0.investigatedDate, generatedAlias0.outcome, generatedAlias0.outcomeDate, generatedAlias0.sequelae, generatedAlias0.sequelaeDetails, generatedAlias0.bloodOrganOrTissueDonated, generatedAlias0.followUpStatus, generatedAlias0.followUpUntil, generatedAlias0.nosocomialOutbreak, generatedAlias0.infectionSetting, generatedAlias0.prohibitionToWork, generatedAlias0.prohibitionToWorkFrom, generatedAlias0.prohibitionToWorkUntil, generatedAlias0.reInfection, generatedAlias0.previousInfectionDate, generatedAlias0.reinfectionStatus, generatedAlias0.reinfectionDetails, generatedAlias0.quarantine, generatedAlias0.quarantineTypeDetails, generatedAlias0.quarantineFrom, generatedAlias0.quarantineTo, generatedAlias0.quarantineHelpNeeded, generatedAlias0.quarantineOrderedVerbally, generatedAlias0.quarantineOrderedOfficialDocument, generatedAlias0.quarantineOrderedVerballyDate, generatedAlias0.quarantineOrderedOfficialDocumentDate, generatedAlias0.quarantineExtended, generatedAlias0.quarantineReduced, generatedAlias0.quarantineOfficialOrderSent, generatedAlias0.quarantineOfficialOrderSentDate, generatedAlias5.admittedToHealthFacility, generatedAlias5.admissionDate, generatedAlias5.dischargeDate, generatedAlias5.leftAgainstAdvice, generatedAlias1.presentCondition, generatedAlias1.deathDate, generatedAlias1.burialDate, generatedAlias1.burialConductor, generatedAlias1.burialPlaceDescription, generatedAlias12.name, generatedAlias13.name, generatedAlias14.name, generatedAlias2.city, generatedAlias2.street, generatedAlias2.houseNumber, generatedAlias2.additionalInformation, generatedAlias2.postalCode, generatedAlias15.name, generatedAlias15.uuid, generatedAlias2.facilityDetails, (select generatedAlias16.contactInformation from PersonContactDetail as generatedAlias16 where ( generatedAlias16.person=generatedAlias1 ) and ( generatedAlias16.primaryContact = true ) and ( generatedAlias16.personContactDetailType=:param0 )), (select case when generatedAlias17.thirdParty = true then generatedAlias17.thirdPartyName else 'This person' end from PersonContactDetail as generatedAlias17 where ( generatedAlias17.person=generatedAlias1 ) and ( generatedAlias17.primaryContact = true ) and ( generatedAlias17.personContactDetailType=:param1 )), (select generatedAlias18.contactInformation from PersonContactDetail as generatedAlias18 where ( generatedAlias18.person=generatedAlias1 ) and ( generatedAlias18.primaryContact = true ) and ( generatedAlias18.personContactDetailType=:param2 )), (select function('array_to_string', function('array_agg', case when generatedAlias19.personContactDetailType=:param3 then ((generatedAlias19.contactInformation || ' (') || (generatedAlias19.details || ')')) else ((generatedAlias19.contactInformation || ' (') || (generatedAlias19.personContactDetailType || ')')) end), ', ') from PersonContactDetail as generatedAlias19 where ( generatedAlias19.person=generatedAlias1 ) and ( ( generatedAlias19.primaryContact = false ) or ( generatedAlias19.personContactDetailType=:param4 ) )), generatedAlias1.educationType, generatedAlias1.educationDetails, generatedAlias1.occupationType, generatedAlias1.occupationDetails, generatedAlias1.armedForcesRelationType, generatedAlias3.contactWithSourceCaseKnown, generatedAlias0.vaccinationStatus, generatedAlias0.postpartum, generatedAlias0.trimester, (select count(distinct generatedAlias20.id) from EventParticipant as generatedAlias21 inner join generatedAlias21.event as generatedAlias20 inner join generatedAlias21.resultingCase as generatedAlias22 where ( generatedAlias22.id=generatedAlias0.id ) and ( generatedAlias20.deleted = false ) and ( generatedAlias20.archived = false ) and ( generatedAlias21.deleted = false )), generatedAlias0.externalID, generatedAlias0.externalToken, generatedAlias0.internalToken, generatedAlias1.birthName, generatedAlias23.isoCode, generatedAlias23.defaultName, generatedAlias24.isoCode, generatedAlias24.defaultName, generatedAlias0.caseIdentificationSource, generatedAlias0.screeningType, generatedAlias25.name, generatedAlias26.name, generatedAlias27.name, generatedAlias0.clinicianName, generatedAlias0.clinicianPhone, generatedAlias0.clinicianEmail, generatedAlias28.id, generatedAlias29.id, generatedAlias0.previousQuarantineTo, generatedAlias0.quarantineChangeComment, case when ( ( generatedAlias0.reportingUser is not null ) and ( generatedAlias0.reportingUser.id=15L ) ) or ( ( generatedAlias0.responsibleDistrict.id=11L ) or ( generatedAlias0.district.id=11L ) ) then true else false end)
from cases as generatedAlias0
         left join generatedAlias0.person as generatedAlias1
         left join generatedAlias1.address as generatedAlias2
         left join generatedAlias2.region as generatedAlias12
         left join generatedAlias2.district as generatedAlias13
         left join generatedAlias2.community as generatedAlias14
         left join generatedAlias1.address as generatedAlias30
         left join generatedAlias30.facility as generatedAlias15
         left join generatedAlias1.birthCountry as generatedAlias23
         left join generatedAlias1.citizenship as generatedAlias24
         left join generatedAlias1.address as generatedAlias31
         left join generatedAlias0.epiData as generatedAlias3
         left join generatedAlias0.symptoms as generatedAlias4
         left join generatedAlias0.hospitalization as generatedAlias5
         left join generatedAlias0.healthConditions as generatedAlias6
         left join generatedAlias0.region as generatedAlias7
         left join generatedAlias0.district as generatedAlias8
         left join generatedAlias0.community as generatedAlias9
         left join generatedAlias0.healthFacility as generatedAlias10
         left join generatedAlias0.pointOfEntry as generatedAlias11
         left join generatedAlias0.responsibleRegion as generatedAlias25
         left join generatedAlias0.responsibleDistrict as generatedAlias26
         left join generatedAlias0.responsibleCommunity as generatedAlias27
         left join generatedAlias0.reportingUser as generatedAlias28
         left join generatedAlias0.followUpStatusChangeUser as generatedAlias29
         left join generatedAlias0.contacts as generatedAlias32
where (((((generatedAlias0.district.id = 11 L) or (generatedAlias0.responsibleDistrict.id = 11 L)) or
         (((generatedAlias32.reportingUser = :param5) or (generatedAlias32.contactOfficer = :param6)) or
          (generatedAlias32.district.id = 11 L))) or (generatedAlias0.sharedToCountry = true)) or
       (((generatedAlias0.reportingUser.id = 15 L) or (generatedAlias0.surveillanceOfficer.id = 15 L)) or
        (generatedAlias0.caseOfficer.id = 15 L)))
  and (generatedAlias0.deleted = :param7)
order by generatedAlias0.reportDate desc, generatedAlias0.id desc