package ru.blackmirrror.messenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerificationActivity extends AppCompatActivity {

    public static final String TAG_PUT_EXTRA = "authData";

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private String authData;

    private EditText etVerify;
    private Button btnVerify;
    private ProgressBar pbVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        configFirebase();
        setUpUi();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null) {
            sendUserToHome();
        }
    }

    private void configFirebase() {
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        authData = getIntent().getStringExtra(TAG_PUT_EXTRA);
    }

    private void setUpUi() {
        etVerify = findViewById(R.id.etCode);
        btnVerify = findViewById(R.id.btnVerify);
        pbVerify = findViewById(R.id.pbVerify);

        btnVerify.setOnClickListener(v -> {
            String codeVerify = etVerify.getText().toString();

            if (codeVerify.isEmpty()) {

            }
            else {
                pbVerify.setVisibility(View.VISIBLE);
                btnVerify.setEnabled(false);
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(authData, codeVerify);
                signInWithPhoneAuthCredential(credential);
            }
        });
    }

    private void sendUserToHome() {
        Intent intent = new Intent(VerificationActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(VerificationActivity.this, task -> {
                    if (task.isSuccessful()) {
                        sendUserToHome();
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        }
                    }
                    pbVerify.setVisibility(View.INVISIBLE);
                    btnVerify.setEnabled(true);
                });
    }
}