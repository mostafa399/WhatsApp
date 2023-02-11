package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class LogInActivity extends AppCompatActivity {
    Animation animationBtn ,animationtxt;
    TextView textView;
    private EditText loginEmail,loginPassword;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        //Auth
        firebaseAuth=FirebaseAuth.getInstance();

        //Widget
        loginEmail=findViewById(R.id.emailLogin);
        loginPassword=findViewById(R.id.passwordLogin);
        Button btnLogin = findViewById(R.id.loginBtn);
        Button btnRegister = findViewById(R.id.RegisterBtn);
        textView=findViewById(R.id.LoginTv);

        //Animation
        animationBtn= AnimationUtils.loadAnimation(this,R.anim.animate_btn);
        animationtxt= AnimationUtils.loadAnimation(this,R.anim.animate_txt);
        btnLogin.setAnimation(animationBtn);
        btnRegister.setAnimation(animationBtn);
        textView.setAnimation(animationtxt);





        //onclick Login
        btnLogin.setOnClickListener(v -> {
            String email=loginEmail.getText().toString().trim();
            String password=loginPassword.getText().toString().trim();
            if (TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
                Toast.makeText(this, "Email Or Password isn't Entered Yet", Toast.LENGTH_SHORT).show();
            }
            else {
               signin(email,password);
            }
        });

        //if the Email is not found on database
        btnRegister.setOnClickListener(v -> {
            Intent i=new Intent(LogInActivity.this,RegisterActivity.class);
            startActivity(i);
        });

    }
    private void signin(String email,String password){
        //using Method signInWithEmailAndPassword With BtnSignIn
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Intent i=new Intent(LogInActivity.this,MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
            else {
                Toast.makeText(this, "LoginFailed", Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Save Current User
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser !=null){
            Intent i=new Intent(LogInActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }
    }

}