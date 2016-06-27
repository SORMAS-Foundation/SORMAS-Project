package de.symeda.sormas.backend.user;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Hier werden alle Rechte aufgelistet die für das System relevant sind.<br>
 * Gruppierte Rechte wie bearbeitbar, einsehbar usw. müssen einen bestimmten
 * Suffix haben, der Präfix wird zum Erstellen der Gruppen genutzt. Innerhalb
 * der Gruppen müssen die Rechte über die Child-Hierarchie geordnet sein.
 * Es sind alle Suffixe erlaubt, die durch {@link SubPermission} abgebildet werden.
 * ACHTUNG: Permissions müssen in die glassfish-ejb-jar.xml des ejb-Projekts eingetragen werden.
 * Siehe PermissionIllustrator im test-Bereich.
 *
 * @RolesAllowed-Annotation an EJB verwendet.<br>
 */
public enum Permission
	implements
	Cloneable {

	// TODO add Permissions

	// ########### System permissions
	ADMIN(),
	USER(),;

	/*
	 * ACHTUNG: Zum Pflegen der Konstanten: {@link PermissionIllustrator}
	 */

	public static final String _ADMIN = "ADMIN";
	public static final String _USER = "USER";

	// ########### Zusätzliche Rechte
	public static final String _SYSTEM_ROLE = "SYSTEM_ROLE";

	private static final Permission[] SIMPLE_PERMISSIONS = {
			ADMIN, };

	private final Permission[] children;
	private Permission[] transitivePermissions = null;

	Permission(Permission... includedPermissions) {
		this.children = includedPermissions;
	}

	/**
	 * Simple Rechte, die nicht Teil anderer Rechte sind und die auch keine
	 * anderen Rechte beinhalten
	 *
	 * @return
	 */
	public static Permission[] getSimplePermissions() {
		return SIMPLE_PERMISSIONS;
	}

	/**
	 * Wandelt die übergebenen Permissions in ein Set inkl. der transitiven Permissions.
	 * 
	 * @param selectedPermissions
	 *            Die aktuell ausgewählten/zugewiesenen Permissions.
	 * @return {@code selectedPermissions} und daraus folgenden transitiven Permissions als {@link Set}.
	 */
	public static Set<Permission> convertWithTransitivePermissions(Permission... selectedPermissions) {

		return convertWithTransitivePermissions(Arrays.asList(selectedPermissions));
	}

	/**
	 * Wandelt die übergebenen Permissions in ein Set inkl. der transitiven Permissions.
	 * 
	 * @param selectedPermissions
	 *            Die aktuell ausgewählten/zugewiesenen Permissions.
	 * @return {@code selectedPermissions} und daraus folgenden transitiven Permissions als {@link Set}.
	 */
	public static Set<Permission> convertWithTransitivePermissions(Iterable<Permission> selectedPermissions) {

		final Set<Permission> permissionsSet = new LinkedHashSet<>();
		for (Permission permission : selectedPermissions) {
			// Direkte Permission
			permissionsSet.add(permission);

			// Transitive Permissions
			permissionsSet.addAll(Arrays.asList(permission.getTransitivePermissions()));
		}

		return permissionsSet;
	}

	public String getName() {
		return toString();
	}

	public Permission[] getChildren() {
		return children.clone();
	}

	/**
	 * @return Permissions, die aufgrund dieser Permission eingeschlossen sind.
	 */
	public Permission[] getTransitivePermissions() {
		return transitivePermissions;
	}


	private static Permission[] calculateTransitivePermissions(Permission permission) {

		if (permission.transitivePermissions != null) {
			return permission.transitivePermissions;
		}
		EnumSet<Permission> set;
		if (permission.children.length == 0) {
			set = EnumSet.noneOf(Permission.class);
		} else {
			set = EnumSet.copyOf(Arrays.asList(permission.children));
		}
		for (Permission child : permission.children) {
			Permission[] childPermissions = calculateTransitivePermissions(child);
			set.addAll(Arrays.asList(childPermissions));
		}
		Permission[] array = set.toArray(new Permission[set.size()]);
		Arrays.sort(array);
		permission.transitivePermissions = array;
		return array;
	}

	private static void calculateTransitiveClosure() {
		for (Permission permission : Permission.values()) {
			calculateTransitivePermissions(permission);
		}
	}

	/*
	 * Die transitive Hülle kann erst berechet werden nachdem das Enum
	 * initialisiert wurde.
	 */
	static {
		calculateTransitiveClosure();
	}
}
