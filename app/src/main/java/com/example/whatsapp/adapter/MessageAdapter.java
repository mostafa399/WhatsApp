package com.example.whatsapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.model.Chats;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private final Context context;
    private final List<Chats> mChats;
    private final String imageUrl;
    FirebaseUser firebaseUser;
    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_Right=1;

    public MessageAdapter(Context context, List<Chats> mChats,String imageUrl) {
        this.context = context;
        this.mChats = mChats;
        this.imageUrl=imageUrl;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MSG_TYPE_Right){
          View view=LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
        return new MessageAdapter.MessageViewHolder(view);
    }else {
            View view=LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return new MessageAdapter.MessageViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {

        Chats chats =mChats.get(position);
        holder.show_msg.setText(chats.getMessage());

        if (imageUrl.equals("default")){
            holder.profile_img.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(context).load(imageUrl).into(holder.profile_img);
        }
        if (position == mChats.size() -1){
            if (chats.getIsSeen().equals("true")){
                holder.seen.setText("Seen");
            }else{
                holder.seen.setText("Delivered");
            }
        }
        else
        {
            holder.seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView profile_img;
       private final TextView show_msg;
        private final TextView seen;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            show_msg=itemView.findViewById(R.id.showMessage);
            profile_img=itemView.findViewById(R.id.profileMessage);
            seen=itemView.findViewById(R.id.textSeen);
        }
    }

    @Override
    public int getItemViewType(int position) {
       firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChats.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_Right;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}
