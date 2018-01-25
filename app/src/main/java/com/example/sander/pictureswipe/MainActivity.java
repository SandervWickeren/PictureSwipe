package com.example.sander.pictureswipe;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set onClickListener for bottomNavigation.
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_swipe);
        bottomNavigationView.setOnNavigationItemSelectedListener(new navigationClicks());

        SwipeSetupFragment fragment = new SwipeSetupFragment();
        replaceFragment(fragment);
        updateNavigation(fragment);

        // Set backstack listener
        getSupportFragmentManager().addOnBackStackChangedListener(new backstackListener());

    }

    /**
     * Launched everytime a fragment transition is necessary
     * @param fragment
     */
    public void replaceFragment (Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        // If fragment not in backstack, create it.
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, fragmentTag);
            // ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_out, R.anim.fade_in);
            ft.addToBackStack(backStateName);
            ft.commit();
        }

        updateNavigation(fragment);
    }

    public void reloadFragment(String tag) {
        // Get Fragment from manager
        Fragment fragment = this.getSupportFragmentManager().findFragmentByTag(tag);

        // Reload the fragment
        final FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ft.detach(fragment);
        ft.attach(fragment);
        ft.commit();
    }

    public void launchImageDialog(String path) {

        // Set up the bundle containing the path.
        Bundle bundle = new Bundle();
        bundle.putString("path", path);

        // Initiate the dialog transaction.
        FullscreenImageFragment fragment = new FullscreenImageFragment();
        fragment.setArguments(bundle);
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        fragment.show(ft, "dialog");
    }


    /**
     * Used to catch data from the gallery. The process is launched
     * from the SwipeSetupFragment.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Class that handles the BottomNavigation clicks.
     */
    private class navigationClicks implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            // Get id
            int id = item.getItemId();

            // Get current selected id
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            int selected_id = bottomNavigationView.getSelectedItemId();

            // Check if already selected
            if (!(Objects.equals(id, selected_id))) {

                // Launch correct fragment
                if (id == R.id.navigation_favorites) {
                    FavoritesFragment fragment = new FavoritesFragment();
                    replaceFragment(fragment);

                } else if (id == R.id.navigation_swipe) {
                    SwipeFragment swipeFragment = new SwipeFragment();
                    String backStateName = swipeFragment.getClass().getName();

                    FragmentManager manager = getSupportFragmentManager();
                    boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

                    if (!fragmentPopped) {
                        SwipeSetupFragment fragment = new SwipeSetupFragment();
                        replaceFragment(fragment);
                    } else {
                        replaceFragment(swipeFragment);
                    }

                    // Check if Swipefragment is active,
                    // otherwise launch SwipeSetup.

                } else if (id == R.id.navigation_account) {
                    /*if (mAuth.getCurrentUser() == null) {
                        Loginfragment fragment = new Loginfragment();
                        replaceFragment(fragment);
                    } else {
                        Profilefragment fragment = new Profilefragment();
                        replaceFragment(fragment);
                    }*/
                    LoginFragment fragment = new LoginFragment();
                    replaceFragment(fragment);
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
     * @param fragment
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

            // All other fragments are from the profile page
        } else {
            bottomNavigationView.setSelectedItemId(R.id.navigation_account);
        }

        // Set the listener back
        bottomNavigationView.setOnNavigationItemSelectedListener(new navigationClicks());
    }

    /**
     * Class that handles navigation to previous fragments. It updates the UI and closes
     * the application when there is no fragment left.
     */
    private class backstackListener implements FragmentManager.OnBackStackChangedListener {
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
