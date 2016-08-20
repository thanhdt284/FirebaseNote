package com.stevedao.note.model;

import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by thanh.dao on 8/4/2016.
 *
 */
public class FirebaseUtil {
    private static FirebaseDatabase mDatabase;

    //// Database ////
    public static FirebaseDatabase getDatabaseInstance() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
//            mDatabase.setPersistenceEnabled(true);
//            enableSync();
        }

        return mDatabase;
    }

    public static DatabaseReference getBaseRef() {
        return getDatabaseInstance().getReference();
    }

    @Nullable
    public static DatabaseReference getNoteRef() {
        String userId = getCurrentUserId();
        if (userId != null) {
            return getBaseRef().child(DatabaseSpec.NoteDB.TABLE_NAME).child(getCurrentUserId());
        } else {
            return null;
        }
    }

    @Nullable
    public static DatabaseReference getItemRef() {
        String userId = getCurrentUserId();
        if (userId != null) {
            return getBaseRef().child(DatabaseSpec.ItemDB.TABLE_NAME).child(getCurrentUserId());
        } else {
            return null;
        }
    }

    @Nullable
    public static DatabaseReference getUserRef() {
        String userId = getCurrentUserId();
        if (userId != null) {
            return getBaseRef().child(User.ROOT).child(getCurrentUserId());
        } else {
            return null;
        }
    }
    ////////////////////

    //// Authentication ////
    public static FirebaseAuth getAuthInstance() {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseUser getCurrentUser() {
        return getAuthInstance().getCurrentUser();
    }

    public static String getCurrentUserId() {
        FirebaseUser mUser = getCurrentUser();
        if (mUser != null) {
            return mUser.getUid();
        }

        return null;
    }
    ////////////////////////

    //// Keep sync data /////
    @SuppressWarnings("unused")
    private static void enableSync() {
        DatabaseReference userRef = getUserRef();

        if (userRef != null) {
            userRef.keepSynced(true);
        }

        DatabaseReference noteRef = getNoteRef();

        if (noteRef != null) {
            noteRef.keepSynced(true);
        }

        DatabaseReference itemRef = getItemRef();

        if (itemRef != null) {
            itemRef.keepSynced(true);
        }
    }
    ////////////////////////
}
