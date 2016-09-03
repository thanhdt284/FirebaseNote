package com.stevedao.note.control;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.firebase.note.R;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.net.InetAddress;
import java.text.Normalizer;
import java.util.Calendar;
import java.util.regex.Pattern;
import com.stevedao.note.view.ITaskResponse;

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

    public static final int LOGIN_NOTE_SYNC = 0;
    public static final int LOGIN_ITEM_SYNC = 1;
    public static final String APPTAG = "FirebaseNote";

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
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(new int[]{ R.attr.actionBarSize});
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

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void hasActiveInternetConnection(Context context, ITaskResponse networkResponse) {
//        if (isNetworkAvailable(context)) {
//            try {
//                HttpURLConnection mConnection = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
//                mConnection.setRequestProperty("User-Agent", "Test");
//                mConnection.setRequestProperty("Connection", "close");
//                mConnection.setConnectTimeout(1500);
//                mConnection.connect();
//                return (mConnection.getResponseCode() == 200);
//            } catch (IOException e) {
//                Log.e(TAG, "Error checking internet connection : " + e.getMessage());
//            }
//        } else {
//            Log.d(TAG, "No network available!");
//        }
//        return false;
        NetworkCheckAsync networkCheck = new NetworkCheckAsync(networkResponse);
        networkCheck.execute();
    }

    private static class NetworkCheckAsync extends AsyncTask<Void, Void, Boolean> {

        private ITaskResponse mInterface;

        public NetworkCheckAsync(ITaskResponse mInterface) {
            this.mInterface = mInterface;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
                Log.e(APPTAG, "Common - Network checking: connected");
                return !ipAddr.toString().equals("");

            } catch (Exception e) {
                Log.e(APPTAG, "Common - Network checking: no internet access");
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mInterface.onResponse(aBoolean);
        }
    }
}
