/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.environment.environmentsample;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationException;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.postgresql.util.PGobject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.disease.PathogenConverter;

public class RequestedPathogensTestsDataType implements UserType {

	public static final String TYPE_NAME = "requestedpathogens";

	private final PathogenConverter pathogenConverter = new PathogenConverter();
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public int[] sqlTypes() {
		return new int[] {
			Types.OTHER };
	}

	@Override
	public Class returnedClass() {
		return Set.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return DataHelper.equal(x, y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		if (x == null) {
			return 0;
		}

		return x.hashCode();
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
		throws HibernateException, SQLException {
		PGobject o = (PGobject) rs.getObject(names[0]);
		if (o != null && o.getValue() != null) {
			try {
				return Stream.of(objectMapper.readValue(o.getValue(), String[].class))
					.map(pathogenConverter::convertToEntityAttribute)
					.collect(Collectors.toSet());
			} catch (JsonProcessingException e) {
				throw new IllegalArgumentException("The value [" + o.getValue() + "] cannot be converted to a set of pathogens", e);
			}
		}

		return new HashSet<>();
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
		throws HibernateException, SQLException {
		if (value == null) {
			st.setNull(index, Types.OTHER);
		} else {
			try {
				Set<String> pathogenValues =
					((Set<Pathogen>) value).stream().map(pathogenConverter::convertToDatabaseColumn).collect(Collectors.toSet());
				st.setObject(index, objectMapper.writeValueAsString(pathogenValues), Types.OTHER);
			} catch (JsonProcessingException e) {
				throw new IllegalArgumentException("The value [" + value + "] is not a set of pathogens and/or cannot be converted to json", e);
			}
		}
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		if (value == null) {
			return null;
		}

		if (!(value instanceof Set)) {
			return null;
		}

		return ((Set<Pathogen>) value).stream().map(Pathogen::deepCopy).collect(Collectors.toSet());
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		Object copy = deepCopy(value);

		if (copy instanceof Serializable) {
			return (Serializable) copy;
		}

		throw new SerializationException(String.format("Cannot serialize '%s', %s is not Serializable.", value, value.getClass()), null);
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return deepCopy(cached);
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return deepCopy(original);
	}
}
