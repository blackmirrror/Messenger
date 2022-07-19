package ru.blackmirrror.messenger.ui.dialog;

import static ru.blackmirrror.messenger.utils.FirebaseHelperUser.*;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
import ru.blackmirrror.messenger.ui.account.AccountFragment;

public class FillProfileDialog extends DialogFragment {

    private View profileWindow;

    private EditText firstName;
    private EditText lastName;
    private EditText link;
    private EditText status;
    private TextView hint;

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
                        setUpUi();
                        setUserData();
                    }
                })
                .setNegativeButton(R.string.remind_me_later, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    private void setUpUi() {
        firstName = profileWindow.findViewById(R.id.eFirstName);
        lastName = profileWindow.findViewById(R.id.eLastName);
        link = profileWindow.findViewById(R.id.eLink);
        status = profileWindow.findViewById(R.id.eStatus);
        hint = profileWindow.findViewById(R.id.tvHint);
    }

    private void setUserData() {
        //getUserData();
        initFirebase();
        User user = new User();
        user.setFirstName(firstName.getText().toString());
        user.setLastName(lastName.getText().toString());
        user.setLink(link.getText().toString());
        if (isRight(status.getText().toString()))
            user.setStatus(status.getText().toString());
        else {
            hint.setText("such a link already exists");
            setUserData();
        }
        user.setPhoneNumber(CURRENT_USER.getPhoneNumber());
        REF_DATABASE_ROOT.child(NODE_USERS).child(UID).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                });
        /*REF_DATABASE_ROOT.child(NODE_USERNAMES).child(link.getText().toString()).setValue(UID)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                });*/
    }

    private boolean isRight(String linka) {
        return true;
    }

    private void getUserData() {
        if (AccountFragment.User != null) {
            firstName.setText(AccountFragment.User.getFirstName());
            lastName.setText(AccountFragment.User.getLastName());
            link.setText(AccountFragment.User.getLink());
            status.setText(AccountFragment.User.getStatus());
        }
    }
}
