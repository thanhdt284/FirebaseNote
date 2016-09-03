package com.stevedao.note.control;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.firebase.note.R;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.stevedao.note.model.FirebaseDAO.FirebaseUtil;
import com.stevedao.note.model.Note;
import com.stevedao.note.model.User;
import com.stevedao.note.model.UserDAOManager;
import com.stevedao.note.view.ITaskResponse;
import com.stevedao.note.view.NoteInterface;
import com.stevedao.note.view.NoteListInterface;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener,
        GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 100;
    private static final String DIALOG_FRAGMENT_TAG = "DIALOG_FRAGMENT_TAG";
    private DrawerLayout mDrawerLayout;
    private NoteFragment mNoteFragment;
    private NoteListFragment mNoteListFragment;
    private FloatingActionButton mainFAB;
    private int mCurrentMode;
    private String mCurrentTitle;
    private GoogleApiClient mGoogleApiClient;
    private LoginFragment mLoginFragment;
    private Context mContext;
    private UserDAOManager mUserDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        mUserDAO = new UserDAOManager(mContext);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle =
                new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.string_open_drawer,
                                          R.string.string_close_drawer) {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                        mNoteListFragment.dismissUndoSnackbar();
                    }
                };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }

        mainFAB = (FloatingActionButton) findViewById(R.id.main_fab);
        if (mainFAB != null) {
            mainFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNoteListFragment.dismissUndoSnackbar();

                    mNoteFragment.initData(null, 0);
                    mNoteFragment.show();
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
            });
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                                .requestProfile().requestIdToken(getString(R.string.default_web_client_id)).build())
                .build();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        mNoteListFragment = new NoteListFragment();
        mNoteListFragment.setInterface(mNoteListInterface);

        mNoteFragment = new NoteFragment();
        mNoteFragment.setInterface(mNoteInterface);

        mLoginFragment = new LoginFragment();
        mLoginFragment.setInterface(mLoginInterface);

        FrameLayout loginContainer = (FrameLayout) findViewById(R.id.login_fragment_container);

        if (fragmentManager.findFragmentById(R.id.note_list_fragment_container) == null) {
            if (findViewById(R.id.note_list_fragment_container) != null) {
                    ft.replace(R.id.note_list_fragment_container, mNoteListFragment);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        }

        if (fragmentManager.findFragmentById(R.id.login_fragment_container) == null) {
            if (findViewById(R.id.login_fragment_container) != null) {
                ft.replace(R.id.login_fragment_container, mLoginFragment);
            }
        }

        if (fragmentManager.findFragmentById(R.id.note_fragment_container) == null) {
            if (findViewById(R.id.note_fragment_container) != null) {
                //                ft.setCustomAnimations(R.animator.enter, R.animator.exit);
                ft.replace(R.id.note_fragment_container, mNoteFragment);
            }
        }

        ft.commitAllowingStateLoss();

        mCurrentTitle = getResources().getString(R.string.string_notes);
        setTitle(mCurrentTitle);

        if (FirebaseUtil.getCurrentUser() == null) {
            if (loginContainer != null) {
                loginContainer.setVisibility(View.VISIBLE);
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            mainFAB.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                if (account != null) {
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    showDialog(getResources().getString(R.string.string_signing_in));
                    FirebaseUtil.getAuthInstance().signInWithCredential(credential)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    hideDialog();
                                    if (task.isSuccessful()) {
                                        Log.w(Common.APPTAG, "MainActivity - onComplete: " + task.getResult().getUser()
                                                .getEmail());
                                        handleLoginAction();
                                        invalidateOptionsMenu();
                                        logInCompleted(false);
                                    } else {
                                        Log.e(Common.APPTAG, "MainActivity - onComplete: Authentication failed.");
                                        Toast.makeText(mContext, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            } else {
                Toast.makeText(mContext, "Google sign in failed", Toast.LENGTH_SHORT).show();
                Log.e(Common.APPTAG, "MainActivity - onActivityResult: Google sign in failed");
            }
        }
        //        else { // for fb sign in
        //            if (mLoginFragment != null) {
        //                mLoginFragment.activeActivityResult(requestCode, resultCode, data);
        //            }
        //        }
    }

    private void logInCompleted(boolean isSkipSignIn) {
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        if (mNoteListFragment != null) {
//            ft.replace(R.id.note_list_fragment_container, mNoteListFragment).commit();
//            mainFAB.setVisibility(View.VISIBLE);
//        }
        mLoginFragment.hide();
        mainFAB.setVisibility(View.VISIBLE);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        if (!isSkipSignIn) {
            ProgressDialogFragment fragment = (ProgressDialogFragment) getExistingDialogFragment();
            if (fragment != null) {
                fragment.setMessage(getResources().getString(R.string.string_synchronizing_with_server));
            }
            //        showDialog(getResources().getString(R.string.string_synchronizing_with_server));
            mNoteListFragment.loginSynchronize();
        }
    }

    private void showLoginFragment() {
        if (mLoginFragment != null) {
            mLoginFragment.show();
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    private void handleLoginAction() {
        String uId = FirebaseUtil.getCurrentUserId();
        if (uId != null) {
            FirebaseUser currentUser = FirebaseUtil.getCurrentUser();
            User user = new User(uId, currentUser.getEmail(), currentUser.getDisplayName(),
                                 currentUser.getPhotoUrl() == null ? "" : currentUser.getPhotoUrl().toString());

            if (mUserDAO != null) {
                mUserDAO.addUser(user);
            }
        }
    }

    public void hideDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getExistingDialogFragment();
        if (prev != null) {
            ft.remove(prev).commit();
        }
    }

    private Fragment getExistingDialogFragment() {
        return getSupportFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG);
    }

    public void showDialog(String message) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getExistingDialogFragment();
        if (prev == null) {
            ProgressDialogFragment fragment = ProgressDialogFragment.newInstance(message);
            //            fragment.show(ft, DIALOG_FRAGMENT_TAG);
            ft.add(fragment, DIALOG_FRAGMENT_TAG);
            ft.commitAllowingStateLoss();
        }
    }

    private LoginFragment.LoginFragmentInterface mLoginInterface = new LoginFragment.LoginFragmentInterface() {
        @Override
        public void ggSignIn() {
            Intent loginIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(loginIntent, RC_SIGN_IN);
        }

        @Override
        public void skipSignIn() {
            logInCompleted(true);
        }

        //        @Override
        //        public void fbSignIn(AccessToken token) {
        //
        //        }
    };

    private NoteInterface mNoteInterface = new NoteInterface() {
        @Override
        public void onHideFragment(int mode, int position, int noteId) {
            mNoteListFragment.onHideNoteFragment(mode, position, noteId);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }

    };

    private NoteListInterface mNoteListInterface = new NoteListInterface() {
        @Override
        public void openNote(Note openNote, int position) {
            mNoteFragment.initData(openNote, position);
            mNoteFragment.show();
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        @Override
        public void changeFABVisibilityState(int state) {
            mainFAB.setVisibility(state);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_search:

            break;
        case R.id.action_sign_out:
            showDialog(getResources().getString(R.string.string_checking_network_connection));
            Common.hasActiveInternetConnection(mContext, networkResponse);

//                handleSignOutAction();
//            } else {
//                showSignOutWarningDialog();
//            }
            break;
        case R.id.action_sign_in:
            showLoginFragment();
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private ITaskResponse networkResponse = new ITaskResponse() {
        @Override
        public void onResponse(Object... params) {
            hideDialog();
            if ((boolean)params[0]) {
                handleSignOutAction();
            } else {
                showSignOutWarningDialog();
            }
        }
    };

    private void showSignOutWarningDialog() {
        AlertDialog signOutWarning = new AlertDialog.Builder(this).create();
        signOutWarning.setTitle(getResources().getString(R.string.string_no_internet_connection));
        signOutWarning.setMessage(getResources().getString(R.string.string_sign_out_no_internet_warning));
        signOutWarning.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.string_sign_out),
                                 new DialogInterface.OnClickListener() {
                                     @Override
                                     public void onClick(DialogInterface dialog, int which) {
                                         handleSignOutAction();
                                         dialog.dismiss();
                                     }
                                 });
        signOutWarning.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.string_cancel),
                                 new DialogInterface.OnClickListener() {
                                     @Override
                                     public void onClick(DialogInterface dialog, int which) {
                                         dialog.dismiss();
                                     }
                                 });
        signOutWarning.show();
    }

    private void handleSignOutAction() {
        if (mLoginFragment != null && FirebaseUtil.getCurrentUser() != null) {
//            showDialog(getResources().getString(R.string.string_signing_out));
            signOut();
            showLoginFragment();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        FirebaseUser currentUser = FirebaseUtil.getCurrentUser();
        MenuItem signOutItem = menu.findItem(R.id.action_sign_out);
        MenuItem signInItem = menu.findItem(R.id.action_sign_in);

        if (signOutItem != null) {
            if (currentUser != null) {
                signOutItem.setVisible(true);
                signInItem.setVisible(false);
            } else {
                signOutItem.setVisible(false);
                signInItem.setVisible(true);
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void signOut() {
        FirebaseUtil.getAuthInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        if (mNoteListFragment != null) {
            mNoteListFragment.removeLocalData();
        }
        mUserDAO.deleteUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_normal_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mNoteListFragment.onMenuItemActionExpand();
                mainFAB.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mNoteListFragment.onMenuActionCollapse();

                if (mNoteListFragment.getCurrentMode() == Common.NOTE_STORAGE_MODE_ACTIVE) {
                    mainFAB.setVisibility(View.VISIBLE);
                }

                return true;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        if (mNoteFragment != null && mNoteFragment.isShowing()) {
            mNoteFragment.onBackPressed();
        } else {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.nav_menu_note:
        case R.id.nav_menu_archive:
        case R.id.nav_menu_trash:
            changeNoteListMode(item.getItemId());
            break;
        case R.id.nav_menu_settings:
            initSampleData();
            break;
        case R.id.nav_menu_help:
            break;
        }


        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initSampleData() {
        mNoteListFragment.initSampleData();
    }

    private void changeNoteListMode(int menuId) {
        switch (menuId) {
        case R.id.nav_menu_note:
            mCurrentMode = Common.NOTE_STORAGE_MODE_ACTIVE;
            mCurrentTitle = getResources().getString(R.string.string_notes);
            break;
        case R.id.nav_menu_archive:
            mCurrentMode = Common.NOTE_STORAGE_MODE_ARCHIVE;
            mCurrentTitle = getResources().getString(R.string.string_archive);
            break;
        case R.id.nav_menu_trash:
            mCurrentMode = Common.NOTE_STORAGE_MODE_TRASH;
            mCurrentTitle = getResources().getString(R.string.string_trash);
            break;
        default:
            break;
        }

        mNoteListFragment.setNoteData(mCurrentMode);
        setTitle(mCurrentTitle);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mNoteListFragment.onQueryTextChange(newText);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(Common.APPTAG, "MainActivity - onConnectionFailed: " + connectionResult.getErrorMessage());
    }


}
