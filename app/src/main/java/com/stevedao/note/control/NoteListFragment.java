package com.stevedao.note.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import android.content.Context;
import android.content.res.TypedArray;
import android.firebase.note.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.stevedao.note.model.Item;
import com.stevedao.note.model.ItemDAOManager;
import com.stevedao.note.model.Note;
import com.stevedao.note.model.NoteDAOManager;
import com.stevedao.note.view.ITaskResponse;
import com.stevedao.note.view.NoteListAdapter;
import com.stevedao.note.view.NoteListAdapterInterface;
import com.stevedao.note.view.NoteListInterface;
import com.stevedao.note.view.touchhelper.ItemTouchHelperCallback;

/**
 * Created by thanh.dao on 26/05/2016.
 *
 */
public class NoteListFragment extends Fragment {
    private Context mContext;
    private View mNoteListFragment;
    private NoteDAOManager mNoteDAO;
    private ItemDAOManager mItemDAO;
    private ArrayList<Note> mNoteList;
    private RecyclerView mNoteListView;
    private NoteListAdapter mNoteListAdapter;
    private Snackbar mUndoSnackbar;
    private int mCurrentMode = Common.NOTE_STORAGE_MODE_ACTIVE;
    private boolean isSearching = false;
    private NoteListInterface mNoteListInterface;
    private ActionModeCallback actionModeCallBack;
    private ActionMode actionmode;
    private ItemTouchHelperCallback touchCallback;
    private HashMap<Integer, Boolean> loginDataSyncKey;
    private MainActivity mainActivity;

    private static final String[] titles = {"Breads", "Dairy products", "Eggs", "Legumes", "Edible plants", "Meat",
            "Edible nuts and seeds", "Cereals", "Seafood", "Staple foods", "Breads", "Dairy products", "Eggs",
            "Legumes", "Edible plants", "Meat",
            "Edible nuts and seeds", "Cereals", "Seafood", "Staple foods"};
    private static final String[][] contents = {
            {"Adobe bread", "Amish friendship bread", "Anadama bread", "Banana bread"},
            {"Beurre noir", "Butter cake", "Butter pecan", "Egg butter", "Hard sauce"},
            {"Boiled egg", "Fried egg", "Omelette", "Poached egg"},
            {"Palmer's grass", "Rice", "Rye", "Sorghum"},
            {"Emblica, see Indian gooseberry", "Emu apple", "Emu berry (Grewia retusifolia)"},
            {"Alambre", "Bakso","Bandeja paisa","Beef ball","Beef bun"},
            {"Spelt", "Teff", "Triticale", "Wheat", "Wild rice"},
            {"Arepa", "Battered sausage", "Bulz", "Cachapa", "Champurrado", "Cocoloşi"},
            {"Fish heads", "Hoe", "Hoedeopbap", "Jaecheopguk", "Kaeng som"},
            {"Banana", "Banku", "Bean", "Black turtle bean", "Blue corn"},
            {"Adobe bread", "Amish friendship bread", "Anadama bread", "Banana bread"},
            {"Beurre noir", "Butter cake", "Butter pecan", "Egg butter", "Hard sauce"},
            {"Boiled egg", "Fried egg", "Omelette", "Poached egg"},
            {"Palmer's grass", "Rice", "Rye", "Sorghum"},
            {"Emblica, see Indian gooseberry", "Emu apple", "Emu berry (Grewia retusifolia)"},
            {"Alambre", "Bakso","Bandeja paisa","Beef ball","Beef bun"},
            {"Spelt", "Teff", "Triticale", "Wheat", "Wild rice"},
            {"Arepa", "Battered sausage", "Bulz", "Cachapa", "Champurrado", "Cocoloşi"},
            {"Fish heads", "Hoe", "Hoedeopbap", "Jaecheopguk", "Kaeng som"},
            {"Banana", "Banku", "Bean", "Black turtle bean", "Blue corn"}
    };

    private ArrayList<Note> localNotes;
    private ArrayList<Item> localItems;

