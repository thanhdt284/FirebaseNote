package com.stevedao.note.control;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by sev_user on 8/18/2016.
 *
 */
public class ProgressDialogFragment extends DialogFragment {
    private static final String KEY_TITLE = "title";
    private ProgressDialog dialog;

    public static ProgressDialogFragment newInstance(String text) {
        ProgressDialogFragment frag = new ProgressDialogFragment();
        frag.setCancelable(false);

        Bundle args = new Bundle();
        args.putString(KEY_TITLE, text);

        frag.setArguments(args);

        return frag;
    }

    public void setMessage(String text) {
        dialog.setMessage(text);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new ProgressDialog(getActivity(), getTheme());
        dialog.setMessage(getArguments().getString(KEY_TITLE));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}
