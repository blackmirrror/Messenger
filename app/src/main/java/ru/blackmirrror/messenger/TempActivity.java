package ru.blackmirrror.messenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TempActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            sendUserToLogin();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            sendUserToLogin();
        }
    }

    private void sendUserToLogin() {
        Intent intent = new Intent(TempActivity.this, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}