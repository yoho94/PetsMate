package com.example.petsmate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
    // TEST ! 왼쪽에 보면 파란색으로 바뀜 이게 수정사항이 있으면 파란색불 들어와
    // 이제 이걸 깃허브에 업로드 하려면 ADD는 추가 파일이 생기면 ADD하는거
    // 지금은 그냥 수정만해서 안해도 되긴 됨 ADD를 한 후
    // 액티비티 띄우기
    public void onSignupClick(View v) {
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(intent);
    }
}

