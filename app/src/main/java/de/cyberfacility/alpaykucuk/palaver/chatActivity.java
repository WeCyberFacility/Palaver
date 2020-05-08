package de.cyberfacility.alpaykucuk.palaver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class chatActivity extends AppCompatActivity {

    TextView empfängername;
    ImageView empfängerbild;
    ImageView sendenbtn;
    RecyclerView message_rv;
    EditText chatsendentxt;

    ProgressBar progressBar;
    JSONObject response;



    public static ArrayList<Message> messages;

    public static Nutzer currentEmpfänger;
    static messageAdapter messageAdapter;

    boolean initial = true;

    nachrichtAnzeigenAbZeitpunkt nAZ;

    static Thread currentThread;

    //TODO: pulle durchgehend die Messages ab der letzten Message in der Liste


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        empfängername = findViewById(R.id.chatname);
        empfängerbild = findViewById(R.id.chatbild);
        sendenbtn = findViewById(R.id.sendenbtn);
        message_rv = findViewById(R.id.messagesrv);
        chatsendentxt = findViewById(R.id.chatsendentxt);

        progressBar = (ProgressBar)findViewById(R.id.loadingspinnermessages);
        Sprite foldingCube = new FoldingCube();
        progressBar.setIndeterminateDrawable(foldingCube);
        progressBar.setVisibility(View.INVISIBLE);

        messageAdapter = new messageAdapter(messages, getApplicationContext());
        empfängername.setText(currentEmpfänger.getNutzername());
        message_rv.setLayoutManager(new LinearLayoutManager(this));







        sendenbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(chatsendentxt.getWindowToken(), 0);
                if(chatsendentxt.getText().toString().equals("")) {
                    new SweetAlertDialog(chatActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Bitte fülle alle Felder aus!")
                            .show();

                    chatsendentxt.setText("");


                } else {

                    nachrichtSenden();
                }
            }
        });


        MessagesLaden();





    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }



    public void notifyRecycleView() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    while (true) {
                        messageAdapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }


    public void listenToChat() {

        if (currentThread == null) {
            currentThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try  {
                        Looper.prepare();
                        while (true) {



                            MessagesAbZeitpunktLaden();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            currentThread.start();
        } else {
            currentThread.interrupt();
            currentThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try  {
                        Looper.prepare();
                        while (true) {

                            MessagesAbZeitpunktLaden();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            currentThread.start();
        }
    }


    public void nachrichtSenden() {

        nachrichtSenden nS = new nachrichtSenden(chatsendentxt.getText().toString());
        nS.execute((Void)null);
        Toast.makeText(chatActivity.this, "gesendet!",Toast.LENGTH_SHORT).show();
        chatsendentxt.setText("");

    }



    public void MessagesLaden() {
        //Lädt die Messages des Nutzers
        messages = new ArrayList<>();
        nachrichtAnzeigen nA = new nachrichtAnzeigen(currentEmpfänger.getNutzername());
        nA.execute((Void)null);
        Toast.makeText(chatActivity.this, "Aktualisiert!",Toast.LENGTH_SHORT).show();

        listenToChat();

    }



    public void MessagesAbZeitpunktLaden() {
        //Lädt die Messages des Nutzers

        if (nAZ == null) {
            nAZ = new nachrichtAnzeigenAbZeitpunkt(currentEmpfänger.getNutzername());
            nAZ.execute((Void)null);
        } else {
            nAZ.cancel(true);
            nAZ = new nachrichtAnzeigenAbZeitpunkt(currentEmpfänger.getNutzername());
            nAZ.execute((Void)null);
        }

    }



    public void updateList(ArrayList<Message> list){
        if(messageAdapter != null) {
            messageAdapter.updateList(list);
        }
    }

    public void printList() {

        for (Message currentMessage : messages) {
            System.out.println(currentMessage.getData());
        }

    }

    public ArrayList<Message> formJSONARRAYtoNormalArray(JSONArray jsonArray) {
        ArrayList<Message> arrayList = new ArrayList(jsonArray.length());
        for(int i=0;i < jsonArray.length();i++){
            try {
                JSONObject currentJSONObjekt = jsonArray.getJSONObject(i);
                Message newMessage = new Message(
                        currentJSONObjekt.get("Sender").toString(),
                        currentJSONObjekt.get("Recipient").toString(),
                        currentJSONObjekt.get("Mimetype").toString(),
                        currentJSONObjekt.get("Data").toString(),
                        currentJSONObjekt.get("DateTime").toString());
                arrayList.add(newMessage);
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



        public nachrichtSenden(String nachrichtEditText1){
            nachrichtText=nachrichtEditText1;
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

                response = MainScreenActivity.currentNutzer.nachrichtSenden(currentEmpfänger.getNutzername(), nachrichtText);

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

                //        Toast.makeText(Chat.this, "Nachricht gesendet", Toast.LENGTH_SHORT).show();


            } else if (msgType == 0) {

                Toast.makeText(chatActivity.this, "Nachricht senden fehlgeschlagen!", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(chatActivity.this, "Fehler", Toast.LENGTH_SHORT).show();
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

                response = MainScreenActivity.currentNutzer.getChatAbZeitpunkt(currentEmpfänger.getNutzername(), messages.get(messages.size()-1).getDate());

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

                    if (list.length() != 0) {

                        messages.addAll(formJSONARRAYtoNormalArray(list));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                messageAdapter = new messageAdapter(messages, chatActivity.this);
                                message_rv.setAdapter(messageAdapter);
                                if (messages.size() != 0) {
                                    message_rv.smoothScrollToPosition(messages.size() - 1);
                                }
                            }
                        });



                    }
                } else {
                    Toast.makeText(chatActivity.this, "Fehler", Toast.LENGTH_SHORT).show();
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

                    messageAdapter = new messageAdapter(messages, chatActivity.this);
                    message_rv.setAdapter(messageAdapter);
                    if (messages.size() != 0) {
                        message_rv.smoothScrollToPosition(messages.size()-1);
                    }
                    //     Toast.makeText(Chat.this, "Nachrichten werden erfolgreich angezeigt!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(chatActivity.this, "Fehler", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
