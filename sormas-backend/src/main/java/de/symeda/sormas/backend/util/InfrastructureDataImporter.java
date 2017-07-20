package de.symeda.sormas.backend.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

public final class InfrastructureDataImporter {

	private static final Logger logger = LoggerFactory.getLogger(InfrastructureDataImporter.class);
	
	public static Region importRegion(String name) {
		
		String resourceFileName = "/facilities/"+name+".csv";
		InputStream stream = MockDataGenerator.class.getResourceAsStream(resourceFileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		
		Region region = new Region();
		region.setName(name);
		region.setDistricts(new ArrayList<District>());

		District district = null;
		Community community = null;
		try {
			String firstLine = reader.readLine();
			if (!firstLine.startsWith("LGA")) {
				throw new IllegalArgumentException("Resource file does not match expected format. First line should contain header starting with 'LGA'. " + resourceFileName);
			}

			while (reader.ready()) {
				String line = reader.readLine();
				String[] columns = line.split(";");
				
				String districtName = columns[0];
				String communityName = columns[1];
				
				if (district == null || !districtName.equals(district.getName())) {
					district = null;
					community = null;
					
					// try to find the district
					for (District existing : region.getDistricts()) {
						if (districtName.equals(existing.getName())) {
							district = existing;
							break;
						}
					}

					// or create it
					if (district == null) {
						district = new District();
						district.setName(districtName);
						district.setRegion(region);
						district.setCommunities(new ArrayList<Community>());
						region.getDistricts().add(district);
					}
				}
				
				if (community == null || !communityName.equals(community.getName())) {
					community = null;
					
					// try to find the community
					for (Community existing : district.getCommunities()) {
						if (communityName.equals(existing.getName())) {
							community = existing;
							break;
						}
					}

					// or create it
					if (community == null) {
						community = new Community();
						community.setName(communityName);
						community.setDistrict(district);
						district.getCommunities().add(community);
					}
				}
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("Exception while reading resource file: " + resourceFileName, e);
		}
		
		return region;
	}
	
	public static List<Facility> importFacilities(Region region) {
		
		List<Facility> result = new ArrayList<Facility>();
		
		String resourceFileName = "/facilities/"+region.getName()+".csv";
		InputStream stream = MockDataGenerator.class.getResourceAsStream(resourceFileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		DecimalFormat geoCoordFormat = new DecimalFormat();
		DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols();
		decimalSymbols.setDecimalSeparator(',');
		decimalSymbols.setGroupingSeparator('.');
		geoCoordFormat.setDecimalFormatSymbols(decimalSymbols);
		
		District district = null;
		Community community = null;
		try {
			String firstLine = reader.readLine();
			if (!firstLine.startsWith("LGA")) {
				throw new IllegalArgumentException("Resource file does not match expected format. First line should contain header starting with 'LGA'. " + resourceFileName);
			}

			while (reader.ready()) {
				String line = reader.readLine();
				String[] columns = line.split(";");

				String districtName = columns[0];
				String communityName = columns[1];
				String cityName = columns[2];
				String facilityName = columns[3];
				String facilityTypeString = columns.length > 4 ? columns[4] : "";
				String ownershipString = columns.length > 5 ? columns[5] : "";
				// String address = columns.length > 6 ? columns[6] : "";
				String longitudeString = columns.length > 7 ? columns[7] : "";
				String latitudeString = columns.length > 8 ? columns[8] : "";
				
				if (district == null || !districtName.equals(district.getName())) {
					district = null;
					community = null;
					// try to find the district
					for (District existing : region.getDistricts()) {
						if (districtName.equals(existing.getName())) {
							district = existing;
							break;
						}
					}
					if (district == null) {
						throw new IllegalArgumentException("Could not find district '" + districtName + "' in resource file " + resourceFileName);
					}
				}	
				
				if (community == null || !communityName.equals(community.getName())) {
					community = null;
					
					// try to find the community
					for (Community existing : district.getCommunities()) {
						if (communityName.equals(existing.getName())) {
							community = existing;
							break;
						}
					}
					if (community == null) {
						throw new IllegalArgumentException("Could not find community '" + communityName + "' in resource file " + resourceFileName);
					}
				}	
				
				Facility facility = new Facility();
				facility.setName(facilityName);
				try { 
					facility.setType(FacilityType.valueOf(facilityTypeString));
				} catch (IllegalArgumentException e) { 
					// that's ok
				}
				facility.setPublicOwnership("PUBLIC".equalsIgnoreCase(ownershipString));
				facility.setRegion(region);
				facility.setDistrict(district);
				facility.setCommunity(community);
				facility.setCity(cityName);
				try {
					if (!longitudeString.isEmpty()) {
						facility.setLongitude(geoCoordFormat.parse(longitudeString).floatValue());
					}
					if (!latitudeString.isEmpty()) {
						facility.setLatitude(geoCoordFormat.parse(latitudeString).floatValue());
					}
				} catch (ParseException e) {
					throw new IllegalArgumentException("Error parsing geo coordinates for facility '" + facilityName + "' in " + resourceFileName, e);
				}
				
				result.add(facility);				
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("Exception while reading resource file: " + resourceFileName, e);
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
				facility.setRegion(region);
				String cityName = columns[1];
				facility.setCity(cityName);
				result.add(facility);				
			}
		} catch (IOException e) {
			logger.info(e.getMessage(), e);
			e.printStackTrace();
		}
		
		return result;
	}
}
