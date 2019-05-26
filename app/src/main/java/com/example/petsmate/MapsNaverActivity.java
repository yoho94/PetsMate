package com.example.petsmate;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.Executor;

public class MapsNaverActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapFragment mapFragment;
    private NaverMap naverMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private ArrayList<CallTable> callTables;
    boolean isFirst = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_naver);

        callTables = new ArrayList<>();


        final Handler handler = new Handler(Looper.getMainLooper());
        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        getCallTable();

                        if(naverMap != null) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    setCallMaker();
                                }
                            });
                        }

                        Thread.sleep(10000);
                    }
                } catch (Exception e) {
                    Log.i("getCallTableThread", e.toString());
                }
            }
        }.start();


        // naver 지도 설정 시작
        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.driver_maps);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.driver_maps, mapFragment).commit();
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission_group.LOCATION}, 0);

        mapFragment.getMapAsync(this); // NaverMap 객체 얻기.
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        // naver 지도 설정 종료

    }

    private void setNaverMap(NaverMap naverMap) {
        this.naverMap = naverMap;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        naverMap.setLocationSource(locationSource);
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
        setNaverMap(naverMap);

        int permssionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permssionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location gps = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double first_Latitude = gps.getLatitude();
            double first_Longitude = gps.getLongitude();

            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(first_Latitude, first_Longitude));
            naverMap.moveCamera(cameraUpdate); // 현재 위치로 지도 옮기기.
        }
    }

    private void setCallMaker() {
        for (int i = 0; i < callTables.size(); i++) {
            CallTable callTable = callTables.get(i);

            if (callTable.getCode() == 0) {
                Marker marker = new Marker();
                LatLng latLng = new LatLng(Double.parseDouble(callTable.startPlace.getLat()), Double.parseDouble(callTable.startPlace.getLon()));
                Log.i("Marker", latLng.toString());

                marker.setIcon(MarkerIcons.BLACK); // 마커 아이콘을 블랙으로
                marker.setIconTintColor(Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));  // 마커 색상 랜덤
                marker.setAlpha(0.5f); // 마커 반투명으로 설정
                marker.setCaptionText("목적지 : " + callTable.destinationPlace.getRoadAddress()); // 마커 캡션 목적지 표기

                marker.setPosition(latLng);  // 마커 위치 설정
                marker.setMap(naverMap);    // 마커 맵에 표기
            }
        }
    }

    class CallTable {
        private Place startPlace, destinationPlace;
        private int serialNumber, code;
        private String guestId, driverId, ps;
        private boolean isCall, isShuttle;
        private Timestamp startTime, destinationTime, generateTime;

        public CallTable() {
        }

        public CallTable(Place startPlace, Place destinationPlace, int serialNumber, int code, String guestId, String driverId, String ps, boolean isCall, boolean isShuttle) {
            this.startPlace = startPlace;
            this.destinationPlace = destinationPlace;
            this.serialNumber = serialNumber;
            this.code = code;
            this.guestId = guestId;
            this.driverId = driverId;
            this.ps = ps;
            this.isCall = isCall;
            this.isShuttle = isShuttle;
        }

        public CallTable(Place startPlace, Place destinationPlace, int serialNumber, int code, String guestId, String driverId, boolean isCall) {
            this.startPlace = startPlace;
            this.destinationPlace = destinationPlace;
            this.serialNumber = serialNumber;
            this.code = code;
            this.guestId = guestId;
            this.driverId = driverId;
            this.isCall = isCall;
        }

        public Timestamp getStartTime() {
            return startTime;
        }

        public void setStartTime(Timestamp startTime) {
            this.startTime = startTime;
        }

        public Timestamp getDestinationTime() {
            return destinationTime;
        }

        public void setDestinationTime(Timestamp destinationTime) {
            this.destinationTime = destinationTime;
        }

        public Timestamp getGenerateTime() {
            return generateTime;
        }

        public void setGenerateTime(Timestamp generateTime) {
            this.generateTime = generateTime;
        }

        public Place getStartPlace() {
            return startPlace;
        }

        public void setStartPlace(Place startPlace) {
            this.startPlace = startPlace;
        }

        public Place getDestinationPlace() {
            return destinationPlace;
        }

        public void setDestinationPlace(Place destinationPlace) {
            this.destinationPlace = destinationPlace;
        }

        public int getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(int serialNumber) {
            this.serialNumber = serialNumber;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getGuestId() {
            return guestId;
        }

        public void setGuestId(String guestId) {
            this.guestId = guestId;
        }

        public String getDriverId() {
            return driverId;
        }

        public void setDriverId(String driverId) {
            this.driverId = driverId;
        }

        public String getPs() {
            return ps;
        }

        public void setPs(String ps) {
            this.ps = ps;
        }

        public boolean isCall() {
            return isCall;
        }

        public void setCall(boolean call) {
            isCall = call;
        }

        public boolean isShuttle() {
            return isShuttle;
        }

        public void setShuttle(boolean shuttle) {
            isShuttle = shuttle;
        }
    }


    private void getCallTable() {
        try {
            String callTableTaskResult = new CallTableTask().execute(MainActivity.memberInfo.getId()).get();
            callTableTaskResult = callTableTaskResult.trim();
            Log.i("callTableTaskResult", callTableTaskResult);
            callTables.clear();

            String[] callTableStr = callTableTaskResult.split("@@");

            for (int i = 0; i < callTableStr.length; i++) {
                Place startPlace = new Place(), desPlace = new Place();
                CallTable callTable = new CallTable();
                String[] callTableField = callTableStr[i].split("//");
                int serialNumber = Integer.parseInt(callTableField[0]);
                String guestId = callTableField[1];
                String driverId = callTableField[2];
                boolean isCall = Boolean.parseBoolean(callTableField[3]);
                int code = Integer.parseInt(callTableField[4]);
                startPlace.setLat(callTableField[5]);
                startPlace.setLon(callTableField[6]);
                desPlace.setLat(callTableField[7]);
                desPlace.setLon(callTableField[8]);
                Timestamp startTime = Timestamp.valueOf(callTableField[9]);
                Timestamp desTime = Timestamp.valueOf(callTableField[10]);
                Timestamp genTime = Timestamp.valueOf(callTableField[11]);
                boolean isShuttle = Boolean.parseBoolean(callTableField[12]);
                desPlace.setName(callTableField[13]);
                desPlace.setRoadAddress(callTableField[14]);
                startPlace.setName(callTableField[15]);
                startPlace.setRoadAddress(callTableField[16]);

                callTable.setSerialNumber(serialNumber);
                callTable.setGuestId(guestId);
                callTable.setDriverId(driverId);
                callTable.setCall(isCall);
                callTable.setCode(code);
                callTable.setStartTime(startTime);
                callTable.setDestinationTime(desTime);
                callTable.setGenerateTime(genTime);
                callTable.setShuttle(isShuttle);
                callTable.setStartPlace(startPlace);
                callTable.setDestinationPlace(desPlace);

                callTables.add(callTable);
            }


//                    Thread.sleep(10000); // 10초에 한번 갱신
        } catch (Exception e) {
            Log.i("getCallTable", e.toString());
        }
    }

    class CallThread implements Runnable {
        @Override
        public void run() {
//            while(true) {
            try {
                String callTableTaskResult = new CallTableTask().execute(MainActivity.memberInfo.getId()).get();
                callTableTaskResult = callTableTaskResult.trim();
                Log.i("callTableTaskResult", callTableTaskResult);
                callTables.clear();
                Place startPlace = new Place(), desPlace = new Place();
                CallTable callTable = new CallTable();

                String[] callTableStr = callTableTaskResult.split("@@");
                for (int i = 0; i < callTableStr.length; i++) {
                    String[] callTableField = callTableStr[i].split("//");
                    int serialNumber = Integer.parseInt(callTableField[0]);
                    String guestId = callTableField[1];
                    String driverId = callTableField[2];
                    boolean isCall = Boolean.parseBoolean(callTableField[3]);
                    int code = Integer.parseInt(callTableField[4]);
                    startPlace.setLat(callTableField[5]);
                    startPlace.setLon(callTableField[6]);
                    desPlace.setLat(callTableField[7]);
                    desPlace.setLon(callTableField[8]);
                    Timestamp startTime = Timestamp.valueOf(callTableField[9]);
                    Timestamp desTime = Timestamp.valueOf(callTableField[10]);
                    Timestamp genTime = Timestamp.valueOf(callTableField[11]);
                    boolean isShuttle = Boolean.parseBoolean(callTableField[12]);
                    desPlace.setName(callTableField[13]);
                    desPlace.setRoadAddress(callTableField[14]);
                    startPlace.setName(callTableField[15]);
                    startPlace.setRoadAddress(callTableField[16]);

                    callTable.setSerialNumber(serialNumber);
                    callTable.setGuestId(guestId);
                    callTable.setDriverId(driverId);
                    callTable.setCall(isCall);
                    callTable.setCode(code);
                    callTable.setStartTime(startTime);
                    callTable.setDestinationTime(desTime);
                    callTable.setGenerateTime(genTime);
                    callTable.setShuttle(isShuttle);
                    callTable.setStartPlace(startPlace);
                    callTable.setDestinationPlace(desPlace);

                    callTables.add(callTable);
                }


//                    Thread.sleep(10000); // 10초에 한번 갱신
            } catch (Exception e) {

            }
//            }
        }
    }

    class CallTableTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;

                // 접속할 서버 주소 (이클립스에서 android.jsp 실행시 웹브라우저 주소)
                URL url = new URL("http://34.66.28.111:8080/DB/callTableSelect.jsp");

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

