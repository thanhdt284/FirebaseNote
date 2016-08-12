package com.stevedao.note.control;

import android.content.Context;
import android.content.res.TypedArray;
import android.firebase.note.R;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Created by thanh.dao on 11/04/2016.
 *
 */
public class Common {
    public static final int NOTE_ACTIVITY_NEW_ENTRY = 0;
    public static final int NOTE_ACTIVITY_EDIT_ENTRY = 1;

    public static final int NOTE_STORAGE_MODE_ACTIVE = 0;
    public static final int NOTE_STORAGE_MODE_ARCHIVE = 1;
    public static final int NOTE_STORAGE_MODE_TRASH = 2;

    public static final int GET_ITEM_CONTENT_ACTION_LIST = 0;
    public static final int GET_ITEM_CONTENT_ACTION_SEND = 1;

//    public static final int NOTE_LIST_NORMAL_MODE = 0;
//    public static final int NOTE_LIST_SELECTION_MODE = 1;

    public static void showIME(Context context, View view) {
        if (context != null && view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void hideIME(Context context, View view) {
        if (context != null && view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return toolbarHeight;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static String unAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
//        return pattern.matcher(temp).replaceAll("");
        return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replaceAll("đ", "d");
    }
}
