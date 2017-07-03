package de.symeda.sormas.api.symptoms;

import javax.ejb.Remote;

@Remote
public interface SymptomsFacade {

    SymptomsDto saveSymptoms(SymptomsDto dto);

}
