package com.stevedao.note.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.stevedao.note.control.Common;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by thanh.dao on 07/04/2016.
 *
 */
public class ItemDAOImpl implements EntityDAO<Item> {
    private static final String TAG = "ItemDaoImpl";
    private Context mContext;

    public ItemDAOImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public String addEntity(Item item) {
        DatabaseReference itemRef = FirebaseUtil.getItemRef();
        final String[] itemKey = {""};

        if (itemRef != null) {
            itemKey[0] = itemRef.push().getKey();

            itemRef.child(itemKey[0]).setValue(item, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        itemKey[0] = "";
                        Log.e(TAG, "onComplete: Add item error: " + databaseError.getMessage());
                    }
                }
            });
        }

        return itemKey[0];
    }

    public int addEntities(ArrayList<Item> entities) {
        int count = 0;

        for (Item entity : entities) {
            if (!addEntity(entity).equals("")) {
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
    public Item getEntity(String key) {
        final Item[] item = new Item[1];
        DatabaseReference itemRef = FirebaseUtil.getItemRef();

        if (itemRef != null) {
            itemRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
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
    public ArrayList<Item> getAllEntities(String column, Object value) {
        DatabaseReference itemRef = FirebaseUtil.getItemRef();
        final ArrayList<Item> itemList = new ArrayList<>();

        if (itemRef != null) {
            Query query = null;

            switch (column) {
                case Item.NOTE_ID:
                    query = itemRef.orderByChild(Item.NOTE_ID).equalTo((String) value);
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
                        Log.e(TAG, "onCancelled: getAllEntities (item) error = " + databaseError.getMessage());
                    }
                });
            }
        }
        return itemList;
    }

    @Override
    public void updateEntity(Item item) {

    }

    @Override
    public void deleteEntity(Item item) {
    }

    public int deleteItemsByNoteId(int noteId) {
    }

    public ArrayList<Item> getAllItemsByNoteId(int noteId) {
        return getAllEntities(DatabaseSpec.ItemDB.FIELD_NOTE_ID, noteId);
    }

    public String getFullContent(int noteId, int action) {
        ArrayList<Item> items = getAllItemsByNoteId(noteId);
        String content = "";
        for (Item item : items) {
            String itemContent = item.getContent();
            if (action == Common.GET_ITEM_CONTENT_ACTION_LIST) {
                if (!itemContent.equals(""))
                content += "\u2022" + itemContent + "  ";
            } else if (action == Common.GET_ITEM_CONTENT_ACTION_SEND) {
                content += "[" + (item.isChecked() ? "x" : "") + "]" + " " + itemContent + "\n";
            }
        }

        return content;
    }
}
