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
import java.util.ArrayList;

class PetInfo {
    private String name;
    private String weight;
    private String ps;

    public PetInfo(String name, String weight, String ps) {
        this.name = name; this.weight = weight; this.ps = ps;
    }

    public PetInfo() {}

    public void setName(String name) {
        this.name = name;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getName() {
        return name;
    }

    public String getWeight() {
        return weight;
    }

    public String getPs() {
        return ps;
    }
}

public class SignupPetActivity extends AppCompatActivity {

    static ArrayList<PetInfo> petInfoArrayList = new ArrayList<>();
    EditText name, weight, comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_signup_petadd);

        name = (EditText) findViewById(R.id.petName);
        weight = (EditText) findViewById(R.id.petWeight);
        comment = (EditText) findViewById(R.id.petComment);
    }

    // 펫 추가 버튼
    public void onAddClick(View v) {
        String nameStr, commentStr, weightStr;
        int weightInt;

        nameStr = name.getText().toString();
        weightStr = weight.getText().toString();
        commentStr = comment.getText().toString();

        if(nameStr == null || weightStr == null) { // TODO int 값 아닌 것 try catch 하기.
            Toast.makeText(getApplicationContext(),"이름 혹은 무게의 값이 비어있습니다.",Toast.LENGTH_LONG);
            return;
        }

        petInfoArrayList.add(new PetInfo(nameStr, weightStr, commentStr));

        String toastStr = String.format("%s(이)가 펫 추가 되었어요!", nameStr);
        Toast.makeText(getApplicationContext(),toastStr,Toast.LENGTH_LONG);

        name.setText("");
        weight.setText("");
        comment.setText("");

    }

    // 확인 버튼
    public void onOkClick(View v) {
        finish();
    }

}

class SignupPetTask extends AsyncTask<String, Void, String> {
    String sendMsg, receiveMsg;

    @Override
    protected String doInBackground(String... strings) {
        try {
            String str;

            // 접속할 서버 주소 (이클립스에서 android.jsp 실행시 웹브라우저 주소)
            URL url = new URL("http://34.66.28.111:8080/DB/signUpPet.jsp");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

            // 전송할 데이터. GET 방식으로 작성
//                sendMsg = "id=" + strings[0] + "&pw=" + strings[1];
            sendMsg = String.format("id=%s&name=%s&weight=%s&ps=%s", strings[0],strings[1],strings[2],strings[3]);

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
