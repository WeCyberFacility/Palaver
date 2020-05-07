package de.cyberfacility.alpaykucuk.palaver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        Nutzer currentLoggedNutzer = new Nutzer(nutzername, passwort);
        MainScreenActivity.currentNutzer = currentLoggedNutzer;
    }


}
