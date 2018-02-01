package com.example.sander.pictureswipe;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * Activity that holds login and register fragments. Launched from MainActivity by pressing either
 * login or logout.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set backstack listener
        getSupportFragmentManager().addOnBackStackChangedListener(new BackstackListener());

        // Launch login fragment
        Fragment fragment = new LoginFragment();
        replaceFragment(fragment);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                LoginActivity.this.finish();
        }
        return true;
    }


    /**
     * Function that is used to replace a fragment with another one.
     * @param fragment the fragment that has to be upfront.
     */
    public void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.loginFragmentContainer, fragment, fragment.getClass().getName());
        ft.addToBackStack(null);
        ft.commit();
    }


    /**
     * BackstackListener keeps track of the backpresses. When the backstack is empty, the activity
     * should close.
     */
    private class BackstackListener implements FragmentManager.OnBackStackChangedListener {
        @Override
        public void onBackStackChanged() {
            // If backstack is empty it should close the activity
            int backstackCount = getSupportFragmentManager().getBackStackEntryCount();
            if (backstackCount == 0) {
                LoginActivity.this.finish();
            }
        }
    }
}