package com.stevedao.note.view;

import com.stevedao.note.model.Note;

/**
 * Created by thanh.dao on 26/05/2016.
 *
 * Interface between MainActivity and NoteListFragment
 */
public interface NoteListInterface {
    void openNote(Note openNote, int position);

    void changeFABVisibilityState(int state);
}
