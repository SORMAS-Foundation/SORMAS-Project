package de.symeda.sormas.ui.caze;

import static org.mockito.Mockito.when;

import java.security.Principal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.Field;
import com.vaadin.util.CurrentInstance;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;


@RunWith(MockitoJUnitRunner.class)
public class CaseControllerTest extends AbstractBeanTest {
	
	@Mock
	private VaadinServletRequest request; 
	
	@Before
    public void initUI() throws Exception {

    	creator.createUser(null, null, null, "ad", "min", UserRole.ADMIN, UserRole.NATIONAL_USER);

        when(request.getUserPrincipal()).thenReturn(new Principal() {
			@Override
			public String getName() {
				return "admin";
			}
		});

        CurrentInstance.setInheritable(VaadinRequest.class, request);
        
        // TODO init UI
    }
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetCaseCreateComponent() {

		CaseController controller = new CaseController();
		CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = controller.getCaseCreateComponent(null,
				null, null, null);

		// TODO add UI class and attach form, so everything works as expected
		CaseCreateForm caseCreateForm = caseCreateComponent.getWrappedComponent();
		((Field<String>) caseCreateForm.getField(CaseCreateForm.FIRST_NAME)).setValue("Steff");
		((Field<String>) caseCreateForm.getField(CaseCreateForm.LAST_NAME)).setValue("Steffson");
		((Field<Disease>) caseCreateForm.getField(CaseDataDto.DISEASE)).setValue(Disease.EVD);
		
		caseCreateComponent.commit();
	}
}
