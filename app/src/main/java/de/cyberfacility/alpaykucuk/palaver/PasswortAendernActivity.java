package de.cyberfacility.alpaykucuk.palaver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FoldingCube;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PasswortAendernActivity extends AppCompatActivity {
    EditText new_pw;
    EditText new_pw_wdh;
    ImageView pw_bestaetigenbtn;
    ImageView logoutbtn;

    ProgressBar progressBar;
    JSONObject response;

    SweetAlertDialog dialog;
    SharedPreferences sharedPreferences;


    //TODO: currentNutzer's Passwort aktualisieren, Dialog anpassen, Edittexte anpassen


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pw_aendern);

        sharedPreferences = getPreferences(MODE_PRIVATE);

        new_pw = findViewById(R.id.new_Password);
        new_pw_wdh = findViewById(R.id.new_password_wdh);
        pw_bestaetigenbtn = findViewById(R.id.pw_bestaetigenbtn);
        logoutbtn = findViewById(R.id.logoutbtn);

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logout_save();
                Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(myIntent);
                finish();




            }
        });

        pw_bestaetigenbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pwcheck1();
            }
        });

        progressBar = (ProgressBar)findViewById(R.id.loadingspinner_pwaendern);
        Sprite foldingCube = new FoldingCube();
        progressBar.setIndeterminateDrawable(foldingCube);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void pwcheck1(){
        progressBar.setVisibility(View.VISIBLE);
        if(new_pw.getText().toString().equals("") || new_pw_wdh.getText().toString().equals("") || !isNetworkAvailable()){
            FehlerAnzeigen("Es ist ein Fehler aufgetreten!");
            progressBar.setVisibility(View.INVISIBLE);
        }else {
            pwcheck2();
        }
    }

    public void pwcheck2(){
        progressBar.setVisibility(View.VISIBLE);
        if(!(new_pw.getText().toString().equals(new_pw_wdh.getText().toString())) &&
                new_pw.getText().toString()!= null && new_pw_wdh.getText().toString() != null){
            FehlerAnzeigen("Passwöter nicht identisch");
            progressBar.setVisibility(View.INVISIBLE);
        }else {
            pwaendern();
        }
    }

    public void pwaendern(){
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    response = APIRequestHandler.pwneu(MainScreenActivity.currentNutzer.getNutzername(),MainScreenActivity.currentNutzer.getPasswort(), new_pw_wdh.getText().toString());


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
                checkResponse(responseString);

                progressBar.setVisibility(View.INVISIBLE);
            }
        }, 2000);




    }


    public void FehlerAnzeigen(String message) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(message)
                .show();
    }


    public void checkResponse(String response) {

        switch (response) {
            case "Benutzer erfolgreich angelegt":
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismissWithAnimation();
                        onBackPressed();
                    }
                }, 2000);

                ErfolgAnzeigen("Password änderung erfolgreich!");
                break;
            default:
                FehlerAnzeigen(response);
                new_pw.setText("");
                new_pw_wdh.setText("");
                break;
        }

    }

    public void ErfolgAnzeigen(String message) {
        dialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("You did it")
                .setContentText(message);
        dialog.show();
    }

    public void logout_save() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("loggedIn",false);
        editor.apply();
    }

}
