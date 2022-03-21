/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.symeda.sormas.api.user.UserRight;

public class UserRightMappingTest {

	@Test
	public void testUserRightsInGlassfishEjbJarXml() throws ParserConfigurationException, IOException, SAXException {
		String[] userRights = Arrays.stream(UserRight.values()).map(UserRight::name).collect(Collectors.toList()).toArray(new String[] {});

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

		// parse XML file
		DocumentBuilder db = dbf.newDocumentBuilder();
		db.setEntityResolver((publicId, systemId) -> new InputSource(new StringReader("")));
		Document doc = db.parse(new File("src/main/resources/META-INF/glassfish-ejb-jar.xml"));

		// optional, but recommended
		// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();

		NodeList roleTags = doc.getElementsByTagName("security-role-mapping");

		List<String> roleNamesInXml = new ArrayList<>(roleTags.getLength());
		List<String> groupNamesInXml = new ArrayList<>(roleTags.getLength());
		for (int i = 0; i < roleTags.getLength(); i++) {
			Node tag = roleTags.item(i);
			Element roleNameTag = (Element) ((Element) tag).getElementsByTagName("role-name").item(0);
			String roleName = roleNameTag.getTextContent();

			if (!UserRight._SYSTEM.equals(roleName)) {
				roleNamesInXml.add(roleName);

				Element groupNameTag = (Element) ((Element) tag).getElementsByTagName("group-name").item(0);
				groupNamesInXml.add(groupNameTag.getTextContent());
			}
		}

		MatcherAssert.assertThat(roleNamesInXml, Matchers.containsInAnyOrder(userRights));
		MatcherAssert.assertThat(groupNamesInXml, Matchers.containsInAnyOrder(userRights));
	}
}
