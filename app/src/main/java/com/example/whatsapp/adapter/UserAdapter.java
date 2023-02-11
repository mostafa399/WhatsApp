package com.example.whatsapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.MessageActivity;
import com.example.whatsapp.R;
import com.example.whatsapp.model.UserModel;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
private final Context context;
private final List<UserModel> userModels;
private final boolean isChat;

    public UserAdapter(Context context, List<UserModel> userModels,boolean isChat) {
        this.context = context;
        this.userModels = userModels;
        this.isChat=isChat;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserAdapter.UserViewHolder
                (LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        UserModel user =userModels.get(position);

        //username call
        holder.textView.setText(user.getUserName());
        //image call
        if (user.getImageUrl().equals("default")){
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(context).load(user.getImageUrl()).into(holder.imageView);
        }
        //status call
        if (isChat){
            if (user.getStatus().equals("onLine")){
            holder.statusOn.setVisibility(View.VISIBLE);
            holder.statusOff.setVisibility(View.GONE);

        }
            else {
                holder.statusOff.setVisibility(View.VISIBLE);
                holder.statusOn.setVisibility(View.GONE);
            }
        }else{
            holder.statusOff.setVisibility(View.GONE);
            holder.statusOn.setVisibility(View.GONE);}



        //send userid to next activity
        holder.itemView.setOnClickListener(v -> {
            Intent i=new Intent(context, MessageActivity.class);
            i.putExtra("userid",user.getId());
            context.startActivity(i);

        });


    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
       private final ImageView imageView;
        private final ImageView statusOn;
        private final ImageView statusOff;
        private final TextView textView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageViewitem);
            textView=itemView.findViewById(R.id.titleItem);
            statusOn=itemView.findViewById(R.id.imageViewStatuson);
            statusOff=itemView.findViewById(R.id.imageViewStatusoff);
        }
    }
}
