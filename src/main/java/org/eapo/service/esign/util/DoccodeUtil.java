package org.eapo.service.esign.util;

import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class DoccodeUtil {

	static Set<String> doccodes = new HashSet<>();

	public DoccodeUtil(Set<String> doccodes) {
		doccodes.add("PattE");
	}

	public static boolean isDoccodeExists(String idletter) {
		return doccodes.stream().anyMatch(s1 -> s1.equals(idletter));
	}
}
