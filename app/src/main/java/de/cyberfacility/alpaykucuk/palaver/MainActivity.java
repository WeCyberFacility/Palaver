package de.cyberfacility.alpaykucuk.palaver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.preference.PowerPreference;

import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getPreferences(MODE_PRIVATE);
        PowerPreference.init(this);

        checkLoggedIn();

    }

    //TODO: Speichere eine eingeloggte Person in den Shared Prefences


    public void checkLoggedIn() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean loggedin = preferences.getBoolean("loggedIn", false);
        System.out.println("LoggedIn: " + loggedin);
        if(!loggedin)
        {
            Intent myIntent = new Intent(MainActivity.this, splashActivity.class);
            this.startActivity(myIntent);
            finish();
        }
        else {

            getLoggedNutzer();
            Intent myIntent = new Intent(MainActivity.this, MainScreenActivity.class);
            this.startActivity(myIntent);
            finish();
        }
    }


    public void getLoggedNutzer() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String nutzername = preferences.getString("Nutzername", "");
        String passwort = preferences.getString("Passwort", "");

        Nutzer offlineNutzer = PowerPreference.getFileByName("Offline").getObject("OfflineNutzer", Nutzer.class);
        MainScreenActivity.currentNutzer = offlineNutzer;
        System.out.println("Beweis: " + offlineNutzer.getNutzername());

    }

    public void logout_save() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("loggedIn",false);
        editor.apply();
    }


}
