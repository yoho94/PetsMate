package com.example.petsmate;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.widget.Toast;

import com.example.petsmate.table.IotTable;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;

public class DriverIot extends BaseActivity {

    private ArrayList<IotTable> iotTables;

    private Context context;

    // 블루투스


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_iot);
        context = this;

        if (!MainActivity.memberInfo.getIsLogin()) {
            Toast.makeText(this, "로그인을 하셔야 이용할 수 있습니다.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DriverIot.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("Pets&Mate 앱 사용을 위해선 다음과 같은 권한이 필요합니다.")
                .setDeniedMessage("권한을 거부하시면 이용이 불가능합니다. [설정] > [권한] 에서 권한을 허용해주세요.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH)
                .check();

        iotTables = new ArrayList<>();

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        configBottomNavigation(this, bottomNavigationView);

        // 블루투스


    }


}