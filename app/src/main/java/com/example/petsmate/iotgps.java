package com.example.petsmate;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.petsmate.table.CallTable;
import com.example.petsmate.table.IotTable;
import com.example.petsmate.table.Place;
import com.example.petsmate.task.SelectIotTask;
import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class iotgps extends BaseActivity implements OnMapReadyCallback{

    private MapFragment mapFragment;
    private NaverMap naverMap = null;
    private ArrayList<Marker> markers;
    private ArrayList<IotTable> iotTables;
    private ImageButton refreshBtn;
    private InfoWindow infoWindow;

    private Button pickerBtn;
    private Context context;
    private Date startDate, lastDate;

    private FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iotgps);
        context = this;

        if (!MainActivity.memberInfo.getIsLogin()) {
            Toast.makeText(this, "로그인을 하셔야 GPS 기능 보기가 가능합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 권한 설정.
        // https://gun0912.tistory.com/61
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(iotgps.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("Pets&Mate 앱 사용을 위해선 다음과 같은 권한이 필요합니다.")
                .setDeniedMessage("권한을 거부하시면 이용이 불가능합니다. [설정] > [권한] 에서 권한을 허용해주세요.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();

        markers = new ArrayList<>();
        iotTables = new ArrayList<>();

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.pet_map);
        mapFragment.getMapAsync(this);



        BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        configBottomNavigation(this, bottomNavigationView);

        // 마커의 정보창
        infoWindow = new InfoWindow();
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(mapFragment.getContext()) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return  (CharSequence)infoWindow.getMarker().getTag();
            }
        });

        startDate = new Date(0);
        lastDate = new Date(System.currentTimeMillis());

        pickerBtn = (Button) findViewById(R.id.iotgps_picker);


    }
    @Override
    public void onMapReady(@Nullable NaverMap naverMap) {
        naverMap.setLocationSource(locationSource);
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
        this.naverMap = naverMap;

//        Marker marker = new Marker();
//        marker.setPosition(new LatLng(37.5670135, 126.9783740));
//        marker.setMap(naverMap);

        try {
           getIotTable();
           setIotMarker();

        } catch (Exception e) {
            Log.e("iotgps", e.toString());
        }

        // 새로고침 버튼
        refreshBtn = (ImageButton) findViewById(R.id.iotgps_refresh_btn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIotTable();
                setIotMarker();
            }
        });

        naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                infoWindow.close();
            }
        });

        pickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DoubleDateAndTimePickerDialog.Builder(context)
                        //.bottomSheet()
                        //.curved()
                        .dayFormatter(new SimpleDateFormat("MMM d일 EEE"))
                        //.minutesStep(15)
                        .title("시간설정")
                        .tab0Text("여기부터")
                        .tab1Text("여기까지")
                        .listener(new DoubleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(List<Date> dates) {
                                startDate = dates.get(0);
                                lastDate = dates.get(1);

                                getIotTable();
                                setIotMarker();
                            }
                        }).display();

            }
        });

    }

    private void setIotMarker() {

        for (int i = 0; i < markers.size(); i++) { // 기존의 마커 삭제
            markers.get(i).setMap(null);
        }
        markers.clear();

        for (int i = 0; i < iotTables.size(); i++) {
            final IotTable iotTable = iotTables.get(i);


                final Marker marker = new Marker();
                LatLng latLng = new LatLng(Double.parseDouble(iotTable.getLatitude()), Double.parseDouble(iotTable.getLongitude()));
                Log.i("Marker", latLng.toString());

                if(i == iotTables.size() -1)
                    marker.setIcon(OverlayImage.fromResource(R.drawable.ic_dog_marker));
                else {
                    marker.setIcon(MarkerIcons.BLACK); // 마커 아이콘을 블랙으로
                    marker.setCaptionText(i+1+"");
                }
                marker.setIconTintColor(Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));  // 마커 색상 랜덤
                marker.setAlpha(0.75f); // 마커 반투명으로 설정
            marker.setTag("시간 : " + iotTable.getGenerate_time() + "\n" + "심박수 : " + iotTable.getHeart_rate());
            marker.setOnClickListener(new Overlay.OnClickListener() {
                @Override
                public boolean onClick(@NonNull Overlay overlay) {
                    infoWindow.open(marker);
                    return true;
                }
            });


//                marker.setCaptionRequestedWidth(250); // 마커의 캡션 넓이(줄바꿈)
//                marker.setCaptionColor(Color.BLUE); // 마커 캡션 색상
//                marker.setCaptionHaloColor(Color.rgb(170, 255, 170)); // 마커 캡션 겉 색상
//                marker.setCaptionText("목적지 : " + callTable.destinationPlace.getRoadAddress()); // 마커 캡션 목적지 표기



                marker.setPosition(latLng);  // 마커 위치 설정
                marker.setMap(naverMap);    // 마커 맵에 표기

                markers.add(marker); // 마커 관리 리스트. (갱신 시 삭제를 위해)
                infoWindow.open(marker);
        }

        double latitude = Double.parseDouble(iotTables.get(iotTables.size()-1).getLatitude());
        double longitude = Double.parseDouble(iotTables.get(iotTables.size()-1).getLongitude());

        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(latitude, longitude));
        naverMap.moveCamera(cameraUpdate); // 마지막 펫 위치로 지도 옮기기.

    }

    private void getIotTable() {
        try {

            String result = new SelectIotTask().execute("SELECT * FROM `IOT` WHERE ID = \'" + MainActivity.memberInfo.getId() +"\'").get();
            result = result.trim();
            Log.i("IotTableResult", result);

            String tmpArr[] = result.split("@@");


            iotTables.clear();

            for(int i=0; i<tmpArr.length; i++) {
                String tempArr[] = tmpArr[i].split("//");
                IotTable iotTable = new IotTable();

                iotTable.setId(MainActivity.memberInfo.getId());
                iotTable.setPet_code(Integer.parseInt(tempArr[0]));
                iotTable.setGenerate_time(Timestamp.valueOf(tempArr[1]));
                iotTable.setLatitude(tempArr[2]);
                iotTable.setLongitude(tempArr[3]);
                iotTable.setHeart_rate(Integer.parseInt(tempArr[4]));

                // 앞 변수가 크면 1, 작으면 -1, 같으면 0
                int start = iotTable.getGenerate_time().compareTo(startDate);
                int last = iotTable.getGenerate_time().compareTo(lastDate);

                Log.i("startlast", start + ":" + last);

                if(start < 0 || last > 0)
                    continue;

                iotTables.add(iotTable);

            }

        } catch (Exception e) {
            Log.i("getIotTable", e.toString());
        }
    }


}