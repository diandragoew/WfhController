package vpn.mailSender.mailSenders;

import org.json.JSONException;
import vpn.mailSender.helpClasses.HTMLTableBuilder;
import vpn.mailSender.helpClasses.HelpClass;
import vpn.mailSender.respondents.Respondent;
import vpn.mailSender.settings.IniReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

public abstract class MailSender {
	private static final Logger logger = LogManager.getLogger(MailSender.class);
    private static final int MAX_WRONG_TRIES = 3;

    private static String HOST = IniReader.properties.get("Emails", "HOST");

	public static MailSender getMailSender() {
		String isTestEmails = IniReader.properties.get("Emails", "TEST_MAILS");
		if ("true".equalsIgnoreCase(isTestEmails)) {
			return new TestMailSender();
		}
		return new RealMailSender();
	}

	public void sendEmails(Respondent respondent) throws MessagingException, JSONException, IOException, InterruptedException {



				String textMails = respondent.getDescriptionEmail();
				String subject = respondent.getSubjectEmail();

				String to = "";
				String cc = "";

				//Get the session object
				Properties properties = System.getProperties();
				properties.setProperty("mail.smtp.host", HOST);
				Session session = Session.getDefaultInstance(properties);

				Multipart multipart = new MimeMultipart();

				to = to + ("," + HelpClass.makeCollectionWithStringsToStringWithCommas(respondent.getTo().getFirstInTo()));
				cc = cc + ("," + HelpClass.makeCollectionWithStringsToStringWithCommas(respondent.getTo().getSecondInTo()));
				cc = cc + ("," + HelpClass.makeCollectionWithStringsToStringWithCommas(respondent.getCc().getCcRecipients()));

				String textMailInHtml = getMailInHtml(to, cc, textMails);

				logger.info("textMailInHtml = " + textMailInHtml);

				BodyPart textMailBody = new MimeBodyPart();
				textMailBody.setContent(textMailInHtml, "text/html; charset=utf-8");
				multipart.addBodyPart(textMailBody);
				if (!"".equals(respondent.getPathExcel())) {
					multipart.addBodyPart(createAttachmentInEmail(respondent.getPathExcel()));
				}
				logger.info( "-------------subject-----------------");
				logger.info( subject);
				logger.info( "-------------text mail-----------------");
				logger.info( textMails);

				MimeMessage message = new MimeMessage(session);

				setMailMembers(to, cc, message);

				message.setSubject(subject);

				message.setContent(multipart);

				// Send message
                for (int i = 0; i <= MAX_WRONG_TRIES; i++) {
                    try {
                         // Send message
                        Transport.send(message);
                        logger.info( "message sent successfully....");
                        break;
                     } catch (Exception e) {
                         if (i == MAX_WRONG_TRIES) {
                             throw e;
                         }
                         logger.error( Arrays.toString(e.getStackTrace()) + e.getMessage());
                         Thread.sleep(2000);
                    }
                }
	}

	/**
	 * Makes an absolute file path to BodyPart (attachment in email).
	 *
	 * @param filePath absolute file path.
	 * @return one attachment file as part of email.
	 */
	private static BodyPart createAttachmentInEmail(String filePath) throws MessagingException {
		Multipart multipart = new MimeMultipart();

		BodyPart messageBodyPart = new MimeBodyPart();
		File file = new File(filePath);
		DataSource source = new FileDataSource(filePath);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(file.getName());
		multipart.addBodyPart(messageBodyPart);

		return messageBodyPart;
	}
	protected abstract String getMailInHtml(String to, String cc, String textMails);

	protected abstract void setMailMembers(String to, String cc, MimeMessage message) throws MessagingException;

	public static void sendMailForErrorOfSending(Exception e) {
		try {
			String EMAIL_IN_TO_FOR_ERROR_EMAIL_MESSAGE = IniReader.properties.get("Emails", "EMAIL_IN_TO_FOR_ERROR_EMAIL_MESSAGE");
			String EMAIL_IN_CC_FOR_ERROR_EMAIL_MESSAGE = IniReader.properties.get("Emails", "EMAIL_IN_CC_FOR_ERROR_EMAIL_MESSAGE");
			String EMAIL_SENDER_FOR_ERROR_EMAIL_MESSAGE = IniReader.properties.get("Emails", "EMAIL_SENDER_FOR_ERROR_EMAIL_MESSAGE");
			String subjectError = IniReader.properties.get("Emails", "SUBJECT_FOR_ERROR_MAIL");
			String textError = IniReader.properties.get("Emails", "DESCRIPTION_FOR_ERROR_MAIL")+ "<br><br>".concat(Arrays.toString(e.getStackTrace()))
					.concat("<br> " + e.getMessage());

			//Get the session object
			Properties properties = System.getProperties();
			properties.setProperty("mail.smtp.host", HOST);
			Session session = Session.getDefaultInstance(properties);

			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(EMAIL_SENDER_FOR_ERROR_EMAIL_MESSAGE));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL_IN_TO_FOR_ERROR_EMAIL_MESSAGE, false));
			message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(EMAIL_IN_CC_FOR_ERROR_EMAIL_MESSAGE, false));
			message.setSubject(subjectError);

			HTMLTableBuilder htmlBuilder = new HTMLTableBuilder(textError, false, 0, 0);
			String textMailInHtml = htmlBuilder.build();
			message.setContent(textMailInHtml, "text/html; charset=utf-8");

			// Send message
			Transport.send(message);
			logger.info("message sent successfully....");

		} catch (MessagingException mex) {
			logger.error( Arrays.toString(mex.getStackTrace()) + mex.getMessage());
		}
	}
}
