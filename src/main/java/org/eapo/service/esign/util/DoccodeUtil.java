package org.eapo.service.esign.util;

import java.util.HashSet;
import java.util.Set;

public class DoccodeUtil {

	private Set<String> doccodes = new HashSet<>();

	public DoccodeUtil() {
		this.doccodes.add("PattE");
		this.doccodes.add("PattI");
		this.doccodes.add("400CE");
		this.doccodes.add("400RE");
		this.doccodes.add("R96E");
	}

	public boolean isDoccodeExists(String doccode) {
		return this.doccodes.stream().anyMatch(s1 -> s1.equals(doccode));
	}
}
