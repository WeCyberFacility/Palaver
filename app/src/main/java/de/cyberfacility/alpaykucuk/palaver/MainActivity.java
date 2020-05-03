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

        logout_save();
        checkLoggedIn();
    }



    public void checkLoggedIn() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean loggedin = preferences.getBoolean("loggedIn", false);
        System.out.println("LoggedIn: " + loggedin);
        if(loggedin)
        {
            Intent myIntent = new Intent(MainActivity.this, splashActivity.class);
            this.startActivity(myIntent);
            finish();
        }
        else {
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            this.startActivity(myIntent);
            finish();
        }
    }


    public void login_save() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("loggedIn",true);
        editor.apply();
    }

    public void logout_save() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("loggedIn",false);
        editor.apply();
    }
}
