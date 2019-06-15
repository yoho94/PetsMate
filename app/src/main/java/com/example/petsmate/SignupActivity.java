package com.example.petsmate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    boolean isCheck = true; // TODO 추후 false로 변경 후 중복 확인 하기.
    EditText idEt, passwordEt,password2Et, nameEt, phoneEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_signup);
        Intent intent = getIntent();

        Button mcancelButton=(Button)findViewById(R.id.mcancel_btn);

        idEt = (EditText)findViewById(R.id.mid_input);
        passwordEt = (EditText)findViewById(R.id.mpw_input);
        password2Et = (EditText)findViewById(R.id.mpw2_input);
        nameEt = (EditText)findViewById(R.id.mname_input);
        phoneEt = (EditText)findViewById(R.id.mphon_input);

        SignupPetActivity.petInfoArrayList.clear(); // Pet 회원가입 정보 초기화

        mcancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
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
    }



    public void onPetAdd(View v) { // 펫 추가 버튼
        Intent intent = new Intent(getApplicationContext(), SignupPetActivity.class);
        startActivity(intent);
    }

    public void onIdCheck(View v) {
        // TODO DB select 후 항목 있는지 확인 하기.
        isCheck = true;
    }

    public void onSignup(View v) { // 확인 버튼
        String id, password, password2, name, phone;
        id = idEt.getText().toString();
        password = passwordEt.getText().toString();
        password2 = password2Et.getText().toString();
        name = nameEt.getText().toString();
        phone = phoneEt.getText().toString();

        if(id.isEmpty() || name.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(),"모든 항목을 기입해주세요.",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!Pattern.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$", phone)) { // 핸드폰 정규식 검사.
            Toast.makeText(getApplicationContext(),"올바른 핸드폰 번호가 아닙니다. -없이 기입해주세요.",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(password2)){
            Toast.makeText(getApplicationContext(),"비밀번호와 재확인 비밀번호를 다시 확인해주세요.",Toast.LENGTH_SHORT).show();
            return;
        }


        if(isCheck) {
            MyTask myTask = new MyTask();

            try {
                String result = myTask.execute(id,password,name,phone).get(); // JSP에 get 방식으로 요청
                result = result.trim(); // 앞, 뒤 공백 제거
                Log.i("signup insert result : ", result);
                for(int i=0; i<SignupPetActivity.petInfoArrayList.size(); i++) {


                    String petName = SignupPetActivity.petInfoArrayList.get(i).getName();
                    String petPs = SignupPetActivity.petInfoArrayList.get(i).getPs();
                    String petWeight = SignupPetActivity.petInfoArrayList.get(i).getWeight();
                    String resultPet =  new SignupPetTask().execute(id,petName,petWeight,petPs).get();
                    resultPet = resultPet.trim();
                    Log.i("signupPet"+i+" result : ", resultPet);
                }

                try {
                    int resultInt = Integer.parseInt(result);

                    if (resultInt == 1 || result.equals("1")) {
                        Toast.makeText(getApplicationContext(),"회원가입 완료.",Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),"회원가입 실패.",Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e) {
                    Log.i("signupResultParse", e.toString());
                    Toast.makeText(getApplicationContext(),"회원가입 실패.",Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Log.i("DB SIGNUP ERROR",e.toString());
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
                URL url = new URL("http://106.10.36.239:8080//DB/signUp.jsp");

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

