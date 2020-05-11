package de.cyberfacility.alpaykucuk.palaver;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.DrawableWrapper;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.MessageHolder> {

    ArrayList<Message> data;
    Context context;


    public messageAdapter(ArrayList<Message> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.messagelist,parent , false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, final int position) {

        holder.setIsRecyclable(false);

        if(data.get(position).getSender().equals(MainScreenActivity.currentNutzer.getNutzername())) {
            holder.messagelayout.setBackgroundResource(R.drawable.senderlay);
            holder.messagetxt.setTextColor(Color.WHITE);
            holder.gpslogo.setBackgroundResource(R.drawable.gpslight);

        } else {
            holder.messagelayout.setBackgroundResource(R.drawable.empfaengerlay);
            holder.messagetxt.setTextColor(Color.BLACK);
            holder.gpslogo.setBackgroundResource(R.drawable.gpsbtn);

        }

        if (data.get(position) instanceof GPSMessage) {
            holder.gpslogo.setVisibility(View.VISIBLE);
            holder.messagetxt.setText(data.get(position).getSender() + "s " + "jetziger Standort");
        } else {
            holder.gpslogo.setVisibility(View.INVISIBLE);
            holder.messagetxt.setText(data.get(position).getData());
        }


        holder.messagetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (data.get(position) instanceof GPSMessage) {
                    GPSMessage currGPSMess = (GPSMessage) data.get(position);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/maps/place/" + currGPSMess.breitengrad + "," + currGPSMess.längengrad));
                    context.startActivity(browserIntent);
                } else {
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void updateList(ArrayList<Message> itemList){
        data.clear();
//        this.list = itemList;
//        list.addAll(itemList);
//        notifyItemInserted(list.size());
//        notifyDataSetChanged();
        this.data.addAll(itemList);
//        notifyItemInserted(list.size());
        notifyDataSetChanged();
    }


    public class MessageHolder extends RecyclerView.ViewHolder {

        ConstraintLayout messagelayout;
        TextView messagetxt;
        ImageView gpslogo;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            messagelayout = itemView.findViewById(R.id.messagelayout);
            messagetxt = itemView.findViewById(R.id.messagetxt);
            gpslogo = itemView.findViewById(R.id.gpslogo);
        }
    }

}
