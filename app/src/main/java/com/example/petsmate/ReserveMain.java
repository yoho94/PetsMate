package com.example.petsmate;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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

import com.example.petsmate.table.PetInfo;
import com.example.petsmate.table.Place;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ReserveMain extends BaseActivity implements OnMapReadyCallback, View.OnTouchListener {

    Place startPlace, destinationPlace;
    LinkedHashMap<Integer, Boolean> petClick; // 펫 체크리스트 클릭 확인
    ArrayList<Place> places;
    ArrayList<PetInfo> petInfos;
    ListView listView;
    SparseBooleanArray sparseBooleanArray;
    CustomScrollView customScrollView;
    MapFragment mapFragment;
    EditText startET, destinationET, anotherNumberET;
    Button callBT, reserveBT, middleBT, largeBT, myNumberBT, anotherNumberBT, oneWayBT, roundBT;

    NaverMap naverMap = null;
    Marker startMaker, desMaker; // 시작 지점, 도착 지점 표기 마커

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;

    private GpsTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reserve_main);

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        configBottomNavigation(this, bottomNavigationView);

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

        startPlace = new Place();
        destinationPlace = new Place();

        // naver 지도 설정 시작
        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.reserve_map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.reserve_map_fragment, mapFragment).commit();
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission_group.LOCATION}, 0);

        mapFragment.getMapAsync(this); // NaverMap 객체 얻기.
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        startMaker = new Marker();
        desMaker = new Marker();

        gpsTracker = new GpsTracker(this);
        // naver 지도 설정 종료

        /**  펫 읽어와서 띄우기 **/
        petInfos = MainActivity.memberInfo.getPetInfos();
        petClick = new LinkedHashMap<>();
        final ArrayList<String> arrayList = new ArrayList<>();

        for (int i = 0; i < petInfos.size(); i++) {
            arrayList.add(petInfos.get(i).getName());
            petClick.put(petInfos.get(i).getCodeInt(), Boolean.FALSE);
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
                        int code = petInfos.get(sparseBooleanArray.keyAt(i)).getCodeInt();
                        petClick.put(code, true);
                        Log.i("petClick", code + "=" + petClick.get(code));
                    }
                    i++;
                }
                ValueHolder = ValueHolder.replaceAll("(,)*$", "");
                Toast.makeText(getApplicationContext(), ValueHolder +"를 선택 했습니다.", Toast.LENGTH_LONG).show();
            }
        });


        startET.setImeOptions(EditorInfo.IME_ACTION_DONE); // edittext 완료 버튼 만들기.
        destinationET.setImeOptions(EditorInfo.IME_ACTION_DONE);

        startET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) { // 지도 검색
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    if (!checkLocationServicesStatus()) {
                        showDialogForLocationServiceSetting();
                    } else {
                        checkRunTimePermission();
                    }

                    double myLat = gpsTracker.getLocation().getLatitude();
                    double myLng = gpsTracker.getLocation().getLongitude();

                    String myGps = myLng + "," + myLat;
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
                        callSearchDialog.callFunction(startET, startPlace);

                    } catch (Exception e) {
                        Log.i("Search Error", e.toString());
                    }
                }
                return false;
            }
        });

        destinationET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) { // 지도 검색
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    if (!checkLocationServicesStatus()) {
                        showDialogForLocationServiceSetting();
                    } else {
                        checkRunTimePermission();
                    }

                    double myLat = gpsTracker.getLocation().getLatitude();
                    double myLng = gpsTracker.getLocation().getLongitude();

                    String myGps = myLng + "," + myLat;
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
                        callSearchDialog.callFunction(destinationET, destinationPlace);




                    } catch (Exception e) {
                        Log.i("Search Error", e.toString());
                    }
                }
                return false;
            }
        });


        callBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(destinationPlace.getX() == null || startPlace.getX() == null) {
                    Toast.makeText(getApplicationContext(), "출발지, 도착지를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                CallTask callTask = new CallTask();
                CallSelectTask callSelectTask = new CallSelectTask();
                DirectionsTask directionsTask = new DirectionsTask();

                String guestId, driverId, isCall, code, startLat, startLon, desLat, desLon, startTime, desTime, guestCount, isShuttle, ps, placeName, placeAddr, placeNameStart, placeAddrStart;
                guestId = MainActivity.memberInfo.getId(); // TODO 전부 임시 값이 아닌 사용자가 선택한 값을 받아오는 작업 필요.
                driverId = "NULL";
                isCall = "TRUE";
                code = "0";
                startLat = startPlace.getLat();
                startLon = startPlace.getLon();
                desLat = destinationPlace.getLat();
                desLon = destinationPlace.getLon();
                startTime = System.currentTimeMillis() + "";
                desTime =  (new java.util.Date().getTime() + directionsTask.getDuration(startLon, startLat, desLon, desLat) )+ ""; // 네이버 API로 실제 시간계산.
                guestCount = "1"; // TODO 없애거나 xml에 추가하기.
                isShuttle = false+"";
                ps = "";
                placeName = destinationPlace.getName();
                placeAddr = destinationPlace.getRoadAddress();
                placeNameStart = startPlace.getName();
                placeAddrStart = startPlace.getRoadAddress();

                try {
                    String result = callTask.execute(guestId, driverId, isCall, code, startLat, startLon, desLat, desLon, startTime, desTime, guestCount, isShuttle, ps, placeName, placeAddr, placeNameStart, placeAddrStart).get();
                    result = result.trim();
                    Log.i("callResult", result);

                    if(result.substring(0,1).equals("1")) {
                        String[] timeStr = result.split("/");

                        String callSelectResult = callSelectTask.execute(guestId,timeStr[1]).get();
                        callSelectResult = callSelectResult.trim();
                        Log.i("callSelectResult", callSelectResult);

                        if(!callSelectResult.equals("-1")) { // -1이 아니면
                            for(int i=0; i<petInfos.size(); i++) {
                                if(petClick.get(petInfos.get(i).getCodeInt())) {
                                    CallPetTask callPetTask = new CallPetTask();
                                    String callPetResult = callPetTask.execute(callSelectResult, guestId, petInfos.get(i).getCode()).get();
                                    callPetResult = callPetResult.trim();
                                    Log.i("callPetResult",callPetResult);
                                }
                            }
                        } else { // -1 이라면
                            Log.i("callSelect ERROR", callSelectResult);
                            Toast.makeText(getApplicationContext(), "콜 요청은 성공하였으나 펫 정보를 등록하지 못했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "정상적으로 콜 요청이 되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "콜 요청 실패.", Toast.LENGTH_SHORT).show();
                    }


                }catch(Exception e) {
                    Log.i("callBT Error", e.toString());
                }
            }
        });

        //  GPS 권한 설정 시작
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }
        // GPS 권한 설정 끝


    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
