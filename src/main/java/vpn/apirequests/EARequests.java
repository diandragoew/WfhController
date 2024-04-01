package vpn.apirequests;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vpn.mailSender.settings.Constants;
import vpn.utils.IniReader;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * Use this class' methods to get data from HRC API.
 */
public class EARequests {

    private static final int MAX_WRONG_TRIES = 3;

    private static final Logger logger = LogManager.getLogger(EARequests.class);

    private static final String AUTH_TOKEN_HR = IniReader.properties
            .get("Tokens", "AUTH_TOKEN_HR");
    private static final String AUTH_TOKEN_VPN = IniReader.properties
            .get("Tokens", "AUTH_TOKEN_VPN");
    private static final String CMS_LINK = IniReader.properties
            .get("URLs", "CMS_LINK");

    private static final String HRC_PARAMS = IniReader.properties
            .get("URLs", "HRC_PARAMS");

    private static final String HRC_LINK = IniReader.properties
            .get("URLs", "HRC_LINK");

    /**
     * This method sends a request to HRC API using a link.
     *
     * @return The data acquired by the request as String
     * @throws IOException e
     */
    public static String makeRequestHrc(String url) throws IOException, InterruptedException {

//        logger.log(Level.INFO, "Connecting to HRC API...");
        return makeRequest(url, AUTH_TOKEN_HR,"Bearer");
    }


    /**
     * This method request MMPI API
     *
     * @return The data acquired by the request as String
     * @throws IOException e
     */
    public static String makeRequestVpn(String url) throws IOException, InterruptedException {
        //setting time out ensures there won't be an exception about it
        return makeRequest(url, AUTH_TOKEN_VPN,"Basic");
    }

    public static String makeRequest(String url, String authToken, String methodAuthentication) throws IOException, InterruptedException {
        OkHttpClient client = new OkHttpClient();
        Response response = null;
        for (int i = 0; i <= MAX_WRONG_TRIES; i++) {
            try {
             Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", methodAuthentication +" "+ authToken)
                .build();

             response = client.newCall(request).execute();

                 if (response != null) {
                     break;
                 }
            } catch (Exception e) {
                if (i == MAX_WRONG_TRIES) {
                    throw e;
                }
                logger.error( Arrays.toString(e.getStackTrace()) + e.getMessage());
                Thread.sleep(2000);
            }
        }

        return response.body().string();
    }




    public static Set<String> getUserInitialsLogedFromVpn() throws Exception {
        Set<String> userInitials = new TreeSet<>();

        String url = Constants.VPN_REQUEST;


        String body = makeRequestVpn(url);

        JsonElement element = new Gson().fromJson(body, JsonElement.class);
        JsonElement data = element.getAsJsonObject().get("logins");
        JsonArray jsonArray = data.getAsJsonArray();

        String userInitial;


        for (int i = 0; i < jsonArray.size(); i++) {
            userInitial = jsonArray.get(i).getAsJsonObject().get("user").getAsString();

            //this split using slashes is because the value of the key "user" can be the domain of the employee. Example: ......\\bgochev
            String [] userInitialSplitted = userInitial.split("[/\\\\]+");
            userInitial = userInitialSplitted.length > 1 ? userInitialSplitted[1] : userInitialSplitted[0];

            //this split using "@" is because the value of the key "user" can be the email of the employee
            userInitial = userInitial.split("@")[0];

            userInitials.add(userInitial.toLowerCase());
        }

        return userInitials;
    }

    public static Set<String> takeWorkFromHome(LocalDate date) throws IOException, JSONException, InterruptedException {

        Set<String> userInitials = new TreeSet<>();

        // date format has to be 2023-12-18
        String url = HRC_LINK + "leaves?filters={\"allOf\":[{\"status\":{\"allOf\":[{\"name\":{\"value\":[\"approved\"],\"operator\":\"in\"}}]}},{\"allOf\":[{\"allOf\":[{\"dt_from\":{\"value\":\""+date+"\",\"operator\":\"<=\"}},{\"dt_to\":{\"value\":\""+date+"\",\"operator\":\">=\"}}]}]}]}&with=[\"user\",\"status\",\"type\"]";
        String body = makeRequestHrc(url);

        JSONObject obj = new JSONObject(body);
        JSONArray data = obj.getJSONArray("data");

        for (int j = 0; j < data.length(); j++) {
            JSONObject wfh = (JSONObject) data.get(j);
            JSONObject user = wfh.getJSONObject("user");

            String samaccountname = user.getString("samaccountname").toLowerCase();

            userInitials.add(samaccountname);
        }

        return userInitials;
    }
}
