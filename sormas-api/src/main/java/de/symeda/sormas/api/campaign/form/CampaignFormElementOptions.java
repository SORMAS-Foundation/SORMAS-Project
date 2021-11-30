package de.symeda.sormas.api.campaign.form;

import java.util.*;

public class CampaignFormElementOptions {
public static String opt1 = null;
public static String opt2 = null;
public static String opt3 = null;
public static String opt4 = null;
public static String opt5 = null;
public static String opt6 = null;
public static String opt7 = null;
public static String opt8 = null;
public static String opt9 = null;
public static String opt10 = null;

public static Integer max = null;
public static Integer min = null;

	
	




public static Integer getMax() {
	return max;
}

public static void setMax(Integer max) {
	CampaignFormElementOptions.max = max;
}

public static Integer getMin() {
	return min;
}

public static void setMin(Integer min) {
	CampaignFormElementOptions.min = min;
}

public static String getOpt1() {
	return opt1;
}

public void setOpt1(String opt1) {
	CampaignFormElementOptions.opt1 = opt1;
}

public static String getOpt2() {
	return opt2;
}

public void setOpt2(String opt2) {
	CampaignFormElementOptions.opt2 = opt2;
}

public static String getOpt3() {
	return opt3;
}

public void setOpt3(String opt3) {
	CampaignFormElementOptions.opt3 = opt3;
}

public static String getOpt4() {
	return opt4;
}

public void setOpt4(String opt4) {
	CampaignFormElementOptions.opt4 = opt4;
}

public static String getOpt5() {
	return opt5;
}

public void setOpt5(String opt5) {
	CampaignFormElementOptions.opt5 = opt5;
}

public static String getOpt6() {
	return opt6;
}

public void setOpt6(String opt6) {
	CampaignFormElementOptions.opt6 = opt6;
}

public static String getOpt7() {
	return opt7;
}

public void setOpt7(String opt7) {
	CampaignFormElementOptions.opt7 = opt7;
}

public static String getOpt8() {
	return opt8;
}

public void setOpt8(String opt8) {
	CampaignFormElementOptions.opt8 = opt8;
}

public static String getOpt9() {
	return opt9;
}

public void setOpt9(String opt9) {
	CampaignFormElementOptions.opt9 = opt9;
}

public static String getOpt10() {
	return opt10;
}

public void setOpt10(String opt10) {
	CampaignFormElementOptions.opt10 = opt10;
}

	//List Methods constraints
	public static List<String> optionsListValues;
	
	
	public static  List<String> getOptionsListValues() {
		return optionsListValues;
	}

	public void setOptionsListValues(List<String> optionsListValues) {
		this.optionsListValues = optionsListValues;
	}

	
	//List Methods constraints
		public static List<String> constraintsListValues;
		
		
		public static  List<String> getConstraintsListValues() {
			return constraintsListValues;
		}

		public void setConstraintsListValues(List<String> constraintsListValues) {
			this.constraintsListValues = constraintsListValues;
		}

		
		
	@Override
	public String toString() {
		return "CampaignFormElementOptions [getOptionsListValues()=" + getOptionsListValues() + "]";
	}

	public List CampaignFormElementOptions() {
		return getOptionsListValues();
		// TODO Auto-generated constructor stub
	}
	
	

}
