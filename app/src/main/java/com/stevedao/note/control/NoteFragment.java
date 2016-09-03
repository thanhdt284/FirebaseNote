package com.stevedao.note.control;

import java.util.ArrayList;
import android.content.Context;
import android.content.res.TypedArray;
import android.firebase.note.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import com.stevedao.note.model.Item;
import com.stevedao.note.model.ItemDAOManager;
import com.stevedao.note.model.Note;
import com.stevedao.note.model.NoteDAOManager;
import com.stevedao.note.view.NoteDetailAdapter;
import com.stevedao.note.view.NoteDetailAdapterInterface;
import com.stevedao.note.view.NoteInterface;
import com.stevedao.note.view.touchhelper.ItemTouchHelperCallback;
import com.stevedao.note.view.touchhelper.OnStartDragListener;

/**
 * Created by thanh.dao on 29/04/2016.
 *
 */
public class NoteFragment extends Fragment {
    private View mNoteFragmentView;
    private Context mContext;
    private View mParentView;
    private NoteDAOManager noteDAO;
    private ItemDAOManager itemDAO;
    private Note mNote;
    private NoteDetailAdapter mDetailAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<Item> mItemData;
    private int mPosition = 0;
    private int mode;
    private NoteInterface mInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("thanh.dao", "Fragment onCreate");
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("thanh.dao", "Fragment onCreateView");
        mNoteFragmentView = inflater.inflate(R.layout.note_fragment_layout, container, false);
        initFragment(mNoteFragmentView);
        return mNoteFragmentView;
    }

    private void initFragment(View fragmentView) {
        noteDAO = new NoteDAOManager(mContext);
        itemDAO = new ItemDAOManager(mContext);

//        Intent intent = getIntent();
//        mode = intent.getIntExtra("mode", 0);
        mode = Common.NOTE_ACTIVITY_NEW_ENTRY;
//
//        if (mode == Common.NOTE_ACTIVITY_NEW_ENTRY) {
            mNote = new Note();
            mItemData = new ArrayList<>();
//        } else {
//            int noteId = intent.getIntExtra("noteId", 0);
//            mPosition = intent.getIntExtra("mPosition", 0);
//            mNote = noteDAO.getEntity(noteId);
//            mItemData = itemDAO.getAllItemsByNoteId(noteId);
//        }

        final TypedArray colorArray = mContext.getResources().obtainTypedArray(R.array.color_list);
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

        RelativeLayout mContainerLayout = (RelativeLayout) fragmentView.findViewById(R.id.container_layout);
        mContainerLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
        mRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.note_content_recycler_view);
        mDetailAdapter = new NoteDetailAdapter(mContext, mNote, mItemData, colorList, adapterInterface, mOnsOnStartDragListener);

        ItemTouchHelperCallback callback = new ItemTouchHelperCallback(mDetailAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        RecyclerView.ItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(300);
        animator.setChangeDuration(300);
        animator.setMoveDuration(300);
        animator.setRemoveDuration(300);

        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mDetailAdapter);

        FrameLayout mBackButton = (FrameLayout) mNoteFragmentView.findViewById(R.id.note_actionbar_back_container);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
                hide();
            }
        });
    }

    private NoteDetailAdapterInterface adapterInterface = new NoteDetailAdapterInterface() {
        @Override
        public void onChangeColor(int color) {
//            mContainerLayout.setBackgroundColor(color50List[color]);
        }

        @Override
        public void scrollToPosition(int position) {
            mLayoutManager.scrollToPosition(position);
        }
    };

    private boolean isEmptyNote() {
        boolean isEmpty = true;

        for (Item item : mItemData) {
            if (!item.getContent().equals("")) {
                isEmpty = false;
                break;
            }
        }

        return (mNote.getTitle() == null || mNote.getTitle().equals("")) && isEmpty;

    }

    private void saveNote() {
        Common.hideIME(mContext, mRecyclerView.getFocusedChild());
        mRecyclerView.clearFocus();

        if (isEmptyNote()) {
            return;
        }

//        if (noteDAO == null) {
//            noteDAO = new NoteDAOManager(mContext);
//        }
//
//        if (itemDAO == null) {
//            itemDAO = new ItemDAOManager(mContext);
//        }

        long currentTime = System.currentTimeMillis();
        mNote.setLastModified(currentTime);
        if (mNote.getTitle().equals("")) {
            mNote.setTitle(DateUtils.getRelativeTimeSpanString(currentTime).toString());
        }

        boolean isDone = true;
        for(Item item : mItemData) {
            if (!item.isChecked()) {
                isDone = false;
                break;
            }
        }
        if (mItemData.size() == 0) {
            isDone = false;
        }
        mNote.setIsDone(isDone);

        if (mode == Common.NOTE_ACTIVITY_NEW_ENTRY) {
            noteDAO.addNote(mNote);

            for (Item item : mItemData) {
                item.setNoteId(mNote.getId());
                item.setFirebaseNoteId(mNote.getFirebaseId());
            }

            itemDAO.addItems(mItemData, false);
        } else {
            noteDAO.updateNote(mNote);

            for (Item item : mItemData) {
                item.setNoteId(mNote.getId());
                item.setFirebaseNoteId(mNote.getFirebaseId());
            }

            itemDAO.updateItemsByNote(mNote, mItemData);

//            itemDAO.deleteItemsByNote(mNote.getId());
//            itemDAO.addItems(mItemData, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void initData(Note note, int position) {
        if (note == null) {
            mode = Common.NOTE_ACTIVITY_NEW_ENTRY;
            mNote = new Note();
            mPosition = 0;
            mItemData = new ArrayList<>();
        } else {
            mode = Common.NOTE_ACTIVITY_EDIT_ENTRY;
            mPosition = position;
            mNote = note;
            mItemData = itemDAO.getAllItemsByNoteId(mNote.getId());
        }

        mDetailAdapter.setData(mNote, mItemData);
        mDetailAdapter.notifyDataSetChanged();
    }

    public void onBackPressed() {
        saveNote();
        hide();
    }

    public boolean isShowing() {
        if (mParentView == null) {
            mParentView = (View) mNoteFragmentView.getParent();
        }
        return (mParentView.getVisibility() == View.VISIBLE);
    }

    public void hide() {
        if (mParentView == null) {
            mParentView = (View) mNoteFragmentView.getParent();
        }

        mParentView.setVisibility(View.GONE);
        if (!isEmptyNote()) {
            mInterface.onHideFragment(mode, mPosition, mNote.getId());
        }
    }

    public void show() {
        if (mParentView == null) {
            mParentView = (View) mNoteFragmentView.getParent();
        }

        mParentView.setVisibility(View.VISIBLE);
    }

    private OnStartDragListener mOnsOnStartDragListener = new OnStartDragListener() {
        @Override
        public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
            mItemTouchHelper.startDrag(viewHolder);
        }
    };

    public void setInterface(NoteInterface interf) {
        mInterface = interf;
    }
}