    public NoteListFragment() {
        loginDataSyncKey = new HashMap<>();
        loginDataSyncKey.put(Common.LOGIN_NOTE_SYNC, false);
        loginDataSyncKey.put(Common.LOGIN_ITEM_SYNC, false);


        mNoteDAO = new NoteDAOManager(mContext);
        mItemDAO = new ItemDAOManager(mContext);

        mNoteDAO.setInterface(fbNoteInterface);
        mItemDAO.setInterface(fbItemInterface);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        mainActivity = (MainActivity) getActivity();
        actionModeCallBack = new ActionModeCallback();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mNoteListFragment = inflater.inflate(R.layout.note_list_fragment_layout, container, false);

        initFragment();

        return mNoteListFragment;
    }

    private void initFragment() {
        TypedArray colorArray = mContext.getResources().obtainTypedArray(R.array.color_list);
        int[] colorList = new int[9];
        for (int i = 0; i < colorList.length; i++) {
            colorList[i] = colorArray.getColor(i, 0);
        }
        colorArray.recycle();

        TypedArray color50Array = mContext.getResources().obtainTypedArray(R.array.color_50_list);
        int[] color50List = new int[9];

        for (int i = 0; i < color50List.length; i++) {
            color50List[i] = color50Array.getColor(i, 0);
        }
        color50Array.recycle();

        TypedArray color100Array = mContext.getResources().obtainTypedArray(R.array.color_100_list);
        int[] color100List = new int[9];
        for (int i = 0; i < color100List.length; i++) {
            color100List[i] = color100Array.getColor(i, 0);
        }
        color100Array.recycle();

        if (mNoteList == null) {
            mNoteList = new ArrayList<>();
        }

        mNoteListView = (RecyclerView) mNoteListFragment.findViewById(R.id.note_list_recycler_view);

        LinearLayoutManager mNoteListLayoutManager = new LinearLayoutManager(mContext);
        mNoteListAdapter = new NoteListAdapter(mContext, mNoteList, colorList, mNoteInterface);
        touchCallback = new ItemTouchHelperCallback(mNoteListAdapter);
        touchCallback.setSwipable(true);
        ItemTouchHelper mNoteItemTouchHelper = new ItemTouchHelper(touchCallback);
        mNoteItemTouchHelper.attachToRecyclerView(mNoteListView);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setMoveDuration(200);
        itemAnimator.setAddDuration(500);
        itemAnimator.setChangeDuration(200);
        itemAnimator.setRemoveDuration(200);
        mNoteListView.setItemAnimator(itemAnimator);
        mNoteListView.setLayoutManager(mNoteListLayoutManager);
        mNoteListView.setAdapter(mNoteListAdapter);
//        mNoteListView.addItemDecoration(new NoteListItemDecoration(mContext, NoteListItemDecoration.VERTICAL_LIST));

        getData(mCurrentMode);
    }

    @SuppressWarnings("unchecked")
    private ITaskResponse fbNoteInterface = new ITaskResponse() {
        @Override
        public void onResponse(Object... params) {
            if (params != null) {
                if (params[0] instanceof Integer && params[0] == 0) {
                    if (mNoteDAO != null && params[1] instanceof ArrayList) {
                        Log.e(Common.APPTAG, "NoteListFragment - onResponse: fbNoteInterface getting notes done");
                        mNoteDAO.addNotes((ArrayList<Note>) params[1], true);
                        loginDataSyncKey.put(Common.LOGIN_NOTE_SYNC, true);
                        if (loginDataSyncKey.get(Common.LOGIN_ITEM_SYNC)) {
                            loginDataSyncKey.put(Common.LOGIN_NOTE_SYNC, false);
                            loginDataSyncKey.put(Common.LOGIN_ITEM_SYNC, false);

                            uploadLocalData(localNotes, localItems);
                            mainActivity.hideDialog();
                            getData(mCurrentMode);
                        }
                    }
                }
            }
        }
    };

