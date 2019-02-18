package de.symeda.sormas.api.i18n;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.junit.Test;

/**
  Intentionally named Generator because we don't want Maven to execute this
 * class automatically.
 */
public class I18nConstantGenerator {
	
	@Test
	public void generateI18nConstants() throws FileNotFoundException, IOException {
		generateI18nConstantClass("captions.properties", "Captions", true);
		generateI18nConstantClass("strings.properties", "Strings", false);
		generateI18nConstantClass("validations.properties", "Validations", false);
	}
	
	private void generateI18nConstantClass(String propertiesFileName, String outputClassName, boolean ignoreChildren) throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        InputStream inputStream = I18nProperties.class.getClassLoader().getResourceAsStream(propertiesFileName);
        if (null != inputStream ){
            properties.load(inputStream);
        }
        
        Enumeration<?> e = properties.propertyNames();
		String filePath = "src/main/java/de/symeda/sormas/api/i18n/" + outputClassName + ".java";
        FileWriter writer = new FileWriter(filePath, false);
        writer.write("package de.symeda.sormas.api.i18n;\n\n");
        writer.write("public interface " + outputClassName + " {\n\n");
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            if (ignoreChildren 
            		&& (key.contains(".") || key.contains("-") || key.contains("_"))) {
            	continue;
            }
            String constant =  key.replaceAll("[\\.\\-]", "_");
            writer.write("\tpublic static String "+constant+" = \""+key+"\";\n");
        }
        writer.write(" }\n");
        writer.flush();      
        writer.close();
	}
}
