package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsapp.adapter.MessageAdapter;
import com.example.whatsapp.model.Chats;
import com.example.whatsapp.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MessageActivity extends AppCompatActivity {
    private TextView textView;
    private ImageView imageView;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    Intent intent;
    private RecyclerView recyclerView;
    private EditText editText;
    private MessageAdapter adapter;
    private List<Chats>mchat;
    private String userId;

    private ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        //widgit
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textView=findViewById(R.id.titleMessage);
        imageView=findViewById(R.id.imageViewMessage);

        recyclerView=findViewById(R.id.recyclerViewmessage);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        editText=findViewById(R.id.text_send);
        ImageButton sendBtn = findViewById(R.id.image_send);


        //Recieving contact id
        intent=getIntent();
        userId= intent.getStringExtra("userid");

            //Handle toolbar
        reference= FirebaseDatabase.getInstance()
                .getReference("MyUsers").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user=snapshot.getValue(UserModel.class);
                assert user != null;
                textView.setText(user.getUserName());

                if (user.getImageUrl().equals("default")){
                    imageView.setImageResource(R.mipmap.ic_launcher);

                }else {
                    Glide.with(MessageActivity.this).load(user.getImageUrl()).into(imageView);
                }

                readMsg(firebaseUser.getUid(),userId,user.getImageUrl());



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        sendBtn.setOnClickListener(v -> {
            if (editText.getText().toString().equals("")){
                Toast.makeText(this, "No Entered Message", Toast.LENGTH_SHORT).show();
            }else {
                sendMsg(firebaseUser.getUid(),userId,editText.getText().toString());

            }
            editText.setText("");

        });

        seenMessage(userId);

    }
    private void sendMsg(String sender, String receiver, String message) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference( );
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("isSeen","false");

        reference.child("Chats").push().setValue(hashMap);

        //adding user Who chat with them  to chat fragment
         final DatabaseReference myRef=FirebaseDatabase.getInstance()
                 .getReference("ChatList")
                .child(firebaseUser.getUid());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    myRef.child("id").setValue(userId);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // show Message in Recycler View
    private void readMsg(final String myid,final String userid,final String imageurl){
        mchat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mchat.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chats chat = dataSnapshot.getValue(Chats.class);
                    assert chat != null;
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid)
                            || chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mchat.add(chat);
                    }
                    adapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                    recyclerView.setAdapter(adapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    private void seenMessage(final String userId){
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chats chat = dataSnapshot.getValue(Chats.class);

                    assert chat != null;
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", true);

                        snapshot.getRef().updateChildren(hashMap);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void checkStatus(String status){
        reference=FirebaseDatabase.getInstance().getReference("MyUsers")
                .child(firebaseUser.getUid());
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("Status",status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkStatus("onLine");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        checkStatus("offLine");
    }



}
