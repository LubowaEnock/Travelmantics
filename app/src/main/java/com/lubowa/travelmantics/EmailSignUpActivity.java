package com.lubowa.travelmantics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmailSignUpActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private AppCompatEditText email, password, fullName;
    private DatabaseReference mRef;
    private User user;
    private RadioGroup signup_level;
    private String userType = "Customer";
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sign_up);
        email = (AppCompatEditText) findViewById(R.id.signup_email_address);
        password = (AppCompatEditText) findViewById(R.id.signup_password);
        fullName = (AppCompatEditText) findViewById(R.id.full_name);
        signup_level = (RadioGroup) findViewById(R.id.signup_level);
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

    public void signUp(View v){
        if(email.getText() == null || password.getText() == null || fullName.getText() == null){
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }
        else{
            dialog.setMessage("signing in...");
            dialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString()).addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                user = new User(email.getText().toString().trim(),fullName.getText().toString());
                                mRef = FirebaseDatabase.getInstance().getReference().child("Users");
                                if(userType.equals("Customer")){
                                    mRef.child("Customers").push().setValue(user);
                                }
                                else {
                                    mRef.child("Admin").push().setValue(user);
                                }
                                startActivity(new Intent(EmailSignUpActivity.this, HomeActivity.class));
                                finish();
                            }
                            else{
                                Toast.makeText(EmailSignUpActivity.this, "Sign up error: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );
        }

    }

    public void onSignUpRadioButton(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.admin_signup:
                if (checked)
                    userType = "Admin";
                break;
            case R.id.customer_signup:
                if (checked)
                    userType = "Customer";
                break;
        }
    }
}
