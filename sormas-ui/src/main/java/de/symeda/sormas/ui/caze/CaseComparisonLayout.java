package de.symeda.sormas.ui.caze;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class CaseComparisonLayout extends HorizontalLayout {
	
	private static final Logger logger = LoggerFactory.getLogger(CaseComparisonLayout.class);

	public CaseComparisonLayout(String firstCaseUuid, String secondCaseUuid) {
		setMargin(true);

		VerticalLayout firstInfoLayout = buildCaseInfoLayout(firstCaseUuid, 1);
		CssStyles.style(firstInfoLayout, CssStyles.HSPACE_RIGHT_4);
		addComponent(firstInfoLayout);
		VerticalLayout secondInfoLayout = buildCaseInfoLayout(secondCaseUuid, 2);
		CssStyles.style(secondInfoLayout, CssStyles.HSPACE_LEFT_4);
		addComponent(secondInfoLayout);
	}

	private VerticalLayout buildCaseInfoLayout(String caseUuid, int position) {
		VerticalLayout caseInfoLayout = new VerticalLayout();
		caseInfoLayout.setSpacing(false);
		caseInfoLayout.setMargin(false);

		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
		PersonDto person = FacadeProvider.getPersonFacade().getPersonByUuid(caze.getPerson().getUuid());

		Label lblHeading = new Label(String.format(I18nProperties.getString(Strings.headingComparisonCase), position, 
				person.getFirstName() + " " + person.getLastName()));
		CssStyles.style(lblHeading, CssStyles.H2, CssStyles.VSPACE_4);
		lblHeading.setWidth(100, Unit.PERCENTAGE);
		caseInfoLayout.addComponent(lblHeading);
		Label lblUuid = new Label(caze.getUuid());
		CssStyles.style(lblUuid, CssStyles.H3, CssStyles.VSPACE_2, CssStyles.VSPACE_TOP_NONE);
		caseInfoLayout.addComponent(lblUuid);

		try {
			PropertyDescriptor[] pds = Introspector.getBeanInfo(CaseDataDto.class, EntityDto.class).getPropertyDescriptors();
			for (PropertyDescriptor pd : pds) {
				if (pd.getReadMethod() == null|| pd.getWriteMethod() == null) {
					continue;
				}
				if (EntityDto.class.isAssignableFrom(pd.getPropertyType())) {
					continue;
				}
				if (PersonReferenceDto.class.isAssignableFrom(pd.getPropertyType())) {
					continue;
				}

				Label lblCaption = new Label(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, pd.getName()));
				CssStyles.style(lblCaption, CssStyles.LABEL_BOLD);
				lblCaption.setWidth(100, Unit.PERCENTAGE);
				caseInfoLayout.addComponent(lblCaption);
				Object value = pd.getReadMethod().invoke(caze);
				Label lblValue = new Label(value != null ? value.toString() : "-");
				CssStyles.style(lblValue, CssStyles.VSPACE_4);
				lblValue.setWidth(100, Unit.PERCENTAGE);
				caseInfoLayout.addComponent(lblValue);
			}

			pds = Introspector.getBeanInfo(PersonDto.class, EntityDto.class).getPropertyDescriptors();
			for (PropertyDescriptor pd : pds) {
				if (pd.getReadMethod() == null|| pd.getWriteMethod() == null) {
					continue;
				}
				if (EntityDto.class.isAssignableFrom(pd.getPropertyType()) && !LocationDto.class.isAssignableFrom(pd.getPropertyType())) {
					continue;
				}

				Label lblCaption = new Label(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, pd.getName()));
				CssStyles.style(lblCaption, CssStyles.LABEL_BOLD);
				lblCaption.setWidth(100, Unit.PERCENTAGE);
				caseInfoLayout.addComponent(lblCaption);
				Object value = pd.getReadMethod().invoke(person);
				Label lblValue = new Label(value != null ? value.toString() : "-");
				CssStyles.style(lblValue, CssStyles.VSPACE_4);
				lblValue.setWidth(100, Unit.PERCENTAGE);
				caseInfoLayout.addComponent(lblValue);
			}
		} catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
			logger.error("Exception while building case comparison layout for uuid " + caseUuid);
			return caseInfoLayout;
		}

		return caseInfoLayout;
	}

}
