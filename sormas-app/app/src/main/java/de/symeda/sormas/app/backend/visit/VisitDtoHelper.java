package de.symeda.sormas.app.backend.visit;

import java.util.List;

import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.symptoms.SymptomsDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class VisitDtoHelper extends AdoDtoHelper<Visit, VisitDto> {

    private SymptomsDtoHelper symptomsDtoHelper = new SymptomsDtoHelper();

    @Override
    protected Class<Visit> getAdoClass() {
        return Visit.class;
    }

    @Override
    protected Class<VisitDto> getDtoClass() {
        return VisitDto.class;
    }

    @Override
    protected Call<List<VisitDto>> pullAllSince(long since) {
        return RetroProvider.getVisitFacade().pullAllSince(since);
    }

    @Override
    protected Call<List<VisitDto>> pullByUuids(List<String> uuids) {
        return RetroProvider.getVisitFacade().pullByUuids(uuids);
    }

    @Override
    protected Call<Integer> pushAll(List<VisitDto> visitDtos) {
        return RetroProvider.getVisitFacade().pushAll(visitDtos);
    }

    @Override
    public void fillInnerFromDto(Visit target, VisitDto source) {

        target.setDisease(source.getDisease());

        target.setPerson(DatabaseHelper.getPersonDao().getByReferenceDto(source.getPerson()));

        target.setSymptoms(symptomsDtoHelper.fillOrCreateFromDto(target.getSymptoms(), source.getSymptoms()));
        target.setVisitDateTime(source.getVisitDateTime());
        target.setVisitRemarks(source.getVisitRemarks());
        target.setVisitStatus(source.getVisitStatus());
        target.setVisitUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getVisitUser()));

        target.setReportLat(source.getReportLat());
        target.setReportLon(source.getReportLon());
        target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());
    }

    @Override
    public void fillInnerFromAdo(VisitDto target, Visit source) {

        target.setDisease(source.getDisease());

        if (source.getPerson() != null) {
            Person person = DatabaseHelper.getPersonDao().queryForId(source.getPerson().getId());
            target.setPerson(PersonDtoHelper.toReferenceDto(person));
        } else {
            target.setPerson(null);
        }

        if (source.getSymptoms() != null) {
            Symptoms symptoms = DatabaseHelper.getSymptomsDao().queryForId(source.getSymptoms().getId());
            SymptomsDto symptomsDto = symptomsDtoHelper.adoToDto(symptoms);
            target.setSymptoms(symptomsDto);
        } else {
            target.setSymptoms(null);
        }

        target.setVisitDateTime(source.getVisitDateTime());
        target.setVisitRemarks(source.getVisitRemarks());
        target.setVisitStatus(source.getVisitStatus());

        if (source.getVisitUser() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(source.getVisitUser().getId());
            target.setVisitUser(UserDtoHelper.toReferenceDto(user));
        } else {
            target.setVisitUser(null);
        }

        target.setReportLat(source.getReportLat());
        target.setReportLon(source.getReportLon());
        target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());
    }
}
