package com.uca.devceargo.internic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.tumblr.remember.Remember;
import com.uca.devceargo.internic.activities.LoginActivity;
import com.uca.devceargo.internic.activities.RouteMapActivity;
import com.uca.devceargo.internic.adapters.ProgressAdapter;
import com.uca.devceargo.internic.adapters.TypeNewsAdapter;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.entities.News;
import com.uca.devceargo.internic.entities.Route;
import com.uca.devceargo.internic.entities.TypeNews;
import com.uca.devceargo.internic.entities.User;
import com.uca.devceargo.internic.fragments.CooperativeFragment;
import com.uca.devceargo.internic.fragments.ProfileFragment;
import com.uca.devceargo.internic.fragments.ProfileUserFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int COMMON_USER = 1;
    private static final int COOPERATIVE_USER = 2;
    //private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //isAuthenticated();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        defineTypeMenuAndUser();
        initView();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @SuppressLint("InflateParams")
    private void initView(){
        FloatingActionMenu floatingActionMenu = findViewById(R.id.fab);
        /*if(user != null){
            if(user.getTypeUserID() != COOPERATIVE_USER){
                floatingActionMenu.setVisibility(View.INVISIBLE);
            }
        }*/
        initFabButtons();
    }

    /*private void isAuthenticated()
    {
        if(Remember.getString(getString(R.string.key_access_token),"").isEmpty())
        {
            backToSignInActivity();
        }
        String userJson = Remember.getString(getString(R.string.key_user_data),"");
        if(!userJson.isEmpty()){
            Gson gson = new Gson();
            user = gson.fromJson(userJson,User.class);
            Log.i(getString(R.string.message),user.getUserName());
        }else{
            Intent intent = this.getIntent();
            user = (User) intent.getSerializableExtra(LoginActivity.USER_ID);
        }
    }*/

    /**
     * Initialize SignInActivity and finish the current activity
     */

    private void backToSignInActivity()
    {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void initFabButtons(){
        FloatingActionButton newRoute = findViewById(R.id.new_route);
        FloatingActionButton newNews = findViewById(R.id.new_news);

        newRoute.setOnClickListener((View v) ->
            showDialogNewRoute());

        newNews.setOnClickListener((View v) -> showDialogNewNews());
    }

    @SuppressLint("InflateParams")
    private void showDialogNewNews(){
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.dialog_news, null);
        RecyclerView recyclerView = view.findViewById(R.id.list_types_news);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Publicar",(DialogInterface dialog, int id) ->{});
        builder.setNegativeButton("Cancelar", (DialogInterface dialog, int id) ->{});
        builder.setView(view);
        builder.setTitle(getString(R.string.choose_news_type));

        AlertDialog dialog = builder.create();

        if(view.getParent() != null)
            ((ViewGroup)view.getParent()).removeView(view);

        dialog.setView(view);
        getTypeNews(recyclerView, dialog);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((View view1) ->
            uploadNews(dialog));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
    }


    public void uploadNews(AlertDialog dialog){
        News news = new News();
        news.setCooperativeID(4);
        news.setLocationID(1);
        news.setTypeNewID(2);
        EditText title = dialog.findViewById(R.id.standard_title);
        EditText description = dialog.findViewById(R.id.standard_description);

        if(title != null && description != null){
            if(!title.getText().toString().isEmpty() || description.getText().toString().isEmpty()){
                news.setDescription(title.getText().toString());
                news.setTitle(description.getText().toString());
            }else{
                Toast.makeText(getApplicationContext(),getString(R.string.empty_fields_message),Toast.LENGTH_SHORT).show();
            }
        }

        Call<News> call = Api.instance().postNews(news);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(@NonNull Call<News> call,@NonNull Response<News> response) {
                if(response.body() != null) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), getString(R.string.post_news_message), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<News> call, @NonNull Throwable throwable) {
                Toast.makeText(getApplicationContext(), "News error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTypeNews(RecyclerView recyclerView, AlertDialog dialog){
        recyclerView.setAdapter(new ProgressAdapter());
        Call<List<TypeNews>> call = Api.instance().getTypesNews();
        call.enqueue(new Callback<List<TypeNews>>() {
            @Override
            public void onResponse(@NonNull Call<List<TypeNews>> call, @NonNull Response<List<TypeNews>> response) {
                if(response.body() != null){
                    recyclerView.setAdapter(new TypeNewsAdapter(response.body(),dialog));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TypeNews>> call, @NonNull Throwable throwable) {

            }
        });
    }

    @SuppressLint({"InflateParams"})
    private void showDialogNewRoute(){

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.dialog_new_route, null);

        EditText newRouteName = view.findViewById(R.id.new_route_name);
        EditText newRouteDescription = view.findViewById(R.id.new_route_description);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Siguiente",(DialogInterface dialog, int id) ->{});
        builder.setNegativeButton("Cancelar", (DialogInterface dialog, int id) ->
                Toast.makeText(getApplicationContext(),"Nueva recorrido, cancelada", Toast.LENGTH_SHORT).show());

        builder.setView(view);
        builder.setTitle("Nuevo recorrido");

        AlertDialog dialog = builder.create();

        if(view.getParent() != null)
            ((ViewGroup)view.getParent()).removeView(view);

        dialog.setView(view);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((View v) -> {

            Route route = new Route();
            route.setName(newRouteName.getText().toString());
            route.setDescription(newRouteDescription.getText().toString());

            if(route.getName().isEmpty() || route.getDescription().isEmpty()){
                Toast.makeText(getApplicationContext(),getText(R.string.empty_fields_message), Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(getApplicationContext(),RouteMapActivity.class);
                intent.putExtra(Route.TAG, route);
                startActivity(intent);
                dialog.dismiss();
            }
        });
    }

    private void defineTypeMenuAndUser(){

        NavigationView navigationView = findViewById(R.id.nav_view);
        /*if(user != null){

            if(user.getTypeUserID() == COOPERATIVE_USER){
                navigationView.inflateMenu(R.menu.activity_main_drawer);
                showFragment(ProfileFragment.class, R.id.nav_profile);
            }else if(user.getTypeUserID() == COMMON_USER){*/
                navigationView.inflateMenu(R.menu.activity_main_drawer_user);
                showFragment(CooperativeFragment.class,R.id.nav_user_profile);
            //}
            navigationView.setNavigationItemSelectedListener(this);
        //}
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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
                break;
            case  R.id.nav_profile:
                showFragment(ProfileFragment.class, R.id.nav_profile);
                break;
            case  R.id.nav_news:
                //Developing, replace this line
                break;
            case  R.id.nav_complaint:
                //Developing, replace this line
                break;
            case  R.id.nav_setting:
                //Developing, replace this line
                break;
            case  R.id.nav_about_us:
                //Developing, replace this line
                break;
            case  R.id.nav_logout:
                Remember.clear();
                backToSignInActivity();
                break;
            case  R.id.nav_cooperatives:
                //Developing, replace this line
                break;
            case  R.id.nav_favorite:
                //Developing, replace this line
                break;
            default:

                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressWarnings("SameParameterValue")
    private void showFragment(Class fragmentClass, int itemSelected){

        Fragment fragment;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            // Insert the fragment by replacing any existing fragment
            /*if( itemSelected == R.id.nav_profile || itemSelected == R.id.nav_user_profile){
                Bundle args = new Bundle();
                args.putSerializable(LoginActivity.USER_ID,user);
                fragment.setArguments(args);
            }*/
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fl_content, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
}