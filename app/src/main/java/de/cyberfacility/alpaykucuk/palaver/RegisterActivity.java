package de.cyberfacility.alpaykucuk.palaver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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


public class RegisterActivity extends AppCompatActivity {
EditText register_username;
EditText register_password;
EditText register_password_wdh;
ImageView registerbtn;
ProgressBar progressBar;
JSONObject response;

SweetAlertDialog dialog;

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

        progressBar = (ProgressBar)findViewById(R.id.loadingspinnerregister);
        Sprite foldingCube = new FoldingCube();
        progressBar.setIndeterminateDrawable(foldingCube);
        progressBar.setVisibility(View.INVISIBLE);

    }

    public void register(){
        progressBar.setVisibility(View.VISIBLE);
        if(register_username.getText().toString().equals("")|| register_password.getText().toString().equals("")||
                register_password_wdh.getText().toString().equals("") || !isNetworkAvailable()){
                    FehlerAnzeigen("Mach Internet an du depp!");
                    progressBar.setVisibility(View.INVISIBLE);
        }else {
            passwordequal();
        }
    }
    public void passwordequal(){
        if(!(register_password.getText().toString().equals(register_password_wdh.getText().toString())) &&
        register_password.getText().toString()!= null && register_password_wdh.getText().toString() != null){
            FehlerAnzeigen("Passwörter nicht identisch!");
            progressBar.setVisibility(View.INVISIBLE);
        }else{
            registrieren();
        }

    }

    public void registrieren() {

        final Nutzer nutzer = new Nutzer(register_username.getText().toString().trim(), register_password.getText().toString().trim());
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    response = APIRequestHandler.registriereNeuenNutzer(nutzer.getNutzername(), nutzer.getPasswort());

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

                ErfolgAnzeigen("Registrierung erfolgreich!");
                break;
            default:
                FehlerAnzeigen(response);
                register_username.setText("");
                register_password.setText("");
                register_password_wdh.setText("");
                break;
        }


    }


    public void FehlerAnzeigen(String message) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(message)
                .show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void ErfolgAnzeigen(String message) {
        dialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("You did it")
                .setContentText(message);
        dialog.show();
    }
}
