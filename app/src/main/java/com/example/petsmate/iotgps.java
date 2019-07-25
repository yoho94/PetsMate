package com.example.petsmate;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

public class iotgps extends BaseActivity implements OnMapReadyCallback{

    private MapFragment mapFragment;
    private NaverMap naverMap = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iotgps);

        if (!MainActivity.memberInfo.getIsLogin()) {
            Toast.makeText(this, "로그인을 하셔야 GPS 기능 보기가 가능합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }

        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.pet_map);
        mapFragment.getMapAsync(this);



        BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        configBottomNavigation(this, bottomNavigationView);


    }
    @Override
    public void onMapReady(@Nullable NaverMap naverMap) {
        this.naverMap = naverMap;

        Marker marker = new Marker();
        marker.setPosition(new LatLng(37.5670135, 126.9783740));
        marker.setMap(naverMap);

    }


}