package com.uca.devceargo.internic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tumblr.remember.Remember;
import com.uca.devceargo.internic.activities.LoginActivity;
import com.google.gson.Gson;
import com.uca.devceargo.internic.classes.LocalGlide;
import com.uca.devceargo.internic.entities.User;
import com.uca.devceargo.internic.fragments.ComplaintFragment;
import com.uca.devceargo.internic.fragments.CooperativeFragment;
import com.uca.devceargo.internic.fragments.NewsFragment;
import com.uca.devceargo.internic.fragments.ProfileFragment;
import com.uca.devceargo.internic.fragments.ProfileUserFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int COMMON_USER = 1;
    private static final int COOPERATIVE_USER = 2;
    private User user;
    private int cooperativeID;
    private int itemSelect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isAuthenticated();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        cooperativeID = 0;
        itemSelect = 0;
        defineTypeMenuAndUser();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void isAuthenticated()
    {
        if(Remember.getString(getString(R.string.key_access_token),"").isEmpty())
        {
            backToSignInActivity();
        }
        String userJson = Remember.getString(getString(R.string.key_user_data),"");
        if(!userJson.isEmpty()){
            Gson gson = new Gson();
            user = gson.fromJson(userJson,User.class);
        }else{
            Intent intent = this.getIntent();
            user = (User) intent.getSerializableExtra(LoginActivity.USER_ID);

        }
    }

    /**
     * Initialize SignInActivity and finish the current activity
     */

    private void backToSignInActivity()
    {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void defineTypeMenuAndUser(){

        if(user != null){
            NavigationView navigationView = findViewById(R.id.nav_view);
            if(user.getTypeUserID() == COOPERATIVE_USER){
                navigationView.inflateMenu(R.menu.activity_main_drawer);
                showFragment(ProfileFragment.class, R.id.nav_profile);
            }else if(user.getTypeUserID() == COMMON_USER){
                navigationView.inflateMenu(R.menu.activity_main_drawer_user);

                showFragment(ProfileUserFragment.class,R.id.nav_user_profile);
            }
            navigationView.setNavigationItemSelectedListener(this);
            initUserData(navigationView.getHeaderView(0));
        }
    }

    private void initUserData(View view) {
        ImageView profileImage =  view.findViewById(R.id.nav_profile_image);
        TextView profileUserName = view.findViewById(R.id.nav_user_name);
        TextView profileEmail = view.findViewById(R.id.nav_user_email);

        new LocalGlide().loadImage(profileImage,user.getUrlImage(),LocalGlide.CIRCLE_CROP);
        profileEmail.setText(user.getEmail());
        profileUserName.setText(user.getUserName());
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(itemSelect == R.id.nav_profile  && user.getTypeUserID() == COOPERATIVE_USER){
            showFragment(ProfileFragment.class,R.id.nav_profile);
        } else if(itemSelect == R.id.nav_user_profile  && user.getTypeUserID() == COMMON_USER){
            showFragment(ProfileUserFragment.class,R.id.nav_user_profile);
        }else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){

            case  R.id.nav_user_profile:
                if(! (R.id.nav_user_profile == itemSelect || R.id.nav_profile  == itemSelect)){
                    showFragment(ProfileUserFragment.class,R.id.nav_user_profile);
                }
                break;
            case  R.id.nav_profile:
                if(! (R.id.nav_user_profile == itemSelect || R.id.nav_profile  == itemSelect)){
                    showFragment(ProfileFragment.class, R.id.nav_profile);
                }
                break;
            case  R.id.nav_news:
                showFragment(NewsFragment.class,R.id.nav_news);
                break;
            case  R.id.nav_complaint:
                System.out.println();
                showFragment(ComplaintFragment.class, R.id.nav_complaint);
                break;
            case  R.id.nav_cooperative_news:
                showFragment(NewsFragment.class,R.id.nav_news);
                break;
            case  R.id.nav_about_us:
                //Developing, replace this line
                break;
            case  R.id.nav_logout:
                Remember.clear();
                backToSignInActivity();
                break;
            case  R.id.nav_cooperatives:
                showFragment(CooperativeFragment.class,R.id.nav_cooperatives);
                break;
            case  R.id.nav_favorite:
                //Developing, replace this line
                break;
            default:
                break;
        }
        this.itemSelect = id;
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressWarnings("SameParameterValue")
    private void showFragment(Class fragmentClass, int itemSelected){

        Fragment fragment;
        try {
            Bundle args = new Bundle();
            fragment = (Fragment) fragmentClass.newInstance();
            // Insert the fragment by replacing any existing fragment
            if( itemSelected == R.id.nav_profile || itemSelected == R.id.nav_user_profile){

                if(itemSelected == R.id.nav_profile)
                    ((ProfileFragment) fragment).setCooperativeID(cooperativeID);

                args.putSerializable(LoginActivity.USER_ID,user);
                fragment.setArguments(args);
            }

            if(itemSelected == R.id.nav_news ||
                    itemSelected == R.id.nav_complaint && user.getTypeUserID() != COMMON_USER){

                args.putInt(NewsFragment.COOPERATIVE_ID,cooperativeID);
                fragment.setArguments(args);
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fl_content, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
}