package com.example.petsmate;

import android.Manifest;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petsmate.ble.BluetoothLeService;
import com.example.petsmate.ble.DeviceControlActivity;
import com.example.petsmate.ble.DeviceScanActivity;
import com.example.petsmate.table.IotTable;
import com.example.petsmate.task.InsertIotTask;
import com.example.petsmate.task.SelectIotTask;
import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
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
import java.util.Date;
import java.util.List;

public class DriverIot extends BaseActivity implements OnMapReadyCallback {

    private ArrayList<IotTable> iotTables;
    private MapFragment mapFragment;
    private NaverMap naverMap = null;
    private ArrayList<Marker> markers;
    private ImageButton refreshBtn;
    private InfoWindow infoWindow;
    private FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    private Context context;
    private ImageButton blueBtn;

    //블루투스
    StringBuilder data = new StringBuilder();
    public static final int REQUEST_CODE = 3000;
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    /* TEST */
    private Context mainContext=this;
    public enum connectionStateEnum{isNull, isScanning, isToScan, isConnecting , isConnected, isDisconnecting};
//    public abstract void onConectionStateChange(connectionStateEnum theconnectionStateEnum);
//    public abstract void onSerialReceived(String theString);
//    public void serialSend(String theString){
//        if (mConnectionState == connectionStateEnum.isConnected) {
//            mSCharacteristic.setValue(theString);
//            mBluetoothLeService.writeCharacteristic(mSCharacteristic);
//        }
//    }

    private int mBaudrate=115200;	//set the default baud rate to 115200
    private String mPassword="AT+PASSWOR=DFRobot\r\n";


    private String mBaudrateBuffer = "AT+CURRUART="+mBaudrate+"\r\n";

//	byte[] mBaudrateBuffer={0x32,0x00,(byte) (mBaudrate & 0xFF),(byte) ((mBaudrate>>8) & 0xFF),(byte) ((mBaudrate>>16) & 0xFF),0x00};;


    public void serialBegin(int baud){
        mBaudrate=baud;
        mBaudrateBuffer = "AT+CURRUART="+mBaudrate+"\r\n";
    }


    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
    private static BluetoothGattCharacteristic mSCharacteristic, mModelNumberCharacteristic, mSerialPortCharacteristic, mCommandCharacteristic;
    public static final String SerialPortUUID="0000dfb1-0000-1000-8000-00805f9b34fb";
    public static final String CommandUUID="0000dfb2-0000-1000-8000-00805f9b34fb";
    public static final String ModelNumberStringUUID="00002a24-0000-1000-8000-00805f9b34fb";

    /*TEST END*/
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
//                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
//                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
//                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
//                displayGattServices(mBluetoothLeService.getSupportedGattServices());

