package de.cyberfacility.alpaykucuk.palaver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FoldingCube;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class chatActivity extends AppCompatActivity {

    TextView empfängername;
    ImageView empfängerbild;
    ImageView sendenbtn;
    RecyclerView message_rv;
    EditText chatsendentxt;

    ProgressBar progressBar;
    JSONObject response;

    public static ArrayList<Message> messages = new ArrayList<>();

    public static Nutzer currentEmpfänger;

    boolean initial = true;

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

        empfängername.setText(currentEmpfänger.getNutzername());
        message_rv.setLayoutManager(new LinearLayoutManager(this));



        MessagesLaden();



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
                } else {

                    nachrichtSenden();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MessagesLaden();
                        }
                    }, 350);
                }
            }
        });

    }

    public void nachrichtSenden() {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    MainScreenActivity.currentNutzer.nachrichtSenden(currentEmpfänger.getNutzername(), chatsendentxt.getText().toString());
                    chatsendentxt.setText("");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }



    public void MessagesLaden() {
        //Lädt die Messages des Nutzers

        if (initial) {
            progressBar.setVisibility(View.VISIBLE);
            initial  = false;
        }

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    response = MainScreenActivity.currentNutzer.getChat(currentEmpfänger.getNutzername());

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

                messages = formJSONARRAYtoNormalArray(list);

                message_rv.setAdapter(new messageAdapter(messages, getApplicationContext()));
                message_rv.smoothScrollToPosition(messages.size()-1);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }, 2000);

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
}
