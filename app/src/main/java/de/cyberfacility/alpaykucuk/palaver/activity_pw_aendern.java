package de.cyberfacility.alpaykucuk.palaver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FoldingCube;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class activity_pw_aendern extends AppCompatActivity {
    EditText new_pw;
    EditText new_pw_wdh;
    ImageView pw_bestaetigenbtn;

    ProgressBar progressBar;
    JSONObject response;

    SweetAlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pw_aendern);

        new_pw = findViewById(R.id.new_Password);
        new_pw_wdh = findViewById(R.id.new_password_wdh);
        pw_bestaetigenbtn = findViewById(R.id.pw_bestaetigenbtn);


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

    public void pwcheck1(){
        progressBar.setVisibility(View.VISIBLE);
        if(new_pw.getText().toString().equals("") || new_pw_wdh.getText().toString().equals("")){
            FehlerAnzeigen("Bitte fülle alle Felder");
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
}
