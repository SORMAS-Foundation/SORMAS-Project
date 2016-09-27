package de.symeda.sormas.ui.utils;

import java.util.ArrayList;
import java.util.List;

public final class FieldHelper {

	public static List<Integer> getDaysInMonth() {
		List<Integer> x = new ArrayList<Integer>();
		for(int i=1; i<32;i++) {
			x.add(i);
		}
		return x;
	}
	public static List<Integer> getMonthsInYear() {
		List<Integer> x = new ArrayList<Integer>();
		for(int i=1; i<13;i++) {
			x.add(i);
		}
		return x;
	}
	
}
