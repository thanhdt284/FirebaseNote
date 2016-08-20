package com.stevedao.note.model.FirebaseDAO;

import java.util.ArrayList;
import java.util.Map;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.stevedao.note.control.Common;
import com.stevedao.note.model.DatabaseSpec;
import com.stevedao.note.model.EntityDAO;
import com.stevedao.note.model.FirebaseUtil;
import com.stevedao.note.model.Item;
import com.stevedao.note.view.ITaskResponse;

/**
 * Created by sev_user on 8/17/2016.
 *
 */
public class FirebaseItemDAOImpl implements EntityDAO<Item> {

    private static final String TAG = "FirebaseItemDAOImpl";
    DatabaseReference itemRef = FirebaseUtil.getItemRef();
    private ITaskResponse mInterface;

    public FirebaseItemDAOImpl() {
    }

    @Override
    public Object addEntity(Item item) {
        if (itemRef != null) {
            itemRef.child("" + item.getId()).updateChildren(item.toMap(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e(TAG, "onComplete: Add item error: " + databaseError.getMessage());
                    }
                }
            });
        }

        return 0;
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
            Log.w(TAG, "addEntities: size = " + entities.size() + " - added = " + count);
            Log.w(TAG, "addEntities: some items not added !!!");
        }

        return count;
    }

    @Override
    public Item getEntity(Object id) {
        final Item[] item = new Item[1];

        if (itemRef != null) {
            itemRef.child((String) id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    item[0] = dataSnapshot.getValue(Item.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled: get item error: " + databaseError.getMessage());
                }
            });
        }

        return item[0];
    }

    @Override
    public ArrayList<Item> getAllEntities(String column, final Object value) {
        final ArrayList<Item> itemList = new ArrayList<>();

        if (itemRef != null) {
            Query query = null;

            switch (column) {
            case DatabaseSpec.ItemDB.FIELD_NOTE_ID:
                query = itemRef.orderByChild(DatabaseSpec.ItemDB.FIELD_NOTE_ID).equalTo((Integer) value);
                break;
            default:
                break;
            }

            if (query != null) {
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            itemList.add(snapshot.getValue(Item.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled: getAllEntities (query items) error = " + databaseError.getMessage());
                    }
                });
            } else {
                itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            itemList.add(snapshot.getValue(Item.class));
                        }

                        if (value instanceof Integer) {
                            int tag = (int) value;

                            if (tag == 0) {
                                mInterface.onResponse(Common.LOGIN_ITEM_SYNC, itemList);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled: getAllEntities (all items) error = " + databaseError.getMessage());
                    }
                });
            }
        }

        //  Dummy code for later implementation
        //  itemList (size = 0)
        //  Need to return value after getting all data from Firebase Server
        return itemList;
    }

    @Override
    public void updateEntity(Item item) {
        if (itemRef != null) {
            Map<String, Object> itemMap = item.toMap();
            itemRef.child("" + item.getId()).updateChildren(itemMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e(TAG, "onComplete: update item error: " + databaseError.getMessage());
                    }
                }
            });
        }
    }

    @Override
    public void deleteEntity(Item item) {
        deleteEntityByKey((Integer) item.getId());
    }

    public void deleteItemsByNoteId(int noteId) {
        if (itemRef != null) {
            Query query = itemRef.orderByChild(DatabaseSpec.ItemDB.FIELD_NOTE_ID).equalTo(noteId);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        deleteEntityByKey(Integer.valueOf(snapshot.getKey()));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                }
            });
        }
    }

    private void deleteEntityByKey(int itemKey) {
        if (itemRef != null) {
            itemRef.child("" + itemKey).removeValue();
        }
    }

    public ArrayList<Item> getAllEntities() {
        return getAllEntities(null, 0);
    }

    public void setInterface(ITaskResponse anInterface) {
        this.mInterface = anInterface;
    }
}
