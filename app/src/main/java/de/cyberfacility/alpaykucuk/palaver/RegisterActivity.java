package de.cyberfacility.alpaykucuk.palaver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import cn.pedant.SweetAlert.SweetAlertDialog;


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
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }

    public void register(){
        if(register_username.getText().toString().equals("")|| register_password.getText().toString().equals("")||
                register_password_wdh.getText().toString().equals("")){
                     new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Bitte fülle alle Felder aus!")
                    .show();
        }else {
            passwordequal();
        }
    }
    public void passwordequal(){
        if(!(register_password.getText().toString().equals(register_password_wdh.getText().toString())) &&
        register_password.getText().toString()!= null && register_password_wdh.getText().toString() != null){
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Passwörter unterschiedlich")
                    .show();
        }else{
            //sever verbinden
        }

    }
}
