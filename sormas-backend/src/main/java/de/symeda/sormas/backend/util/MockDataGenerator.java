package de.symeda.sormas.backend.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

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
        for (int i = 0; i < 5; i++) {
            Case p = createCase();
            cases.add(p);
        }
        return cases;
    }

    private static Case createCase() {
    	Case dto = new Case();
        dto.setDescription(generateName());
        dto.setCaseStatus(generateStatus());
        dto.setDisease(Disease.EBOLA);
        return dto;
    }

    public static Person createPerson() {
    	Person dto = new Person();
    	dto.setFirstName(generateFirstName());
    	dto.setLastName(generateLastName());
    	return dto;
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

	public static List<Region> createRegions() {
		Region region = new Region();
		region.setName("ABIA");
		region.setDistricts(new ArrayList<District>());
		createDistrict(region, "ABA NORTH", "OSUSU WARD 1","OSUSU WARD 11","UMUOGOR WARD","UMUOLA WARD","URATTA WARD",
				"ARIARIA WARD","ASAOKPULOR WARD","EZIAMA WARD","ASAOKPUALAJA","INDUSTRIAL WARD","OGBOR WARD 1",
				"OGBOR WARD 11","OLD GRA WARD");
		createDistrict(region, "ABA SOUTH", "GLOCESTER","TOWN HALL WARD","IGWEBUIKE WARD","UMUOGELE WARD",
				"EZIUKWU WARD 1","EZIUKWU WARD 11","NGWA WARD 1","NGWA WARD 11","ENYIMBA WARD","ELUOHAZU WARD",
				"IHEORJI","OKPOROENYI","EKEOHA","ABA RIVER","ABA TOWN HALL","ASA WARD");
		return Arrays.asList(region);
	}
	
	public static District createDistrict(Region region, String name, String ...communityNames) {
		District district = new District();
		district.setName(name);
		List<Community> communities = new ArrayList<Community>();
		for	(String communityName : communityNames) {
			Community community = new Community();
			community.setName(communityName);
			community.setDistrict(district);
			communities.add(community);
		}
		district.setCommunities(communities);
		
		district.setRegion(region);
		region.getDistricts().add(district);
		return district;
	}
}

