package fish.payara.security.openid;

import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

/**
 * Deactivate OpenIdExtension (all @Observes methods) because {@code bean-test} Framework with
 * {@link org.jboss.weld.bootstrap.events.BeforeBeanDiscoveryImpl} (version 2.1.2.Final)
 * is incompatible with {@link BeforeBeanDiscovery} in CDI 2.0.
 */
public class OpenIdExtension implements Extension {

	//@formatter:off
	/*
	 * I tried to update weld (weld-se 2.1.2.Final -> weld-se-core 3.1.8.Final) but then the JTA transaction did not work anymore.
	 * Workaround: Deactivate OpenIdExtension in tests (we don't need them here).
	 * 
	 * Dependency snippets:

	>>> sormas-base/pom.xml

 		<deltaspike.version>1.9.5</deltaspike.version>

 			<dependency>
				<groupId>info.novatec</groupId>
				<artifactId>bean-test</artifactId>
				<version>0.2.Final</version>
				<scope>test</scope>
				<exclusions>
					<exclusion>
						<groupId>org.jboss.weld.se</groupId>
						<artifactId>weld-se</artifactId>
					</exclusion>
				</exclusions>
			</dependency>
			<dependency>
				<groupId>org.jboss.weld.se</groupId>
				<artifactId>weld-se-core</artifactId>
				<version>3.1.8.Final</version>
				<scope>test</scope>
			</dependency>
			<dependency>
				<groupId>org.apache.deltaspike.core</groupId>
				<artifactId>deltaspike-core-impl</artifactId>
				<version>${deltaspike.version}</version>
				<scope>test</scope>
			</dependency>
			<dependency>
				<groupId>org.apache.deltaspike.cdictrl</groupId>
				<artifactId>deltaspike-cdictrl-weld</artifactId>
				<version>${deltaspike.version}</version>
				<scope>test</scope>
			</dependency>

	>>> sormas-backend/pom.xml

			<dependency>
				<groupId>org.jboss.weld.se</groupId>
				<artifactId>weld-se-core</artifactId>
			</dependency>

	 */
	//@formatter:on
}
