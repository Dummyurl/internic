package com.uca.devceargo.internic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.uca.devceargo.internic.R;

public class CooperativeRegister extends AppCompatActivity {

    private ViewSwitcher viewSwitcher;
    private int oscar = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooperative_register);

        Button nextButton;

        viewSwitcher = findViewById(R.id.view_switcher_cooperative);
        nextButton = findViewById(R.id.next_button);

        nextButton.setOnClickListener((View v) -> {
            oscar = 1;
            viewSwitcher.showNext();
        });
    }

    @Override
    public void onBackPressed() {
        if(oscar == 0 || oscar == 2){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }else {
            if (oscar == 1){
                viewSwitcher.showPrevious();
                oscar =2;
            }
        }
    }
}
