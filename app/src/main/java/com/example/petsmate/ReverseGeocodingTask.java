package com.example.petsmate;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

class ReverseGeocodingTask extends AsyncTask<Double, Void, String> {
    String sendMsg, receiveMsg;
    String api_key = "asFGcIrUvqf2ee3esvh6G6KW5ZT6iWqJ0fYAzy3v";
    String api_id = "krakspsq17";
    @Override
    protected String doInBackground(Double... doubles) {
        try {
            String str;
            sendMsg ="request=coordsToaddr&coords="+doubles[0]+","+doubles[1]+"&sourcecrs=epsg:4326&output=json&orders=legalcode,admcode,addr";

            // 접속할 서버 주소 (이클립스에서 android.jsp 실행시 웹브라우저 주소)
            URL url = new URL("https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?"+sendMsg);
//            URL url = new URL("https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?request=coordsToaddr&coords=129.1133567,35.2982640&sourcecrs=epsg:4326&output=json&orders=legalcode,admcode");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "krakspsq17");
            conn.setRequestProperty("X-NCP-APIGW-API-KEY", "asFGcIrUvqf2ee3esvh6G6KW5ZT6iWqJ0fYAzy3v");
//            conn.setRequestMethod("GET");
//            conn.setDoInput(true);
//                conn.setDoOutput(true);
//                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            // 전송할 데이터. GET 방식으로 작성
//                sendMsg = "id=" + strings[0] + "&pw=" + strings[1];


//                osw.write(sendMsg);
//                osw.flush();

            InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuffer buffer = new StringBuffer();

            // jsp에서 보낸 값을 받는 부분
            while ((str = reader.readLine()) != null) {
                buffer.append(str);
            }
            receiveMsg = buffer.toString();

//            Log.d("통신 코드", conn.getResponseCode() + "");
//            Log.d("통신 메시지", conn.getResponseMessage());


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //jsp로부터 받은 리턴 값
        return receiveMsg;
    }
}