    @SuppressWarnings("unchecked")
    private ITaskResponse fbItemInterface = new ITaskResponse() {
        @Override
        public void onResponse(Object... params) {
            if (params != null) {
                if (params[0] instanceof Integer && params[0] == Common.LOGIN_ITEM_SYNC) {
                    if (mItemDAO != null && params[1] instanceof ArrayList) {
                        Log.e(Common.APPTAG, "NoteListFragment - onResponse: fbNoteInterface getting items done");
                        mItemDAO.addItems((ArrayList<Item>) params[1], true);
                        loginDataSyncKey.put(Common.LOGIN_ITEM_SYNC, true);
                        if (loginDataSyncKey.get(Common.LOGIN_NOTE_SYNC)) {
                            loginDataSyncKey.put(Common.LOGIN_NOTE_SYNC, false);
                            loginDataSyncKey.put(Common.LOGIN_ITEM_SYNC, false);

                            uploadLocalData(localNotes, localItems);
                            mainActivity.hideDialog();
                            getData(mCurrentMode);
                        }
                    }
                }
            }
        }
    };

//    public void show() {
//        if (mParentView == null) {
//            mParentView = (View) mNoteListFragment.getParent();
//        }
//
//        mParentView.setVisibility(View.VISIBLE);
//    }
//
//    public boolean isShowing() {
//        if (mParentView == null) {
//            mParentView = (View) mNoteListFragment.getParent();
//        }
//        return (mParentView.getVisibility() == View.VISIBLE);
//    }
//
//    public void hide() {
//        if (mParentView == null) {
//            mParentView = (View) mNoteListFragment.getParent();
//        }
//
//        mParentView.setVisibility(View.GONE);
//    }

    private NoteListAdapterInterface mNoteInterface = new NoteListAdapterInterface() {
        @Override
        public void onRemoved(final int position) {
            final Note remNote = mNoteList.get(position);
            String notice = "";
            switch (mCurrentMode) {
            case Common.NOTE_STORAGE_MODE_ACTIVE:
            case Common.NOTE_STORAGE_MODE_ARCHIVE:
                notice = mContext.getResources().getString(R.string.string_note_is_deleted);
                break;
            case Common.NOTE_STORAGE_MODE_TRASH:
                notice = mContext.getResources().getString(R.string.string_note_is_permanently_deleted);
                break;
            default:
                break;
            }
            mUndoSnackbar = Snackbar
                    .make(mNoteListView, notice, Snackbar.LENGTH_LONG).setAction(R.string.string_undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mNoteList.add(position, remNote);
                            mNoteListView.scrollToPosition(position);
                            mNoteListAdapter.notifyItemInserted(position);
                        }
                    });

