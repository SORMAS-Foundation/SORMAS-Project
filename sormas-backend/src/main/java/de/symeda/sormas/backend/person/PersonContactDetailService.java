package de.symeda.sormas.backend.person;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.backend.common.BaseAdoService;

@Stateless
@LocalBean
public class PersonContactDetailService extends BaseAdoService<PersonContactDetail> {

	public PersonContactDetailService() {
		super(PersonContactDetail.class);
	}
}