//            return;
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    @Override
    public void onMapReady(@NonNull final NaverMap naverMap) {
        naverMap.setLocationSource(locationSource);
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
        this.naverMap = naverMap;

        // 지도 클릭하여 시작, 도착지 설정
        naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                try {
                    String result = new ReverseGeocodingTask().execute(latLng.longitude,latLng.latitude).get(); // 위경도를 넘겨 네이버 API에서 검색.
                    result = result.trim();
//                    Log.d("RGTresult",result);

                    String name = null;
                    String placeName = null;

                    // 파싱 작업.
                    JSONObject jsonObject = new JSONObject(result), j1, j2, j0;
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for(int i=0; i<jsonArray.length(); i++) {
                        j1 = jsonArray.getJSONObject(i);
                        name = "";

                        if(!j1.isNull("region")) {
                            j0 = j1.getJSONObject("region");
                            for(int j=1; j<j1.length(); j++) {
                                j2 = j0.getJSONObject("area" + j);
                                String str = j2.getString("name");

                                if(str == null || str.equalsIgnoreCase("")) {

                                } else {
                                    name += str + " ";
                                    placeName = str + " ";
                                }
                            }
                        }

                        if(!j1.isNull("land")) {
                            j2 = j1.getJSONObject("land");
                            String num1, num2;
                            num1 = j2.getString("number1");
                            num2 = j2.getString("number2");

                            if(num2 == null || num2.equalsIgnoreCase("")) {
                                name += num1;
                                placeName += num1;
                            }
                            else {
                                name += num1 + "-" + num2;
                                placeName += num1 + "-" + num2;
                            }
                        }

                    } // 파싱 끝.

                    setPlaceDialog(name, placeName, latLng);


//                    Log.d("주소 : " , name);

                } catch (Exception e) {
                    Log.e("ReverseGeocodingTask",e.toString());
                }

//                final Marker marker = new Marker();
//                marker.setPosition(latLng);
//
//                marker.setOnClickListener(new Overlay.OnClickListener() {
//                    @Override
//                    public boolean onClick(@NonNull Overlay overlay) {
//                        marker.setMap(null);
//                        return false;
//                    }
//                });
//
//                marker.setMap(naverMap);
            }
        });

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

    public void setPlaceDialog(final String name, final String placeName, final LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("출발지, 도착지 선택");
        builder.setMessage("주소 : " + name);
        builder.setPositiveButton("출발지",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"출발지로 선택하셨습니다.",Toast.LENGTH_LONG).show();

                        startPlace.setName(placeName);
                        startPlace.setJibunAdress(name);
                        startPlace.setRoadAddress(name);
                        startPlace.setLat(latLng.latitude+"");
                        startPlace.setLon(latLng.longitude+"");
                        startPlace.setX(latLng.longitude+"");
                        startPlace.setY(latLng.latitude+"");

                        startET.setText(placeName);

                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton("도착지",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"도착지로 선택하셨습니다.",Toast.LENGTH_LONG).show();

                        destinationPlace.setName(placeName);
                        destinationPlace.setJibunAdress(name);
                        destinationPlace.setRoadAddress(name);
                        destinationPlace.setLat(latLng.latitude+"");
                        destinationPlace.setLon(latLng.longitude+"");
                        destinationPlace.setX(latLng.longitude+"");
                        destinationPlace.setY(latLng.latitude+"");

                        destinationET.setText(placeName);

                        dialog.dismiss();
                    }
                });
        builder.setNeutralButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"취소하셨습니다.",Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
        builder.show();
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
                conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "jhqn1qq7oo");
                conn.setRequestProperty("X-NCP-APIGW-API-KEY", "nAvzIZvaB0J3lQQ9pmHmxMtGYdSKgPIB6lfUyrSn");
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

    class CallTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;

                // 접속할 서버 주소 (이클립스에서 android.jsp 실행시 웹브라우저 주소)
                URL url = new URL(MainActivity.serverIP +"call.jsp");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                // 전송할 데이터. GET 방식으로 작성
