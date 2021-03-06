package com.example.petsmate.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + token);

        // 생성등록된 토큰을 개인 앱서버에 보내 저장해 두었다가 추가 뭔가를 하고 싶으면 할 수 있도록 한다.
//        sendRegistrationToServer(token);
    }

//    private void sendRegistrationToServer(String token) {
//        // Add custom implementation, as needed.
//
//        OkHttpClient client = new OkHttpClient();
//        RequestBody body = new FormBody.Builder()
//                .add("Token", token)
//                .build();
//
//        // 만들어진 토큰을 저장한다!!!
//        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString("token", token);
//        editor.commit();
//        // 여기까지!!!
//
//        //request
//        Request request = new Request.Builder()
//                .url("서버에 전송할 URL")
//                .post(body)
//                .build();
//
//        try {
//            client.newCall(request).execute();
//            Log.d("token", token);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
}