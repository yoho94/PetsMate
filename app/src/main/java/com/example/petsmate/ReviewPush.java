package com.example.petsmate;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;

public class ReviewPush extends BaseActivity {

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_driver);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        configBottomNavigation(this, bottomNavigationView);

}}