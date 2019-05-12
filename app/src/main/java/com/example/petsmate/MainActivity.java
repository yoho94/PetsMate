package com.example.petsmate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//          TEST
//        EditText test = (EditText) findViewById(R.id.id_input); 로그인의 ID 입력 창
//        String id = test.getText().toString(); // 그것을 String로 바꿈 !
    }

    // 액티비티 띄우기
    public void onSignupClick(View v) {
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(intent);
    }
}

