package com.example.groupalarmsync.menufragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groupalarmsync.LoginActivity;
import com.example.groupalarmsync.R;
import com.example.groupalarmsync.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    String NAME;
    String STATUS;
    String EMAIL;

    CircleImageView profilePic;
    EditText nameEditText, statusEditText;
    TextView emailTextView;
    ImageButton verifyBtn;
    ProgressBar progressBarProfile, progressBarImg;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userId;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    Uri imageUri;
    String profileImageUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_5_profile, container, false);

        verifyBtn = view.findViewById(R.id.verifyBtn);
        progressBarProfile = view.findViewById(R.id.progressBarProfile);
        progressBarImg = view.findViewById(R.id.progressBarImg);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users/" + userId);
        storageReference = FirebaseStorage.getInstance().getReference().child("Users/" + userId + "/profilePic.jpg");

        profilePic = view.findViewById(R.id.profilePic);
        nameEditText = view.findViewById(R.id.nameEditText);
        statusEditText = view.findViewById(R.id.statusEditText);
        emailTextView = view.findViewById(R.id.emailTextView);

        /* fetching profilePic.jpg from FirebaseStorage to a local file named profilePic.jpg and setting
        the CircularImageView to that image*/
        try {
            final File localFile = File.createTempFile("profilePic", "jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            profilePic.setImageBitmap(bitmap);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Failed to load image!!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        progressBarProfile.setVisibility(View.VISIBLE);

        /* getting the user info from FireBaseDatabase into object "userInfo" of class "UserInfo.java" and
        displaying them in the screen*/
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserInfo userInfo = snapshot.getValue(UserInfo.class);

                if(userInfo != null){
                    NAME = userInfo.getName();
                    STATUS = userInfo.getStatus();
                    EMAIL = userInfo.getEmail();

                    nameEditText.setText(NAME);
                    statusEditText.setText(STATUS);
                    emailTextView.setText(EMAIL);
                }
                progressBarProfile.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBarProfile.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.choosePicBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        view.findViewById(R.id.saveNameBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNameChanged()){
                    Toast.makeText(getActivity(), "Name updated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.findViewById(R.id.saveStatusBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStatusChanged()){
                    Toast.makeText(getActivity(), "Status updated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // checking if current user is email verified
        if(user.isEmailVerified()){
            verifyBtn.setImageResource(R.drawable.ic_verified);
        }

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user.isEmailVerified()){
                    user.sendEmailVerification();
                    Toast.makeText(getActivity(), "Verification email sent\n" +
                            "Click on the link sent to your email to verify your email\n", Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(), "Login again to see verified email", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), "Your email is already verified!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.findViewById(R.id.deleteAccountBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("Are you sure?");

                dialog.setMessage("Deleting account completely removes " +
                        "it from the system and cannot be retrieved back later.");

                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressBarProfile.setVisibility(View.VISIBLE);

                        // removing data from the FireBaseDataBase for the corresponding user
                        databaseReference.removeValue();
                        Toast.makeText(getActivity(), "Details deleted.", Toast.LENGTH_SHORT).show();

                        // removing profilePic from the FireBaseStorage for the corresponding user
                        storageReference.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "Profile picture deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        // deleting user account
                        user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressBarProfile.setVisibility(View.GONE);
                                if(task.isSuccessful()){
                                    Toast.makeText(getActivity(), "Account deleted.", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });

        return view;
    }// onCreateView ends

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            uploadProfilePic();
        }
        else{
            Toast.makeText(getActivity(), "No image chosen!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showImageChooser() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(getContext(), this);
    }

    private void uploadProfilePic() {
        if(imageUri != null)
        {
            progressBarImg.setVisibility(View.VISIBLE);
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getActivity(), "Profile image updated", Toast.LENGTH_SHORT).show();
                            progressBarImg.setVisibility(View.GONE);
                            profilePic.setImageURI(imageUri);
                            profileImageUrl = taskSnapshot.getStorage().getDownloadUrl().toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBarImg.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean isNameChanged() {
        if(nameEditText.getText().toString().trim().compareTo(NAME) != 0){
            NAME = nameEditText.getText().toString().trim();
            databaseReference.child("name").setValue(NAME);
            return true;
        }
        return false;
    }

    private boolean isStatusChanged() {
        if(statusEditText.getText().toString().trim().compareTo(STATUS) != 0) {
            STATUS = statusEditText.getText().toString().trim();
            databaseReference.child("status").setValue(STATUS);
            return true;
        }
        return false;
    }

}