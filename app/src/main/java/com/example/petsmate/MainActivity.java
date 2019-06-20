package com.example.petsmate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

import static com.nhn.android.naverlogin.OAuthLogin.mOAuthLoginHandler;


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

    public void clear() {
        petInfos.clear();
    }
}

public class MainActivity extends AppCompatActivity {

    private static String OAUTH_CLIENT_ID="7nlmp3_oTZszqdkLgv2u";
    private static String OAUTH_CLIENT_SECRET="e8WuNOzDTl";
    private static String OAUTH_CLIENT_NAME="네이버 아이디로 로그인";

    public static OAuthLoginButton mOAuthLoginButton;
    public static OAuthLogin mOAuthLoginInstance;

    public static Context mContext;

    static MemberInfo memberInfo = new MemberInfo();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //context저장
        mContext = MainActivity.this;

        //1.초기화
        mOAuthLoginInstance = OAuthLogin.getInstance();
        mOAuthLoginInstance.showDevelopersLog(true);
        mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME);

        //2.로그인버튼셋팅
        mOAuthLoginButton = (OAuthLoginButton) findViewById(R.id.buttonNaverLogin);
        mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);
        mOAuthLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOAuthLoginInstance.startOauthLoginActivity(MainActivity.this, mOAuthLoginHandler);
            }
        });


        final Intent intent = getIntent();
        Button loginButton = (Button) findViewById(R.id.login);
        Button userjoinButton = (Button) findViewById(R.id.user_join);
        Button driverjoinButton = (Button) findViewById(R.id.driver_join);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        userjoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });
        driverjoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DriverSignupActivity.class);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView;

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                Class clas = null;

                switch (id) {
                    case R.id.tab_main:
                        clas = MainActivity.class;
                        break;
                    case R.id.tab_msg:
                        break;
                    case R.id.tab_mypage:
                        clas = mypage01.class;
                        break;
                    case R.id.tab_pet:
                        break;
                }

                if(clas != null)
                    startActivity(new Intent(getApplicationContext(), clas));

                return false;
            }
        });

    }
    static private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            //로그인인증성공

            if (success) {
                //사용자정보가져오기
                String accessToken = mOAuthLoginInstance.getAccessToken(mContext);
                String refreshToken = mOAuthLoginInstance.getRefreshToken(mContext);
                long expitesAt = mOAuthLoginInstance.getExpiresAt(mContext);
                String tokenType = mOAuthLoginInstance.getTokenType(mContext);

            } else {
                //실패
                String errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).getCode();
                String errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext);
                Toast.makeText(mContext, "errorCode:" + errorCode + ",errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();

            }
        }
    };

}