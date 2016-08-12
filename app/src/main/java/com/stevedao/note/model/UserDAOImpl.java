package com.stevedao.note.model;

import java.util.ArrayList;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by sev_user on 8/8/2016.
 *
 */
public class UserDAOImpl implements EntityDAO<User>{
    private static final String TAG = "UserDAOImpl";
    private Context mContext;

    public UserDAOImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public String addEntity(User user) {
        DatabaseReference userRef = FirebaseUtil.getUserRef();
        FirebaseUser mCurrentUser = FirebaseUtil.getCurrentUser();

        if (userRef != null) {
            userRef.setValue(user, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e(TAG, "onComplete: addEntity Error " + databaseError.getMessage());
                        Toast.makeText(mContext, "Adding user failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        return FirebaseUtil.getCurrentUserId();
    }

    @Override
    public User getEntity(String key) { // id is useless when using firebase
        final User[] mUser = new User[1];
        DatabaseReference userRef = FirebaseUtil.getUserRef();
        if (userRef != null) {
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mUser[0] = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "onCancelled: get user Error " + databaseError.getMessage());
                }
            });
        }

        return mUser[0];
    }

    @Override
    public ArrayList<User> getAllEntities(String column, int value) {
        ArrayList<User> mList = new ArrayList<>();
        mList.add(getEntity(""));

        return mList;
    }

    @Override
    public void updateEntity(User user) {
        DatabaseReference userRef = FirebaseUtil.getUserRef();
        if (userRef != null) {
            userRef.setValue(user, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e(TAG, "onComplete: Update user error: " + databaseError.getMessage());
                    }
                }
            });
        }
    }

    @Override
    public void deleteEntity(User user) {
        DatabaseReference userRef = FirebaseUtil.getUserRef();
        if (userRef != null) {
            userRef.removeValue();
        }
    }

    public void addCurrentUser() {
        FirebaseUser mCurrentUser = FirebaseUtil.getCurrentUser();

        User user = new User(mCurrentUser.getEmail(), mCurrentUser.getDisplayName(),
                             mCurrentUser.getPhotoUrl() == null ? "" : mCurrentUser.getPhotoUrl().toString());
        addEntity(user);
    }
}
