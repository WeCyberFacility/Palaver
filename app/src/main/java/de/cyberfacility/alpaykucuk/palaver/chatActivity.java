package de.cyberfacility.alpaykucuk.palaver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.google.firebase.FirebaseApp;
import com.preference.PowerPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.crypto.spec.OAEPParameterSpec;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class chatActivity extends AppCompatActivity{

    TextView empfängername;
    ImageView empfängerbild;
    ImageView sendenbtn;
    RecyclerView message_rv;
    EditText chatsendentxt;
    ImageView gpsbtn;

    ProgressBar progressBar;

    SharedPreferences sharedPreferences;



    public static ArrayList<Message> messages = new ArrayList<>();

    public static Nutzer currentEmpfänger;
    static messageAdapter messageAdapter;

    NotificationReciever notificationReciever = new NotificationReciever();


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("de.cyberfacility.alpaykucuk.palaver");
        filter.addCategory("de.uni_due.paluno.se.palaver");
        registerReceiver(notificationReciever, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(notificationReciever);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        FirebaseApp.initializeApp(this);

        notificationReciever.currChatActivity = this;
        sharedPreferences = getPreferences(MODE_PRIVATE);

        IntentFilter filter = new IntentFilter();
        filter.addAction("de.cyberfacility.alpaykucuk.palaver");
        filter.addCategory("de.uni_due.paluno.se.palaver");
        registerReceiver(notificationReciever, filter);

        empfängername = findViewById(R.id.chatname);
        empfängerbild = findViewById(R.id.chatbild);
        sendenbtn = findViewById(R.id.sendenbtn);
        message_rv = findViewById(R.id.messagesrv);
        chatsendentxt = findViewById(R.id.chatsendentxt);
        gpsbtn = findViewById(R.id.gpsbtn);

        progressBar = (ProgressBar)findViewById(R.id.loadingspinnermessages);
        Sprite foldingCube = new FoldingCube();
        progressBar.setIndeterminateDrawable(foldingCube);
        progressBar.setVisibility(View.INVISIBLE);

        messageAdapter = new messageAdapter(messages, getApplicationContext());
        empfängername.setText(currentEmpfänger.getNutzername());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext() ,RecyclerView.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        message_rv.setLayoutManager(layoutManager);
        message_rv.setAdapter(messageAdapter);


        if (isNetworkAvailable()) {
            TokenAktualisieren();
        } else {

        }

        gpsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isNetworkAvailable()) {
                    new SweetAlertDialog(chatActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Etwas ist schiefgelaufen!")
                            .show();

                } else {

                    gpsNachrichtSenden();
                }

            }
        });

        sendenbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(chatsendentxt.getWindowToken(), 0);
                if(chatsendentxt.getText().toString().equals("") || !isNetworkAvailable()) {
                    new SweetAlertDialog(chatActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Etwas ist schiefgelaufen!")
                            .show();

                    chatsendentxt.setText("");


                } else {

                    nachrichtSenden();
                }
            }
        });


        if (isNetworkAvailable()) {
            pullMessages();
        } else {

            messages = MainScreenActivity.currentNutzer.searchFreundInListe(currentEmpfänger.getNutzername()).getMessages();

            messageAdapter.updateList(messages);
            message_rv.scrollToPosition(messages.size() - 1);

        }





    }


    public void refreshList() {

        new Thread(new Runnable() {

            @Override
            public void run() {


                MessagesAbZeitpunktLaden();


            }
        }).start();
    }

    public void pullMessages() {

        new Thread(new Runnable() {

            @Override
            public void run() {


                MessagesLaden();


            }
        }).start();
    }






    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }


    public void nachrichtSenden() {

        String opCodeMerge = OpCodesMessages.TEXT.ordinal() + chatsendentxt.getText().toString();
        nachrichtSenden nS = new nachrichtSenden("text/plain", opCodeMerge);
        nS.execute((Void)null);
        chatsendentxt.setText("");



    }



    public void MessagesLaden() {
        //Lädt die Messages des Nutzers
        messages = new ArrayList<>();
        nachrichtAnzeigen nA = new nachrichtAnzeigen(currentEmpfänger.getNutzername());
        nA.execute((Void)null);

    }

    public void MessagesAbZeitpunktLaden() {
        //Lädt die Messages des Nutzers
        nachrichtAnzeigenAbZeitpunkt nz = new nachrichtAnzeigenAbZeitpunkt(currentEmpfänger.getNutzername());
        nz.execute((Void)null);

    }


    public void TokenAktualisieren() {
        //Lädt die Messages des Nutzers
        Tokenmoken tm = new Tokenmoken();
        tm.execute((Void)null);

    }

    public ArrayList<Message> formJSONARRAYtoNormalArray(JSONArray jsonArray) {
        ArrayList<Message> arrayList = new ArrayList(jsonArray.length());
        for(int i=0;i < jsonArray.length();i++){
            try {
                JSONObject currentJSONObjekt = jsonArray.getJSONObject(i);
                if (String.valueOf(currentJSONObjekt.get("Data").toString().charAt(0)).equals(String.valueOf(OpCodesMessages.GEO.ordinal()))) {
                    GPSMessage newMessage = new GPSMessage(
                            currentJSONObjekt.get("Sender").toString(),
                            currentJSONObjekt.get("Recipient").toString(),
                            currentJSONObjekt.get("Mimetype").toString(),
                            currentJSONObjekt.get("Data").toString().substring(1),
                            currentJSONObjekt.get("DateTime").toString());
                    newMessage.generateLängenUndBreitenGrad();
                    arrayList.add(newMessage);
                } else {
                    Message newMessage = new Message(
                            currentJSONObjekt.get("Sender").toString(),
                            currentJSONObjekt.get("Recipient").toString(),
                            currentJSONObjekt.get("Mimetype").toString(),
                            currentJSONObjekt.get("Data").toString().substring(1),
                            currentJSONObjekt.get("DateTime").toString());
                    arrayList.add(newMessage);
                }

                //TODO: FALLS PIC NEUES ELSE-IF
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }


    public class nachrichtSenden extends AsyncTask<Void,Void,Boolean> {

        String nachrichtText;
        String mimetype;
        JSONObject response = new JSONObject();



        public nachrichtSenden(String newmimetype, String nachrichtEditText1){
            nachrichtText=nachrichtEditText1;
            mimetype = newmimetype;
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

                response = MainScreenActivity.currentNutzer.nachrichtSenden(currentEmpfänger.getNutzername(), mimetype, nachrichtText);

            }

            catch (Exception e) {
                e.printStackTrace();
            }


            int msgType = 0;

            try {
                msgType = response.getInt("MsgType");

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (msgType == 1) {

                refreshList();


            } else if (msgType == 0) {

            } else {
            }



        }
    }


    public class nachrichtAnzeigenAbZeitpunkt extends AsyncTask<Void,Void,Boolean> {
        String freund;
        JSONObject response = new JSONObject();




        public nachrichtAnzeigenAbZeitpunkt(String friend){
            freund=friend;

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

                if(messages.size() == 0) {
                    pullMessages();
                } else {
                    response = MainScreenActivity.currentNutzer.getChatAbZeitpunkt(currentEmpfänger.getNutzername(), messages.get(messages.size()-1).getDate());
                }

            }

            catch (Exception e) {
                e.printStackTrace();
            }


            int msgType = 0;

            try {

                if (messages.size() == 0) {

                } else {
                    msgType = response.getInt("MsgType");


                    if (msgType == 1) {

                        JSONArray list = new JSONArray();
                        try {
                            list = (JSONArray) response.get("Data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (list.length() != 0) {

                            messages.addAll(formJSONARRAYtoNormalArray(list));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    messageAdapter.updateList(messages);
                                    message_rv.scrollToPosition(messages.size() - 1);
                                    MainScreenActivity.currentNutzer.searchFreundInListe(currentEmpfänger.getNutzername()).setMessages(messages);
                                    PowerPreference.getFileByName("Offline").putObject("OfflineNutzer", MainScreenActivity.currentNutzer);
                                }
                            });


                        }
                    } else {
                        Toast.makeText(chatActivity.this, "Fehler", Toast.LENGTH_SHORT).show();
                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class nachrichtAnzeigen extends AsyncTask<Void,Void,Boolean> {
        String freund;
        JSONObject response = new JSONObject();



        public nachrichtAnzeigen(String friend){
            freund=friend;

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

                response = MainScreenActivity.currentNutzer.getChat(currentEmpfänger.getNutzername());

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
                        list = (JSONArray)response.get("Data");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    messages = formJSONARRAYtoNormalArray(list);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageAdapter.updateList(messages);
                            message_rv.scrollToPosition(messages.size() - 1);
                            MainScreenActivity.currentNutzer.searchFreundInListe(currentEmpfänger.getNutzername()).setMessages(messages);
                            MainScreenActivity.currentNutzer.saveNutzerOffline(sharedPreferences);
                            MainScreenActivity.currentNutzer.getNutzerOffline(sharedPreferences);

                        }
                    });

                } else {
                    Toast.makeText(chatActivity.this, "Fehler", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void gpsNachrichtSenden() {
        GPS gps = new GPS(getApplicationContext());
        Location l = gps.getLocation();
        if (l == null) {
            Toast.makeText(getApplicationContext(), "GPS Fehler", Toast.LENGTH_SHORT).show();
        } else {
            double lat = l.getLatitude();
            double lon = l.getLongitude();
            Uri intentUri = Uri.parse("geo:" + lat + "," + lon);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            String opCodeMerge = OpCodesMessages.GEO.ordinal() + intentUri.toString();
            nachrichtSenden nS = new nachrichtSenden("text/plain", opCodeMerge);
            nS.execute((Void) null);
            refreshList();

        }
    }


    public class Tokenmoken extends AsyncTask<Void, Void, Boolean> {

        JSONObject response;


        public Tokenmoken(){
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

                response = MainScreenActivity.currentNutzer.tokenmoken(MainScreenActivity.currentNutzer.getNutzername(), MainScreenActivity.currentNutzer.getPasswort());

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
                Toast.makeText(chatActivity.this, "Fehler",Toast.LENGTH_SHORT).show();
            }

        }
    }


}
