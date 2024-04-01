package vpn.mailSender.mailSenders;

import vpn.mailSender.helpClasses.HTMLTableBuilder;
import vpn.mailSender.settings.IniReader;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

class TestMailSender extends MailSender {

   static String TEST_SENDER = IniReader.properties.get("Emails", "TEST_SENDER");
   static String TEST_TO = IniReader.properties.get("Emails", "TEST_TO");
   static String TEST_CC = IniReader.properties.get("Emails", "TEST_CC");

   @Override
   protected void setMailMembers(String to, String cc, MimeMessage message) throws MessagingException {
	   message.setFrom(new InternetAddress(TEST_SENDER));

	   message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TEST_TO, false));

	   message.setRecipients(Message.RecipientType.CC,
			   InternetAddress.parse(TEST_CC, false));
   }

   @Override
   protected String getMailInHtml(String to, String cc, String textMails) {
	   HTMLTableBuilder htmlBuilder = new HTMLTableBuilder(textMails, true, 2, 2);
	   htmlBuilder.addTableHeader("TO", "CC");
	   htmlBuilder.addRowValues(to, cc);
	   return htmlBuilder.build();
   }
}