                for (BluetoothGattService gattService : mBluetoothLeService.getSupportedGattServices()) {
                    System.out.println("ACTION_GATT_SERVICES_DISCOVERED  "+
                            gattService.getUuid().toString());
                }
                getGattServices(mBluetoothLeService.getSupportedGattServices());

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                /* TEST */
                if(mSCharacteristic==mModelNumberCharacteristic)
                {
                    if (intent.getStringExtra(BluetoothLeService.EXTRA_DATA).toUpperCase().startsWith("DF BLUNO")) {
                        mBluetoothLeService.setCharacteristicNotification(mSCharacteristic, false);
                        mSCharacteristic=mCommandCharacteristic;
                        mSCharacteristic.setValue(mPassword);
                        mBluetoothLeService.writeCharacteristic(mSCharacteristic);
                        mSCharacteristic.setValue(mBaudrateBuffer);
                        mBluetoothLeService.writeCharacteristic(mSCharacteristic);
                        mSCharacteristic=mSerialPortCharacteristic;
                        mBluetoothLeService.setCharacteristicNotification(mSCharacteristic, true);
//                        mConnectionState = connectionStateEnum.isConnected;
//                        onConectionStateChange(mConnectionState);

                    }
                    else {
                        Toast.makeText(mainContext, "Please select DFRobot devices",Toast.LENGTH_SHORT).show();
//                        mConnectionState = connectionStateEnum.isToScan;
//                        onConectionStateChange(mConnectionState);
                    }
                }
                else if (mSCharacteristic==mSerialPortCharacteristic) {
                    Log.i("onSerialReceived", intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
//                    onSerialReceived(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                }
                try {
                    String tmp = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                    System.out.println("displayData " + intent.getStringExtra(BluetoothLeService.EXTRA_DATA));

                    if (tmp != null || !tmp.isEmpty()) {
                        data.append(tmp);
                        Log.i("data", data.toString());
                        if (!data.toString().startsWith("Lat:")) {
                            data = new StringBuilder();
                        } else {
                            String[] dataArr = data.toString().split("\n");
                            if (dataArr.length == 6) {

                                IotTable iotTable = new IotTable();
                                String latitude, longitude, date, hert;
                                Integer pet_code, heart_rate;
                                Timestamp generate_time;
                                latitude = dataArr[0].substring(5).trim();
                                longitude = dataArr[1].substring(5).trim();
                                date = dataArr[2].substring(6) + " " + dataArr[3].substring(6).trim();
                                hert = dataArr[5].substring(5).trim();

                                Log.i("DATE", date);


                                if (hert.isEmpty())
                                    heart_rate = 0;
                                else
                                    heart_rate = Integer.parseInt(hert);

                                if (date.isEmpty() || 14 > date.length())
                                    generate_time = new Timestamp(System.currentTimeMillis());
//                                    generate_time = null;
                                else
                                    generate_time = new Timestamp(System.currentTimeMillis());
//                                    generate_time = Timestamp.valueOf("20"+date);

                                iotTable.setLatitude(latitude);
                                iotTable.setLongitude(longitude);
                                iotTable.setGenerate_time(generate_time);
                                iotTable.setHeart_rate(heart_rate);
                                iotTable.setPet_code(69); // TEST TODO 실제 펫 코드, 아이디 받기.
                                iotTable.setId("Test"); // TEST

                                Log.i("iotTable", iotTable.toString());


                                if (iotTable.getLatitude().isEmpty() || iotTable.getLongitude().isEmpty()) {
                                    Log.i("insertIot", "gps isEmpty");
                                } else {
                                    iotTables.add(iotTable);
                                    setIotMarker();
                                }

//                                Toast.makeText(getApplicationContext(), iotTable.toString(), Toast.LENGTH_SHORT).show();

                                data = new StringBuilder();
                            }

                        }
                    }
                } catch (Exception e) {
                    Log.e("IoT에러(DriverIOT)", e.toString());
                    data = new StringBuilder();
                    setIotMarker();
                }

//                try {
//                    String tmp = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
//                    Log.i("tmp", tmp);
//                    if(tmp != null || !tmp.isEmpty()) {
//                        data.append(tmp);
//                        Log.i("data", data.toString());
//                        if(!data.toString().startsWith("Lat:")) {
//                            data = new StringBuilder();
//                        } else {
//                            String[] dataArr = data.toString().split("\n");
//                            if(dataArr.length == 6) {
//
//                                IotTable iotTable = new IotTable();
//                                String latitude, longitude, date, hert;
//                                Integer pet_code, heart_rate;
//                                Timestamp generate_time;
//                                latitude = dataArr[0].substring(5).trim();
//                                longitude = dataArr[1].substring(5).trim();
//                                date = dataArr[2].substring(6) + " " +dataArr[3].substring(6).trim();
//                                hert = dataArr[5].substring(5).trim();
//
//                                Log.i("DATE", date);
//
//
//                                if(hert.isEmpty())
//                                    heart_rate = 0;
//                                else
//                                    heart_rate = Integer.parseInt(hert);
//
//                                if (date.isEmpty() || 14 > date.length())
//                                    generate_time = new Timestamp(System.currentTimeMillis());
////                                    generate_time = null;
//                                else
//                                    generate_time = new Timestamp(System.currentTimeMillis());
////                                    generate_time = Timestamp.valueOf("20"+date);
//
//                                iotTable.setLatitude(latitude);
//                                iotTable.setLongitude(longitude);
//                                iotTable.setGenerate_time(generate_time);
//                                iotTable.setHeart_rate(heart_rate);
//                                iotTable.setPet_code(69); // TEST
//                                iotTable.setId("Test"); // TEST
//
//                                Log.i("iotTable", iotTable.toString());
////                                Toast.makeText(getApplicationContext(), iotTable.toString(), Toast.LENGTH_SHORT).show();
//
//                                data = new StringBuilder();
//                            }
//
//                        }
//                    }
//
//
//                }catch (Exception e) {
//                    Log.e("IoT파싱에러", e.toString());
//                    data = new StringBuilder();
//                }

//            	mPlainProtocol.mReceivedframe.append(intent.getStringExtra(BluetoothLeService.EXTRA_DATA)) ;
//            	System.out.print("mPlainProtocol.mReceivedframe:");
//            	System.out.println(mPlainProtocol.mReceivedframe.toString());


            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBluetoothLeService!= null) {
            unbindService(mServiceConnection);
            mBluetoothLeService = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



//    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
//    // In this sample, we populate the data structure that is bound to the ExpandableListView
//    // on the UI.
//    private void displayGattServices(List<BluetoothGattService> gattServices) {
//        if (gattServices == null) return;
//        String uuid = null;
//        String unknownServiceString = getResources().getString(R.string.unknown_service);
//        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
//        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
//        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
//                = new ArrayList<ArrayList<HashMap<String, String>>>();
//        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
//
//        // Loops through available GATT Services.
//        for (BluetoothGattService gattService : gattServices) {
//            HashMap<String, String> currentServiceData = new HashMap<String, String>();
//            uuid = gattService.getUuid().toString();
//            currentServiceData.put(
//                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
//            currentServiceData.put(LIST_UUID, uuid);
//            gattServiceData.add(currentServiceData);
//
//            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
//                    new ArrayList<HashMap<String, String>>();
//            List<BluetoothGattCharacteristic> gattCharacteristics =
//                    gattService.getCharacteristics();
//            ArrayList<BluetoothGattCharacteristic> charas =
//                    new ArrayList<BluetoothGattCharacteristic>();
//
//            // Loops through available Characteristics.
//            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
//                charas.add(gattCharacteristic);
//                HashMap<String, String> currentCharaData = new HashMap<String, String>();
//                uuid = gattCharacteristic.getUuid().toString();
//                currentCharaData.put(
//                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
//                currentCharaData.put(LIST_UUID, uuid);
//                gattCharacteristicGroupData.add(currentCharaData);
//            }
//            mGattCharacteristics.add(charas);
//            gattCharacteristicData.add(gattCharacteristicGroupData);
//        }
//
////        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
////                this,
////                gattServiceData,
////                android.R.layout.simple_expandable_list_item_2,
////                new String[] {LIST_NAME, LIST_UUID},
////                new int[] { android.R.id.text1, android.R.id.text2 },
////                gattCharacteristicData,
////                android.R.layout.simple_expandable_list_item_2,
////                new String[] {LIST_NAME, LIST_UUID},
////                new int[] { android.R.id.text1, android.R.id.text2 }
////        );
////        mGattServicesList.setAdapter(gattServiceAdapter);
//    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    /*TEST*/
    private void getGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        mModelNumberCharacteristic=null;
        mSerialPortCharacteristic=null;
        mCommandCharacteristic=null;
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            System.out.println("displayGattServices + uuid="+uuid);

            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                uuid = gattCharacteristic.getUuid().toString();
                if(uuid.equals(ModelNumberStringUUID)){
                    mModelNumberCharacteristic=gattCharacteristic;
                    System.out.println("mModelNumberCharacteristic  "+mModelNumberCharacteristic.getUuid().toString());
                }
                else if(uuid.equals(SerialPortUUID)){
                    mSerialPortCharacteristic = gattCharacteristic;
                    System.out.println("mSerialPortCharacteristic  "+mSerialPortCharacteristic.getUuid().toString());
//                    updateConnectionState(R.string.comm_establish);
                }
                else if(uuid.equals(CommandUUID)){
                    mCommandCharacteristic = gattCharacteristic;
                    System.out.println("mSerialPortCharacteristic  "+mSerialPortCharacteristic.getUuid().toString());
//                    updateConnectionState(R.string.comm_establish);
                }
            }
            mGattCharacteristics.add(charas);
        }

