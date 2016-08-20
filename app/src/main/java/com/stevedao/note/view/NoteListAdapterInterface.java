package com.stevedao.note.view;


import com.stevedao.note.model.Note;

/**
 * Created by thanh.dao on 26/04/2016.
 *
 */
public interface NoteListAdapterInterface {
    void onRemoved(int position);

    void scrollToPosition(int position);

    void onItemClicked(Note note, int position);

    void onItemLongClicked(int position);
}
