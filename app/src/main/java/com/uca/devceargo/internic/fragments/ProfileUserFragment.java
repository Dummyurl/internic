package com.uca.devceargo.internic.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.activities.LoginActivity;
import com.uca.devceargo.internic.adapters.CommentAdapter;
import com.uca.devceargo.internic.adapters.ProgressAdapter;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.entities.Comment;
import com.uca.devceargo.internic.entities.User;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileUserFragment extends Fragment {
    private User user;
    private RecyclerView recyclerView;

    public ProfileUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        assert getArguments() != null;
        user = (User) getArguments().getSerializable(LoginActivity.USER_ID);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        recyclerView = view.findViewById(R.id.profile_recycler_view);
        getComments();
        return view;
    }

    @SuppressLint("LogNotTimber")
    private void getComments(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ProgressAdapter());
        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comments.add(comment);
        String filter = String.format(getString(R.string.user_comments_filter_in_request),user.getId());

        Call<List<Comment>> call = Api.instance().getCommentUser(filter);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Comment>> call, @NonNull Response<List<Comment>> response) {
                System.out.print(call.request().url());
                if(response.body() != null){
                    if(response.body().size() > 0){
                        comments.addAll(response.body());
                    }else{
                        Log.e(getString(R.string.message),response.message()+" "+response.code());
                        //Empty message
                    }
                }else{
                    Log.e(getString(R.string.message),response.message()+" "+response.code());
                }
                Log.e(getString(R.string.message),response.message()+" "+response.code());
                recyclerView.setAdapter(new CommentAdapter(comments,user));
            }

            @Override
            public void onFailure(@NonNull Call<List<Comment>> call,@NonNull Throwable throwable) {
                Log.e(getString(R.string.error_message_api),throwable.getMessage());
            }
        });
    }
}