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

	/**
	 * Einsehen der eigenen Klienten (Fallmanager und Mitarbeite der Gruppe)
	 */
	KLIENTEN_EINSEHEN(),

	EPISODEN_EINSEHEN(KLIENTEN_EINSEHEN),
	EPISODEN_ALLE_EINSEHEN(EPISODEN_EINSEHEN),
	EPISODEN_BEARBEITEN(EPISODEN_EINSEHEN),
	EPISODEN_LOESCHEN(EPISODEN_BEARBEITEN),

	ADRESSBUCH_EINSEHEN(),
	ADRESSBUCH_BEARBEITEN(ADRESSBUCH_EINSEHEN),

	ABRECHNUNG_EINSEHEN(),
	ABRECHNUNG_BEARBEITEN(ABRECHNUNG_EINSEHEN),

	BENUTZER_EINSEHEN(),
	BENUTZER_BEARBEITEN(BENUTZER_EINSEHEN),
	BERECHTIGUNGSGRUPPE_EINSEHEN(),
	BERECHTIGUNGSGRUPPE_BEARBEITEN(BERECHTIGUNGSGRUPPE_EINSEHEN),
	BERECHTIGUNGSGRUPPE_LOESCHEN(BERECHTIGUNGSGRUPPE_BEARBEITEN),

	CONTROLLING_EINSEHEN,

	BEFRAGUNG_EINSEHEN,
	BEFRAGUNG_BEARBEITEN(BEFRAGUNG_EINSEHEN),

	STAMMDATEN_EINSEHEN(KLIENTEN_EINSEHEN),
	STAMMDATEN_BEARBEITEN(STAMMDATEN_EINSEHEN),
	DIAGNOSE_EINSEHEN(KLIENTEN_EINSEHEN),
	DIAGNOSE_BEARBEITEN(DIAGNOSE_EINSEHEN),
	AUFNAHME_EINSEHEN(KLIENTEN_EINSEHEN),
	AUFNAHME_BEARBEITEN(AUFNAHME_EINSEHEN),
	ENTLASSUNG_EINSEHEN(KLIENTEN_EINSEHEN),
	ENTLASSUNG_BEARBEITEN(ENTLASSUNG_EINSEHEN),

	DOKUMENTE_EINSEHEN(KLIENTEN_EINSEHEN),
	DOKUMENTE_BEARBEITEN(DOKUMENTE_EINSEHEN),
	DOKUMENTE_LOESCHEN(DOKUMENTE_BEARBEITEN),

	JOURNAL_EINSEHEN(),
	JOURNAL_BEARBEITEN(JOURNAL_EINSEHEN),
	JOURNAL_LOESCHEN(JOURNAL_BEARBEITEN),
	AUFGABEN_EINSEHEN(),
	AUFGABEN_BEARBEITEN(AUFGABEN_EINSEHEN),
	TERMINE_EINSEHEN(),
	TERMINE_BEARBEITEN(TERMINE_EINSEHEN),

	LEISTUNGEN_EINSEHEN(),
	LEISTUNGEN_BEARBEITEN(LEISTUNGEN_EINSEHEN),
	LEISTUNGEN_LOESCHEN(LEISTUNGEN_BEARBEITEN),
	LEISTUNGEN_ALLE_BEARBEITEN(LEISTUNGEN_EINSEHEN),
	LEISTUNGEN_ALLE_STATUS_BEARBEITEN(LEISTUNGEN_EINSEHEN),
	LEISTUNGEN_ALLE_LOESCHEN(LEISTUNGEN_EINSEHEN),
	MASSNAHMEN_EINSEHEN(),
	MASSNAHMEN_BEARBEITEN(MASSNAHMEN_EINSEHEN),
	MASSNAHMEN_LOESCHEN(MASSNAHMEN_BEARBEITEN),
	MASSNAHMEN_ASSIGN_THERAPEUT(MASSNAHMEN_BEARBEITEN),

	// Verwaltung der Therapiegruppen: Anlegen, Bearbeiten, Löschen
	GRUPPENVERWALTUNG_EINSEHEN,
	GRUPPENVERWALTUNG_BEARBEITEN(GRUPPENVERWALTUNG_EINSEHEN),

	// Kurstermine verwalten (ohne Teilnehmer)
	KURSTERMIN_EINSEHEN,
	KURSTERMIN_BEARBEITEN(KURSTERMIN_EINSEHEN),
	KURSTERMIN_LOESCHEN(KURSTERMIN_BEARBEITEN),

	// Teilnehmer von Kursterminen verwalten
	KURSTEILNEHMER_EINSEHEN(EPISODEN_EINSEHEN, KURSTERMIN_EINSEHEN),
	KURSTEILNEHMER_BEARBEITEN(KURSTEILNEHMER_EINSEHEN),

	TRAININGSBEREICH_EINSEHEN,
	TRAININGSBEREICH_BEARBEITEN(TRAININGSBEREICH_EINSEHEN),

	ADRESSBUCH_ROLLEN_EINSEHEN(),
	ADRESSBUCH_ROLLEN_BEARBEITEN(ADRESSBUCH_ROLLEN_EINSEHEN),

	/**
	 * Einsehen aller Klienten
	 */
	KLIENTEN_ALLE_EINSEHEN(KLIENTEN_EINSEHEN, EPISODEN_ALLE_EINSEHEN),
	KLIENTEN_ANLEGEN(KLIENTEN_EINSEHEN, STAMMDATEN_BEARBEITEN, EPISODEN_BEARBEITEN),
	KLIENTEN_LOESCHEN(KLIENTEN_ANLEGEN),

	// ########### Andere Rechte
	ADMIN(),
	USER(),;

	/*
	 * ACHTUNG: Zum Pflegen der Konstanten: {@link PermissionIllustrator}
	 */

	public static final String _KLIENTEN_EINSEHEN = "KLIENTEN_EINSEHEN";
	public static final String _EPISODEN_EINSEHEN = "EPISODEN_EINSEHEN";
	public static final String _EPISODEN_ALLE_EINSEHEN = "EPISODEN_ALLE_EINSEHEN";
	public static final String _EPISODEN_BEARBEITEN = "EPISODEN_BEARBEITEN";
	public static final String _EPISODEN_LOESCHEN = "EPISODEN_LOESCHEN";
	public static final String _ADRESSBUCH_EINSEHEN = "ADRESSBUCH_EINSEHEN";
	public static final String _ADRESSBUCH_BEARBEITEN = "ADRESSBUCH_BEARBEITEN";
	public static final String _ABRECHNUNG_EINSEHEN = "ABRECHNUNG_EINSEHEN";
	public static final String _ABRECHNUNG_BEARBEITEN = "ABRECHNUNG_BEARBEITEN";
	public static final String _BENUTZER_EINSEHEN = "BENUTZER_EINSEHEN";
	public static final String _BENUTZER_BEARBEITEN = "BENUTZER_BEARBEITEN";
	public static final String _BERECHTIGUNGSGRUPPE_EINSEHEN = "BERECHTIGUNGSGRUPPE_EINSEHEN";
	public static final String _BERECHTIGUNGSGRUPPE_BEARBEITEN = "BERECHTIGUNGSGRUPPE_BEARBEITEN";
	public static final String _BERECHTIGUNGSGRUPPE_LOESCHEN = "BERECHTIGUNGSGRUPPE_LOESCHEN";
	public static final String _CONTROLLING_EINSEHEN = "CONTROLLING_EINSEHEN";
	public static final String _BEFRAGUNG_EINSEHEN = "BEFRAGUNG_EINSEHEN";
	public static final String _BEFRAGUNG_BEARBEITEN = "BEFRAGUNG_BEARBEITEN";
	public static final String _STAMMDATEN_EINSEHEN = "STAMMDATEN_EINSEHEN";
	public static final String _STAMMDATEN_BEARBEITEN = "STAMMDATEN_BEARBEITEN";
	public static final String _DIAGNOSE_EINSEHEN = "DIAGNOSE_EINSEHEN";
	public static final String _DIAGNOSE_BEARBEITEN = "DIAGNOSE_BEARBEITEN";
	public static final String _AUFNAHME_EINSEHEN = "AUFNAHME_EINSEHEN";
	public static final String _AUFNAHME_BEARBEITEN = "AUFNAHME_BEARBEITEN";
	public static final String _ENTLASSUNG_EINSEHEN = "ENTLASSUNG_EINSEHEN";
	public static final String _ENTLASSUNG_BEARBEITEN = "ENTLASSUNG_BEARBEITEN";
	public static final String _DOKUMENTE_EINSEHEN = "DOKUMENTE_EINSEHEN";
	public static final String _DOKUMENTE_BEARBEITEN = "DOKUMENTE_BEARBEITEN";
	public static final String _DOKUMENTE_LOESCHEN = "DOKUMENTE_LOESCHEN";
	public static final String _JOURNAL_EINSEHEN = "JOURNAL_EINSEHEN";
	public static final String _JOURNAL_BEARBEITEN = "JOURNAL_BEARBEITEN";
	public static final String _JOURNAL_LOESCHEN = "JOURNAL_LOESCHEN";
	public static final String _AUFGABEN_EINSEHEN = "AUFGABEN_EINSEHEN";
	public static final String _AUFGABEN_BEARBEITEN = "AUFGABEN_BEARBEITEN";
	public static final String _TERMINE_EINSEHEN = "TERMINE_EINSEHEN";
	public static final String _TERMINE_BEARBEITEN = "TERMINE_BEARBEITEN";
	public static final String _LEISTUNGEN_EINSEHEN = "LEISTUNGEN_EINSEHEN";
	public static final String _LEISTUNGEN_BEARBEITEN = "LEISTUNGEN_BEARBEITEN";
	public static final String _LEISTUNGEN_LOESCHEN = "LEISTUNGEN_LOESCHEN";
	public static final String _LEISTUNGEN_ALLE_BEARBEITEN = "LEISTUNGEN_ALLE_BEARBEITEN";
	public static final String _LEISTUNGEN_ALLE_STATUS_BEARBEITEN = "LEISTUNGEN_ALLE_STATUS_BEARBEITEN";
	public static final String _LEISTUNGEN_ALLE_LOESCHEN = "LEISTUNGEN_ALLE_LOESCHEN";
	public static final String _MASSNAHMEN_EINSEHEN = "MASSNAHMEN_EINSEHEN";
	public static final String _MASSNAHMEN_BEARBEITEN = "MASSNAHMEN_BEARBEITEN";
	public static final String _MASSNAHMEN_LOESCHEN = "MASSNAHMEN_LOESCHEN";
	public static final String _MASSNAHMEN_ASSIGN_THERAPEUT = "MASSNAHMEN_ASSIGN_THERAPEUT";
	public static final String _GRUPPENVERWALTUNG_EINSEHEN = "GRUPPENVERWALTUNG_EINSEHEN";
	public static final String _GRUPPENVERWALTUNG_BEARBEITEN = "GRUPPENVERWALTUNG_BEARBEITEN";
	public static final String _KURSTERMIN_EINSEHEN = "KURSTERMIN_EINSEHEN";
	public static final String _KURSTERMIN_BEARBEITEN = "KURSTERMIN_BEARBEITEN";
	public static final String _KURSTERMIN_LOESCHEN = "KURSTERMIN_LOESCHEN";
	public static final String _KURSTEILNEHMER_EINSEHEN = "KURSTEILNEHMER_EINSEHEN";
	public static final String _KURSTEILNEHMER_BEARBEITEN = "KURSTEILNEHMER_BEARBEITEN";
	public static final String _TRAININGSBEREICH_EINSEHEN = "TRAININGSBEREICH_EINSEHEN";
	public static final String _TRAININGSBEREICH_BEARBEITEN = "TRAININGSBEREICH_BEARBEITEN";
	public static final String _ADRESSBUCH_ROLLEN_EINSEHEN = "ADRESSBUCH_ROLLEN_EINSEHEN";
	public static final String _ADRESSBUCH_ROLLEN_BEARBEITEN = "ADRESSBUCH_ROLLEN_BEARBEITEN";
	public static final String _KLIENTEN_ALLE_EINSEHEN = "KLIENTEN_ALLE_EINSEHEN";
	public static final String _KLIENTEN_ANLEGEN = "KLIENTEN_ANLEGEN";
	public static final String _KLIENTEN_LOESCHEN = "KLIENTEN_LOESCHEN";
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
