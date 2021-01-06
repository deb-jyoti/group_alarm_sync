package com.example.groupalarmsync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

public class SignupActivity extends AppCompatActivity {

    EditText editTextPersonName, editTextEmailAddress, editTextNewPassword, editTextConfirmPassword;
    CheckBox checkBox;
    ProgressBar progressBar2;

    private FirebaseAuth mAuth;

    // Checking the requirements for the entered email and password validity for SignUp
    private boolean checkRequirements(String name,String  email,String  newPassword,String  confirmPassword){
        boolean flag = true;

        if(confirmPassword.isEmpty()){
            editTextConfirmPassword.setError("Please confirm password");
            editTextConfirmPassword.requestFocus();
            flag = false;
        }
        else if(newPassword.compareTo(confirmPassword) != 0){
            editTextConfirmPassword.setError("Please enter same password as above");
            editTextConfirmPassword.requestFocus();
            flag = false;
        }
        if(newPassword.isEmpty()){
            editTextNewPassword.setError("Password Required");
            editTextNewPassword.requestFocus();
            flag = false;
        }
        else if(newPassword.length()<6){
            editTextNewPassword.setError("Minimum length of password is 6");
            editTextNewPassword.requestFocus();
            flag = false;
        }
        if(email.isEmpty()){
            editTextEmailAddress.setError("Email required");
            editTextEmailAddress.requestFocus();
            flag = false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmailAddress.setError("Please enter a valid email");
            editTextEmailAddress.requestFocus();
            flag = false;
        }
        if(name.isEmpty()){
            editTextPersonName.setError("Username Required");
            editTextPersonName.requestFocus();
            flag = false;
        }
        return flag;
    }

    // SignUp the user using the given inputs (email, password)
    private void resisterUser(){

        String name = editTextPersonName.getText().toString().trim();
        String email = editTextEmailAddress.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // calling function to check the validity of input details
        if(!checkRequirements(name, email, newPassword, confirmPassword))
            return;

        // checking if the checkBox is checked
        if(!checkBox.isChecked()){
            checkBox.requestFocus();
            Toast.makeText(this, "Agree to the terms and conditions to proceed", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar2.setVisibility(View.VISIBLE);

        // Since till now the validity is checked we proceed to SignUp the user
        mAuth.createUserWithEmailAndPassword(email, newPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();

                            // passing the user input details to store in an object "userInfo" of class "UserInfo.java"
                            UserInfo userInfo = new UserInfo(name, "I'm too sleepy, wake me!!", email);

                            // passing the "userInfo" object to the FirebaseDatabase for storing using a reference corresponding to the userId
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(userInfo)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressBar2.setVisibility(View.INVISIBLE);
                                            if(task.isSuccessful()){
                                                Toast.makeText(SignupActivity.this, "User Info Updated Successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                                finish();
                                            }
                                            else{
                                                Toast.makeText(SignupActivity.this, "Sorry...User info update failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            // setting a default Profile Image "profile.png" inside the FirebaseStorage corresponding to the userId
                            FirebaseStorage.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("profilePic.jpg")
                                    .putFile(Uri.parse("android.resource://com.example.groupalarmsync/" + R.drawable.profile))
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Toast.makeText(SignupActivity.this, "Default profile image set", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignupActivity.this, "Sorry...Default profile image setting failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else{
                            if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                                progressBar2.setVisibility(View.INVISIBLE);
                                Toast.makeText(SignupActivity.this, "User already registered\n" +
                                        "Please try Logging in!!", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        editTextPersonName = findViewById(R.id.editTextPersonName);
        editTextEmailAddress = findViewById(R.id.editTextEmailAddress);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        checkBox = findViewById(R.id.checkBox);
        progressBar2 = findViewById(R.id.progressBar2);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resisterUser();
            }
        });

        findViewById(R.id.loginFromSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }
}