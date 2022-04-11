package org.eapo.service.esign.util;

import java.util.HashSet;
import java.util.Set;

public class DoccodeUtil {

	static Set<String> doccodes = new HashSet<>();

	public DoccodeUtil() {
		doccodes.add("PATTE");
		doccodes.add("PattE");
	}

	public boolean isDoccodeExists(String doccode) {
		return doccodes.stream().anyMatch(s1 -> s1.equals(doccode));
	}
}
