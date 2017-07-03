package de.symeda.sormas.backend.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.junit.Test;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.symptoms.Symptoms;

public class DtoAdoCodeGenerator {

	Class<? extends AbstractDomainObject> ado = Symptoms.class;
	Class<? extends DataTransferObject> dto = SymptomsDto.class;

	@Test
	public void generateCopyJava() throws IntrospectionException {

		System.out.println("\n\ngenerateCopyJava:\n");

		for (PropertyDescriptor property : Introspector.getBeanInfo(dto).getPropertyDescriptors()){

			if (property.getWriteMethod() == null) {
				continue;
			}
			
			String propertyName = property.getName();
			if (DataTransferObject.UUID.equals(propertyName)
					|| DataTransferObject.CHANGE_DATE.equals(propertyName)
					|| DataTransferObject.CREATION_DATE.equals(propertyName)) {
				continue;
			}
			
			try {
				Method toWriteMethod = ado.getMethod(property.getWriteMethod().getName(), property.getWriteMethod().getParameterTypes());
				System.out.println("target." + toWriteMethod.getName() + "(source." + property.getReadMethod().getName() + "());");
			} catch (NoSuchMethodException e) {
				System.out.println("target." + property.getWriteMethod().getName() + "(source." + property.getReadMethod().getName() + "());");
			} catch (SecurityException e) {
				System.out.println("target." + property.getWriteMethod().getName() + "(source." + property.getReadMethod().getName() + "());");
			}
		}
	}
	
	@Test
	public void generateNotMatchingMembersList() throws IntrospectionException {

		System.out.println("\n\ngenerateNotMatchingMembersList:\n");
		
		for (PropertyDescriptor property : Introspector.getBeanInfo(ado).getPropertyDescriptors()){

			if (property.getWriteMethod() == null) {
				continue;
			}
			
			String propertyName = property.getName();
			if (AbstractDomainObject.UUID.equals(propertyName)
					|| AbstractDomainObject.ID.equals(propertyName)
					|| AbstractDomainObject.CHANGE_DATE.equals(propertyName)
					|| AbstractDomainObject.CREATION_DATE.equals(propertyName)) {
				continue;
			}
			
			try {
				dto.getMethod(property.getWriteMethod().getName(), property.getWriteMethod().getParameterTypes());
			} catch (NoSuchMethodException e) {
				System.out.println(property.getWriteMethod().getName());
			} catch (SecurityException e) {
			}
		}
	}
	@Test
	public void generatePropertyConstantsJava() throws IntrospectionException {

		System.out.println("\n\ngeneratePropertyConstantsJava:\n");

		for (PropertyDescriptor property : Introspector.getBeanInfo(dto).getPropertyDescriptors()){
			String propertyName = property.getName();
			
			if (DataTransferObject.UUID.equals(propertyName)
					|| DataTransferObject.CHANGE_DATE.equals(propertyName)
					|| DataTransferObject.CREATION_DATE.equals(propertyName)
					|| "class".equals(propertyName)) {
				continue;
			}

			System.out.println(String.format("public static final String %s = \"%s\";", 
					propertyName.replaceAll("(.)([A-Z])", "$1_$2").toUpperCase(), propertyName));
		}
	}
	
	// see sormas-api/tools/Fields.xlsx
//	@Test
//	public void generateI18nProperties() throws IntrospectionException {
//
//		String i18nPrefixName = ado.getSimpleName();
//		
//		for (PropertyDescriptor property : Introspector.getBeanInfo(dto).getPropertyDescriptors()){
//			String propertyName = property.getName();
//			
//			if (DataTransferObject.UUID.equals(propertyName)
//					|| DataTransferObject.CHANGE_DATE.equals(propertyName)
//					|| DataTransferObject.CREATION_DATE.equals(propertyName)
//					|| "class".equals(propertyName)) {
//				continue;
//			}
//			
//			String caption = propertyName.replaceAll("(.)([A-Z])", "$1 $2").toLowerCase();
//			caption = caption.substring(0, 1).toUpperCase() + caption.substring(1);
//
//			System.out.println(String.format("%s.%s = %s", 
//					i18nPrefixName, propertyName, caption));
//		}
//	}

}
