package com.stevedao.note.model.FirebaseDAO;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.stevedao.note.model.EntityDAO;
import com.stevedao.note.model.FirebaseUtil;
import com.stevedao.note.model.User;

import java.util.ArrayList;

/**
 * Created by sev_user on 8/8/2016.
 *
 */
public class FirebaseUserDAOImpl implements EntityDAO<User> {
    private static final String TAG = "FirebaseUserDAOImpl";

    public FirebaseUserDAOImpl() {
    }

    @Override
    public Object addEntity(User user) {
        DatabaseReference userRef = FirebaseUtil.getUserRef();

        if (userRef != null) {
            userRef.updateChildren(user.toMap(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e(TAG, "onComplete: addEntity Error " + databaseError.getMessage());
                    }
                }
            });
        }

        return FirebaseUtil.getCurrentUserId();
    }

    @Override
    public int addEntities(ArrayList<User> entities) {
        for (User user : entities) {
            addEntity(user);
        }
        return 0;
    }

    @Override
    public User getEntity(Object key) { // id is useless when using firebase
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
    public ArrayList<User> getAllEntities(String column, Object value) {
        ArrayList<User> mList = new ArrayList<>();
        mList.add(getEntity(0));

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
}
