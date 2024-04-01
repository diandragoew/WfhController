package vpn.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.springframework.stereotype.Service;
import vpn.mailSender.api.HrApi;
import vpn.mailSender.mailSenders.MailSender;
import vpn.mailSender.respondents.Boss;
import vpn.mailSender.respondents.NotifiedEmployee;
import vpn.mailSender.respondents.Respondent;
import vpn.model.User;
import vpn.model.VpnHistory;
import vpn.model.WorkFromHome;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.time.*;
import java.util.*;

@Service
public class CompareVpnToWfhBoss extends CompareVpnToWfh {
    private static final Logger logger = LogManager.getLogger(CompareVpnToWfhBoss.class);

    public static final int WORKING_DAYS = 5;

    @Override
    public void compareVpnToWfh() throws Exception {
        LocalDate localDate = LocalDate.now();
        LocalDateTime startWorkingTime = LocalDateTime.now().withHour(9).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endWorkingTime = LocalDateTime.now().withHour(18).withMinute(0).withSecond(0).withNano(0);


        if (localDate.getDayOfWeek() == DayOfWeek.FRIDAY && LocalTime.now().isAfter(LocalTime.of(18, 0)) && LocalTime.now().isBefore(LocalTime.of(18, 15))) {
            sleepProgram(15);

            Set<NotifiedEmployee> notifiedEmployees = new LinkedHashSet<>();

            for (int i = 0; i < WORKING_DAYS; i++) {
                LocalDate workingDay = localDate.minusDays(i);
                // to take all users from VpnHistoryDao which have vpnHistory from the given date
                Map<User, Set<VpnHistory>> users = vpnHistoryDao.findAllUsersWhoConnectedFromVpn(workingDay);
                WorkFromHome workFromHome = new WorkFromHome();
                workFromHome.setDateWfh(workingDay);

                for (Map.Entry<User, Set<VpnHistory>> entry : users.entrySet()) {
                    User user = entry.getKey();
                    workFromHome.setUser(user);
                    double vpnTime = 0.0;
                    if (user.getUserInitials() != null && !"".equals(user.getUserInitials())) {
                        NotifiedEmployee notifiedEmployee = HrApi.takeNotifiedEmployee(user.getUserInitials().toLowerCase(), workingDay);
                        if (notifiedEmployee != null&&("BG").equalsIgnoreCase(notifiedEmployee.getCountry()) && !notifiedEmployees.contains(notifiedEmployee) && !user.getWorkFromHome().contains(workFromHome)) {
                            Set<VpnHistory> vpnHistories = entry.getValue();
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
                                    if (checkIfItIsWorkingTime("BG",workingDay)) {
                                        notifiedEmployees.add(notifiedEmployee);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (notifiedEmployees.size() > 0) {
                Respondent respondent = createRespondent(notifiedEmployees);
                MailSender.getMailSender().sendEmails(respondent);
            } else {
                logger.info("notifiedEmployees.size() = 0");
            }
        }
    }

    @Override
    protected Respondent createRespondent(Set<NotifiedEmployee> notifiedEmployees) throws JSONException, IOException {
        Respondent respondent = new Boss(notifiedEmployees);
        return respondent;
    }
}
