package com.stevedao.note.control;

import android.content.Intent;
import android.firebase.note.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.SignInButton;

/**
 * Created by sev_user on 19/07/2016.
 *
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

//    private static final int RC_SIGN_IN = 103;
    private LoginFragmentInterface mInterface;
//    private CallbackManager mCallbackManager;
//    private LoginButton fbLoginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View loginView = inflater.inflate(R.layout.sign_in_fragment_layout, container, false);
        SignInButton ggLoginButton = (SignInButton) loginView.findViewById(R.id.google_signin_button);

        ggLoginButton.setOnClickListener(this);

//        mCallbackManager = CallbackManager.Factory.create();
//        fbLoginButton = (LoginButton) loginView.findViewById(R.id.facebook_signin_button);
//        fbLoginButton.setReadPermissions("email", "public_profile");
//        fbLoginButton.setFragment(this);
//        fbLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.d(TAG, "onSuccess: Facebook" + loginResult.toString());
//                mInterface.fbSignIn(loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
//                Log.d(TAG, "onCancel: Facebook");
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.d(TAG, "onError: Facebook" + error.toString());
//            }
//        });

        return loginView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.google_signin_button:
            mInterface.ggSignIn();
            break;
        default:
            break;
        }
    }

//    public void activeActivityResult(int requestCode, int resultCode, Intent data) {
//        mCallbackManager.onActivityResult(requestCode, resultCode, data);
//    }

    public void setInterface(LoginFragmentInterface anInterface) {
        this.mInterface = anInterface;
    }

    public interface LoginFragmentInterface{

        void ggSignIn();

//        void fbSignIn(AccessToken token);
    }
}
