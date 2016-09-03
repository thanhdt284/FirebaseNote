package com.stevedao.note.control;

import android.firebase.note.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

/**
 * Created by sev_user on 19/07/2016.
 *
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

//    private static final int RC_SIGN_IN = 103;
    private LoginFragmentInterface mInterface;
    private View mParentView;
    private View loginView;
    //    private CallbackManager mCallbackManager;
//    private LoginButton fbLoginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loginView = inflater.inflate(R.layout.sign_in_fragment_layout, container, false);

        SignInButton ggLoginButton = (SignInButton) loginView.findViewById(R.id.google_sign_in_button);
        ggLoginButton.setSize(SignInButton.SIZE_WIDE);
        ggLoginButton.setColorScheme(SignInButton.COLOR_DARK);

        TextView skipButton = (TextView) loginView.findViewById(R.id.skip_button);

        ggLoginButton.setOnClickListener(this);
        skipButton.setOnClickListener(this);

//        mCallbackManager = CallbackManager.Factory.create();
//        LoginButton fbLoginButton = (LoginButton) loginView.findViewById(R.id.facebook_sign_in_button);
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
        case R.id.google_sign_in_button:
            mInterface.ggSignIn();
            break;
        case R.id.skip_button:
            mInterface.skipSignIn();
            break;
        default:
            break;
        }
    }

    public void hide() {
        if (mParentView == null) {
            mParentView = (View) loginView.getParent();
        }

        mParentView.setVisibility(View.GONE);
    }

    public void show() {
        if (mParentView == null) {
            mParentView = (View) loginView.getParent();
        }

        mParentView.setVisibility(View.VISIBLE);
    }

//    public void activeActivityResult(int requestCode, int resultCode, Intent data) {
//        mCallbackManager.onActivityResult(requestCode, resultCode, data);
//    }

    public void setInterface(LoginFragmentInterface anInterface) {
        this.mInterface = anInterface;
    }

    public interface LoginFragmentInterface{

        void ggSignIn();

        void skipSignIn();

//        void fbSignIn(AccessToken token);
    }
}