//                sendMsg = "id=" + strings[0] + "&pw=" + strings[1];
                sendMsg = String.format("guestId=%s&driverId=%s&isCall=%s&code=%s&startLat=%s&startLon=%s&desLat=%s&desLon=%s&startTime=%s&desTime=%s&guestCount=%s&isShuttle=%s&ps=%s&placeName=%s&placeAddr=%s&placeNameStart=%s&placeAddrStart=%s",
                        strings[0],strings[1],strings[2],strings[3],strings[4],strings[5],strings[6],strings[7],strings[8],strings[9],strings[10],strings[11],strings[12],strings[13],strings[14],strings[15],strings[16]);

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

    class CallPetTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;

                // 접속할 서버 주소 (이클립스에서 android.jsp 실행시 웹브라우저 주소)
                URL url = new URL(MainActivity.serverIP +"callPet.jsp");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                // 전송할 데이터. GET 방식으로 작성
//                sendMsg = "id=" + strings[0] + "&pw=" + strings[1];
                sendMsg = String.format("serialNumber=%s&id=%s&petCode=%s",
                        strings[0],strings[1],strings[2]);

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

    class CallSelectTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;

                // 접속할 서버 주소 (이클립스에서 android.jsp 실행시 웹브라우저 주소)
                URL url = new URL(MainActivity.serverIP +"callSelect.jsp");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                // 전송할 데이터. GET 방식으로 작성
//                sendMsg = "id=" + strings[0] + "&pw=" + strings[1];
                sendMsg = String.format("guestId=%s&time=%s",
                        strings[0],strings[1]);

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

    // GPS 권한
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
}
