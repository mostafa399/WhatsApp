package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText userEt,emailEt,passwordEt;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
        //Realtime database
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

            //Widgets
        userEt=findViewById(R.id.username);
        emailEt=findViewById(R.id.email);
        passwordEt=findViewById(R.id.password);
        progressBar=findViewById(R.id.loading);
        progressBar.setVisibility(View.INVISIBLE);
        Button registerBtn = findViewById(R.id.login);

        //Auth
        firebaseAuth=FirebaseAuth.getInstance();
        //btn Click
        registerBtn.setOnClickListener(v -> {
         String username=userEt.getText().toString().trim();
         String email=emailEt.getText().toString().trim();
         String password=passwordEt.getText().toString().trim();


         if (TextUtils.isEmpty(username)||TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
             Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
         }
         else {
             registerNow(username,email,password);
         }
        });

    }
    public void registerNow(final String username,String email,String password ){
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        progressBar.setVisibility(View.VISIBLE);
                        firebaseUser=firebaseAuth.getCurrentUser();

                        //RealtimeData Creation
                        myRef= FirebaseDatabase.getInstance()
                                .getReference("MyUsers").child(firebaseUser.getUid());

                        //Creating HashMap
                        HashMap<String,String>hashMap=new HashMap<>();
                        hashMap.put("id",firebaseUser.getUid());
                        hashMap.put("userName",username);
                        hashMap.put("ImageUrl","default");
                        hashMap.put("Status","offLine");
                        //Registeration
                        myRef.setValue(hashMap).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){

                                Intent i=new Intent(RegisterActivity.this,MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            }

                        });




                    }else {
                        Toast.makeText(this, "Invalied Email Or Password", Toast.LENGTH_SHORT).show();
                    }
                });


    }

}