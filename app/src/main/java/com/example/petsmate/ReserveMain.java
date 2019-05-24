package com.example.petsmate;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class ReserveMain extends AppCompatActivity implements OnMapReadyCallback, View.OnTouchListener {

    ArrayList<Place> places;
    ArrayList<PetInfo> petInfos;
    ListView listView;
    SparseBooleanArray sparseBooleanArray;
    CustomScrollView customScrollView;
    MapFragment mapFragment;
    EditText startET, destinationET, anotherNumberET;
    Button callBT, reserveBT, middleBT, largeBT, myNumberBT, anotherNumberBT, oneWayBT, roundBT;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reserve_main);

        customScrollView = (CustomScrollView) findViewById(R.id.reserve_CustomScrollView);
        startET = (EditText) findViewById(R.id.reserve_text_startsearch);
        destinationET = (EditText) findViewById(R.id.reserve_text_arrival);
        anotherNumberET = (EditText) findViewById(R.id.reserve_text_inputphonenumber);
        callBT = (Button) findViewById(R.id.reserve_btn_callnow);
        reserveBT = (Button) findViewById(R.id.reserve_btn_booking);
        middleBT = (Button) findViewById(R.id.reserve_btn_medium);
        largeBT = (Button) findViewById(R.id.reserve_btn_large);
        myNumberBT = (Button) findViewById(R.id.reserve_btn_mynum);
        anotherNumberBT = (Button) findViewById(R.id.reserve_btn_anothernum);
        oneWayBT = (Button) findViewById(R.id.reserve_btn_oneway);
        roundBT = (Button) findViewById(R.id.reserve_btn_roundtrip);

        // naver 지도 설정 시작
        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.reserve_map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.reserve_map_fragment, mapFragment).commit();
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission_group.LOCATION}, 0);

        mapFragment.getMapAsync(this); // NaverMap 객체 얻기.
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        // naver 지도 설정 종료

        /**  펫 읽어와서 띄우기 **/
        petInfos = MainActivity.memberInfo.getPetInfos();
        final ArrayList<String> arrayList = new ArrayList<>();

        for (int i = 0; i < petInfos.size(); i++) {
            arrayList.add(petInfos.get(i).getName());
            Log.i("PetArrayList", petInfos.get(i).getName());
        }

        listView = (ListView) findViewById(R.id.reserve_petListView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,
                android.R.id.text1, arrayList);

        listView.setAdapter(adapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        // 이중 스크롤뷰 스크롤 가능하게 하기.
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                customScrollView.setNeedIntercept(false);
                return false;
            }
        });

        mapFragment.getMapView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                customScrollView.setNeedIntercept(false);
                return false;
            }
        });

        // 다른 곳 터치시 정상적으로 스크롤 가능하게 하기.
        customScrollView.setOnTouchListener(this);
        startET.setOnTouchListener(this);
        destinationET.setOnTouchListener(this);
        anotherNumberET.setOnTouchListener(this);
        callBT.setOnTouchListener(this);
        reserveBT.setOnTouchListener(this);
        middleBT.setOnTouchListener(this);
        largeBT.setOnTouchListener(this);
        myNumberBT.setOnTouchListener(this);
        anotherNumberBT.setOnTouchListener(this);
        oneWayBT.setOnTouchListener(this);
        roundBT.setOnTouchListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                sparseBooleanArray = listView.getCheckedItemPositions();

                String ValueHolder = "";

                int i = 0;
                while (i < sparseBooleanArray.size()) {
                    if (sparseBooleanArray.valueAt(i)) {
                        ValueHolder += arrayList.get(sparseBooleanArray.keyAt(i)) + ",";
                    }
                    i++;
                }
                ValueHolder = ValueHolder.replaceAll("(,)*$", "");
                Toast.makeText(getApplicationContext(), "ListView Selected Values = " + ValueHolder, Toast.LENGTH_LONG).show();
            }
        });


        startET.setImeOptions(EditorInfo.IME_ACTION_DONE); // edittext 완료 버튼 만들기.
        destinationET.setImeOptions(EditorInfo.IME_ACTION_DONE);

        startET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) { // TODO 지도 검색 만들기
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String myGps = getMyGps().longitude + "," + getMyGps().latitude;
                    String searchStr = v.getText().toString();
                    Log.i("myGps", myGps);
                    Log.i("searchStr", searchStr);
                    SearchTask searchTask = new SearchTask();
                    try {
                        String result = searchTask.execute(searchStr, myGps).get();
                        Log.i("SearchResult", result);
                        JSONObject jsonObject = new JSONObject(result); // 전체 json
                        JSONArray jsonArray = jsonObject.getJSONArray("places"); // 장소

                        places = new ArrayList<>();

                        for(int i=0; i<jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            String name = jsonObject1.getString("name");
                            String roadAddress = jsonObject1.getString("road_address");
                            String jibunAddress = jsonObject1.getString("jibun_address");
                            String phoneNumber = jsonObject1.getString("phone_number");
                            String x = jsonObject1.getString("x");
                            String y = jsonObject1.getString("y");
                            String distance = jsonObject1.getString("distance");

                            places.add(new Place(name,roadAddress,jibunAddress,phoneNumber,x,y,distance));
                        }

                        CallSearchDialog callSearchDialog = new CallSearchDialog(ReserveMain.this, places);
                        callSearchDialog.callFunction();


                    } catch (Exception e) {
                        Log.i("Search Error", e.toString());
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        naverMap.setLocationSource(locationSource);
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);

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

    private LatLng getMyGps() {
        int permssionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        LatLng latLng = null;

        if (permssionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location gps = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double first_Latitude = gps.getLatitude();
            double first_Longitude = gps.getLongitude();

            latLng = new LatLng(first_Latitude, first_Longitude);
        }
        return latLng;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        customScrollView.setNeedIntercept(true);
        return false;
    }

    class SearchTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                sendMsg = "query=" + strings[0] + "&coordinate=" + strings[1];

                // 접속할 서버 주소 (이클립스에서 android.jsp 실행시 웹브라우저 주소)
                URL url = new URL("https://naveropenapi.apigw.ntruss.com/map-place/v1/search?" + sendMsg);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "krakspsq17");
                conn.setRequestProperty("X-NCP-APIGW-API-KEY", "asFGcIrUvqf2ee3esvh6G6KW5ZT6iWqJ0fYAzy3v");
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
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

                Log.i("통신 코드", conn.getResponseCode() + "");
                Log.i("통신 메시지", conn.getResponseMessage());

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

