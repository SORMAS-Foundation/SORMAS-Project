package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.List;

public final class ImportLevel {

	public enum Kind {
		ENTITY,
		EMBEDDED
	}

	private final String name;
	private final Kind kind;
	private final List<String> prefixes;

	public ImportLevel(String name, Kind kind, String... prefixes) {
		this.name = name;
		this.kind = kind;
		this.prefixes = List.of(prefixes);
	}

	public String name() {
		return name;
	}

	public Kind kind() {
		return kind;
	}

	public boolean isEmbedded() {
		return kind == Kind.EMBEDDED;
	}

	public boolean owns(String header) {
		return prefixes.contains(prefixOf(header));
	}

	public static String prefixOf(String header) {
		int dot = header.indexOf('.');
		return dot > 0 ? header.substring(0, dot) : "";
	}

	@Override
	public String toString() {
		return name;
	}
}
