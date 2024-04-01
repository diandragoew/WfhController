package vpn.mailSender.respondents;

import vpn.mailSender.excels.PrintInExcel;
import vpn.mailSender.recipients.To;
import vpn.mailSender.settings.IniReader;
import org.json.JSONException;

import javax.naming.AuthenticationException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Boss extends Respondent {
	public static PrintInExcel printInExcel = new PrintInExcel();


	public Boss(Set<NotifiedEmployee> notifiedEmployees) throws  JSONException, IOException {
		super(notifiedEmployees);

		setRecipients(notifiedEmployees);
		generateExcel(notifiedEmployees);
	}

	private void generateExcel(Set<NotifiedEmployee> notifiedEmployees) {
		String folderPath = printInExcel.createFolder();
		String excelPath = printInExcel.createExcelFile(notifiedEmployees,folderPath);
		setPathExcel(excelPath);
	}

	public void setDescriptionEmail() throws IOException, JSONException {
		this.descriptionEmail = IniReader.properties.get("Emails", "DESCRIPTION_MAIL_FOR_BOSS") ;
	}

	public void setSubjectEmail() throws IOException, JSONException {
		this.subjectEmail = IniReader.properties.get("Emails", "SUBJECT_MAIL_FOR_BOSS") ;
	}

	void setRecipients(Set<NotifiedEmployee> notifiedEmployees)  {
		To to = new To();
		to.addFirstInTo(IniReader.properties.get("Emails", "EMAIL_CEO"));
		to.addSecondInTo(IniReader.properties.get("Emails", "EMAIL_HR"));
		setTo(to);
	}
}
