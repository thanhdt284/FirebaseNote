package com.stevedao.note.model;

import java.util.ArrayList;
import android.content.Context;
import com.stevedao.note.control.Common;
import com.stevedao.note.model.FirebaseDAO.FirebaseItemDAOImpl;
import com.stevedao.note.model.SQLiteDAO.DatabaseSpec;
import com.stevedao.note.model.SQLiteDAO.SQLiteItemDAOImpl;
import com.stevedao.note.view.ITaskResponse;

/**
 * Created by sev_user on 8/23/2016.
 *
 */
public class ItemDAOManager {
    private final SQLiteItemDAOImpl sqLiteItemDAO;
    private final FirebaseItemDAOImpl firebaseItemDAO;

    public ItemDAOManager(Context context) {
        sqLiteItemDAO = new SQLiteItemDAOImpl(context);
        firebaseItemDAO = new FirebaseItemDAOImpl();
    }

    public void setInterface(ITaskResponse anInterface) {
        firebaseItemDAO.setInterface(anInterface);
    }

    public void addItem(Item item) {
        String itemKey = (String) firebaseItemDAO.addEntity(item);
        item.setFirebaseId(itemKey);
        sqLiteItemDAO.addEntity(item);
    }

    public void addItems(ArrayList<Item> itemList, boolean isLocalOnly) {
        for (Item item : itemList) {
            String itemKey = item.getFirebaseId();
            if (!isLocalOnly) {
                itemKey = (String) firebaseItemDAO.addEntity(item);
            }

            if (!itemKey.equals("")) {
                item.setFirebaseId(itemKey);
            }

            sqLiteItemDAO.addEntity(item);
        }
    }

    public ArrayList<Item> getAllItemsByNoteId(int noteId) {
        return sqLiteItemDAO.getAllEntities(DatabaseSpec.ItemDB.FIELD_NOTE_ID, noteId);
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

    public void deleteItemsByNote(Note note) {
        sqLiteItemDAO.deleteItemsByNote(note);
        firebaseItemDAO.deleteItemsByNote(note);
    }

    public void updateItemsByNote(Note note, ArrayList<Item> itemData) {
        sqLiteItemDAO.deleteItemsByNote(note);
        sqLiteItemDAO.addEntities(itemData);

        firebaseItemDAO.updateEntitiesByNote(note, itemData);
    }

    public ArrayList<Item> getLocalItems() {
        return sqLiteItemDAO.getAllEntities(null, 0);
    }

    public void uploadItemsData(ArrayList<Item> itemList) {
        firebaseItemDAO.addEntities(itemList);
    }

    public String uploadItem(Item item, String noteKey) {
        item.setFirebaseNoteId(noteKey);
        String itemKey = (String) firebaseItemDAO.addEntity(item);
        item.setFirebaseId(itemKey);
        return itemKey;
    }

    public void deleteLocalData() {
        sqLiteItemDAO.deleteAllItemData();
    }

    public ArrayList<Item> getServerData() {
        return firebaseItemDAO.getAllEntities(null, 0);
    }
}
