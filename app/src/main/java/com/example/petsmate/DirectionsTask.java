package com.example.petsmate;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DirectionsTask extends AsyncTask<Double, Void, String> {
    String sendMsg, receiveMsg;
    String api_key = "asFGcIrUvqf2ee3esvh6G6KW5ZT6iWqJ0fYAzy3v";
    String api_id = "krakspsq17";

    @Override
    protected String doInBackground(Double... doubles) {
        try {
            String str;
            sendMsg = "start=" + doubles[0] + "," + doubles[1] + "&goal=" + doubles[2] + "," + doubles[3] + "&option=trafast";

            // 접속할 서버 주소 (이클립스에서 android.jsp 실행시 웹브라우저 주소)
            URL url = new URL("https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving?" + sendMsg);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", api_id);
            conn.setRequestProperty("X-NCP-APIGW-API-KEY", api_key);
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

    public long getDuration(double startLng, double startLat, double desLng, double desLat) {
        long time = 0;

        try {
            String result = new DirectionsTask().execute(startLng, startLat, desLng, desLat).get();
            result = result.trim();

            // 파싱 시작
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONObject("route").getJSONArray("trafast");
            JSONObject summary = jsonArray.getJSONObject(0).getJSONObject("summary");

            time = summary.getLong("duration");


            // 파싱 끝
        } catch (Exception e) {
            Log.e("getDuration", e.toString());
        }



        return time;
    }

    public long getDuration(String startLng, String startLat, String desLng, String desLat) {
        long time = 0;

        double sLng, sLat, dLng, dLat;
        sLng = Double.parseDouble(startLng);
        sLat = Double.parseDouble(startLat);
        dLng = Double.parseDouble(desLng);
        dLat = Double.parseDouble(desLat);

        try {
            String result = new DirectionsTask().execute(sLng, sLat, dLng, dLat).get();
            result = result.trim();
            Log.d("getDurationResult", result);

            // 파싱 시작
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONObject("route").getJSONArray("trafast");
            JSONObject summary = jsonArray.getJSONObject(0).getJSONObject("summary");

            time = summary.getLong("duration");


            // 파싱 끝
        } catch (Exception e) {
            Log.e("getDuration", e.toString());
        }



        return time;
    }
}