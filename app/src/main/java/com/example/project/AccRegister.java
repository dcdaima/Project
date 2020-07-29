package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccRegister extends AppCompatActivity {


    private EditText mPassWord;
    private EditText mEmail;
    private EditText mUsername;
    private EditText mPasswordCheck;
    private FirebaseAuth mAuth;
    private Button registerButton;
    private Button logInButton;
    private static final String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseRef = database.getReference("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_register);

        registerButton = (Button)findViewById(R.id.registerButton);
        mPassWord = (EditText) findViewById(R.id.PasswordRegister);
        mEmail = (EditText) findViewById(R.id.EmailRegister);
        mUsername= (EditText) findViewById(R.id.usernameText);
        mPasswordCheck= (EditText) findViewById(R.id.passwordCheck);
        mAuth = FirebaseAuth.getInstance();

        logInButton = (Button)findViewById(R.id.backToLogIn);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AccRegister.this,loginActivity.class);
                startActivity(intent);
                finish();

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final String mEmailInput = mEmail.getText().toString().trim();
                final String mPasswordInput = mPassWord.getText().toString().trim();
                final String mUsernameInput = mUsername.getText().toString().trim();
                final String mPasswordC = mPasswordCheck.getText().toString().trim();

                Pattern pattern = Pattern.compile(emailRegex);
                Matcher matcher = pattern.matcher((mEmailInput));
                Pattern passwordPattern = Pattern.compile(passwordRegex);
                Matcher passwordMatcher = passwordPattern.matcher((mPasswordInput));
                Matcher passwordMatcher2 = passwordPattern.matcher((mPasswordC));

                if (mUsernameInput.equals("")){
                    mUsername.setError("Please insert username!");
                }
                if (!matcher.matches()){
                    mEmail.setError("Please use a valid email");
                    return;
                }
                if (!passwordMatcher.matches()){
                    mPassWord.setError("Please use a valid password of at least 8 characters long. It must contain at least one upper case, one lower case and one number and have no white spaces");
                    return;
                }
                if (!passwordMatcher2.matches()){
                    mPasswordCheck.setError("Please use a valid password of at least 8 characters long. It must contain at least one upper case, one lower case and one number and have no white spaces");
                    return;
                }
                if (!mPasswordInput.equals(mPasswordC)){
                    mPasswordCheck.setError("The passwords do not match!");
                    return;
                }
                //String tempUser= "";
                //databaseRef.child(tempUser);
                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mAuth.createUserWithEmailAndPassword(mEmailInput, mPasswordInput).addOnCompleteListener(AccRegister.this ,new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String mRole="To be set";
                                    Users user = new Users(mUsernameInput,mEmailInput,mRole);
                                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    databaseRef.child(uid).setValue(user);

                                    Toast.makeText(AccRegister.this, "Register worked!.",Toast.LENGTH_SHORT).show();

                                } else {

                                    Toast.makeText(AccRegister.this, "Register failed.",Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    throw error.toException();
                    }
                });

            }
        });
    }
}