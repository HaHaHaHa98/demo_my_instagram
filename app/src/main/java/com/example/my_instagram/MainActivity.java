package com.example.my_instagram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.my_instagram.Fragment.HomeFragment;
import com.example.my_instagram.Fragment.NotificationFragment;
import com.example.my_instagram.Fragment.ProfileFragment;
import com.example.my_instagram.Fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_contain, new HomeFragment()).commit();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String publisherId = bundle.getString("publisherId");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileId", publisherId);
            editor.apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_contain,new ProfileFragment()).commit();
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_contain, new HomeFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.nav_search:
                    selectedFragment = new SearchFragment();
                    break;
                case R.id.nav_add:
                    selectedFragment = null;
                    startActivity(new Intent(MainActivity.this, PostActivity.class));
                    break;
                case R.id.nav_heart:
                    selectedFragment = new NotificationFragment();
                    break;
                case R.id.nav_profile:
                    SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileId", FirebaseAuth.getInstance().getUid());
                    editor.apply();
                    selectedFragment = new ProfileFragment();
                    break;
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_contain, selectedFragment).commit();
            }
            return true;
        }
    };
}