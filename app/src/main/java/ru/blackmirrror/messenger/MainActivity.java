package ru.blackmirrror.messenger;

import static ru.blackmirrror.messenger.utils.FirebaseHelperUser.*;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import ru.blackmirrror.messenger.databinding.ActivityMain2Binding;
import ru.blackmirrror.messenger.ui.dialog.FillProfileDialog;

public class MainActivity extends AppCompatActivity {

    private ActivityMain2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_chats, R.id.navigation_calls, R.id.navigation_account)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main2);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        checkFillingProfileUser();
    }

    private void checkFillingProfileUser() {
        initFirebase();
        REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_PHONE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists())
                    showFillProfileWindow();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showFillProfileWindow() {
        FillProfileDialog fillProfileDialog = new FillProfileDialog();
        fillProfileDialog.show(getSupportFragmentManager(), "ProfileDialogFragment");
    }

    @Override
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
                        path.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task2) {
                                if (task2.isSuccessful()) {
                                    String photoUrl = task2.getResult().toString();
                                    REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_PHOTO_URL)
                                            .setValue(photoUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task3) {
                                            if (task3.isSuccessful()) {
                                                //ImageView imvPhoto = findViewById(R.id.imvPhoto);
                                                Picasso.get()
                                                        .load(photoUrl)
                                                        .placeholder(R.drawable.img_logo)
                                                        .into((ImageView) findViewById(R.id.imvPhoto));
                                                //USER.setPhotoUrl(photoUrl);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }
    }

}