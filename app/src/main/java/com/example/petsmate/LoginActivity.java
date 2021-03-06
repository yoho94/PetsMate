package com.example.petsmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.petsmate.table.PetInfo;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends BaseActivity {
    EditText idET, pwET;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_login);

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        configBottomNavigation(this, bottomNavigationView);

        if(MainActivity.memberInfo.getIsLogin()) { // 로그인시 로그인창 안보고 바로 넘어가기
            if(MainActivity.memberInfo.isGuest()) {
                Intent intent = new Intent(getApplicationContext(), ReserveMain.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), MapsNaverActivity.class);
                startActivity(intent);
            }
            finish();
        }

        idET = (EditText) findViewById(R.id.id_input);
        pwET = (EditText) findViewById(R.id.pw_input);
        Button findButton = (Button)findViewById(R.id.idserch_btn);
        Button pwfindBT=(Button)findViewById(R.id.pwserch_btn);

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Idfind.class);
                startActivity(intent);
            }
        });

        pwfindBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),PwfindActivity.class);
                startActivity(intent);
            }
        });

        // 푸시 알림 테스트.
//        String id = "test";
//        String title  = "제목입니다.";
//        String body = "본문입니다.";
//        String type = "driver"; // 기사일 경우.
//
//        new PushMsgTask().execute(id, title, body, ""); // 손님일 경우
//        new PushMsgTask().execute(id, title, body, type); // 기사일 경우



    }

    public void onLogin(View v) {
        String id, password;
        id = idET.getText().toString();
        password = pwET.getText().toString();

        if(id == null || password == null) {
            Toast.makeText(getApplicationContext(),"아이디 혹은 패스워드 값이 비어있습니다.",Toast.LENGTH_SHORT).show();
        } else {
            LoginTask loginTask = new LoginTask();
            LoginPetTask loginPetTask = new LoginPetTask();

            try {
                String result = (loginTask.execute(id, password).get());
                String resultPet = loginPetTask.execute(id).get();
                result = result.trim(); // 앞, 뒤 공백 제거
                resultPet = resultPet.trim();
                Log.i("Login result : ", result);
                Log.i("Login petReslut", resultPet);



                if(result.equals("0")) {
                    Toast.makeText(getApplicationContext(),"아이디 혹은 비밀번호를 틀렸어요.",Toast.LENGTH_SHORT).show();
                } else {
                    String[] info = result.split("/");
                    if(info[1].equals("error")) {
                        Toast.makeText(getApplicationContext(),"서버 DB 에러.",Toast.LENGTH_SHORT).show();
                        Log.i("LoginERROR", info[0]);
                    } else if(info[0].equalsIgnoreCase("GUEST")){
                        Toast.makeText(getApplicationContext(), "로그인 성공 !", Toast.LENGTH_SHORT).show();

                        // 회원 정보 초기화
                        MainActivity.memberInfo.clear();

                        // 회원 정보
                        MainActivity.memberInfo.setGuest(true);
                        MainActivity.memberInfo.setName(info[1]);
                        MainActivity.memberInfo.setPhone(info[2]);
                        MainActivity.memberInfo.setId(id);
                        MainActivity.memberInfo.setIsLogin(true);

                        // 펫 정보
                        if(!(resultPet.equals("")) && !(resultPet == null)) {
                            String[] petInfo = resultPet.split("@end");

                            for (int i = 0; i < petInfo.length; i++) {
                                String petPs = null;
                                String[] petStr = petInfo[i].split("/");
                                String petCode = petStr[0];
                                String petName = petStr[1];
                                String petWeight = petStr[2];
                                if (petStr.length == 4)
                                    petPs = petStr[3];

                                MainActivity.memberInfo.getPetInfos().add(new PetInfo(petCode, petName, petWeight, petPs));
                            }
                        }

                        //파이어베이스 푸시알림 토큰 보내기

                        // 저장해놨던 토큰값 가져와서 전달
                        String token = FirebaseInstanceId.getInstance().getToken();
                        Log.d("토큰", token);


                        new TokenTask().execute(id, token, "guest");

                        Intent intent = new Intent(getApplicationContext(), ReserveMain.class);
                        startActivity(intent);

                        finish();
                    } else if(info[0].equalsIgnoreCase("DRIVER")) {
                        Toast.makeText(getApplicationContext(), "로그인 성공 !", Toast.LENGTH_SHORT).show();
                        // 회원 정보 초기화
                        MainActivity.memberInfo.clear();

                        // 회원 정보
                        MainActivity.memberInfo.setGuest(false);
                        MainActivity.memberInfo.setName(info[1]);
                        MainActivity.memberInfo.setPhone(info[2]);
                        MainActivity.memberInfo.setCarNumber(info[3]);
                        MainActivity.memberInfo.setId(id);
                        MainActivity.memberInfo.setIsLogin(true);

                        // 저장해놨던 토큰값 가져와서 전달
                        String token = FirebaseInstanceId.getInstance().getToken();
                        Log.d("토큰", token);


                        new TokenTask().execute(id, token, "driver");

                        Intent intent = new Intent(getApplicationContext(), MapsNaverActivity.class);
                        startActivity(intent);

                        finish();
                    }

                }
            } catch(Exception e) {
                Log.i("DB LOGIN ERROR",e.toString());
            }
        }

    }

    class TokenTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;

                // 접속할 서버 주소 (이클립스에서 android.jsp 실행시 웹브라우저 주소)
                URL url = new URL(MainActivity.serverIP +"token.jsp");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                // 전송할 데이터. GET 방식으로 작성
//                sendMsg = "id=" + strings[0] + "&pw=" + strings[1];
                sendMsg = String.format("id=%s&token=%s&type=%s", strings[0],strings[1], strings[2]);

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


    class LoginTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;

                // 접속할 서버 주소 (이클립스에서 android.jsp 실행시 웹브라우저 주소)
                URL url = new URL(MainActivity.serverIP +"Login.jsp");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                // 전송할 데이터. GET 방식으로 작성
//                sendMsg = "id=" + strings[0] + "&pw=" + strings[1];
                sendMsg = String.format("id=%s&password=%s", strings[0],strings[1]);

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

    class LoginPetTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;

                // 접속할 서버 주소 (이클립스에서 android.jsp 실행시 웹브라우저 주소)
                URL url = new URL(MainActivity.serverIP +"LoginPet.jsp");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                // 전송할 데이터. GET 방식으로 작성
//                sendMsg = "id=" + strings[0] + "&pw=" + strings[1];
                sendMsg = String.format("id=%s", strings[0]);

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