            mUndoSnackbar.setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                        if (mNoteDAO != null) {
                            if (mCurrentMode == Common.NOTE_STORAGE_MODE_ACTIVE || mCurrentMode == Common.NOTE_STORAGE_MODE_ARCHIVE) {
                                mNoteDAO.changeStorageMode(remNote, Common.NOTE_STORAGE_MODE_TRASH);
                            } else {
                                mNoteDAO.deleteNote(remNote);
                            }
                        }
                    }
                }
            });

            mUndoSnackbar.show();
        }

        @Override
        public void scrollToPosition(int position) {
            mNoteListView.scrollToPosition(position);
        }

        @Override
        public void onItemClicked(Note openNote, int position) {
            if (actionmode != null) {
                toggleSelection(position);
            } else {
                mNoteListInterface.openNote(openNote, position);
            }
        }

        @Override
        public void onItemLongClicked(int position) {
            if (actionmode == null) {
                actionmode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallBack);
            }

            toggleSelection(position);
        }
    };

    private void toggleSelection(int position) {
        mNoteListAdapter.toggleSelection(position);

        int count = mNoteListAdapter.getSelectedItemCount();

//        if (count == 0) {
//            actionmode.finish();
//        } else {
        if (actionmode != null) {
            actionmode.setTitle(String.valueOf(count));
            actionmode.invalidate();
        }
//        }
    }

    public void onHideNoteFragment(int mode, int position, int noteId) {
        if (mode == Common.NOTE_ACTIVITY_NEW_ENTRY) {
            Note newNote = mNoteDAO.getLocalNote(noteId);
            newNote.setFullContent(mItemDAO.getFullContent(noteId, Common.GET_ITEM_CONTENT_ACTION_LIST));

            mNoteList.add(0, newNote);
            mNoteListView.scrollToPosition(0);
            mNoteListAdapter.notifyItemInserted(0);
        } else if (mode == Common.NOTE_ACTIVITY_EDIT_ENTRY) {
            Note editedNote = mNoteDAO.getLocalNote(noteId);
            editedNote.setFullContent(mItemDAO.getFullContent(noteId, Common.GET_ITEM_CONTENT_ACTION_LIST));

            mNoteList.remove(position);
            mNoteList.add(position, editedNote);
            mNoteListView.scrollToPosition(position);
            if (isSearching) {
                mNoteListAdapter.updateBackupNoteData(editedNote);
            }

            mNoteListAdapter.notifyItemChanged(position);
        }
    }

    public void onMenuItemActionExpand() {
        mNoteListAdapter.backupBeforeSearching();
        isSearching = true;
    }

    public void onMenuActionCollapse() {
        isSearching = false;
        mNoteListAdapter.setSearching(false);
    }

    public int getCurrentMode() {
        return mCurrentMode;
    }

    private void getData(int storageMode) {
        if (mNoteList == null) {
            mNoteList = new ArrayList<>();
        }

        mNoteList.clear();
        mNoteList.addAll(mNoteDAO.getNotesByStorageMode(storageMode));

        for (Note note : mNoteList) {
            note.setFullContent(mItemDAO.getFullContent(note.getId(), Common.GET_ITEM_CONTENT_ACTION_LIST));
        }

        if (mNoteListAdapter != null) {
            mNoteListAdapter.notifyDataSetChanged();
        }
    }

    public void setNoteData(int mode) {
        mCurrentMode = mode;
        switch (mCurrentMode) {
        case Common.NOTE_STORAGE_MODE_ACTIVE:
            mNoteListInterface.changeFABVisibilityState(View.VISIBLE);
            break;
        case Common.NOTE_STORAGE_MODE_ARCHIVE:
        case Common.NOTE_STORAGE_MODE_TRASH:
            mNoteListInterface.changeFABVisibilityState(View.GONE);
            break;
        default:
            break;
        }

        getData(mCurrentMode);
    }

    public void onQueryTextChange(String newText) {
        mNoteListAdapter.queryTextChanged(newText);
    }

    public void setInterface(NoteListInterface mInterface) {
        mNoteListInterface = mInterface;
    }

    public void dismissUndoSnackbar() {
        if (mUndoSnackbar != null && mUndoSnackbar.isShown()) {
            mUndoSnackbar.dismiss();
        }
    }

    public void initSampleData() {
        int size = titles.length;
        Random random = new Random();
        if (mItemDAO == null) {
            mItemDAO = new ItemDAOManager(mContext);
        }

        for (int i = 0; i < size; i++) {
            //init note data
            Note note = new Note();
            if (i == size - 1) {
                note.setStorageMode(Common.NOTE_STORAGE_MODE_TRASH);

                note.setDeletedTime(System.currentTimeMillis());
            } else if (i > 7 && i<size-1) {
                note.setStorageMode(Common.NOTE_STORAGE_MODE_ARCHIVE);
            } else {
                note.setStorageMode(Common.NOTE_STORAGE_MODE_ACTIVE);
            }
            note.setColor(Math.abs(random.nextInt()) % 8);
            note.setTitle(titles[i]);
            note.setIsDone(false);
            long current = System.currentTimeMillis();
            Log.e(Common.APPTAG, "initSampleData: " + current);
            note.setLastModified(current);
            mNoteDAO.addNote(note);

            //init note details data
            int detailSize = contents[i].length;
            for (int j = 0; j < detailSize; j++) {
                Item item = new Item(note.getFirebaseId(), note.getId(), contents[i][j], false, j);
                mItemDAO.addItem(item);
            }

            note.setFullContent(mItemDAO.getFullContent(note.getId(), Common.GET_ITEM_CONTENT_ACTION_LIST));
            mNoteList.add(note);
        }

        mNoteListAdapter.notifyDataSetChanged();
    }

    public void removeLocalData() {
        if (mNoteDAO != null) {
            mNoteDAO.deleteLocalData();
        }

        if (mItemDAO != null) {
            mItemDAO.deleteLocalData();
        }

        getData(Common.NOTE_STORAGE_MODE_ACTIVE);
    }

    public void loginSynchronize() {

        localNotes = mNoteDAO.getLocalNotes();
        localItems = mItemDAO.getLocalItems();
        downloadServerData();
    }

    private void uploadLocalData(ArrayList<Note> localNotes, ArrayList<Item> localItems) {
        Log.e(Common.APPTAG, "NoteListFragment - uploadLocalData: ");
        for (Note localNote : localNotes) {
            String noteKey = mNoteDAO.uploadNote(localNote);
            for (int i = localItems.size() - 1; i >= 0; i++) {
                if (localItems.get(i).getNoteId() == localNote.getId()) {
                    mItemDAO.uploadItem(localItems.get(i), noteKey);
                    localItems.remove(i);
                }
            }
        }
    }

    private void downloadServerData() {
        mNoteDAO.getServerData();
        mItemDAO.getServerData();
    }

    private final class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            touchCallback.setSwipable(false);
            mode.getMenuInflater().inflate(R.menu.main_action_menu, menu);
            showOverFlowMenu(menu);
            return true;
        }

        private void showOverFlowMenu(Menu menu) {
            if (mCurrentMode == Common.NOTE_STORAGE_MODE_ACTIVE) {
                menu.setGroupVisible(R.id.group_archive, false);
                menu.setGroupVisible(R.id.group_trash, false);
            } else if (mCurrentMode == Common.NOTE_STORAGE_MODE_ARCHIVE) {
                menu.setGroupVisible(R.id.group_active, false);
                menu.setGroupVisible(R.id.group_trash, false);
            } else {
                menu.setGroupVisible(R.id.group_archive, false);
                menu.setGroupVisible(R.id.group_active, false);
            }
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            List<Integer> selectedItems = mNoteListAdapter.getSelectedItems();

            switch (item.getItemId()) {
            case R.id.active_delete:
            case R.id.archive_delete:
            case R.id.delete_delete:
                if (mCurrentMode != Common.NOTE_STORAGE_MODE_TRASH) {
                    for (Integer index : selectedItems) {
                        mNoteDAO.changeStorageMode(mNoteList.get(index), Common.NOTE_STORAGE_MODE_TRASH);
                    }
                } else {
                    for (Integer index : selectedItems) {
                        mNoteDAO.deleteNote(mNoteList.get(index));
                    }
                }

                mNoteListAdapter.removeItems(selectedItems);
                mode.finish();
                return true;
            case R.id.active_archive:
            case R.id.delete_archive:
                if (mCurrentMode == Common.NOTE_STORAGE_MODE_ACTIVE) {
                    for (Integer index : selectedItems) {
                        mNoteDAO.changeStorageMode(mNoteList.get(index), Common.NOTE_STORAGE_MODE_ARCHIVE);
                    }
                }

                mNoteListAdapter.removeItems(selectedItems);
                mode.finish();
                return true;
            case R.id.archive_unarchive:
            case R.id.delete_restore:
                for (Integer index : selectedItems) {
                    mNoteDAO.changeStorageMode(mNoteList.get(index), Common.NOTE_STORAGE_MODE_ACTIVE);
                }

                mNoteListAdapter.removeItems(selectedItems);
                mode.finish();
                return true;
            case R.id.action_select_all:
                mNoteListAdapter.selectAll();
                if (actionmode != null) {
                    actionmode.setTitle(String.valueOf(mNoteListAdapter.getItemCount()));
                    actionmode.invalidate();
                }
                return true;
            case R.id.action_clear_selection:
                mNoteListAdapter.clearSelection();
                if (actionmode != null) {
                    actionmode.setTitle(String.valueOf(0));
                    actionmode.invalidate();
                }
                return false;

            default:
                return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mNoteListAdapter.clearSelection();
            actionmode = null;
            touchCallback.setSwipable(true);
        }
    }
}
