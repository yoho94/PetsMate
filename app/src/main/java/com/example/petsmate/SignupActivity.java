package com.example.petsmate;

import android.os.AsyncTask;
import android.os.Bundle;
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

public class SignupActivity extends AppCompatActivity {

    boolean isCheck = true; // TODO 추후 false로 변경 후 중복 확인 하기.
    EditText idEt, passwordEt,password2Et, nameEt, phoneEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_signup);

        idEt = (EditText)findViewById(R.id.mid_input);
        passwordEt = (EditText)findViewById(R.id.mpw_input);
        password2Et = (EditText)findViewById(R.id.mpw2_input);
        nameEt = (EditText)findViewById(R.id.mname_input);
        phoneEt = (EditText)findViewById(R.id.mphon_input);
    }

    public void onIdCheck(View v) {
        // TODO DB select 후 항목 있는지 확인 하기.
        isCheck = true;
    }

    public void onSignup(View v) {
        String id, password, password2, name, phone;
        id = idEt.getText().toString();
        password = passwordEt.getText().toString();
        password2 = password2Et.getText().toString();
        name = nameEt.getText().toString();
        phone = phoneEt.getText().toString();

        if(!password.equals(password2)){
            Toast.makeText(getApplicationContext(),"비밀번호와 재확인 비밀번호를 다시 확인해주세요.",Toast.LENGTH_SHORT).show();
            return;
        }


        if(isCheck) {
            MyTask myTask = new MyTask();

            try {
                String result = myTask.execute(id,password,name,phone).get();
                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
//                finish();
            } catch (Exception e) {
                Log.i("DB ERROR",e.toString());
            }

        } else {
            Toast.makeText(getApplicationContext(),"아이디 중복 검사를 해주세요.",Toast.LENGTH_SHORT).show();
        }
    }


    class MyTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;

                // 접속할 서버 주소 (이클립스에서 android.jsp 실행시 웹브라우저 주소)
                URL url = new URL("http://34.66.28.111:8080/DB/signUp.jsp");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                // 전송할 데이터. GET 방식으로 작성
//                sendMsg = "id=" + strings[0] + "&pw=" + strings[1];
                sendMsg = String.format("id=%s&password=%s&name=%s&phone=%s", strings[0],strings[1],strings[2],strings[3]);

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
