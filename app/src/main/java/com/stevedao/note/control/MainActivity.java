package com.stevedao.note.control;

import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.stevedao.note.model.FirebaseUtil;
import com.stevedao.note.model.Note;
import com.stevedao.note.model.User;
import com.stevedao.note.model.UserDAOImpl;
import com.stevedao.note.view.NoteInterface;
import com.stevedao.note.view.NoteListInterface;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView
        .OnQueryTextListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MainActivity";
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
    private UserDAOImpl mUserDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        mUserDAO = new UserDAOImpl(mContext);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.string_open_drawer, R.string.string_close_drawer) {
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

        if (getFragmentManager().findFragmentById(R.id.note_list_fragment_container) == null) {
            if (findViewById(R.id.note_list_fragment_container) != null) {
                if (FirebaseUtil.getCurrentUser() != null) {
                    ft.replace(R.id.note_list_fragment_container, mNoteListFragment);
                } else {
                    ft.replace(R.id.note_list_fragment_container, mLoginFragment);
                }
            }
        }
//        mNoteListFragment.show();


        if (getFragmentManager().findFragmentById(R.id.note_fragment_container) == null) {
            if (findViewById(R.id.note_fragment_container) != null) {
//                ft.setCustomAnimations(R.animator.enter, R.animator.exit);
                ft.replace(R.id.note_fragment_container, mNoteFragment);
            }
        }

        ft.commitAllowingStateLoss();

        mainFAB = (FloatingActionButton) findViewById(R.id.main_fab);
        if (mainFAB != null) {
            mainFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNoteListFragment.dismissUndoSnackbar();

                    mNoteFragment.initData(null, 0);
                    mNoteFragment.show();
                }
            });
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
                    showDialog(getResources().getString(R.string.string_logging_in));
                    FirebaseUtil.getAuthInstance().signInWithCredential(credential)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    hideDialog();
                                    if (task.isSuccessful()) {
                                        Log.w(TAG, "onComplete: " + task.getResult().getUser().getEmail());
                                        handleLoginAction();
                                        invalidateOptionsMenu();
                                        logInCompleted();
                                    } else {
                                        Log.e(TAG, "onComplete: Authentication failed.");
                                        Toast.makeText(mContext, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            } else {
                Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onActivityResult: Google sign in failed");
            }
        }
//        else { // for fb sign in
//            if (mLoginFragment != null) {
//                mLoginFragment.activeActivityResult(requestCode, resultCode, data);
//            }
//        }
    }

    private void logInCompleted() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mNoteListFragment != null) {
            ft.replace(R.id.note_list_fragment_container, mNoteListFragment).commit();
        }

        showDialog(getResources().getString(R.string.string_synchronizing_with_server));
        mNoteListFragment.loginSynchronize();
    }

    private void handleLoginAction() {
        String uId = FirebaseUtil.getCurrentUserId();
        if (uId != null) {
            FirebaseUser currentUser = FirebaseUtil.getCurrentUser();
            User user = new User(uId, currentUser.getEmail(), currentUser.getDisplayName(),
                                 currentUser.getPhotoUrl() == null ? "" : currentUser.getPhotoUrl().toString());

            if (mUserDAO != null) {
                mUserDAO.addEntity(user);
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

//        @Override
//        public void fbSignIn(AccessToken token) {
//
//        }
    };

    private NoteInterface mNoteInterface = new NoteInterface() {
        @Override
        public void onHideFragment(int mode, int position, int noteId) {
            mNoteListFragment.onHideNoteFragment(mode, position, noteId);
        }

    };

    private NoteListInterface mNoteListInterface = new NoteListInterface() {
        @Override
        public void openNote(Note openNote, int position) {
            mNoteFragment.initData(openNote, position);
            mNoteFragment.show();
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
            if (mLoginFragment != null) {
                signOut();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.note_list_fragment_container, mLoginFragment).commit();
            }
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        FirebaseUtil.getAuthInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        if (mNoteListFragment != null) {
            mNoteListFragment.removeAllData();
        }
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
        Log.w(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
    }


}
