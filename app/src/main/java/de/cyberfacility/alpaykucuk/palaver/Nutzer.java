package de.cyberfacility.alpaykucuk.palaver;

import org.json.JSONException;
import org.json.JSONObject;

public class Nutzer {

    private String nutzername;
    private String passwort;


    public Nutzer(String nutzername, String passwort) {
        this.nutzername = nutzername;
        this.passwort = passwort;
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


    public JSONObject NutzerVonFreundeslisteLöschen(String nutzernameFreund) throws Exception{
        JSONObject json = new JSONObject();
        try {
            json.put("Username", getNutzername());
            json.put("Password", getPasswort());
            json.put("Friend",   nutzernameFreund);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return APIRequestHandler.DatenÜbermitteln("http://palaver.se.paluno.uni-due.de/api/friends/delete", json);
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

    public JSONObject getChat(String empfängerNutzername) throws Exception{
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

    public JSONObject nachrichtSenden(String empfängerNutzername, String nachricht) throws Exception {
        JSONObject json = new JSONObject();
        try {
            json.put("Username", getNutzername());
            json.put("Password", getPasswort());
            json.put("Recipient", empfängerNutzername);
            json.put("Mimetype", "text/plain");
            json.put("Data", nachricht);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return APIRequestHandler.DatenÜbermitteln("http://palaver.se.paluno.uni-due.de/api/message/send", json);
    }


}
