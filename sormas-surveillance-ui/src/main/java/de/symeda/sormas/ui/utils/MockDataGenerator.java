package de.symeda.sormas.ui.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.api.person.PersonDto;

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

    public static List<CaseDataDto> createCases() {
        List<CaseDataDto> cases = new ArrayList<CaseDataDto>();
        for (int i = 0; i < 5; i++) {
            CaseDataDto p = createCase();
            cases.add(p);
        }
        return cases;
    }

    private static CaseDataDto createCase() {
        CaseDataDto c = new CaseDataDto();
        c.setUuid(java.util.UUID.randomUUID().toString());
        c.setDescription(generateName());
        c.setCaseStatus(generateStatus());
        c.setDisease(Disease.EBOLA);
        return c;
    }

    public static PersonDto createPerson() {
    	PersonDto personDto = new PersonDto();
    	personDto.setUuid(java.util.UUID.randomUUID().toString());
    	personDto.setFirstName(generateFirstName());
    	personDto.setLastName(generateLastName());
    	return personDto;
    }
    
    
    private static CaseStatus generateStatus() {
        return CaseStatus.values()[random.nextInt(CaseStatus.values().length)];
    }

    private static String generateName() {
        return word1[random.nextInt(word1.length)] + " "
                + word2[random.nextInt(word2.length)];
    }

	private static String generateFirstName() {
	    return firstnames[random.nextInt(firstnames.length)];
	}
	private static String generateLastName() {
		return lastnames[random.nextInt(lastnames.length)];
	}
	
}

