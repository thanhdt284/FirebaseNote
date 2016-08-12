package com.stevedao.note.view;

import android.content.Context;
import android.graphics.Paint;
import android.firebase.note.R;
import com.stevedao.note.control.Common;
import com.stevedao.note.model.Note;
import com.stevedao.note.view.touchhelper.ItemTouchHelperAdapter;
import com.stevedao.note.view.touchhelper.ItemTouchHelperViewHolder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.util.SparseBooleanArray;
import com.amulyakhare.textdrawable.TextDrawable;

/**
 * Created by thanh.dao on 20/04/2016.
 *
 */
public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteListViewHolder> implements ItemTouchHelperAdapter{
    private SparseBooleanArray selectedItems;
    private Context mContext;
    private ArrayList<Note> mNoteData;
    private ArrayList<Note> mOriginalData;
    private int[] mColorList;
    private NoteListAdapterInterface mInterface;
    private String searchingText;
    private boolean isSearching;
    private String TAG = "NoteListAdapter";

    public NoteListAdapter(Context context, ArrayList<Note> noteData, int[] colorList, NoteListAdapterInterface noteInterface) {
        mContext = context;
        mNoteData = noteData;
        mColorList = colorList;
        mInterface = noteInterface;

        mOriginalData = new ArrayList<>();
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public NoteListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.note_list_item_linear_mode, parent, false);

        return new NoteListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NoteListViewHolder holder, int position) {
        if (mNoteData.get(position).isDone()) {
            holder.mNoteTitle.setPaintFlags(holder.mNoteTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.mNoteTitle.setPaintFlags(holder.mNoteTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        holder.mNoteTitle.setText(mNoteData.get(position).getTitle());
        holder.mNoteContent.setText(mNoteData.get(position).getFullContent());

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(mNoteData.get(position).getTitle().substring(0, 1).toUpperCase(),
                            mColorList[mNoteData.get(position).getColor()]);
        holder.mIndicator.setImageDrawable(drawable);
//        holder.mIndicator.setColorFilter(mColorList[mNoteData.get(position).getColor()], PorterDuff.Mode.SRC_ATOP);
//        holder.backgroundLayout.setBackgroundColor(mColor100List[mNoteData.get(position).getColor()]);
//        holder.backgroundLayout.setBackgroundColor(mContext.getResources().getColor(R.color.color_white));

        holder.mOverlayView.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mNoteData.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        mInterface.onRemoved(position);
        removeItem(position, true);
//        notifyItemRemoved(position);
    }

    public void setSearching(boolean searching) {
        isSearching = searching;
    }

    ////// Selected item ///////////////////
    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());

        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }

        return items;
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    private boolean isSelected(int position) {
        return getSelectedItems().contains(position);
    }

    public void clearSelection() {
        List<Integer> selection = getSelectedItems();
        selectedItems.clear();

        for (Integer i : selection) {
            notifyItemChanged(i);
        }
    }

    public void toggleSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }

        notifyItemChanged(position);
    }

    public void removeItems(List<Integer> positions) {
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        for (Integer i : positions) {
            if (i < mNoteData.size()) {
                removeItem(i, true);
            }
        }
    }
    ////// Selected item ///////////////////

    ////// Search function /////////////////
    private Note removeItem(int position, boolean isDeleteData) {
        Note removedNote = mNoteData.remove(position);
        if (isDeleteData && isSearching) {
            mOriginalData.remove(position);
        }
        notifyItemRemoved(position);
        return removedNote;
    }

    private void addItem(int position, Note note) {
        mNoteData.add(position, note);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        Note note = mNoteData.remove(fromPosition);
        mNoteData.add(toPosition, note);
        notifyItemMoved(fromPosition, toPosition);
    }

    private void animateTo(ArrayList<Note> data) {
        applyAndAnimateRemovals(data);
        applyAndAnimateAdditions(data);
        applyAndAnimateMovedItems(data);
    }

    private void applyAndAnimateRemovals(ArrayList<Note> newData) {
        for (int i = mNoteData.size() - 1; i >= 0; i--) {
            Note note = mNoteData.get(i);
            if (!newData.contains(note)) {
                removeItem(i, false);
            }
        }
    }

    private void applyAndAnimateAdditions(ArrayList<Note> newData) {
        for (int i = 0; i < newData.size(); i++) {
            Note note = newData.get(i);
            if (!mNoteData.contains(note)) {
                addItem(i, note);
            }
        }
    }

    private void applyAndAnimateMovedItems(ArrayList<Note> newData) {
        for (int toPosition = newData.size() - 1; toPosition >= 0; toPosition--) {
            Note note = newData.get(toPosition);
            int fromPosition = mNoteData.indexOf(note);
            if(fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public void backupBeforeSearching() {
        mOriginalData.clear();

        mOriginalData.addAll(mNoteData);
        setSearching(true);
    }

    private ArrayList<Note> filter(ArrayList<Note> data, String query) {
        query = query.toLowerCase();

        ArrayList<Note> filteredList = new ArrayList<>();
        for (Note note : data) {
            String title = note.getTitle().toLowerCase();
            if (Common.unAccent(title).contains(Common.unAccent(query))) {
                filteredList.add(note);
            }
        }
        return filteredList;
    }

    public void queryTextChanged(String text) {
        searchingText = text;
        ArrayList<Note> filteredList = filter(mOriginalData, text);
        animateTo(filteredList);

        mInterface.scrollToPosition(0);
    }

    public void updateBackupNoteData(Note editedNote) {
        int noteId = editedNote.getId();

        for (Note note : mOriginalData) {
            if (note.getId() == noteId) {
                int index = mOriginalData.indexOf(note);

                mOriginalData.remove(index);
                mOriginalData.add(index, editedNote);
                break;
            }
        }
        queryTextChanged(searchingText);
    }

    ////// Search function /////////////////

    public class NoteListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener,
            ItemTouchHelperViewHolder {
        public ImageView mIndicator;
        public TextView mNoteTitle;
        public TextView mNoteContent;
        public View mOverlayView;

        public NoteListViewHolder(View itemView) {
            super(itemView);

            mIndicator = (ImageView) itemView.findViewById(R.id.note_list_indicator);
            mNoteTitle = (TextView) itemView.findViewById(R.id.note_list_item_title_text);
            mNoteContent = (TextView) itemView.findViewById(R.id.note_list_item_content_text);
            mOverlayView = itemView.findViewById(R.id.selected_overlay);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
//            Intent noteIntent = new Intent(mContext, NoteActivity.class);
//            int pos = getAdapterPosition();
//            noteIntent.putExtra("position", pos);
//            noteIntent.putExtra("noteId", mNoteData.get(pos).getId());
//            noteIntent.putExtra("mode", Common.NOTE_ACTIVITY_EDIT_ENTRY);
//            ((Activity)mContext).startActivityForResult(noteIntent, Common.NOTE_ACTIVITY_EDIT_ENTRY);
            int pos = getAdapterPosition();
            if (mInterface != null) {
                mInterface.onItemClicked(mNoteData.get(pos), pos);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int pos = getAdapterPosition();
            if (mInterface != null) {
                mInterface.onItemLongClicked(pos);
            }
            return true;
        }

        @Override
        public void onItemSelected() {
        }

        @Override
        public void onItemClear() {
        }

        public void clearAnimation() {
            itemView.clearAnimation();
        }

    }

    @Override
    public void onViewDetachedFromWindow(NoteListViewHolder holder) {
        holder.clearAnimation();
    }
}
