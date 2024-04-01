package vpn.service;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import vpn.dao.VpnHistoryDao;
import vpn.dao.UserDao;
import vpn.dao.WorkFromHomeDao;
import org.springframework.beans.factory.annotation.Autowired;
import vpn.apirequests.EARequests;
import vpn.mailSender.api.HrApi;
import vpn.mailSender.mailSenders.MailSender;
import vpn.mailSender.respondents.Employee;
import vpn.mailSender.respondents.NotifiedEmployee;
import vpn.mailSender.respondents.Respondent;
import vpn.model.VpnHistory;
import vpn.model.User;
import vpn.model.WorkFromHome;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.text.ParseException;
import java.time.*;
import java.util.*;

@Service
public class CompareVpnToWfh {

    @Autowired
    protected UserDao userDao;
    @Autowired
    protected VpnHistoryDao vpnHistoryDao;
    @Autowired
    protected WorkFromHomeDao workFromHomeDao;


    Map<String, User> userInitialsToUsersFromVpn = new TreeMap<>();
    private Set<String> userInitialsFromVpn = new TreeSet<>();

    protected Set<User> notifiedUsers = new TreeSet<>();

    //this is 7200 seconds = 2 hours
    protected double vpnLimitForNotification = 7200.0;

    public void addUsersLoggedFromVpn() throws Exception {
        userInitialsFromVpn = EARequests.getUserInitialsLogedFromVpn();
        for (String userInitial : userInitialsFromVpn) {
            User user = new User();
            user.setInitials(userInitial.toLowerCase());
            VpnHistory vpnHistory = new VpnHistory();
            vpnHistory.setStartDate(LocalDateTime.now());
            user.addVpnHistory(vpnHistory);

            userInitialsToUsersFromVpn.put(userInitial.toLowerCase(), user);

            userDao.insertUser(user);

            //insert vpn history only with start date
            vpnHistoryDao.insertVpnHistoryIfNotExists(vpnHistory);
        }
        addEndDateVpnHistory();
    }

    public void addEndDateVpnHistory()  {
        Set<VpnHistory> vpnHistories = vpnHistoryDao.getActiveUsersInVpn();

        for (VpnHistory vpnHistory : vpnHistories) {
            User user = vpnHistory.getUser();
            String userInitials = user.getUserInitials().toLowerCase();

            if (!userInitialsFromVpn.contains(userInitials)) {
                vpnHistory.setEndDate(LocalDateTime.now());
                //insert/update vpn history again with end date
                vpnHistoryDao.insertEndDateVpnHistory(vpnHistory);
            }
        }
    }

    public void addUsersWorkedFromHome() throws Exception {
        LocalDate date = LocalDate.now();

        Set<String> wfhUserInitials = EARequests.takeWorkFromHome(date);


        for (String wfhUserInitial : wfhUserInitials) {
            WorkFromHome workFromHome = new WorkFromHome();
            workFromHome.setDateWfh(date);

            if (userInitialsToUsersFromVpn.containsKey(wfhUserInitial.toLowerCase())) {
                userInitialsToUsersFromVpn.get(wfhUserInitial.toLowerCase()).addWorkFromHome(workFromHome);
            } else {
                User newUser = new User();
                newUser.setInitials(wfhUserInitial.toLowerCase());
                newUser.addWorkFromHome(workFromHome);
                userDao.insertUser(newUser);
            }
            workFromHomeDao.insertWorkFromHome(workFromHome);
        }
    }


