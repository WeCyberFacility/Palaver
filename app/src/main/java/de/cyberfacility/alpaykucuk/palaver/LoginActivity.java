package de.cyberfacility.alpaykucuk.palaver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FoldingCube;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {


    EditText login_username;
    EditText login_password;
    ImageView loginbtn;
    TextView registrierenbtn;
    ProgressBar progressBar;

    JSONObject response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_username = findViewById(R.id.login_username);
        login_password = findViewById(R.id.login_password);
        loginbtn = findViewById(R.id.loginbtn);
        registrierenbtn =findViewById(R.id.registrierenbtn);

        progressBar = (ProgressBar)findViewById(R.id.loadingspinner);
        Sprite foldingCube = new FoldingCube();
        progressBar.setIndeterminateDrawable(foldingCube);
        progressBar.setVisibility(View.INVISIBLE);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        registrierenbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(myIntent);
            }
        });


    }



    public void login() {

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(login_password.getWindowToken(), 0);

        progressBar.setVisibility(View.VISIBLE);
        if (login_username.getText().toString().equals("") || login_password.getText().toString().equals("")) {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Bitte fülle alle Felder aus!")
                    .show();
            progressBar.setVisibility(View.INVISIBLE);
        } else {

            final Nutzer nutzer = new Nutzer(login_username.getText().toString().trim(), login_password.getText().toString().trim());
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try  {
                        response = APIRequestHandler.checkNutzernameUndPasswordFureLogin(nutzer.getNutzername(), nutzer.getPasswort());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();


            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    while (response == null) {}

                    String responseString = null;
                    try {
                        responseString = response.get("Info").toString().trim();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    checkResponseFromLogin(responseString);

                    progressBar.setVisibility(View.INVISIBLE);
                }
            }, 2000);

        }

    }

    public void checkResponseFromLogin(String response) {

        switch (response) {
            case "Benutzer erfolgreich validiert":
                MainScreenActivity.currentNutzer = new Nutzer(login_username.getText().toString(), login_password.getText().toString());
                Intent myIntent = new Intent(LoginActivity.this, MainScreenActivity.class);
                startActivity(myIntent);
                break;
                default:
                    FehlerAnzeigen(response);
                    login_username.setText("");
                    login_password.setText("");
                    break;
        }


    }


    public void FehlerAnzeigen(String message) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(message)
                .show();
    }
}
