package vpn.mailSender.recipients;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Cc {
	private List<String> ccRecipients =new ArrayList<>();

	public List<String> getCcRecipients() {
		return Collections.unmodifiableList(ccRecipients);
	}

	public void setCcRecipients(Collection<String> ccRecipients) {
		this.ccRecipients.addAll(ccRecipients);
	}
}
