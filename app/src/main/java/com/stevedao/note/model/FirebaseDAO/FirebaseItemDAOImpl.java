package com.stevedao.note.model.FirebaseDAO;

import java.util.ArrayList;
import java.util.Map;
import android.content.Context;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.stevedao.note.control.Common;
import com.stevedao.note.model.Note;
import com.stevedao.note.model.SQLiteDAO.DatabaseSpec;
import com.stevedao.note.model.EntityDAO;
import com.stevedao.note.model.Item;
import com.stevedao.note.model.SQLiteDAO.SQLiteItemDAOImpl;
import com.stevedao.note.view.ITaskResponse;

/**
 * Created by sev_user on 8/17/2016.
 *
 */
public class FirebaseItemDAOImpl implements EntityDAO<Item> {
    private ITaskResponse mInterface;

    public FirebaseItemDAOImpl() {
    }

    @Override
    public Object addEntity(final Item item) {
        DatabaseReference itemRef = FirebaseUtil.getItemRef();
        String itemKey = item.getFirebaseId();
        if (FirebaseUtil.getCurrentUser() != null) {
            if (itemRef != null) {
                if (itemKey.equals("")) {
                    itemKey = itemRef.push().getKey();
                    item.setFirebaseId(itemKey);
                }
                itemRef.child(itemKey).updateChildren(item.toMap(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.e(Common.APPTAG, "FirebaseItemDAOImpl - onComplete: Add item error: " + databaseError
                                    .getMessage());
                        }
                    }
                });
            }
        }

        return itemKey;
    }

    @Override
    public int addEntities(ArrayList<Item> entities) {
        int count = 0;

        for (Item entity : entities) {
            if (addEntity(entity) == 0) {
                count++;
            }
        }

        if (count != entities.size()){
            Log.w(Common.APPTAG, "FirebaseItemDAOImpl - addEntities: size = " + entities.size() + " - added = " + count);
            Log.w(Common.APPTAG, "FirebaseItemDAOImpl - addEntities: some items not added !!!");
        }

        return count;
    }

    @Override
    public Item getEntity(Object id) {
        DatabaseReference itemRef = FirebaseUtil.getItemRef();
        final Item[] item = new Item[1];
        if (FirebaseUtil.getCurrentUser() != null) {
            if (itemRef != null) {
                itemRef.child((String) id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        item[0] = getItemFromSnapshot(snapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(Common.APPTAG, "FirebaseItemDAOImpl - onCancelled: get item error: " + databaseError.getMessage());
                    }
                });
            }
        }

        return item[0];
    }

    @Override
    public ArrayList<Item> getAllEntities(String column, final Object value) {
        DatabaseReference itemRef = FirebaseUtil.getItemRef();
        final ArrayList<Item> itemList = new ArrayList<>();

        if (FirebaseUtil.getCurrentUser() != null) {
            if (itemRef != null) {
                Query query = null;

                if (column != null) {
                    switch (column) {
                    case DatabaseSpec.ItemDB.FIELD_NOTE_ID:
                        query = itemRef.orderByChild(DatabaseSpec.ItemDB.FIELD_NOTE_ID).equalTo((Integer) value);
                        break;
                    default:
                        break;
                    }
                }

                if (query != null) {
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                itemList.add(getItemFromSnapshot(snapshot));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(Common.APPTAG, "FirebaseItemDAOImpl - onCancelled: getAllEntities (query items) error = " + databaseError.getMessage());
                        }
                    });
                } else {
                    itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                itemList.add(getItemFromSnapshot(snapshot));
                            }

                            Log.e(Common.APPTAG, "FirebaseItemDAOImpl - onDataChange: ");
                            if (value instanceof Integer) {
                                int tag = (int) value;

                                if (tag == 0) {
                                    Log.e(Common.APPTAG, "FirebaseItemDAOImpl - onDataChange: onResponse");
                                    mInterface.onResponse(Common.LOGIN_ITEM_SYNC, itemList);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(Common.APPTAG, "FirebaseItemDAOImpl - onCancelled: getAllEntities (all items) error = " + databaseError.getMessage());
                        }
                    });
                }
            }
        }

        //  Dummy code for later implementation
        //  itemList (size = 0)
        //  Need to return value after getting all data from Firebase Server
        return itemList;
    }

    @Override
    public void updateEntity(Item item) {
        DatabaseReference itemRef = FirebaseUtil.getItemRef();

        if (itemRef != null) {
            Map<String, Object> itemMap = item.toMap();
            itemRef.child("" + item.getFirebaseId()).updateChildren(itemMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e(Common.APPTAG, "FirebaseItemDAOImpl - onComplete: update item error: " + databaseError.getMessage());
                    }
                }
            });
        }
    }

    @Override
    public void deleteEntity(Item item) {
        deleteEntityByKey(item.getFirebaseId());
    }

    public void deleteItemsByNote(Note note) {
        DatabaseReference itemRef = FirebaseUtil.getItemRef();
        if (itemRef != null) {
            Query query;
            if (note.getFirebaseId().equals("")) {
                query = itemRef.orderByChild(DatabaseSpec.ItemDB.FIELD_NOTE_ID).equalTo(note.getId());
            } else {
                query = itemRef.orderByChild(DatabaseSpec.ItemDB.FIELD_FIREBASE_NOTE_KEY).equalTo(note.getFirebaseId());
            }

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        deleteEntityByKey(snapshot.getKey());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(Common.APPTAG, "FirebaseItemDAOImpl - onCancelled: " + databaseError.getMessage());
                }
            });
        }
    }

    private void deleteEntityByKey(String itemKey) {
        if (FirebaseUtil.getCurrentUser() != null) {
            DatabaseReference itemRef = FirebaseUtil.getItemRef();
            if (itemRef != null) {
                itemRef.child(itemKey).removeValue();
            }
        }
    }

    public ArrayList<Item> getAllEntities() {
        return getAllEntities(null, 0);
    }

    public void setInterface(ITaskResponse anInterface) {
        this.mInterface = anInterface;
    }

    public void updateEntitiesByNote(Note note, final ArrayList<Item> itemData) {
        DatabaseReference itemRef = FirebaseUtil.getItemRef();
        if (itemRef != null) {
            Query query;
            if (note.getFirebaseId().equals("")) {
                query = itemRef.orderByChild(DatabaseSpec.ItemDB.FIELD_NOTE_ID).equalTo(note.getId());
            } else {
                query = itemRef.orderByChild(DatabaseSpec.ItemDB.FIELD_FIREBASE_NOTE_KEY).equalTo(note.getFirebaseId());
            }

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        deleteEntityByKey(snapshot.getKey());

                        addEntities(itemData);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(Common.APPTAG, "FirebaseItemDAOImpl - onCancelled: " + databaseError.getMessage());
                }
            });
        }
    }

    private Item getItemFromSnapshot(DataSnapshot snapshot) {
        int id = ((Long) snapshot.child(DatabaseSpec.ItemDB.FIELD_PKEY).getValue()).intValue();
        String firebaseId = snapshot.getKey();
        int noteId = ((Long) snapshot.child(DatabaseSpec.ItemDB.FIELD_NOTE_ID).getValue()).intValue();
        String content = (String) snapshot.child(DatabaseSpec.ItemDB.FIELD_CONTENT).getValue();
        String firebaseNoteId = (String) snapshot.child(DatabaseSpec.ItemDB.FIELD_FIREBASE_NOTE_KEY).getValue();
        boolean checked = (boolean) snapshot.child(DatabaseSpec.ItemDB.FIELD_CHECKED).getValue();
        int index = ((Long) snapshot.child(DatabaseSpec.ItemDB.FIELD_INDEX).getValue()).intValue();

        return new Item(id, firebaseId, firebaseNoteId, noteId, content, checked, index);
    }
}
