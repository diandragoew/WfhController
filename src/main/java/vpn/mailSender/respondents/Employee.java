package vpn.mailSender.respondents;

import vpn.mailSender.api.HrApi;
import vpn.mailSender.recipients.To;
import vpn.mailSender.settings.IniReader;
import org.json.JSONException;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Employee extends Respondent {


    public Employee(Set<NotifiedEmployee> notifiedEmployees) throws InterruptedException, JSONException, AuthenticationException, IOException {
        super(notifiedEmployees);
        setRecipients(notifiedEmployees);
    }


    public void setDescriptionEmail() {
        this.descriptionEmail = IniReader.properties.get("Emails", "DESCRIPTION_MAIL_FOR_EMPLOYEE").replaceAll("<name>", Objects.requireNonNull(getNotifiedEmployees().stream().findFirst().orElse(null)).getEmployeeName());
    }

    public void setSubjectEmail() throws IOException, JSONException {
        this.subjectEmail = IniReader.properties.get("Emails", "SUBJECT_MAIL_FOR_EMPLOYEE").replaceAll("<name>", Objects.requireNonNull(getNotifiedEmployees().stream().findFirst().orElse(null)).getEmployeeName()).replaceAll("<date>", Objects.requireNonNull(getNotifiedEmployees().stream().findFirst().orElse(null)).getDate().toString());
    }

    void setRecipients(Set<NotifiedEmployee> notifiedEmployees) {
        NotifiedEmployee notifiedEmployee = notifiedEmployees.stream().findFirst().orElse(null);
        if (notifiedEmployee == null){
            System.out.println();
        }
        To to = new To();
        to.addFirstInTo(notifiedEmployee.getEmployeeEmail());
        to.addSecondInTo(notifiedEmployee.getTeamLeaderEmail());
        setTo(to);
        setCc(notifiedEmployee);
    }

}

