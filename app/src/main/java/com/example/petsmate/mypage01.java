package com.example.petsmate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class mypage01 extends BaseActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage01);

        Button info = (Button) findViewById(R.id.info_btn);
        info.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), mypage04.class);
                        startActivity(intent);
                    }
                }
        );

        Button version = (Button) findViewById(R.id.call_btn);
        version.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), mypage07.class);
                        startActivity(intent);
                    }
                }
        );

        Button call = (Button) findViewById(R.id.call_btn);
        call.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), mypage06.class);
                        startActivity(intent);
                    }
                }
        );
        Button help = (Button) findViewById(R.id.help_btn);
        help.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), mypage03.class);
                        startActivity(intent);
                    }
                }
        );


        Button alarm =(Button)findViewById(R.id.alarm_btn);
        alarm.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        Intent intent= new Intent(getApplicationContext(), mypage02.class);
                        startActivity(intent);
                    }
                }
        );


        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        configBottomNavigation(this, bottomNavigationView);


    }

}
