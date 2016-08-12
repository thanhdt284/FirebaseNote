package com.stevedao.note.control;

import android.app.FragmentTransaction;
import android.firebase.note.R;
import com.stevedao.note.model.Note;
import com.stevedao.note.view.NoteInterface;
import com.stevedao.note.view.NoteListInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView
        .OnQueryTextListener {
    private DrawerLayout mDrawerLayout;
    private NoteFragment mNoteFragment;
    private NoteListFragment mNoteListFragment;
    private FloatingActionButton mainFAB;
    private int mCurrentMode;
    private String mCurrentTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        mNoteListFragment = new NoteListFragment();
        mNoteListFragment.setInterface(mNoteListInterface);

        if (getFragmentManager().findFragmentById(R.id.note_list_fragment_container) == null) {
            if (findViewById(R.id.note_list_fragment_container) != null) {
                ft.replace(R.id.note_list_fragment_container, mNoteListFragment);
            }
        }
//        mNoteListFragment.show();

        mNoteFragment = new NoteFragment();
        mNoteFragment.setInterface(mNoteInterface);

        if (getFragmentManager().findFragmentById(R.id.note_fragment_container) == null) {
            if (findViewById(R.id.note_fragment_container) != null) {
                ft.setCustomAnimations(R.animator.enter, R.animator.exit);
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
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
}
