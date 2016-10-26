package de.symeda.sormas.api.utils;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.symeda.sormas.api.Disease;

/**
 * Used to annotate which fields are visible for a specific disease.
 * Use the code generated in sormas-api/tools/Fields.xlsx to get annotated members 
 * 
 * @author Martin Wahnschaffe
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Diseases {
    Disease[] value() default {};

    public static class DiseasesConfiguration {

    	// TODO thread safety, etc.?!
    	private static final HashMap<Class<?>,HashMap<String,List<Disease>>> diseaseConfigCache = 
    			new HashMap<Class<?>,HashMap<String,List<Disease>>>(); 

    	public static boolean isDefinedOrMissing(Class<?> clazz, String propertyName, Disease disease) {
    		
    		if (isMissing(clazz, propertyName, disease)) {
    			return true;
    		}
    		
    		return isDefined(clazz, propertyName, disease);
    	}
    	
    	public static boolean isMissing(Class<?> clazz, String propertyName, Disease disease) {
			
    		if (!diseaseConfigCache.containsKey(clazz)) {
    			readDiseaseConfig(clazz);
    		}
        	
    		HashMap<String, List<Disease>> diseaseConfig = diseaseConfigCache.get(clazz);
    		if (!diseaseConfig.containsKey(propertyName)) {
    			// missing
    			return true;
    		}
    		
    		return false;
        }

    	public static boolean isDefined(Class<?> clazz, String propertyName, Disease disease) {
			
    		if (!diseaseConfigCache.containsKey(clazz)) {
    			readDiseaseConfig(clazz);
    		}
        	
    		HashMap<String, List<Disease>> diseaseConfig = diseaseConfigCache.get(clazz);
    		if (!diseaseConfig.containsKey(propertyName)) {
    			// missing
    			return false;
    		}
    		
    		// defined?
    		return diseaseConfig.get(propertyName).contains(disease);
        }

    	private static synchronized void readDiseaseConfig(Class<?> clazz) {
    		
    		HashMap<String,List<Disease>> diseaseConfig = new HashMap<String,List<Disease>>();
    		
    		for (Field field : clazz.getDeclaredFields()){

    			Annotation[] annotations = field.getDeclaredAnnotations();
    			for(Annotation annotation : annotations) {
    			    if(annotation instanceof Diseases) {
    			    	Disease[] diseases = ((Diseases)annotation).value();
   			    		diseaseConfig.put(field.getName(), Arrays.asList(diseases));
    			        break;
    			    }
    			}
    		}
    		
    		diseaseConfigCache.put(clazz, diseaseConfig);
    	}
    }
}
