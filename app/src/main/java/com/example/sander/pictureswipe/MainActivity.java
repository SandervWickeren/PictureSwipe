package com.example.sander.pictureswipe;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_swipe);

        SwipeSetupFragment fragment = new SwipeSetupFragment();
        replaceFragment(fragment);


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
        if (resultCode == RESULT_OK) {
            Uri pictureUri = data.getData();

            Toast.makeText(MainActivity.this, pictureUri.getPath(), Toast.LENGTH_SHORT).show();

            // Make new bundle with uri and inflate new fragment
            SwipeFragment fragment = new SwipeFragment();
            Bundle bundle = new Bundle();
            bundle.putString("uri", pictureUri.getPath());
            fragment.setArguments(bundle);
            replaceFragment(fragment);
        }
    }
}
