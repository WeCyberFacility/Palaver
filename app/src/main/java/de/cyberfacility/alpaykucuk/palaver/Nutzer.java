package de.cyberfacility.alpaykucuk.palaver;

import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Nutzer {

    private String nutzername;
    private String passwort;

    private String pictureurl;

    private ArrayList<Message> messages = new ArrayList<>();

    private ArrayList<Nutzer> freunde = new ArrayList<>();


    public Nutzer(String nutzername, String passwort) {
        this.nutzername = nutzername;
        this.passwort = passwort;
    }

    public Nutzer(String nutzername) {
        this.nutzername = nutzername;
    }

    public String getNutzername() {
        return nutzername;
    }

    public void setNutzername(String nutzername) {
        this.nutzername = nutzername;
    }

    public String getPasswort() {
        return passwort;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    public String getPictureurl() {
        return pictureurl;
    }

    public void setPictureurl(String pictureurl) {
        this.pictureurl = pictureurl;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public ArrayList<Nutzer> getFreunde() {
        return freunde;
    }

    public void setFreunde(ArrayList<Nutzer> freunde) {
        this.freunde = freunde;
    }

    public void addFreund(Nutzer newFriend) {
        getFreunde().add(newFriend);
    }

    public void saveNutzerOffline(SharedPreferences sharedPreferences) {

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(this);
        prefsEditor.putString(getNutzername(), json);
        prefsEditor.commit();


    }

    public void getNutzerOffline(SharedPreferences sharedPreferences) {

        Gson gson = new Gson();
        String json = sharedPreferences.getString(getNutzername(), "");
        Nutzer currNutzer = gson.fromJson(json, Nutzer.class);
        this.setMessages(currNutzer.getMessages());
        this.setFreunde(currNutzer.getFreunde());

    }


    public Nutzer searchFreundInListe(String nutzername) {

        for (Nutzer currFreund : getFreunde()) {
            if (currFreund.getNutzername().equals(nutzername)) {
                return currFreund;
            }
        }

        return new Nutzer("notfound");

    }


    public void addFreundeInOffline(ArrayList<Nutzer> onlinelist) {

        for (Nutzer cu : onlinelist) {
            if (!istOfflineVorhanden(cu)) {
                addFreund(cu);
            }
        }

    }

    public boolean istOfflineVorhanden(Nutzer cu) {
        for (Nutzer cui : getFreunde()) {
            if (cui.getNutzername().equals(cu.getNutzername())) {
                return true;
            }
        }
        return false;
    }


    //API Abfragen:

    public JSONObject NutzerAlsFreundHinzuzfuegen(String nutzernameFreund) throws Exception{
        JSONObject json = new JSONObject();
        try {
            json.put("Username", getNutzername());
            json.put("Password", getPasswort());
            json.put("Friend",   nutzernameFreund);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return APIRequestHandler.DatenÜbermitteln("http://palaver.se.paluno.uni-due.de/api/friends/add", json);
    }


    public JSONObject getListeDerFreunde() throws Exception{
        JSONObject json = new JSONObject();
        try {
            json.put("Username", getNutzername());
            json.put("Password", getPasswort());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return APIRequestHandler.DatenÜbermitteln("http://palaver.se.paluno.uni-due.de/api/friends/get", json);
    }

    public JSONObject getChatMitEmpfaenger(String empfängerNutzername) throws Exception{
        JSONObject json = new JSONObject();
        try {
            json.put("Username", getNutzername());
            json.put("Password", getPasswort());
            json.put("Recipient", empfängerNutzername);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return APIRequestHandler.DatenÜbermitteln("http://palaver.se.paluno.uni-due.de/api/message/get", json);
    }

    public JSONObject getChatAbZeitpunkt(String empfängerNutzername, String date) throws Exception{
        JSONObject json = new JSONObject();
        try {
            json.put("Username", getNutzername());
            json.put("Password", getPasswort());
            json.put("Recipient", empfängerNutzername);
            json.put("Offset", date);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return APIRequestHandler.DatenÜbermitteln("http://palaver.se.paluno.uni-due.de/api/message/getoffset", json);
    }

    public JSONObject nachrichtVersenden(String empfängerNutzername, String mimetype, String nachricht) throws Exception {
        JSONObject json = new JSONObject();
        try {
            json.put("Username", getNutzername());
            json.put("Password", getPasswort());
            json.put("Recipient", empfängerNutzername);
            json.put("Mimetype", mimetype);
            json.put("Data", nachricht);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return APIRequestHandler.DatenÜbermitteln("http://palaver.se.paluno.uni-due.de/api/message/send", json);
    }


    public JSONObject tokenAktualisieren(String benutzernameText, String passwordText) throws Exception {

        JSONObject json = new JSONObject();
        String currentInstanceToken = FirebaseInstanceId.getInstance().getToken();

        System.out.println("TOKEN: " + currentInstanceToken);

        try {
            json.put("Username", benutzernameText);
            json.put("Password", passwordText);
            json.put("PushToken", currentInstanceToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return APIRequestHandler.DatenÜbermitteln("http://palaver.se.paluno.uni-due.de/api/user/pushtoken",
                json);


    }


}