        if (mModelNumberCharacteristic==null || mSerialPortCharacteristic==null || mCommandCharacteristic==null) {
            Toast.makeText(mainContext, "Please select DFRobot devices",Toast.LENGTH_SHORT).show();
//            mConnectionState = connectionStateEnum.isToScan;
//            onConectionStateChange(mConnectionState);
        }
        else {
            mSCharacteristic=mModelNumberCharacteristic;
            mBluetoothLeService.setCharacteristicNotification(mSCharacteristic, true);
            mBluetoothLeService.readCharacteristic(mSCharacteristic);
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_iot);
        context = this;

//        if (!MainActivity.memberInfo.getIsLogin()) {
//            Toast.makeText(this, "로그인을 하셔야 이용할 수 있습니다.", Toast.LENGTH_SHORT).show();
//            finish();
//        }

        // 권한 설정.
        // https://gun0912.tistory.com/61
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(DriverIot.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("Pets&Mate 앱 사용을 위해선 다음과 같은 권한이 필요합니다.")
                .setDeniedMessage("권한을 거부하시면 이용이 불가능합니다. [설정] > [권한] 에서 권한을 허용해주세요.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH)
                .check();


        BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        configBottomNavigation(this, bottomNavigationView);

        // 블루투스
        blueBtn = (ImageButton) findViewById(R.id.driver_iot_bluetooth_btn);

        blueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverIot.this, DeviceScanActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        markers = new ArrayList<>();
        iotTables = new ArrayList<>();

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.driver_iot_map);
        mapFragment.getMapAsync(this);

        // 마커의 정보창
        infoWindow = new InfoWindow();
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(mapFragment.getContext()) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return  (CharSequence)infoWindow.getMarker().getTag();
            }
        });

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
            setIotMarker();

        } catch (Exception e) {
            Log.e("iotgps", e.toString());
        }

        // 새로고침 버튼
        refreshBtn = (ImageButton) findViewById(R.id.driver_iot_refresh_btn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIotMarker();
            }
        });

        naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                infoWindow.close();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    final Intent intent = data;
                    mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
                    mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
                    Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
                    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                    break;
            }
        }
    }

    private void setIotMarker() {

        for (int i = 0; i < markers.size(); i++) { // 기존의 마커 삭제
            markers.get(i).setMap(null);
        }
        markers.clear();

        if(iotTables.isEmpty())
            return;

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

}