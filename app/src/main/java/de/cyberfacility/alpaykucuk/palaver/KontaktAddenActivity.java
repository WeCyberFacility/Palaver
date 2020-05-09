package de.cyberfacility.alpaykucuk.palaver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FoldingCube;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class KontaktAddenActivity extends AppCompatActivity {


    EditText newcontacttxt;
    ImageView addcontact;
    ProgressBar progressBar;
    JSONObject response;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kontakt_adden);
        sharedPreferences = getPreferences(MODE_PRIVATE);

        newcontacttxt = findViewById(R.id.newcontacttxt);
        addcontact = findViewById(R.id.addcontactconfirm);

        progressBar = (ProgressBar)findViewById(R.id.loadingspinnerkontakthinzufügen);
        Sprite foldingCube = new FoldingCube();
        progressBar.setIndeterminateDrawable(foldingCube);
        progressBar.setVisibility(View.INVISIBLE);


        addcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContact();
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void addContact(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(newcontacttxt.getWindowToken(), 0);

        progressBar.setVisibility(View.VISIBLE);
        if (newcontacttxt.getText().toString().equals("") || !isNetworkAvailable()) {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Etwas ist schief gelaufen!")
                    .show();
            progressBar.setVisibility(View.INVISIBLE);
        } else {

            final Nutzer newNutzer = new Nutzer(newcontacttxt.getText().toString());
            kontaktHinzufügen(newNutzer.getNutzername());


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

                    MainScreenActivity.currentNutzer.addFreund(newNutzer);
                    MainScreenActivity.currentNutzer.saveNutzerOffline(sharedPreferences);

                    progressBar.setVisibility(View.INVISIBLE);
                }
            }, 2000);
        }
    }

    public void kontaktHinzufügen(final String newNutzername) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    response = MainScreenActivity.currentNutzer.NutzerAlsFreundHinzuzfuegen(newNutzername);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void checkResponseFromLogin(String response) {

        switch (response) {
            case "Freund hinzugefügt":
                ErfolgAnzeigen(response);
                newcontacttxt.setText("");

                break;
            default:
                FehlerAnzeigen(response);
                newcontacttxt.setText("");
                break;
        }


    }


    public void FehlerAnzeigen(String message) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(message)
                .show();
    }

    public void ErfolgAnzeigen(String message) {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("You did it")
                .setContentText(message)
                .show();
    }


}
