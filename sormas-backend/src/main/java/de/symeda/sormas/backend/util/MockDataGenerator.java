package de.symeda.sormas.backend.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
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
    	caze.setCaseStatus(generateStatus());
    	caze.setDisease(Disease.EBOLA);
		caze.setReportDate(new Date());
        return caze;
    }

    public static Person createPerson() {
    	Person person = new Person();
    	person.setFirstName(generateFirstName());
    	person.setLastName(generateLastName());
    	return person;
    }
    
    public static User createUser(UserRole userRole) {
    	User user = new User();
    	user.setFirstName(generateFirstName());
    	user.setLastName(generateLastName());
    	user.setUserRoles(new HashSet<UserRole>(Arrays.asList(userRole)));
    	user.setUserName(user.getFirstName() + user.getLastName());
    	user.setPassword("");
    	user.setSeed("");
    	return user;
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

	public static Region importRegion(String name) {
		
		InputStream stream = MockDataGenerator.class.getResourceAsStream("/facilities/"+name+".csv");
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		Region region = new Region();
		region.setName(name);
		region.setDistricts(new ArrayList<District>());

		District district = null;
		Community community = null;
		try {
			while (reader.ready()) {
				String line = reader.readLine();
				String[] columns = line.split(";");
				if (columns.length < 2)
					continue;
				
				if (columns[0].length() > 0) {
					district = new District();
					district.setName(columns[0]);
					district.setRegion(region);
					district.setCommunities(new ArrayList<Community>());
					region.getDistricts().add(district);
				}
				
				if (columns[1].length() > 0) {
					community = new Community();
					community.setName(columns[1]);
					community.setDistrict(district);
					district.getCommunities().add(community);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return region;
	}
	
	public static List<Facility> importFacilities(Region region) {
		
		List<Facility> result = new ArrayList<Facility>();
		
		InputStream stream = MockDataGenerator.class.getResourceAsStream("/facilities/"+region.getName()+".csv");
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		District district = null;
		Community community = null;
		try {
			while (reader.ready()) {
				String line = reader.readLine();
				String[] columns = line.split(";");
				if (columns.length < 5)
					continue;
				
				if (columns[0].length() > 0) {
					district = null;
					for (District d : region.getDistricts()) {
						if (columns[0].equals(d.getName())) {
							district = d;
							break;
						}
					}
				}
				
				if (columns[1].length() > 0) {
					community = null;
					for (Community c : district.getCommunities()) {
						if (columns[1].equals(c.getName())) {
							community = c;
							break;
						}
					}
				}
				
				Facility facility = new Facility();
				facility.setName(columns[2]);
				facility.setType(FacilityType.valueOf(columns[3]));
				facility.setPublicOwnership("PUBLIC".equals(columns[4]));
				Location location = new Location();
				location.setRegion(region);
				location.setDistrict(district);
				location.setCommunity(community);
				facility.setLocation(location);
				result.add(facility);				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}

