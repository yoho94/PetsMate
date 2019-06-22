package com.example.petsmate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.petsmate.table.PetInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;



public class SignupPetActivity extends BaseActivity {

    static ArrayList<PetInfo> petInfoArrayList = new ArrayList<>();
    EditText name, weight, comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_signup_petadd);

        name = (EditText) findViewById(R.id.petName);
        weight = (EditText) findViewById(R.id.petWeight);
        comment = (EditText) findViewById(R.id.petComment);

        Button cancelButton = (Button) findViewById(R.id.cancel_btn);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignupPetActivity.this);
                builder.setMessage("취소하시겠습니까?");
                builder.setTitle("취소 알림창").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        finish();
                    }

                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.setTitle("종료알림창");
                alert.show();
            }
        });//취소 버튼 누르면 알림창뜨는거.

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        configBottomNavigation(this, bottomNavigationView);

    }

    // 펫 추가 버튼
    public void onAddClick(View v) {
        String nameStr, commentStr, weightStr;
        int weightInt;

        nameStr = name.getText().toString();
        weightStr = weight.getText().toString();
        commentStr = comment.getText().toString();

        if (nameStr == null || weightStr == null) { // TODO int 값 아닌 것 try catch 하기.
            Toast.makeText(getApplicationContext(), "이름 혹은 무게의 값이 비어있습니다.", Toast.LENGTH_LONG);
            return;
        }

        petInfoArrayList.add(new PetInfo(nameStr, weightStr, commentStr));

        String toastStr = String.format("%s(이)가 펫 추가 되었어요!", nameStr);
        Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG);

        name.setText("");
        weight.setText("");
        comment.setText("");

    }

    // 확인 버튼
    public void onOkClick(View v) {
        finish();
    }



}