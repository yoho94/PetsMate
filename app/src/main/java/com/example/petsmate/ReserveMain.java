package com.example.petsmate;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
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

import java.util.ArrayList;
import java.util.Map;

public class ReserveMain extends AppCompatActivity implements OnMapReadyCallback, View.OnTouchListener {

    ArrayList<PetInfo> petInfos;
    ListView listView;
    SparseBooleanArray sparseBooleanArray;
    CustomScrollView customScrollView;
    MapFragment mapFragment;
    EditText startET, destinationET, anotherNumberET;
    Button callBT, reserveBT, middleBT ,largeBT, myNumberBT, anotherNumberBT, oneWayBT, roundBT;

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
        startET.setOnTouchListener(this); destinationET.setOnTouchListener(this); anotherNumberET.setOnTouchListener(this);
        callBT.setOnTouchListener(this); reserveBT.setOnTouchListener(this); middleBT.setOnTouchListener(this); largeBT.setOnTouchListener(this);
        myNumberBT.setOnTouchListener(this); anotherNumberBT.setOnTouchListener(this); oneWayBT.setOnTouchListener(this); roundBT.setOnTouchListener(this);

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
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        customScrollView.setNeedIntercept(true);
        return false;
    }
}