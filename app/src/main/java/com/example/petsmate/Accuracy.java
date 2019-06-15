package com.example.petsmate;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Accuracy extends Activity
{
    protected void onCreate(Bundle savedInstancestate)
    {
        super.onCreate(savedInstancestate);
        setContentView(R.layout.activity_maps_naver);

        View.OnClickListener listener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                {
                    //이곳에 버튼을 눌렀을 때 동작 설정
            }
            }
        };
        Button accuracy =(Button) findViewById(R.id.accuracy);
        accuracy.setOnClickListener(listener);
    }
}

