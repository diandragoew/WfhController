package vpn.mailSender.mailSenders;

import vpn.mailSender.helpClasses.HTMLTableBuilder;
import vpn.mailSender.settings.IniReader;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

class RealMailSender extends MailSender {
	String FROM_MAIL = IniReader.properties.get("Emails", "FROM_MAIL");

	@Override
	public void setMailMembers(String to, String cc, MimeMessage message) throws MessagingException {
		message.setFrom(new InternetAddress(FROM_MAIL));

		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));

		message.setRecipients(Message.RecipientType.CC,
				InternetAddress.parse(cc, false));
	}

	@Override
	public String getMailInHtml(String to, String cc, String textMails) {
		HTMLTableBuilder htmlBuilder = new HTMLTableBuilder(textMails, false, 0, 0);
		return htmlBuilder.build();
	}
}