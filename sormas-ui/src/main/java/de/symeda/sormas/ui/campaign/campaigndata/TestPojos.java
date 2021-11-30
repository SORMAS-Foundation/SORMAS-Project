package de.symeda.sormas.ui.campaign.campaigndata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
public class TestPojos {
public static Set<String> listIndexer(HashSet<Object> hashSet) {
			final Set<String> selected = new HashSet<String>();
		
			String dcs	=	hashSet.toString().replace("[[null, ", "").replace("]", "").replaceAll(", ", ",");
			 String strArray[] = dcs.split(",");
		        for (int i = 0; i < strArray.length; i++) {
		        	selected.add(strArray[i]);
		        }
		        
		return selected;
		}
		
		}  

	


