package com.example.groupalarmsync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailCheckInput, passwordCheckInput;
    ProgressBar progressBar1;

    private FirebaseAuth mAuth;

    // Checking the requirements for the entered email and password validity
    private boolean checkRequirements(String  email,String  password){
        boolean flag = true;

        if(password.isEmpty()){
            passwordCheckInput.requestFocus();
            flag = false;
        }
        if(email.isEmpty()){
            emailCheckInput.requestFocus();
            flag = false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailCheckInput.setError("Hey this is not a valid email!!");
            emailCheckInput.requestFocus();
            flag = false;
        }
        return flag;
    }

    // Login the user using the given input credentials (email, password)
    private void userLogin(){
        String email = emailCheckInput.getText().toString().trim();
        String password = passwordCheckInput.getText().toString().trim();

        // calling function to check the validity of input details
        if(!checkRequirements(email, password))
            return;

        progressBar1.setVisibility(View.VISIBLE);

        // Since till now the validity is checked we proceed to login the user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar1.setVisibility(View.INVISIBLE);
                        if(task.isSuccessful()){
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        emailCheckInput = findViewById(R.id.emailCheckInput);
        passwordCheckInput = findViewById(R.id.passwordCheckInput);
        progressBar1 = findViewById(R.id.progressBar1);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.forgotPassBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PasswordResetActivity.class));
                finish();
            }
        });

        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        findViewById(R.id.signUpBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}