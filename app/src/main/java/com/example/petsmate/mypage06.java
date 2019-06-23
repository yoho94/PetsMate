package com.example.petsmate;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;

public class mypage06 extends BaseActivity {

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage06);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        configBottomNavigation(this, bottomNavigationView);
    }
}
