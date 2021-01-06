package com.example.groupalarmsync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class PasswordResetActivity extends AppCompatActivity {

    TextView errorStatus;
    EditText emailCheckEditText;

    FirebaseAuth mAuth;

    String email;

    // checking requirements for the email to be valid
    private boolean checkRequirements(String email) {
        boolean flag = true;

        if(email.isEmpty()){
            emailCheckEditText.requestFocus();
            flag = false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailCheckEditText.setError("Hey this is not a valid email!!");
            emailCheckEditText.requestFocus();
            flag = false;
        }
        return flag;
    }

    // checking if email is registered or not
    private void checkEmailRegistered() {
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if(task.getResult().getSignInMethods().isEmpty()){
                    if(!(errorStatus.getVisibility() == View.VISIBLE))
                        errorStatus.setVisibility(View.VISIBLE);
                }
                else{
                    if(errorStatus.getVisibility() == View.VISIBLE)
                        errorStatus.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    // function to send password reset email
    private void sendPasswordResetEmail() {
        email = emailCheckEditText.getText().toString().trim();

        if(!checkRequirements(email))
            return;

        checkEmailRegistered();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(PasswordResetActivity.this, "Password reset link has been sent to your email", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(PasswordResetActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        mAuth = FirebaseAuth.getInstance();

        errorStatus = findViewById(R.id.errorStatus);
        emailCheckEditText = findViewById(R.id.emailCheckEditText);

        findViewById(R.id.resetLinkBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordResetEmail();
            }
        });

        findViewById(R.id.loginFromPasswordReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }

}