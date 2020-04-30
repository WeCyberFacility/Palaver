package de.cyberfacility.alpaykucuk.palaver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;


public class RegisterActivity extends AppCompatActivity {
EditText register_username;
EditText register_password;
EditText register_password_wdh;
ImageView registerbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_username = findViewById(R.id.register_username);
        register_password = findViewById(R.id.register_password);
        register_password_wdh = findViewById(R.id.register_password_wdh);
        registerbtn = findViewById(R.id.registerbtn);

    }
}
