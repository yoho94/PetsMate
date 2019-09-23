package com.example.petsmate.task;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;

import com.example.petsmate.BaseActivity;
import com.example.petsmate.R;

import org.jsoup.Connection;

public class mypage_review extends BaseActivity {

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_review);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        configBottomNavigation(this, bottomNavigationView);
    }
}