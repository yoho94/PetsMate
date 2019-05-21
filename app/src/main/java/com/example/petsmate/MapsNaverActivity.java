package com.example.petsmate;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

public class MapsNaverActivity extends Activity implements OnMapReadyCallback{

    private MapView mapView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_naver);
        mapView = findViewById(R.id.map_view);

        mapView.getMapAsync(this);
    }
    @Override
    public void onMapReady(@Nullable NaverMap naverMap) {
        Marker marker = new Marker();
        marker.setPosition(new LatLng(37.5670135, 126.9783740));
        marker.setMap(naverMap);

    }


}

