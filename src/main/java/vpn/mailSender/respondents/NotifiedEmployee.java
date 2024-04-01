package vpn.mailSender.respondents;

import java.time.LocalDate;
import java.util.Objects;

public class NotifiedEmployee {
   private String employeeInitials ="";
   private String employeeName ="";
    private String employeeEmail ="";
    private String employeeTeam ="";
    private String teamLeaderEmail="";
    private LocalDate date;
    private String country="";

    public NotifiedEmployee(String employeeInitials, String employeeName, String employeeEmail, String employeeTeam, String teamLeaderEmail, LocalDate date, String country) {
        this.employeeInitials = employeeInitials;
        this.employeeName = employeeName;
        this.employeeEmail = employeeEmail;
        this.employeeTeam = employeeTeam;
        this.teamLeaderEmail = teamLeaderEmail;
        this.date = date;
        this.setCountry(country);  
    }

    public String getEmployeeInitials() {
        return employeeInitials;
    }

    public void setEmployeeInitials(String employeeInitials) {
        this.employeeInitials = employeeInitials;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public String getEmployeeTeam() {
        return employeeTeam;
    }

    public void setEmployeeTeam(String employeeTeam) {
        this.employeeTeam = employeeTeam;
    }

    public String getTeamLeaderEmail() {
        return teamLeaderEmail;
    }

    public void setTeamLeaderEmail(String teamLeaderEmail) {
        this.teamLeaderEmail = teamLeaderEmail;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotifiedEmployee that = (NotifiedEmployee) o;
        return employeeInitials.equals(that.employeeInitials) && date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeInitials, date);
    }
    public void setCountry(String country) {

        if (country != null) {

            switch (country) {
                case "..... BG":
                    this.country = "BG";
                    break;
                case "..... FR":
                case "..... SA":
                    this.country = "FR";
                    break;
                case "..... Tunis":
                case "..... Tunisia":
                    this.country = "TN";
                    break;
                case "..... Vietnam":
                    this.country = "VN";
                    break;
                case "..... USA":
                    this.country = "US";
                    break;
                case "..... MX":
                    this.country = "MX";
                    break;
                case "..... Spain":
                    this.country = "ES";
                    break;
                case "CZ":
                    this.country = "CZ";
                    break;
                case "..... DE":
                    this.country = "DE";
                    break;
                case "..... RO":
                    this.country = "RO";
                    break;
                case "..... CA":
                    this.country = "CA";
                    break;
                case "..... CO":
                    this.country = "CO";
                    break;
            }
        }
    }
}