    public void compareVpnToWfh() throws Exception {
        if (LocalTime.now().isAfter(LocalTime.of(0, 0)) && LocalTime.now().isBefore(LocalTime.of(0, 15))) {
            clearDatabaseData();
            clearNotificatedUsers();
            sleepProgram(15);
        }
        LocalDate currentDate = LocalDate.now();
        LocalDateTime startWorkingTime = LocalDateTime.now().withHour(9).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endWorkingTime = LocalDateTime.now().withHour(18).withMinute(0).withSecond(0).withNano(0);

        // to take all users from VpnHistoryDao which have vpnHistory from the given date
        Map<User, Set<VpnHistory>> users = vpnHistoryDao.findAllUsersWhoConnectedFromVpn(currentDate);
        WorkFromHome workFromHome = new WorkFromHome();
        workFromHome.setDateWfh(currentDate);

        for (Map.Entry<User, Set<VpnHistory>> entry : users.entrySet()) {
            User user = entry.getKey();
            workFromHome.setUser(user);
            double vpnTime = 0.0;
            if (!notifiedUsers.contains(user) && !user.getWorkFromHome().contains(workFromHome)) {
                Set<VpnHistory> vpnHistories = entry.getValue();
                if (vpnHistories.size() > 0) {
                    for (VpnHistory vpnHistory : vpnHistories) {
                        LocalDateTime startDate = vpnHistory.getStartDate();
                        LocalDateTime endDate = vpnHistory.getEndDate();
                        if (endDate == null) {
                            endDate = LocalDateTime.now();
                        }
                        if (startDate.isBefore(startWorkingTime)) {
                            startDate = startWorkingTime;
                        }
                        if (endDate.isBefore(startWorkingTime)) {
                            endDate = startWorkingTime;
                        }
                        if (startDate.isAfter(endWorkingTime)) {
                            startDate = endWorkingTime;
                        }
                        if (endDate.isAfter(endWorkingTime)) {
                            endDate = endWorkingTime;
                        }
                        vpnTime += Duration.between(startDate, endDate).getSeconds();
                        if (vpnTime >= vpnLimitForNotification) {
                            if (user.getUserInitials() != null && !"".equals(user.getUserInitials())) {
                                NotifiedEmployee notifiedEmployee = HrApi.takeNotifiedEmployee(user.getUserInitials().toLowerCase(), currentDate);
                                if (notifiedEmployee != null && ("BG").equalsIgnoreCase(notifiedEmployee.getCountry()) && !notifiedUsers.contains(user)) {
                                    Respondent respondent = createRespondent(Collections.singleton(notifiedEmployee));
                                    if (checkIfItIsWorkingTime("BG",currentDate)) {
                                        MailSender.getMailSender().sendEmails(respondent);
                                        notifiedUsers.add(user);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected Respondent createRespondent(Set<NotifiedEmployee> notifiedEmployees) throws AuthenticationException, JSONException, IOException, InterruptedException {
        Respondent respondent = new Employee(notifiedEmployees);
        return respondent;
    }

    protected void clearNotificatedUsers() {
        notifiedUsers.clear();
    }
    public void sleepProgram(long sleepTimeMinutes) {
        try {
            Thread.sleep(sleepTimeMinutes * 60000); // 1 minute = 60,000 milliseconds;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

        private void clearDatabaseData() {
        LocalDate currentDate = LocalDate.now(); // Get the current date
        int currentMonth = currentDate.getMonthValue(); // Get the current month
        if ((currentMonth == 1 || currentMonth == 4 || currentMonth == 7 || currentMonth == 10) && currentDate.getDayOfMonth() == 1) {
            vpnHistoryDao.deleteAll();
            workFromHomeDao.deleteAll();
            userDao.deleteAll();
            userInitialsToUsersFromVpn.clear();
        }
    }

    protected boolean checkIfItIsWorkingTime(String country,LocalDate localDate) throws JSONException, IOException, ParseException {
        // Convert LocalDate to Date
        Date currentDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Map<String, Set<Date>> holidays = HrApi.takeHolidays();
        if (holidays.containsKey(country) && holidays.get(country).contains(currentDate)) {
            return false;
        }


        if (LocalDate.now().getDayOfWeek() == DayOfWeek.SATURDAY ||
                LocalDate.now().getDayOfWeek() == DayOfWeek.SUNDAY ||
                LocalTime.now().isAfter(LocalTime.of(18, 0)) ||
                LocalTime.now().isBefore(LocalTime.of(9, 0))) {
            return false;
        }
        return true;
    }

}
