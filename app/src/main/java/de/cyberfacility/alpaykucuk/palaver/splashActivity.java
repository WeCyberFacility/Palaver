package de.cyberfacility.alpaykucuk.palaver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class splashActivity extends Activity {

    private final int Splashscreenduration = 2300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                //springe direkt zum hauptmenü
                Intent myIntent = new Intent(splashActivity.this, LoginActivity.class);
                startActivity(myIntent);
                finish();

            }
        }, Splashscreenduration);


    }
}
