package vpn.mailSender.respondents;

import vpn.mailSender.recipients.Cc;
import vpn.mailSender.recipients.To;
import vpn.mailSender.settings.IniReader;
import org.ini4j.Profile;
import org.json.JSONException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public abstract class Respondent {

	private Set<NotifiedEmployee> notifiedEmployees;
	private LocalDate date = LocalDate.now();
	private DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d/MM/uuuu");
	private String currentDate = date.format(formatters);

	private String name = "";
	private String team = "";

	private String pathExcel = "";
	protected String subjectEmail = "";
	protected String descriptionEmail = "";
	private boolean isThereTasks = false;

	//emails of project respondents
	private HashSet<String> projectRespondents = new HashSet<>();

	private To to = new To();
	private Cc cc = new Cc();

	public Respondent(Set<NotifiedEmployee> notifiedEmployees) throws  JSONException,  IOException {
		this.notifiedEmployees = notifiedEmployees;
		setSubjectEmail();
		setDescriptionEmail();
	}


	public void setTo(To to) {
		if (to != null) {
			this.to = to;
		}
	}

	protected void setCc(NotifiedEmployee notifiedEmployee) {
		Collection<String> ccRecipients = new ArrayList<>();
		addDefaultCc(ccRecipients);
		addSpecificCcRecipients(ccRecipients, notifiedEmployee);

		cc.setCcRecipients(ccRecipients);
	}

	private void addDefaultCc(Collection<String> ccRecipients) {
		Profile.Section defaultCcRecipients = IniReader.properties.get("DefaultCc");
		if (defaultCcRecipients != null) {
			ccRecipients.addAll(defaultCcRecipients.values());
		}
	}

	private void addSpecificCcRecipients(Collection<String> ccRecipients,NotifiedEmployee notifiedEmployee) {
		String specificCcRespondents = IniReader.properties.get("teamsWithSpecificCcRecipients", notifiedEmployee.getEmployeeTeam());

		if (specificCcRespondents != null) {
			ccRecipients.addAll(Arrays.asList(specificCcRespondents.split(",")));
		}
	}

	public To getTo() {
		return to;
	}

	public Cc getCc() {
		return cc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPathExcel() {
		return pathExcel;
	}

	public void setPathExcel(String pathExcel) {
		this.pathExcel = pathExcel;
	}

	public String getSubjectEmail() {
		return subjectEmail;
	}

	public void setSubjectEmail() throws IOException, JSONException {
		this.subjectEmail = IniReader.properties.get("Emails", "SUBJECT_MAIL") + " - " + name ;
	}

	public abstract void setDescriptionEmail()throws IOException, JSONException ;


	public String getDescriptionEmail()throws IOException, JSONException  {

		return descriptionEmail;

	}

	public String getCurrentDate() {
		return currentDate;
	}



	public void addProjectRespondent(String projectRespondent) {
		projectRespondents.add(projectRespondent);
	}

	public Set<String> getProjectRespondents() {
		return Collections.unmodifiableSet( projectRespondents);
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public Set<NotifiedEmployee> getNotifiedEmployees() {
		return Collections.unmodifiableSet(notifiedEmployees);
	}
}
