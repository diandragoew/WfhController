package vpn.mailSender.recipients;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class To {
	private Set<String> firstInTo = new LinkedHashSet<>();
	private Set<String> secondInTo = new LinkedHashSet<>();



	public Set<String> getFirstInTo() {
		return Collections.unmodifiableSet(firstInTo);
	}

	public void addFirstInTo(String firstInTo) {
		this.firstInTo.add(firstInTo);
	}

	public Set<String> getSecondInTo() {
		return Collections.unmodifiableSet(secondInTo);
	}

	public void addSecondInTo(String secondInTo) {
		this.secondInTo.add(secondInTo);
	}
}
