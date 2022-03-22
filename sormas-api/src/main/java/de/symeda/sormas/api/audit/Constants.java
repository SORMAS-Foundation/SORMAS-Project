package de.symeda.sormas.api.audit;

import org.apache.commons.collections.set.UnmodifiableSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Constants {

	public static final Set<String> createPrefix =
		Collections.unmodifiableSet(new HashSet<>(Arrays.asList("create", "generate", "build", "clone", "calculate")));
	public static final Set<String> readPrefix = Collections.unmodifiableSet(
		new HashSet<>(
			Arrays.asList(
				"count",
				"get",
				"is",
				"has",
				"does",
				"validate",
				"exists",
				"read",
				"import",
				"find",
				"query",
				"load",
				"check",
				"uses",
				"fetch")));
	public static final Set<String> updatePrefix = Collections.unmodifiableSet(
		new HashSet<>(
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
				"mark")));
	public static final Set<String> deletePrefix = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("delete", "merge", "remove")));
	public static final Set<String> executePrefix = Collections.unmodifiableSet(
		new HashSet<>(
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
				"disable")));

}
