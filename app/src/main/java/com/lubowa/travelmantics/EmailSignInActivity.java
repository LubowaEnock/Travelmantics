package com.lubowa.travelmantics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EmailSignInActivity extends AppCompatActivity {
    private AppCompatEditText email, password;
    private FirebaseAuth firebaseAuth;
    private String userType = "Customer";
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sign_in);
        email = (AppCompatEditText) findViewById(R.id.signin_email_address);
        password = (AppCompatEditText) findViewById(R.id.signin_password);
        firebaseAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this){
            @Override
            public void onBackPressed() {
                dialog.cancel();
                dialog.dismiss();
            }
        };
        dialog.setCancelable(false);

    }

    public void signIn(View v){

        if(email.getText() == null || password.getText() == null){
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }
        else{
            dialog.setMessage("signing in...");
            dialog.show();
            firebaseAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString()).addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(EmailSignInActivity.this, HomeActivity.class));
                                finish();
                            }
                            else{
                                Toast.makeText(EmailSignInActivity.this, "Sign in error: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );
        }

    }


}
