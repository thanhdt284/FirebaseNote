package com.stevedao.note.control;

import java.util.ArrayList;
import com.stevedao.note.model.Note;
import com.stevedao.note.model.NoteWrapper;

/**
 * Created by thanh.dao on 8/15/2016.
 * - Convert between Note and NoteWrapper objects
 */
public class ObjectUtil {
    public static ArrayList<NoteWrapper> getWrapperFromList(ArrayList<Note> list) {
        ArrayList<NoteWrapper> wrapperList = new ArrayList<>();
        for (Note note : list) {
            NoteWrapper wrapper = new NoteWrapper(note);
            wrapperList.add(wrapper);
        }

        return wrapperList;
    }
}
