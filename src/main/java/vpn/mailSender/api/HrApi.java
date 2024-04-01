package vpn.mailSender.api;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vpn.mailSender.respondents.NotifiedEmployee;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import vpn.mailSender.settings.Constants;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class HrApi {
    private static final Logger logger = LogManager.getLogger(HrApi.class);
    private static final String HR_API_TOKEN = vpn.utils.IniReader.properties
            .get("Tokens", "AUTH_TOKEN_HR");

    public static final SimpleDateFormat dateFormatterPoints = new SimpleDateFormat("dd.MM.yyyy");

    private static final SimpleDateFormat dateFormatterFromHrApi = new SimpleDateFormat("yyyy-MM-dd");
    private static final int MAX_WRONG_TRIES = 3;


    public static NotifiedEmployee takeNotifiedEmployee(String userInitials, LocalDate date) throws IOException, JSONException {
//        logger.info("hr center api, to take employee data");

        OkHttpClient client = new OkHttpClient();
        NotifiedEmployee notifiedEmployee = null;
        try {

            String url = Constants.HRC_URL +"users?filters={\n" +
                    "\"allOf\": [\n" +
                    "{\n" +
                    "\"samaccountname\": {\n" +
                    "\"value\": [\n"
                    + "\"" + userInitials + "\"" +
                    "],\n" +
                    "\"operator\": \"in\"\n" +
                    "}\n" +
                    "}\n" +
                    "]\n" +
                    "}&with[]=department&with[]=manager";
//            logger.info(url);
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + HR_API_TOKEN)
                    .build();
//            logger.info("before connect with hr center api to take employee data");
            Response response = null;
            for (int i = 0; i <= MAX_WRONG_TRIES; i++) {
                try {
                     response = client.newCall(request).execute();
                    break;
                } catch (Exception e) {
                    if (i == MAX_WRONG_TRIES) {
                        throw e;
                    }
                    logger.error(Arrays.toString(e.getStackTrace()) + e.getMessage());
                    Thread.sleep(2000);
                }
            }
//            logger.info("response code :" + response.code());
//            logger.info("after connect with hr center api to take employee data");
            String body = response.body().string();

            JSONObject jsonObject = new JSONObject(body);
            JSONArray data = jsonObject.getJSONArray("data");
//            JSONArray data = new JSONArray(body);

            for (int j = 0; j < data.length(); j++) {
                JSONObject employeeData = (JSONObject) data.get(j);

                String employeeName = employeeData.getString("display_name");
                String employeeTeam = employeeData.getJSONObject("department").getString("department_name");
                String employeeEmail = employeeData.getString("email");
                String teamLeaderEmail = employeeData.getJSONObject("manager").getString("email");
                String country = employeeData.getString("country");


                notifiedEmployee = new NotifiedEmployee(userInitials, employeeName, employeeEmail, employeeTeam, teamLeaderEmail, date,country);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return notifiedEmployee;
    }
    public static Map<String, Set<Date>> takeHolidays() throws IOException, JSONException, ParseException {
        System.out.println("hr center api to take holidays ");

        OkHttpClient client = new OkHttpClient();
        Map<String, Set<Date>> holidays = new HashMap<>();

        String url = Constants.HRC_URL + "holidays?limit=1000000";
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + HR_API_TOKEN)
                .build();

        System.out.println("before connect with hr center api to take holidays");
        Response response = client.newCall(request).execute();
        System.out.println("response code :" +response.code());
        System.out.println("after connect with hr center api to take holidays");
        String body = response.body().string();

        JSONArray data = null;
        JSONObject obj = new JSONObject(body);

        data = obj.getJSONArray("data");

        for (int j = 0; j < data.length(); j++) {
            JSONObject holiday = (JSONObject) data.get(j);
            String country = holiday.getString("country");
            //тук
            Date date = dateFormatterFromHrApi.parse(holiday.getString("date"));

            holidays.putIfAbsent(country, new TreeSet<Date>());
            holidays.get(country).add(date);
        }
        return holidays;
    }
}
