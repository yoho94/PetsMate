package com.example.petsmate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

class MemberInfo { // 회원의 정보를 저장할 클래스.
    private String id; // 아이디
    private String name; // 이름
    private String phone; // 폰 번호
    private boolean isGuest; // true 손님, false 기사
    private String carNumber; // 차량 번호
    private String carModel; // 차량 모델
    private String carColor; // 차량 색깔
    private boolean isLogin; // true 로그인, false 비 로그인
    private ArrayList<PetInfo> petInfos = new ArrayList<>();

    public ArrayList<PetInfo> getPetInfos() {
        return petInfos;
    }

    public void setPetInfos(ArrayList<PetInfo> petInfos) {
        this.petInfos = petInfos;
    }

    public MemberInfo(String id, String name, String phone, boolean isGuest, String carNumber, String carModel, String carColor, boolean isLogin) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.isGuest = isGuest;
        this.carNumber = carNumber;
        this.carModel = carModel;
        this.carColor = carColor;
        this.isLogin = isLogin;
    }

    public MemberInfo() {
    }

//    public void setGuest(String id, String name, String phone) {
//        this.id = id;
//        this.name = name;
//        this.phone = phone;
//        this.isGuest = true;
//        this.isLogin = true;
//    }
//
//    public void setDriver(String id, String name, String phone) {
//        this.id = id;
//        this.name = name;
//        this.phone = phone;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public void setGuest(boolean guest) {
        isGuest = guest;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public boolean getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }
}

public class MainActivity extends AppCompatActivity {

    static MemberInfo memberInfo = new MemberInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//          TEST
//        EditText test = (EditText) findViewById(R.id.id_input); 로그인의 ID 입력 창
//        String id = test.getText().toString(); // 그것을 String로 바꿈 !
    }


    // 액티비티 띄우기
    public void onSignupClick(View v) { // 회원가입 클릭
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(intent);
    }

    public void onLoginClick(View v) { // 로그인 클릭
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}

