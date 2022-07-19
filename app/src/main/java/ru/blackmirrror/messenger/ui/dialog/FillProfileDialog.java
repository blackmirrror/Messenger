package ru.blackmirrror.messenger.ui.dialog;

import static ru.blackmirrror.messenger.utils.FirebaseHelperUser.*;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ru.blackmirrror.messenger.R;
import ru.blackmirrror.messenger.models.User;

public class FillProfileDialog extends DialogFragment {

    private View profileWindow;

    private EditText firstName;
    private EditText lastName;
    private EditText link;
    private EditText status;
    private TextView hint;
    Button positiveButton;

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setEnabled(false);
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        profileWindow = inflater.inflate(R.layout.fill_profile_window, null);
        
        builder.setView(profileWindow);
        builder.setTitle(R.string.fill_profile)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setUserData();
                    }
                })
                .setNegativeButton(R.string.remind_me_later, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        setUpUi();
        return builder.create();
    }

    private void setUpUi() {
        firstName = profileWindow.findViewById(R.id.eFirstName);
        lastName = profileWindow.findViewById(R.id.eLastName);
        link = profileWindow.findViewById(R.id.eLink);
        status = profileWindow.findViewById(R.id.eStatus);
        hint = profileWindow.findViewById(R.id.tvHint);

        getUserData();

        TextWatcher watcher = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) {
                    String s1 = firstName.getText().toString().trim();
                    String s2 = lastName.getText().toString().trim();
                    String s3 = status.getText().toString().trim();
                    String s4 = link.getText().toString().trim();

                    if (s1.isEmpty() || s2.isEmpty() || s3.isEmpty() || s4.isEmpty()) {
                        hint.setText("Fill in all the fields");
                        positiveButton.setEnabled(false);
                    } else {
                        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(s4)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            if (!snapshot.getValue().toString().equals(UID)) {
                                                hint.setText("Such a link already exists");
                                                positiveButton.setEnabled(false);
                                            }
                                        } else {
                                            hint.setText("");
                                            positiveButton.setEnabled(true);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                    }
                }
        };

        firstName.addTextChangedListener(watcher);
        lastName.addTextChangedListener(watcher);
        link.addTextChangedListener(watcher);
        status.addTextChangedListener(watcher);
    }

    private void setUserData() {
        initFirebase();
        User user = new User();
        user.setFirstName(firstName.getText().toString());
        user.setLastName(lastName.getText().toString());
        user.setLink(link.getText().toString());
        user.setStatus(status.getText().toString());
        user.setPhoneNumber(CURRENT_USER.getPhoneNumber());

        REF_DATABASE_ROOT.child(NODE_USERS).child(UID).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                });
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(link.getText().toString()).setValue(UID);
    }

    private void getUserData() {
        REF_DATABASE_ROOT.child(NODE_USERS).child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                firstName.setText(user.getFirstName());
                lastName.setText(user.getLastName());
                status.setText(user.getStatus());
                link.setText(user.getLink());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
