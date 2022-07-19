package ru.blackmirrror.messenger;

import static ru.blackmirrror.messenger.VerificationActivity.TAG_PUT_EXTRA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {

    private Button btnEnter;
    private EditText etPhoneNumber;
    private ProgressBar pbGenerateCode;
    private TextView tvHelper;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        configFirebase();
        setUpUi();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null) {
            sendUserHome();
        }
    }

    private void configFirebase() {
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                tvHelper.setText(R.string.hint_try_again);
                tvHelper.setTextColor(Color.RED);
                pbGenerateCode.setVisibility(View.INVISIBLE);
                btnEnter.setEnabled(true);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                Intent intent = new Intent(AuthActivity.this, VerificationActivity.class);
                intent.putExtra(TAG_PUT_EXTRA, s);
                startActivity(intent);
            }
        };
    }

    private void setUpUi() {
        btnEnter = findViewById(R.id.btnGenerate);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        pbGenerateCode = findViewById(R.id.pbGenerate);
        tvHelper = findViewById(R.id.tvHelper);

        btnEnter.setOnClickListener(v -> {
            String phoneNumber = etPhoneNumber.getText().toString();

            if (phoneNumber.isEmpty()) {
                tvHelper.setText(R.string.hint_enter_number);
                tvHelper.setTextColor(Color.RED);
            }
            else {
                pbGenerateCode.setVisibility(View.VISIBLE);
                btnEnter.setEnabled(false);
                etPhoneNumber.setEnabled(false);

                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(auth)
                                .setPhoneNumber(phoneNumber)
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setActivity(AuthActivity.this)
                                .setCallbacks(callbacks)
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(AuthActivity.this, task -> {
                    if (task.isSuccessful()) {
                        sendUserHome();
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        }
                    }
                    pbGenerateCode.setVisibility(View.INVISIBLE);
                    btnEnter.setEnabled(true);
                });
    }

    private void sendUserHome() {
        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}