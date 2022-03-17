package de.symeda.sormas.api.audit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Constants {

	public static final Set<String> createPrefix = new HashSet<>(Arrays.asList("create", "generate", "build", "clone", "calculate"));
	public static final Set<String> readPrefix = new HashSet<>(
		Arrays
			.asList("count", "get", "is", "has", "does", "validate", "exists", "read", "import", "find", "query", "load", "check", "uses", "fetch"));
	public static final Set<String> updatePrefix = new HashSet<>(
		Arrays.asList(
			"update",
			"post",
			"set",
			"archive",
			"dearchive",
			"save",
			"overwrite",
			"convert",
			"link",
			"bulkAssign",
			"write",
			"cleanup",
			"mark"));
	public static final Set<String> deletePrefix = new HashSet<>(Arrays.asList("delete", "merge", "remove"));
	public static final Set<String> executePrefix = new HashSet<>(
		Arrays.asList(
			"send",
			"start",
			"share",
			"signAndEncrypt",
			"decryptAndVerify",
			"reject",
			"report",
			"unlink",
			"notify",
			"register",
			"cancel",
			"end",
			"accept",
			"sync",
			"request",
			"revoke",
			"reset",
			"enable",
			"disable"));

}
