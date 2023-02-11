package com.example.whatsapp.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.model.UserModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.HashMap;
import java.util.Objects;


public class ProfileFragment extends Fragment {
   private ImageView imageView;
   private TextView textView;
   //Cloud Storage
   private StorageReference storageReference;
    private Uri imageUri;
    private static final int IMGE_REQUEST=0;
   private FirebaseUser firebaseUser;
    private StorageTask uploadTAsk;
    //RealTime DataBase
    private DatabaseReference reference;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile, container, false);

        imageView=view.findViewById(R.id.imageViewProfile);
        textView=view.findViewById(R.id.userNameProfile);

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

         reference=FirebaseDatabase.getInstance().getReference("MyUsers")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user=snapshot.getValue(UserModel.class);
                textView.setText(user.getUserName());
                if (user.getImageUrl().equals("default")){
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(getContext()).load(user.getImageUrl())
                            .circleCrop().autoClone().into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Image
        storageReference= FirebaseStorage.getInstance().getReference("Uploads");
        imageView.setOnClickListener(v -> {
         selectImage();
        });

        return view;
    }

    private void selectImage() {
        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/");
        Intent pickIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/");
        Intent chooserIntent=Intent.createChooser(galleryIntent,"Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,new Intent[]{pickIntent});
        startActivityForResult(Intent.createChooser(chooserIntent, "Select Image from here..."), IMGE_REQUEST);

    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver= requireContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadMyImage(){
        final ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading");
        progressDialog.show();
        if(imageUri!=null){
            final StorageReference fileReference=storageReference
                    .child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTAsk=fileReference.putFile(imageUri);
            uploadTAsk.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                if (!task.isSuccessful()){
                    throw task.getException();

                }
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) (Task<Uri> task) -> {
                if (task.isSuccessful()){
                    Uri downloadUri=task.getResult();
                    String mUri=downloadUri.toString();
                    reference=FirebaseDatabase.getInstance()
                            .getReference("MyUsers").child(firebaseUser.getUid());
                    HashMap<String,Object>map=new HashMap<>();
                    map.put("ImageUrl",mUri);
                    reference.updateChildren(map);
                    progressDialog.dismiss();
                }
                else {
                    Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), ""+e.getMessage() , Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            });
        }
        else {
            Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMGE_REQUEST&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            imageUri=data.getData();
            if (uploadTAsk!=null&&uploadTAsk.isInProgress()){
                Toast.makeText(getContext(), "Upload In Progress ...", Toast.LENGTH_SHORT).show();

            }
            else {
                uploadMyImage();
            }

        }
    }
}