package de.cyberfacility.alpaykucuk.palaver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.preference.PowerPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainScreenActivity extends AppCompatActivity {


    ArrayList<Nutzer> nutzerliste = new ArrayList<>();
    RecyclerView nutzer_rv;

    public static Nutzer currentNutzer;
    ProgressBar progressBar;
    JSONObject response;
    ImageView addcontactbtn;
    ImageView settingsbtn;
    SharedPreferences sharedPreferences;

    boolean firstStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        sharedPreferences = getPreferences(MODE_PRIVATE);
        addcontactbtn = findViewById(R.id.addcontactbtn);
        settingsbtn = findViewById(R.id.settingsbtn);
        nutzer_rv = findViewById(R.id.chatsrv);
        progressBar = (ProgressBar)findViewById(R.id.loadingspinnermain);
        Sprite foldingCube = new FoldingCube();
        progressBar.setIndeterminateDrawable(foldingCube);
        progressBar.setVisibility(View.INVISIBLE);

        nutzer_rv.setLayoutManager(new LinearLayoutManager(this));


        if(istMitInternetVerbunden()) {
            ChatsEmpfangen nz = new ChatsEmpfangen();
            nz.execute((Void)null);
        } else {

            nutzerliste = currentNutzer.getFreunde();
            nutzer_rv.setAdapter(new ChatAdapter(nutzerliste, MainScreenActivity.this));

        }

        if (istMitInternetVerbunden() && firstStart) {
            TokenAktualisieren();
            firstStart = false;
        } else {

        }




        addcontactbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainScreenActivity.this, KontaktAddenActivity.class);
                startActivity(myIntent);

            }
        });


        settingsbtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             Intent myIntent = new Intent(MainScreenActivity.this, PasswortAendernActivity.class);
             startActivity(myIntent);
             }
     });
    }


    private boolean istMitInternetVerbunden() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netzwerkInfo = connectivityManager.getActiveNetworkInfo();
        return netzwerkInfo != null && netzwerkInfo.isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(istMitInternetVerbunden()) {
            ChatsEmpfangen nz = new ChatsEmpfangen();
            nz.execute((Void)null);
        } else {

            nutzerliste = currentNutzer.getFreunde();
            nutzer_rv.setAdapter(new ChatAdapter(nutzerliste, MainScreenActivity.this));

        }
    }


    public void TokenAktualisieren() {
        //Lädt die Messages des Nutzers
        TokenAktualisieren newTokenAktualisierenTask = new TokenAktualisieren();
        newTokenAktualisierenTask.execute((Void)null);

    }

    public ArrayList<Nutzer> formJSONARRAYtoNormalArray(JSONArray jsonArray) {
        ArrayList<Nutzer> arrayList = new ArrayList(jsonArray.length());
        for(int i=0;i < jsonArray.length();i++){
            try {
                arrayList.add(new Nutzer(jsonArray.get(i).toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }

    public void printNutzerlist() {
        for (int i = 0; i<nutzerliste.size(); i++) {

            System.out.println(nutzerliste.get(i).getNutzername());
        }

    }



    public class ChatsEmpfangen extends AsyncTask<Void,Void,Boolean> {

        JSONObject response = new JSONObject();



        public ChatsEmpfangen(){
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return null;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);

            try{


                    response = MainScreenActivity.currentNutzer.getListeDerFreunde();
                    currentNutzer.addFreundeInOffline(nutzerliste);
                    PowerPreference.getFileByName("Offline").putObject("OfflineNutzer", currentNutzer);
                    currentNutzer = PowerPreference.getFileByName("Offline").getObject("OfflineNutzer", Nutzer.class);


            }

            catch (Exception e) {
                e.printStackTrace();
            }


            int msgType = 0;

            try {

                    msgType = response.getInt("MsgType");


                    if (msgType == 1) {

                        JSONArray list = new JSONArray();
                        try {
                            list = (JSONArray) response.get("Data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (list.length() != 0) {

                            nutzerliste = (formJSONARRAYtoNormalArray(list));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    nutzer_rv.setAdapter(new ChatAdapter(nutzerliste, MainScreenActivity.this));
                                }
                            });


                        }
                    } else {

                    }




            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    public class TokenAktualisieren extends AsyncTask<Void, Void, Boolean> {

        JSONObject response;


        public TokenAktualisieren(){
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return null;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {


            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);


            try {

                response = MainScreenActivity.currentNutzer.tokenAktualisieren(MainScreenActivity.currentNutzer.getNutzername(), MainScreenActivity.currentNutzer.getPasswort());

            } catch (Exception e) {
                e.printStackTrace();
            }


            int msgType = 0;

            try {
                msgType = response.getInt("MsgType");

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (msgType == 1) {

            }
            else{
                Toast.makeText(MainScreenActivity.this, "Es ist ein Fehler aufgetreten",Toast.LENGTH_SHORT).show();
            }

        }
    }

}
