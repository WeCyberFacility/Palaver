package de.cyberfacility.alpaykucuk.palaver;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {

    ArrayList<Nutzer> data;
    Context context;


    public ChatAdapter(ArrayList<Nutzer> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.chatlist,parent , false);
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, final int position) {

        holder.contactname.setText(data.get(position).getNutzername());

        holder.contactlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatActivity.currentEmpfänger = data.get(position);
                chatActivity currentchChatActivity = new chatActivity();
                Intent myIntent = new Intent(context, currentchChatActivity.getClass());
                context.startActivity(myIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ChatHolder extends RecyclerView.ViewHolder {

        TextView contactname;
        ImageView bild;
        ConstraintLayout contactlayout;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            contactname = itemView.findViewById(R.id.contactname);
            bild = itemView.findViewById(R.id.profilbild);
            contactlayout = itemView.findViewById(R.id.contactlayout);
        }
    }

}
