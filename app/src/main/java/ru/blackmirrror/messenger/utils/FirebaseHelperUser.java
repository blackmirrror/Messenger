package ru.blackmirrror.messenger.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ru.blackmirrror.messenger.models.User;

public class FirebaseHelperUser {

    public static FirebaseAuth auth;
    public static DatabaseReference REF_DATABASE_ROOT;
    public static FirebaseUser CURRENT_USER;
    public static User USER;
    public static String UID;

    public static final String NODE_USERS = "users";
    public static final String CHILD_ID = "id";
    public static final String CHILD_PHONE = "phone";
    public static final String CHILD_FIRSTNAME = "firstname";
    public static final String CHILD_LASTNAME = "lastname";
    public static final String CHILD_LINK = "link";
    public static final String CHILD_STATUS = "status";

    public static void initFirebase() {
        auth = FirebaseAuth.getInstance();
        CURRENT_USER = auth.getCurrentUser();
        REF_DATABASE_ROOT = FirebaseDatabase.getInstance().getReference();
        UID = auth.getCurrentUser().getUid();
    }
}
