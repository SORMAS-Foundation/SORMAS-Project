package de.symeda.sormas.api.user;

public final class UserHelper {

	public static String getSuggestedUsername(String firstname, String lastname) {
		StringBuilder sb = new StringBuilder();
		String trim = firstname.replaceAll("\\s", "");
		sb.append(trim.length()>4?trim.substring(0, 4):trim);
		String trim2 = lastname.replaceAll("\\s", "");
		sb.append(trim2.length()>4?trim2.substring(0, 4):trim2);
		return sb.toString();
	}
}
