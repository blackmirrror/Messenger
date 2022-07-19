package ru.blackmirrror.messenger.ui.account;

import static android.app.Activity.RESULT_OK;
import static ru.blackmirrror.messenger.utils.FirebaseHelperUser.*;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import ru.blackmirrror.messenger.AuthActivity;
import ru.blackmirrror.messenger.R;
import ru.blackmirrror.messenger.databinding.FragmentAccountBinding;
import ru.blackmirrror.messenger.models.User;
import ru.blackmirrror.messenger.ui.dialog.FillProfileDialog;

public class AccountFragment extends Fragment {

    public static User User;

    private AccountViewModel accountViewModel;
    private FragmentAccountBinding binding;
    private View root;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private TextView btnLogout;
    private TextView etFirstName;
    private TextView etLastName;
    private TextView etStatus;
    private TextView tvPhoneNumber;
    private TextView tvLink;
    private ImageView imvPhoto;
    private TextView tvEditProfile;
    private ImageView imvEditPhoto;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        accountViewModel =
                new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        final TextView textView = binding.textAccount;
        accountViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        setUpUi();
        initDb();

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        return root;
    }

    private void setUpUi() {
        etFirstName = root.findViewById(R.id.etFirstName);
        etLastName = root.findViewById(R.id.etLastName);
        etStatus = root.findViewById(R.id.etStatus);
        tvPhoneNumber = root.findViewById(R.id.tvPhoneNumber);
        tvLink = root.findViewById(R.id.tvLink);
        imvPhoto = root.findViewById(R.id.imvPhoto);

        imvEditPhoto = root.findViewById(R.id.imvEditPhoto);
        imvEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPhoto();
            }
        });

        tvEditProfile = root.findViewById(R.id.tvEditProfile);
        tvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FillProfileDialog fillProfileDialog = new FillProfileDialog();
                fillProfileDialog.show(getActivity().getSupportFragmentManager(), "ProfileDialogFragment");
            }
        });

        btnLogout = root.findViewById(R.id.tvLogout);
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            sendUserToLogin();
        });
    }

    private void setPhoto() {
        CropImage.activity()
                .setAspectRatio(1,1)
                .setRequestedSize(600,600)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(getActivity());
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK &&
        data != null) {
            Uri uri = CropImage.getActivityResult(data).getUri();
            StorageReference path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE).child(UID);
            path.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        //toast is successful
                    }
                }
            });
        }
    }*/

    private void initDb() {
        REF_DATABASE_ROOT.child(NODE_USERS).child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                etFirstName.setText(user.getFirstName());
                etLastName.setText(user.getLastName());
                etStatus.setText(user.getStatus());
                tvLink.setText(user.getLink());
                tvPhoneNumber.setText(user.getPhoneNumber());
                if (user.getPhotoUrl() != null) {
                    Picasso.get()
                            .load(user.getPhotoUrl())
                            .placeholder(R.drawable.img_logo)
                            .into(imvPhoto);
                }

                User = user;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void sendUserToLogin() {
        Intent intent = new Intent(getActivity(), AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}