class Place {
    private String name; // 장소 명
    private String roadAddress; // 도로명 주소
    private String jibunAdress; // 지번 주소
    private String phoneNumber; // 전화번호
    private String x, lon; // 경도 LONG
    private String y, lat; // 위도 LAT
    private String distance; // 검색 좌표로부터 거리 (미터)

    public Place() {
    }

    public Place(String name, String roadAddress, String jibunAdress, String phoneNumber, String x, String y, String distance) {
        this.name = name;
        this.roadAddress = roadAddress;
        this.jibunAdress = jibunAdress;
        this.phoneNumber = phoneNumber;
        this.x = x; lon = x;
        this.y = y; lat = y;
        this.distance = distance;
    }

    public Place(String name, String roadAddress, String jibunAdress, String phoneNumber, String x, String lon, String y, String lat, String distance) {
        this.name = name;
        this.roadAddress = roadAddress;
        this.jibunAdress = jibunAdress;
        this.phoneNumber = phoneNumber;
        this.x = x;
        this.lon = lon;
        this.y = y;
        this.lat = lat;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoadAddress() {
        return roadAddress;
    }

    public void setRoadAddress(String roadAddress) {
        this.roadAddress = roadAddress;
    }

    public String getJibunAdress() {
        return jibunAdress;
    }

    public void setJibunAdress(String jibunAdress) {
        this.jibunAdress = jibunAdress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}