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

        Glide.with(context)
                .load("https://scontent-cdt1-1.cdninstagram.com/v/t51.2885-15/e35/17077671_1237883376249541_7111054876699787264_n.jpg?_nc_ht=scontent-cdt1-1.cdninstagram.com&_nc_cat=101&_nc_ohc=hYQIffDsixQAX9Hi6UD&oh=91cd583835d562bf62069b1dfc318dd2&oe=5ED93C32")
                .circleCrop()
                .into(holder.bild);


        holder.contactname.setText(data.get(position).getNutzername());

        holder.contactlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: wechsel zu ChatDetails
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
