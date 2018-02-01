package com.example.sander.pictureswipe;


import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;

/**
 * Activity that holds all fragments except for login and register. It contains functions
 * that are used by various fragments.
 */
public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set onClickListener for bottomNavigation.
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_swipe);
        bottomNavigationView.setOnNavigationItemSelectedListener(new NavigationClicks());

        // Launch the SwipeSetupFragment first.
        SwipeSetupFragment fragment = new SwipeSetupFragment();
        replaceFragment(fragment);
        updateNavigation(fragment);

        // Set BackStack listener
        getSupportFragmentManager().addOnBackStackChangedListener(new BackstackListener());

        // Get Firebase reference
        mAuth = FirebaseAuth.getInstance();
    }


    /**
     * Launched everytime a fragment transition is necessary. It handles this transaction.
     * @param fragment fragment that has to be on top.
     */
    public void replaceFragment (Fragment fragment) {
        // Get fragment name.
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;

        // Check if fragment is in the BackStack.
        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        // If fragment not in the BackStack, create it.
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, fragmentTag);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
        // Update the BottomNavigationView.
        updateNavigation(fragment);
    }


    /**
     * Function that is used to reload a fragment. Often used if the fragment needs a forced
     * redraw.
     * @param tag of the fragment that needs to be reloaded.
     */
    public void reloadFragment(String tag) {
        // Find fragment by tag.
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);

        // Only if the fragment is currently active and visible.
        if (fragment != null && fragment.isVisible()) {
            // Reload the fragment
            final FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
            ft.detach(fragment);
            ft.attach(fragment);
            ft.commit();
        }
    }


    /**
     * Used to launch DialogFragments
     * @param fragment DialogFragment that has to be launched
     * @param path that to be added as a bundle value.
     */
    public void launchDialog(DialogFragment fragment, String path) {
        // Set up the bundle containing the path.
        Bundle bundle = new Bundle();
        bundle.putString("path", path);

        // Initiate the dialog transaction.
        fragment.setArguments(bundle);
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        fragment.show(ft, "dialog");
    }


    /**
     * Function that checks if the user is logged in and returns the text accordingly. It is used
     * for displaying the correct ButtonText in the ActionBar.
     * @return the correct display string.
     */
    public String logText() {
        if (mAuth.getCurrentUser() != null) {
            return "Logout";
        } else {
            return "Login";
        }
    }


    /**
     * Handles the action when the user clicks on the login / logout button in the ActionBar.
     */
    public void logAction() {
        if (mAuth.getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }


    /**
     * Used to catch data from the gallery. The process is launched from the SwipeSetupFragment.
     * Necessary to be able to process the data at fragment level.
     * @param requestCode Code used to launch the gallery activity.
     * @param resultCode Code that gives if it was successful or not.
     * @param data Retrieved from the activity (the selected image).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Class that handles the BottomNavigation clicks.
     */
    private class NavigationClicks implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // Get id
            int id = item.getItemId();

            // Get current selected id
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            int selected_id = bottomNavigationView.getSelectedItemId();

            // Check if already selected
            if (!(Objects.equals(id, selected_id))) {

                // Launch correct fragment based on the selected navigation item id.
                if (id == R.id.navigation_favorites) {
                    FavoritesFragment fragment = new FavoritesFragment();
                    replaceFragment(fragment);

                } else if (id == R.id.navigation_swipe) {

                    // Get name from SwipeFragment.
                    SwipeFragment swipeFragment = new SwipeFragment();
                    String backStateName = swipeFragment.getClass().getName();

                    // Check if the name is in the BackStack.
                    FragmentManager manager = getSupportFragmentManager();
                    boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

                    // Launch either the setup fragment or the swipe fragment based on if the
                    // Swipe fragment was already active.
                    if (!fragmentPopped) {
                        SwipeSetupFragment fragment = new SwipeSetupFragment();
                        replaceFragment(fragment);
                    } else {
                        replaceFragment(swipeFragment);
                    }

                } else if (id == R.id.navigation_bin) {
                    BinFragment fragment = new BinFragment();
                    replaceFragment(fragment);
                }
            }
            // Visually show the selected item;
            return true;
        }
    }


    /**
     * Functions that makes sure that the current inflated fragment
     * represents the correct selection at the BottomNavigationView.
     * @param fragment the fragment that is now active.
     */
    private void updateNavigation (Fragment fragment) {
        // Get fragment class name
        String name = fragment.getClass().getName();

        // Get view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Remove Listener to prevent infinite looping.
        bottomNavigationView.setOnNavigationItemSelectedListener(null);

        // Change selected item based on current fragment
        if (Objects.equals(name, SwipeSetupFragment.class.getName()))  {
            bottomNavigationView.setSelectedItemId(R.id.navigation_swipe);
        } else if (Objects.equals(name, BinFragment.class.getName())) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_bin);
        } else if (Objects.equals(name, FavoritesFragment.class.getName())) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_favorites);
        } else if (Objects.equals(name, SwipeFragment.class.getName())) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_swipe);
        }

        // Set the listener back
        bottomNavigationView.setOnNavigationItemSelectedListener(new NavigationClicks());
    }


    /**
     * Class that handles navigation to previous fragments. It updates the UI and closes
     * the application when there is no fragment left.
     */
    private class BackstackListener implements FragmentManager.OnBackStackChangedListener {
        @Override
        public void onBackStackChanged() {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment != null) {
                updateNavigation(fragment);
            }

            // If backstack is empty it should close the app
            int backstackCount = getSupportFragmentManager().getBackStackEntryCount();
            if (backstackCount == 0) {
                MainActivity.this.finish();
            }

        }
    }
}