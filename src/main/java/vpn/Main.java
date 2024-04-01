package vpn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import vpn.mailSender.mailSenders.MailSender;
import vpn.service.CompareVpnToWfh;
import vpn.service.CompareVpnToWfhBoss;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;


@SpringBootApplication
public class Main implements CommandLineRunner {

    @Autowired
    private CompareVpnToWfh compareVpnToWfh;
    @Autowired
    private CompareVpnToWfhBoss compareVpnToWfhBoss;

    public static void main(String[] args) {
        while (true) {
            try {
                SpringApplication app = new SpringApplication(Main.class);
                app.run(args);
            } catch (Exception e) {
                MailSender.sendMailForErrorOfSending(e);
            }
        }
    }

    // Put your logic here.
    @Override
    public void run(String... args) throws Exception {

        while (true) {
            try {
                compareVpnToWfh.addUsersLoggedFromVpn();
                compareVpnToWfh.addUsersWorkedFromHome();
                compareVpnToWfh.compareVpnToWfh();
                compareVpnToWfhBoss.compareVpnToWfh();
            } catch (Exception e) {
                MailSender.sendMailForErrorOfSending(e);
                compareVpnToWfh.sleepProgram(5);
            }
        }
    }
}

