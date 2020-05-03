package de.cyberfacility.alpaykucuk.palaver;

import android.content.Context;
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
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {

       /* Glide.with(context)
                .load(data.get(position).getPictureurl())
                .into(filialenHolder.projectlistbild);
                */

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ChatHolder extends RecyclerView.ViewHolder {

        TextView contactname;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            contactname = itemView.findViewById(R.id.contactname);
        }
    }

}
