package de.symeda.sormas.backend.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.user.UserHelper;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;

public class MockDataGenerator {
	
    private static final Random random = new Random(1);

    private static String[] word1 = new String[] { "The art of", "Mastering",
            "The secrets of", "Avoiding", "For fun and profit: ",
            "How to fail at", "10 important facts about",
            "The ultimate guide to", "Book of", "Surviving", "Encyclopedia of",
            "Very much", "Learning the basics of", "The cheap way to",
            "Being awesome at", "The life changer:", "The Vaadin way:",
            "Becoming one with", "Beginners guide to",
            "The complete visual guide to", "The mother of all references:" };

    private static String[] word2 = new String[] { "gardening",
            "living a healthy life", "designing tree houses", "home security",
            "intergalaxy travel", "meditation", "ice hockey",
            "children's education", "computer programming", "Vaadin TreeTable",
            "winter bathing", "playing the cello", "dummies", "rubber bands",
            "feeling down", "debugging", "running barefoot",
            "speaking to a big audience", "creating software", "giant needles",
            "elephants", "keeping your wife happy" };
    
    
    private static String[] firstnames = new String[] { "Nala",
    		"Amara", "Ayana", "Nia", "Imani", "Khari",
    		"Adisa", "Akachi", "Jaheem", "Amare", "Adebowale",
    		"Jabari", "Abioye", "Ebele", "Sanaa", "Afia"};
    private static String[] lastnames = new String[] { 
    		"Azikiwe","Chahine", "Bello", "Cisse", "Akintola", "Okotie-Eboh", "Nzeogwu", "Onwuatuegwu", "Okafor", "Contee", "Okeke", "Conteh", "Okoye", 
		    "Diallo", "Obasanjo", "Babangida", "Buhari", "Dimka", "Toure", "Diya", "Odili", "Ibori", "Igbinedion", "Alamieyeseigha", "Asari-Dokubo", 
		    "Jalloh", "Anikulapo-Kuti","Iwu", "Anenih", "Mensah", "Biobaku","Tinibu", "Sesay", "Akinyemi", "Akiloye", "Adeyemi", 
		    "Adesida", "Omehia", "Sekibo", "Amaechi", "Bankole", "Nnamani", "Turay", "Okadigbo", "Yeboah", "Ojukwu"};

    public static List<Case> createCases() {
        List<Case> cases = new ArrayList<Case>();
        for (int i = 0; i < 20; i++) {
            Case p = createCase();
            cases.add(p);
        }
        return cases;
    }

    private static Case createCase() {
    	Case caze = new Case();
    	caze.setDescription(generateName());
    	caze.setCaseClassification(generateStatus());
    	caze.setDisease(Disease.EVD);
		caze.setReportDate(new Date());
        return caze;
    }

    public static Person createPerson() {
    	Person person = new Person();
    	person.setFirstName(generateFirstName());
    	person.setLastName(generateLastName());
    	return person;
    }
    
    public static User createUser(UserRole userRole, String firstName, String lastName, String password) {
    	User user = new User();
    	user.setFirstName(firstName);
    	user.setLastName(lastName);
    	if (userRole != null) {
    		user.setUserRoles(new HashSet<UserRole>(Arrays.asList(userRole)));
    	}
    	user.setUserName(UserHelper.getSuggestedUsername(user.getFirstName(), user.getLastName()));
		user.setSeed(PasswordHelper.createPass(16));
		user.setPassword(PasswordHelper.encodePassword(password, user.getSeed()));
    	return user;
    }
    
    
    private static CaseClassification generateStatus() {
        return CaseClassification.values()[random.nextInt(CaseClassification.values().length)];
    }

    private static String generateName() {
        return word1[random.nextInt(word1.length)] + " "
                + word2[random.nextInt(word2.length)];
    }

	public static String generateFirstName() {
	    return firstnames[random.nextInt(firstnames.length)];
	}
	
	public static String generateLastName() {
		return lastnames[random.nextInt(lastnames.length)];
	}
}

