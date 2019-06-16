package com.example.petsmate;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.location.Address;
import android.location.Geocoder;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

public class MapsNaverActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapFragment mapFragment;
    private NaverMap naverMap;
    private GpsTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private ArrayList<CallTable> callTables;
    private ArrayList<Marker> markers;
    private ImageButton refreshBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_naver);

        callTables = new ArrayList<>();
        markers = new ArrayList<>();

        // 처음 콜 정보 받아오기
        final Handler handler = new Handler(Looper.getMainLooper());
        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {

                        if (naverMap != null) {
                            getCallTable();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    setCallMaker();

                                }
                            });

                            break;
                        }
                    }
                } catch (Exception e) {
                    Log.i("getCallTableThread", e.toString());
                }
            }
        }.start();
        // 처음 콜 정보 받아오기 끝

        //  GPS 권한 설정 시작
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }
        // GPS 권한 설정 끝

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

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                //위치 값을 가져올 수 있음
                ;
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                } else {

                    Toast.makeText(this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }


    void checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress(double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString() + "\n";

    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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

        gpsTracker = new GpsTracker(this);

        double first_Latitude = gpsTracker.getLatitude();
        double first_Longitude = gpsTracker.getLongitude();

        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(first_Latitude, first_Longitude));
        naverMap.moveCamera(cameraUpdate); // 현재 위치로 지도 옮기기.


        // 새로고침 버튼
        refreshBtn = (ImageButton) findViewById(R.id.driver_refresh_btn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCallTable();
                setCallMaker();
            }
        });
    }

    private void setCallMaker() {

        for (int i = 0; i < markers.size(); i++) { // 기존의 마커 삭제
            markers.get(i).setMap(null);
        }
        markers.clear();
        for (int i = 0; i < callTables.size(); i++) {
            final CallTable callTable = callTables.get(i);

            if (callTable.getCode() == 0) {
                Marker marker = new Marker();
                LatLng latLng = new LatLng(Double.parseDouble(callTable.startPlace.getLat()), Double.parseDouble(callTable.startPlace.getLon()));
                Log.i("Marker", latLng.toString());

                marker.setIcon(MarkerIcons.BLACK); // 마커 아이콘을 블랙으로
                marker.setIconTintColor(Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));  // 마커 색상 랜덤
                marker.setAlpha(0.75f); // 마커 반투명으로 설정

                InfoWindow infoWindow = new InfoWindow();
                infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(mapFragment.getContext()) {
                    @NonNull
                    @Override
                    public CharSequence getText(@NonNull InfoWindow infoWindow) {
                        return "목적지 : " + callTable.destinationPlace.getRoadAddress();
                    }
                });

//                marker.setCaptionRequestedWidth(250); // 마커의 캡션 넓이(줄바꿈)
//                marker.setCaptionColor(Color.BLUE); // 마커 캡션 색상
//                marker.setCaptionHaloColor(Color.rgb(170, 255, 170)); // 마커 캡션 겉 색상
//                marker.setCaptionText("목적지 : " + callTable.destinationPlace.getRoadAddress()); // 마커 캡션 목적지 표기


                marker.setOnClickListener(new Overlay.OnClickListener() {
                    @Override
                    public boolean onClick(@NonNull Overlay o) {
                        CallDialog(callTable);
                        return true;
                    }
                });

                marker.setPosition(latLng);  // 마커 위치 설정
                marker.setMap(naverMap);    // 마커 맵에 표기

                markers.add(marker); // 마커 관리 리스트. (갱신 시 삭제를 위해)
                infoWindow.open(marker);
            }
        }
    }

    public void CallDialog(final CallTable callTable) {
        final String start = callTable.startPlace.getRoadAddress();
        final String des = callTable.destinationPlace.getRoadAddress();
        final String serialNumber = callTable.getSerialNumber() + "";
        final String id = MainActivity.memberInfo.getId();
        final String start_time = callTable.getStartTime().toString();
        final String code = "1";
        final double myLat = gpsTracker.getLatitude();
        final double myLng = gpsTracker.getLongitude();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("콜 선택");
        builder.setMessage("출발지 : " + start + "\n목적지 : " + des + "\n콜 요청 시간 : " + start_time);
        builder.setPositiveButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "취소 하셨습니다.", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton("콜 받기",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            String result = new CallUpdateTask().execute(id, code, serialNumber).get();
                            result = result.trim();
                            String[] results = result.split("@!@");

                            if (results[0].equalsIgnoreCase("1")) {
                                Toast.makeText(getApplicationContext(), "정상적으로 수락 됐습니다.", Toast.LENGTH_LONG).show();
                                long duration = new DirectionsTask().getDuration(myLng+"", myLat+"", callTable.getStartPlace().getLon(), callTable.getStartPlace().getLat());
                                Timestamp timestamp = new Timestamp(duration + System.currentTimeMillis());
                                long startToDes = callTable.getDestinationTime().getTime() - callTable.getStartTime().getTime();
                                String sendTime = (timestamp.getTime()+duration+startToDes)+"";

                                String updateResult = new CallDesTimeUpdateTask().execute(callTable.getSerialNumber()+"", sendTime).get();
                                Log.d("updateResult", updateResult);

                                // 푸시 알림 보내기.
                                String id;
                                String title = getString(R.string.callNoti);
                                String body = "출발지 : " + start + "\n목적지 : " + des +"\n기사 예상 도착시간 : " + timestamp.toString();
                                String type = "guest";
                                if (results[1].isEmpty())
                                    id = null;
                                else
                                    id = results[1];

                                new PushMsgTimeTask().execute(id, title, body, type, sendTime); // 푸시 알림 전송
                            } else {
                                Toast.makeText(getApplicationContext(), "수락 실패.", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            Log.i("CallUpdate", e.toString());
                        }
                        dialog.dismiss();
                    }
                });
        builder.show();
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
                URL url = new URL("http://106.10.36.239:8080//DB/callTableSelect.jsp");

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

    class CallUpdateTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;

                // 접속할 서버 주소 (이클립스에서 android.jsp 실행시 웹브라우저 주소)
                URL url = new URL("http://106.10.36.239:8080//DB/callUpdate.jsp");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                // 전송할 데이터. GET 방식으로 작성
//                sendMsg = "id=" + strings[0] + "&pw=" + strings[1];
                sendMsg = String.format("id=%s&code=%s&serialNumber=%s", strings[0], strings[1], strings[2]);

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

    class PushMsgTimeTask extends AsyncTask<String, Void, String> {

        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;

                // 접속할 서버 주소 (이클립스에서 android.jsp 실행시 웹브라우저 주소)
                URL url = new URL("http://106.10.36.239:8080/DB/sendPushMsg.jsp");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                // 전송할 데이터. GET 방식으로 작성
//                sendMsg = "id=" + strings[0] + "&pw=" + strings[1];
                sendMsg = String.format("id=%s&title=%s&body=%s&type=%s&data_time=%s", strings[0],strings[1], strings[2], strings[3], strings[4]);

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

