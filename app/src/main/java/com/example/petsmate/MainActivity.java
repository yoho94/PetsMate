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


import com.example.petsmate.table.MemberInfo;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

import static com.nhn.android.naverlogin.OAuthLogin.mOAuthLoginHandler;



public class MainActivity extends BaseActivity {

    public static String serverIP = "http://101.101.160.93:8080/DB/";

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
        configBottomNavigation(this, bottomNavigationView);
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