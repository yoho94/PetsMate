package com.example.petsmate;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ReserveButtons extends AppCompatActivity {
    Button callBT;
    Button reserveBT;
    Button middleBT;
    Button largeBT;
    Button myNumberBT;
    Button anotherNumberBT;
    Button oneWayBT;
    Button roundBT;
    Button finishBT;
    Button articleBT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reserve_main);
        callBT = (Button) findViewById(R.id.reserve_btn_callnow);
        reserveBT = (Button) findViewById(R.id.reserve_btn_booking);
        middleBT = (Button) findViewById(R.id.reserve_btn_medium);
        largeBT = (Button) findViewById(R.id.reserve_btn_large);
        myNumberBT = (Button) findViewById(R.id.reserve_btn_mynum);
        anotherNumberBT = (Button) findViewById(R.id.reserve_btn_anothernum);
        oneWayBT = (Button) findViewById(R.id.reserve_btn_oneway);
        roundBT = (Button) findViewById(R.id.reserve_btn_roundtrip);
        finishBT = (Button) findViewById(R.id.reserve_btn_finish);
        articleBT = (Button) findViewById(R.id.reserve_btn_articledesignate);



    }
    public void RB(View v){
        ColorChange(v.getId());
    }


    private void ColorChange(int id){
        switch(id){
            case R.id.reserve_btn_callnow:
                callBT.setBackgroundColor(Color.parseColor("#F06464"));
                callBT.setTextColor(Color.WHITE);
                reserveBT.setBackgroundColor(Color.parseColor("#FFFFFF"));
                reserveBT.setTextColor(Color.BLACK);
                break; //#F06464
            case R.id.reserve_btn_booking:
                callBT.setBackgroundColor(Color.parseColor("#FFFFFF"));
                callBT.setTextColor(Color.BLACK);
                reserveBT.setBackgroundColor(Color.parseColor("#F06464"));
                reserveBT.setTextColor(Color.WHITE);
                break;
            case R.id.reserve_btn_medium:
                middleBT.setBackgroundColor(Color.parseColor("#F06464"));
                middleBT.setTextColor(Color.WHITE);
                largeBT.setBackgroundColor(Color.parseColor("#FFFFFF"));
                largeBT.setTextColor(Color.BLACK);
                break; //#F06464
            case R.id.reserve_btn_large:
                middleBT.setBackgroundColor(Color.parseColor("#FFFFFF"));
                middleBT.setTextColor(Color.BLACK);
                largeBT.setBackgroundColor(Color.parseColor("#F06464"));
                largeBT.setTextColor(Color.WHITE);
                break;
            case R.id.reserve_btn_mynum:
                myNumberBT.setBackgroundColor(Color.parseColor("#F06464"));
                myNumberBT.setTextColor(Color.WHITE);
                anotherNumberBT.setBackgroundColor(Color.parseColor("#FFFFFF"));
                anotherNumberBT.setTextColor(Color.BLACK);
                break; //#F06464
            case R.id.reserve_btn_anothernum:
                myNumberBT.setBackgroundColor(Color.parseColor("#FFFFFF"));
                myNumberBT.setTextColor(Color.BLACK);
                anotherNumberBT.setBackgroundColor(Color.parseColor("#F06464"));
                anotherNumberBT.setTextColor(Color.WHITE);
                break;
            case R.id.reserve_btn_oneway:
                oneWayBT.setBackgroundColor(Color.parseColor("#F06464"));
                oneWayBT.setTextColor(Color.WHITE);
                roundBT.setBackgroundColor(Color.parseColor("#FFFFFF"));
                roundBT.setTextColor(Color.BLACK);
                break; //#F06464
            case R.id.reserve_btn_roundtrip:
                oneWayBT.setBackgroundColor(Color.parseColor("#FFFFFF"));
                oneWayBT.setTextColor(Color.BLACK);
                roundBT.setBackgroundColor(Color.parseColor("#F06464"));
                reserveBT.setTextColor(Color.WHITE);
                break;
            case R.id.reserve_btn_finish:
                oneWayBT.setBackgroundColor(Color.parseColor("#F06464"));
                oneWayBT.setTextColor(Color.WHITE);
                roundBT.setBackgroundColor(Color.parseColor("#FFFFFF"));
                roundBT.setTextColor(Color.BLACK);
                break; //#F06464
            case R.id.reserve_btn_articledesignate:
                oneWayBT.setBackgroundColor(Color.parseColor("#FFFFFF"));
                oneWayBT.setTextColor(Color.BLACK);
                roundBT.setBackgroundColor(Color.parseColor("#F06464"));
                reserveBT.setTextColor(Color.WHITE);
                break;

        }
    }
}