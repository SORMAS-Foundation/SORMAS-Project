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
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.user.UserHelper;
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
					String districtName = columns[0].substring(0, 1).toUpperCase() + columns[0].substring(1).toLowerCase();
					district.setName(districtName);
					district.setRegion(region);
					district.setCommunities(new ArrayList<Community>());
					region.getDistricts().add(district);
				}
				
				if (columns[1].length() > 0) {
					community = new Community();
					String communityName = columns[1].substring(0, 1).toUpperCase() + columns[1].substring(1).toLowerCase();
					community.setName(communityName);
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
				
				if (region != null && columns[0].length() > 0) {
					district = null;
					for (District d : region.getDistricts()) {
						if (columns[0].equalsIgnoreCase(d.getName())) {
							district = d;
							break;
						}
					}
				}
				
				if (district != null && columns[1].length() > 0) {
					community = null;
					for (Community c : district.getCommunities()) {
						if (columns[1].equalsIgnoreCase(c.getName())) {
							community = c;
							break;
						}
					}
				}
				
				Facility facility = new Facility();
				String facilityName = columns[2].substring(0, 1).toUpperCase() + columns[2].substring(1).toLowerCase();
				facility.setName(facilityName);
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
	
	public static List<Facility> importLaboratories(List<Region> regions) {
		
		List<Facility> result = new ArrayList<Facility>();
		
		InputStream stream = MockDataGenerator.class.getResourceAsStream("/facilities/Laboratories.csv");
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		
		Region region = null;
		try {
			while (reader.ready()) {
				String line = reader.readLine();
				String[] columns = line.split(";");
				
				// region
				if(columns[0].length() > 0) {
					region = null;
					for(Region r : regions) {
						if(columns[0].equalsIgnoreCase(r.getName())) {
							region = r;
							break;
						}
					}
				}
				
				Facility facility = new Facility();
				String facilityName = columns[2];
				facility.setName(facilityName);
				facility.setType(FacilityType.LABORATORY);
				facility.setPublicOwnership("PUBLIC".equalsIgnoreCase(columns[3]));
				Location location = new Location();
				location.setRegion(region);
				String cityName = columns[1];
				location.setCity(cityName);
				facility.setLocation(location);
				result.add(facility);				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}

