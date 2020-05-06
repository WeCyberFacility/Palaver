package de.cyberfacility.alpaykucuk.palaver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;


    public class APIRequestHandler {


        public static JSONObject DatenÜbermitteln(String _url, JSONObject json) throws Exception{

        URL url=new URL(_url);
        HttpURLConnection httpcon=(HttpURLConnection)url.openConnection();
        httpcon.setDoOutput(true);
        httpcon.setRequestMethod("POST");
        httpcon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        OutputStream os = httpcon.getOutputStream();
        os.write(json.toString().getBytes("UTF-8"));
        os.close();

        InputStream is = httpcon.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        JSONObject response = new JSONObject();
        String line;
        while((line = br.readLine() ) != null) {
            try {
                response = new JSONObject(line);
                System.out.println(response);

            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        httpcon.disconnect();

        return response;
    }

        public static JSONObject registriereNeuenNutzer(String username, String password) throws Exception{
            //Neues JSON Objekt erstellen mit den gewünschten Daten, um diese zu übermitteln
            JSONObject json = new JSONObject();
            try {
                json.put("Username", username);
                json.put("Password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return DatenÜbermitteln("http://palaver.se.paluno.uni-due.de/api/user/register",
                    json);
        }

        public static JSONObject checkNutzernameUndPasswordFureLogin(String username, String password) throws Exception{
            //Neues JSON Objekt erstellen mit den gewünschten Daten, um diese zu übermitteln
            JSONObject json = new JSONObject();
            try {
                json.put("Username", username);
                json.put("Password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return DatenÜbermitteln("http://palaver.se.paluno.uni-due.de/api/user/validate",
                    json);
         }

        public static JSONObject TokenSenden(String username, String password, String pushToken ) throws Exception {
            JSONObject messageBody = new JSONObject();
            try {
                messageBody.put("Username", username);
                messageBody.put("Password", password);
                messageBody.put("PushToken", pushToken );
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return DatenÜbermitteln("http://palaver.se.paluno.uni-due.de/api/user/pushtoken",messageBody);

        }
        public static JSONObject pwneu (String username, String password, String newPassword) throws Exception{
            //Neues JSON Objekt erstellen mit den gewünschten Daten, um diese zu übermitteln
            JSONObject json = new JSONObject();
            try {
                json.put("Username", username);
                json.put("Password", password);
                json.put("NewPassword", newPassword);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return DatenÜbermitteln("http://palaver.se.paluno.uni-due.de/api/user/password",
                    json);
        }

}
