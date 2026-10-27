package de.symeda.sormas.backend.docgeneration.valueresolvers;

import static de.symeda.sormas.backend.docgeneration.DocumentTemplateFacadeEjb.BASENAME_PATTERN;

import java.util.Properties;
import java.util.regex.Matcher;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.EntityDtoAccessHelper;
import de.symeda.sormas.api.uuid.HasUuid;

public abstract class AbstractRootValuePropertyFiller implements RootEntityPropertyFiller {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final EntityDtoAccessHelper.IReferenceDtoResolver referenceDtoResolver;

	public AbstractRootValuePropertyFiller(EntityDtoAccessHelper.IReferenceDtoResolver referenceDtoResolver) {
		this.referenceDtoResolver = referenceDtoResolver;
	}

	@Override
	public void fillValueInto(@NotNull String propertyKey, @NotNull Properties properties) {
		String propertySeparator = propertyKey.contains("_") ? "_" : ".";

		String variableBaseName = getVariableBaseName(propertyKey);

		String propertyPath = AbstractRootValuePropertyFiller.buildPropertyPath(propertyKey, variableBaseName, propertySeparator);

		Object propertyValue;
		try {
			propertyValue = EntityDtoAccessHelper.getPropertyPathValueString(getEntity(), propertyPath, referenceDtoResolver);
		} catch (Exception e) {
			logger.error("Error while accessing to property: [{}].", propertyKey, e);
			propertyValue = String.format("*** Error for property: [%s] ***", propertyKey);
		}
		if (propertyValue != null) {
			properties.put(propertyKey, propertyValue);
		}
	}

	private static @NotNull String buildPropertyPath(String propertyKey, String variableBaseName, String propertySeparator) {
		String propertyPath = propertyKey.replaceFirst("(?i)" + variableBaseName + "[" + propertySeparator + "]", "");
		if (!".".equals(propertySeparator)) {
			propertyPath = propertyPath.replaceAll(propertySeparator, ".");
		}
		return propertyPath;
	}

	@Nullable
	protected abstract HasUuid getEntity();

	private String getVariableBaseName(String propertyKey) {
		String propertyKeyLowerCase = propertyKey.toLowerCase();
		Matcher matcher = BASENAME_PATTERN.matcher(propertyKeyLowerCase);
		return matcher.matches() ? matcher.group(1) : "";
	}
}
