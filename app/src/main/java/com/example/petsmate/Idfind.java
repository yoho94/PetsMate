package com.example.petsmate;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Idfind extends BaseActivity {
    EditText phone, name;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.idpw_find);
        Intent intent = getIntent();

        phone = (EditText) findViewById(R.id.findPhone);
        name = (EditText) findViewById(R.id.findName);

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        configBottomNavigation(this, bottomNavigationView);
    }

    public void onNext(View v) {
        String phoneStr, nameStr;
        phoneStr = phone.getText().toString();
        nameStr = name.getText().toString();

        FindTask findTask = new FindTask();

        try {

            String result = findTask.execute(nameStr,phoneStr).get();
            result = result.trim();
            Log.i("find result",result);
            Log.i("name,phone", nameStr + phoneStr);

            if(result.equals("-1")) {
                Toast.makeText(getApplicationContext(),"아이디가 없습니다.",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),"아이디는 : " + result + " 입니다.",Toast.LENGTH_LONG).show();
            }
        }catch (Exception e) {
            Log.i("Find ID ERROR",e.toString());
        }



    }

    class FindTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;

                // 접속할 서버 주소 (이클립스에서 android.jsp 실행시 웹브라우저 주소)
                URL url = new URL(MainActivity.serverIP +"FindId.jsp");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                // 전송할 데이터. GET 방식으로 작성
//                sendMsg = "id=" + strings[0] + "&pw=" + strings[1];
                sendMsg = String.format("name=%s&phone=%s", strings[0],strings[1]);

                osw.write(sendMsg);
                osw.flush();

                //jsp와 통신 성공 시 수행
                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();

                    // jsp에서 보낸 값을 받는 부분
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();
                } else {
                    // 통신 실패
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //jsp로부터 받은 리턴 값
            return receiveMsg;
        }

    }

}
