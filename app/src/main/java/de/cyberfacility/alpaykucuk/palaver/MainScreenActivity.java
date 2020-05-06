package de.cyberfacility.alpaykucuk.palaver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FoldingCube;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainScreenActivity extends AppCompatActivity {


    ArrayList<Nutzer> nutzerliste = new ArrayList<>();
    RecyclerView nutzer_rv;

    static Nutzer currentNutzer;
    ProgressBar progressBar;
    JSONObject response;
    ImageView addcontactbtn;
    ImageView settingsbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        addcontactbtn = findViewById(R.id.addcontactbtn);
        settingsbtn = findViewById(R.id.settingsbtn);
        nutzer_rv = findViewById(R.id.chatsrv);
        progressBar = (ProgressBar)findViewById(R.id.loadingspinnermain);
        Sprite foldingCube = new FoldingCube();
        progressBar.setIndeterminateDrawable(foldingCube);
        progressBar.setVisibility(View.INVISIBLE);

        nutzer_rv.setLayoutManager(new LinearLayoutManager(this));
        KontaktListeLaden();


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
             Intent myIntent = new Intent(MainScreenActivity.this, activity_pw_aendern.class);
             startActivity(myIntent);
             }
     });
    }


    @Override
    protected void onResume() {
        super.onResume();
        KontaktListeLaden();
    }

    public void KontaktListeLaden() {
        //Lädt die Kontaktliste des Nutzers

        progressBar.setVisibility(View.VISIBLE);

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try  {
                        response = currentNutzer.getListeDerFreunde();

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
                    JSONArray list = new JSONArray();
                    try {
                        list = (JSONArray)response.get("Data");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    nutzerliste = formJSONARRAYtoNormalArray(list);
                    //printNutzerlist();

                    nutzer_rv.setAdapter(new ChatAdapter(nutzerliste, getApplicationContext()));
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }, 2000);

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

}
