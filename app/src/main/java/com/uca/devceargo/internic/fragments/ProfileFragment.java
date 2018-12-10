package com.uca.devceargo.internic.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.activities.LoginActivity;
import com.uca.devceargo.internic.activities.RouteMapActivity;
import com.uca.devceargo.internic.adapters.ProfileAdapter;
import com.uca.devceargo.internic.adapters.ProgressAdapter;
import com.uca.devceargo.internic.adapters.TypeNewsAdapter;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.api.ApiMessage;
import com.uca.devceargo.internic.entities.News;
import com.uca.devceargo.internic.entities.Route;
import com.uca.devceargo.internic.entities.TypeNews;
import com.uca.devceargo.internic.entities.User;
import com.uca.devceargo.internic.entities.UserCoperative;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    public static final int REQUEST_CODE = 2905;
    public static final String ROUTE_NAME = "route_name";
    public static final String ROUTE_DESCRIPTION = "route_description";
    public static final String ROUTE_ID = "routeObject";
    private User user;
    private View view;
    private RecyclerView recyclerView;
    FloatingActionButton newRoute;
    FloatingActionButton newNews;
    List<Route> routes;
    ProfileAdapter profileAdapter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        assert getArguments() != null;
        user = (User) getArguments().getSerializable(LoginActivity.USER_ID);
        recyclerView = view.findViewById(R.id.profile_recycler_view);
        routes = new ArrayList<>();//Define List of routes
        getCooperativeAPI();
        initFabButtons();
        return view;
    }

    private void initFabButtons(){
        FloatingActionMenu fab = view.findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        newRoute = view.findViewById(R.id.new_route);
        newNews = view.findViewById(R.id.new_news);

        newRoute.setOnClickListener((View v) ->{
            fab.close(true);
            showDialogNewRoute();
        });

        newNews.setOnClickListener((View v) ->{
            showDialogNewNews();
            fab.close(true);
        });
    }

    @SuppressLint("InflateParams")
    private void showDialogNewNews(){
        assert getContext() != null;
        Context context = this.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.dialog_news, null);
        RecyclerView recyclerView = view.findViewById(R.id.list_types_news);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
        assert getContext() != null;
        Context context = this.getContext();

        if(title != null && description != null){
            if(!title.getText().toString().isEmpty() || description.getText().toString().isEmpty()){
                news.setDescription(title.getText().toString());
                news.setTitle(description.getText().toString());
            }else{
                Toast.makeText(context,getString(R.string.empty_fields_message),Toast.LENGTH_SHORT).show();
            }
        }

        Call<News> call = Api.instance().postNews(news);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(@NonNull Call<News> call,@NonNull Response<News> response) {
                if(response.body() != null) {
                    dialog.dismiss();
                    Toast.makeText(context, getString(R.string.post_news_message), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<News> call, @NonNull Throwable throwable) {
                Toast.makeText(context, "News error", Toast.LENGTH_SHORT).show();
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
        assert getContext() != null;
        Context context = this.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.dialog_new_route, null);

        EditText newRouteName = view.findViewById(R.id.new_route_name);
        EditText newRouteDescription = view.findViewById(R.id.new_route_description);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton("Siguiente",(DialogInterface dialog, int id) ->{});
        builder.setNegativeButton("Cancelar", (DialogInterface dialog, int id) ->
                Toast.makeText(context,"Nueva recorrido, cancelada", Toast.LENGTH_SHORT).show());

        builder.setView(view);
        builder.setTitle("Nuevo recorrido");

        AlertDialog dialog = builder.create();

        if(view.getParent() != null)
            ((ViewGroup)view.getParent()).removeView(view);

        dialog.setView(view);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((View v) -> {

            String name = newRouteName.getText().toString();
            String description = newRouteDescription.getText().toString();

            if(name.isEmpty() || description.isEmpty()){
                Toast.makeText(context,getText(R.string.empty_fields_message), Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(context,RouteMapActivity.class);
                intent.putExtra(ROUTE_NAME, name);
                intent.putExtra(ROUTE_DESCRIPTION, description);
                startActivityForResult(intent, REQUEST_CODE);
                dialog.dismiss();
            }
        });
    }

    @SuppressLint("LogNotTimber")
    private void getCooperativeAPI(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ProgressAdapter());
        String filter = String.format(getString(R.string.user_cooperative_filter_in_request),user.getId());

        Call<List<UserCoperative>> call = Api.instance().getUserCooperative(filter);
        call.enqueue(new Callback<List<UserCoperative>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserCoperative>> call,@NonNull Response<List<UserCoperative>> response) {
                if(response.body() != null){
                    if(response.body().size() > 0){
                        Route route = new Route();
                        profileAdapter = new ProfileAdapter(routes);//Define new profileAdapter Object
                        routes.add(route);
                        recyclerView.setAdapter(profileAdapter);
                        profileAdapter.setCooperative(response.body().get(0).getCooperative());
                        getRoutes(response.body().get(0).getCooperative().getId());
                    }
                }else{
                    showMessageInSnackbar(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserCoperative>> call,@NonNull Throwable throwable) {
                showMessageInSnackbar(ApiMessage.DEFAULT_ERROR_CODE);
                Log.e(getString(R.string.error_message_api),throwable.getMessage());
            }
        });
    }
    private void getRoutes(int cooperativeID){
        String filter = String.format(getString(R.string.cooperative_routes_filter_in_request),
                cooperativeID);
        Call<List<Route>> call = Api.instance().getCooperativeRoutes(filter);
        call.enqueue(new Callback<List<Route>>() {
            @Override
            public void onResponse(@NonNull Call<List<Route>> call,@NonNull Response<List<Route>> response) {
                if(response.body() != null){
                    routes.addAll(response.body());
                    profileAdapter.notifyDataSetChanged();
                }else{
                    //setViewInAdapter();
                    showMessageInSnackbar(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Route>> call, @NonNull Throwable throwable) {
                showMessageInSnackbar(ApiMessage.DEFAULT_ERROR_CODE);
                //setViewInAdapter();
            }
        });
    }

    /*private void setViewInAdapter(){
        profileAdapter.setAdapterSize(profileAdapter.getAdapterSize()+1);
        profileAdapter.setInflateOpc(ProfileAdapter.INFLATE_ERROR_VIEW);
        profileAdapter.notifyItemRangeChanged(0,(routes.size()));
    }*/
    private void showMessageInSnackbar(int code){
        String message = new ApiMessage().sendMessageOfResponseAPI(code,view.getContext());
        Snackbar.make(view,message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @SuppressLint("LogNotTimber")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Bundle bundle = data.getExtras();

            assert bundle != null;
            Route route =(Route) bundle.getSerializable(ROUTE_ID);
            if(route != null){
                routes.add(1,route);
                profileAdapter.notifyItemInserted(1);
                recyclerView.smoothScrollToPosition(1);
            }
            Snackbar.make(view,"Éxito, nueva ruta registrada",
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
}