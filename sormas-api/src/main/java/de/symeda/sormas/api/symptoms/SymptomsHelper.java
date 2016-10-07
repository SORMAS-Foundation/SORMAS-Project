package de.symeda.sormas.api.symptoms;

import java.util.ArrayList;
import java.util.List;

public final class SymptomsHelper {

	
    /**
     * Returns a list for days in month (1-31)
     * @return
     */
    public static List<Float> getTemperatureValues() {
		List<Float> x = new ArrayList<Float>();
		for(int i=350; i<=440;i++) {
			x.add(i / 10.0f);
		}
		return x;
	}
    
    public static String getTemperatureString(float value) {
    	return String.format("%.1f °C", value);
    }
}
