/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.backend.region.Region;

public final class InfrastructureDataImporter {
	
    @FunctionalInterface
    public interface RegionConsumer {
        public void consume(String regionName, String epidCode, Integer population, Float growthRate);
    }

    @FunctionalInterface
    public interface DistrictConsumer {
        public void consume(String regionName, String districtName, String epidCode, Integer population, Float growthRate);
    }
    
    @FunctionalInterface
    public interface CommunityConsumer {
        public void consume(String regionName, String districtName, String communityName);
    }

    @FunctionalInterface
    public interface FacilityConsumer {
        public void consume(String regionName, String districtName, String communityName,
        		String facilityName, String city, String address, Double latitude, Double longitude, 
        		FacilityType type, boolean publicOwnership);
    }

	private static final Logger logger = LoggerFactory.getLogger(InfrastructureDataImporter.class);
	
	public static void importRegions(String countryName, RegionConsumer regionConsumer) {
		
		String resourceFileName = "/regions/" + countryName + "/regions.csv";
		InputStream stream = InfrastructureDataImporter.class.getResourceAsStream(resourceFileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		
		NumberFormat populationFormat = NumberFormat.getIntegerInstance(Locale.ENGLISH);
		NumberFormat growthRateFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);

		String currentLine = null;
		try {
			currentLine = reader.readLine();
			if (!currentLine.equals("region;epidcode;population;growthrate")) {
				throw new IllegalArgumentException("Resource file does not match expected format. "
						+ "First line has to be 'region;epidcode;population;growthrate'. " + resourceFileName);
			}

			while (reader.ready()) {
				currentLine = reader.readLine();
				String[] columns = currentLine.split(";");
				
				String regionName = columns[0];
				String epidCode = columns.length > 1 ? columns[1] : null;
				Number populationNumber = columns.length > 2 ? populationFormat.parse(columns[2]) : null;
				Number growthRateNumber = columns.length > 3 ? growthRateFormat.parse(columns[3]) : null;
				Integer population = populationNumber != null ? populationNumber.intValue() : null;
				Float growthRate = growthRateNumber != null ? growthRateNumber.floatValue() : null;

				regionConsumer.consume(regionName, epidCode, population, growthRate);
			}
			
			stream.close();
		} catch (Exception e) {
			throw new IllegalArgumentException("Exception while reading resource file '" + resourceFileName + "' line '" + currentLine + "'", e);
		}
	}
	
