package de.symeda.sormas.ui;

public class qucik_test {
	 public static void main (String [] args)
	    {
		 String decimalNumber = "22.3";
		 
	        if (decimalNumber.contains("."))
	        {
	        	if(decimalNumber.length() - decimalNumber.indexOf(".")-1 == 2) {
	            System.out.print ("within Range");
	        	}else {
	        		System.out.print ("not ithin range!");
	        		
	        	}
	        }
	        else
	        {
	            System.out.print ("Your number is an integer!");
	        }
	    }
	//String.parseString(decimalNumber).contains(".");
	
	
}