	public static void importDistricts(String countryName, DistrictConsumer districtConsumer) {
		
		String resourceFileName = "/regions/" + countryName + "/districts.csv";
		InputStream stream = InfrastructureDataImporter.class.getResourceAsStream(resourceFileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		
		NumberFormat populationFormat = NumberFormat.getIntegerInstance(Locale.ENGLISH);
		NumberFormat growthRateFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
		
		String currentLine = null;
		try {
			currentLine = reader.readLine();
			if (!currentLine.equals("region;district;epidcode;population;growthrate")) {
				throw new IllegalArgumentException("Resource file does not match expected format. "
						+ "First line has to be 'region;district;epidcode;population;growthrate'. " + resourceFileName);
			}

			while (reader.ready()) {
				currentLine = reader.readLine();
				String[] columns = currentLine.split(";");
				
				String regionName = columns[0];
				String districtName = columns[1];
				String epidCode = columns.length > 2 ? columns[2] : null;
				Number populationNumber = columns.length > 3 ? populationFormat.parse(columns[3]) : null;
				Number growthRateNumber = columns.length > 4 ? growthRateFormat.parse(columns[4]) : null;
				Integer population = populationNumber != null ? populationNumber.intValue() : null;
				Float growthRate = growthRateNumber != null ? growthRateNumber.floatValue() : null;

				districtConsumer.consume(regionName, districtName, epidCode, population, growthRate);
			}
			
			stream.close();
		} catch (Exception e) {
			throw new IllegalArgumentException("Exception while reading resource file '" + resourceFileName + "' line '" + currentLine + "'", e);
		}
	}

	public static void importCommunities(String countryName, CommunityConsumer communityConsumer) {
		
		String resourceFileName = "/regions/" + countryName + "/communities.csv";
		InputStream stream = InfrastructureDataImporter.class.getResourceAsStream(resourceFileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		
		String currentLine = null;
		try {
			currentLine = reader.readLine();
			if (!currentLine.equals("region;district;community")) {
				throw new IllegalArgumentException("Resource file does not match expected format. "
						+ "First line has to be 'region;district;community'. " + resourceFileName);
			}

			while (reader.ready()) {
				currentLine = reader.readLine();
				String[] columns = currentLine.split(";");
				
				String regionName = columns[0];
				String districtName = columns[1];
				String communityName = columns[2];

				communityConsumer.consume(regionName, districtName, communityName);
			}
			
			stream.close();
		} catch (Exception e) {
			throw new IllegalArgumentException("Exception while reading resource file '" + resourceFileName + "' line '" + currentLine + "'", e);
		}
	}
	
	public static void importFacilities(String countryName, Region region, FacilityConsumer facilityConsumer) {
		
		String resourceFileName = "/facilities/"+countryName + "/"+region.getName()+".csv";
		InputStream stream = InfrastructureDataImporter.class.getResourceAsStream(resourceFileName);
		if (stream == null) {
			logger.warn("No facilities for region '" + region.getName() + "' defined.");
			return;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		DecimalFormat geoCoordFormat = new DecimalFormat();
		DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols();
		decimalSymbols.setDecimalSeparator('.');
		decimalSymbols.setGroupingSeparator(',');
		geoCoordFormat.setDecimalFormatSymbols(decimalSymbols);
		
		String currentLine = null;
		try {
			currentLine = reader.readLine();
			if (!currentLine.startsWith("LGA") // old data
					&& !currentLine.equals("district;community;city;facility;type;ownership;address;longitude;latitude")) {
				throw new IllegalArgumentException("Resource file does not match expected format. "
						+ "First line has to be 'district;community;city;facility;type;ownership;address;longitude;latitude'. " + resourceFileName);
			}

			while (reader.ready()) {
				currentLine = reader.readLine();
				String[] columns = currentLine.split(";");

				String districtName = columns[0];
				String communityName = columns[1];
				String cityName = columns[2];
				String facilityName = columns[3];
				String facilityTypeString = columns.length > 4 ? columns[4] : "";
				String ownershipString = columns.length > 5 ? columns[5] : "";
				String address = columns.length > 6 ? columns[6] : null;
				String longitudeString = columns.length > 7 ? columns[7] : "";
				String latitudeString = columns.length > 8 ? columns[8] : "";
				
				Double latitude = null, longitude = null;
				
				try {
					if (!longitudeString.isEmpty()) {
						longitude = geoCoordFormat.parse(longitudeString).doubleValue();
					}
					if (!latitudeString.isEmpty()) {
						latitude = geoCoordFormat.parse(latitudeString).doubleValue();
					}
				} catch (ParseException e) {
					logger.warn("Failed parsing geo coordinates for facility '" + facilityName + "' in " + resourceFileName, e);
				}

				FacilityType facilityType = null;
				try { 
					facilityType = FacilityType.valueOf(facilityTypeString);
				} catch (IllegalArgumentException e) { 
					// that's ok
				}
				
				boolean publicOwnership = "PUBLIC".equalsIgnoreCase(ownershipString);

				facilityConsumer.consume(region.getName(), districtName, communityName, facilityName, cityName, address, 
						latitude, longitude, facilityType, publicOwnership);
			}
			
			stream.close();
		} catch (Exception e) {
			throw new IllegalArgumentException("Exception while reading resource file '" + resourceFileName + "' line '" + currentLine + "'", e);
		}
	}
	
	public static void importLaboratories(String countryName, FacilityConsumer facilityConsumer) {
		
		String resourceFileName = "/facilities/" + countryName + "/Laboratories.csv";
		InputStream stream = InfrastructureDataImporter.class.getResourceAsStream(resourceFileName);
		if (stream == null) {
			logger.warn("No laboratories defined.");
			return;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		
		String currentLine = null;
		try {
			currentLine = reader.readLine();
			if (!currentLine.equals("region;city;name;ownership")) {
				throw new IllegalArgumentException("Resource file does not match expected format. "
						+ "First line has to be 'region;city;name;ownership'. " + resourceFileName);
			}
			
			while (reader.ready()) {
				currentLine = reader.readLine();
				String[] columns = currentLine.split(";");

				String regionName = columns[0];
				String cityName = columns[1];
				String facilityName = columns[2];
				boolean publicOwnership = "PUBLIC".equalsIgnoreCase(columns[3]);
				
				facilityConsumer.consume(regionName, null, null, facilityName, cityName, null, null, null, FacilityType.LABORATORY, publicOwnership);
			}
			
			stream.close();
		} catch (Exception e) {
			throw new IllegalArgumentException("Exception while reading resource file '" + resourceFileName + "' line '" + currentLine + "'", e);
		}
	}
}
