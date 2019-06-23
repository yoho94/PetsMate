package com.example.petsmate;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.petsmate.service.CallListViewAdapter;
import com.example.petsmate.table.CallTable;
import com.example.petsmate.table.MemberInfo;
import com.example.petsmate.table.Place;
import com.example.petsmate.task.CallTableTask;
import com.example.petsmate.task.DriverInfoTask;
import com.example.petsmate.task.MemberInfoTask;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

public class CallListPage extends BaseActivity {
    ListView listView;
    CallListViewAdapter adapter;
    ArrayList<CallTable> callTables = new ArrayList<>();
    Drawable iconRed, iconGreen, iconYellow, iconBlue;
    ImageButton callListRefreshBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reserve_list);

        if (!MainActivity.memberInfo.getIsLogin()) {
            Toast.makeText(this, "로그인을 하셔야 나의 예약보기가 가능합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        configBottomNavigation(this, bottomNavigationView);

        adapter = new CallListViewAdapter();
        listView = (ListView) findViewById(R.id.callListView);
        listView.setAdapter(adapter);

        iconRed = ContextCompat.getDrawable(this, R.drawable.ic_circle_red);
        iconGreen = ContextCompat.getDrawable(this, R.drawable.ic_circle_green);
        iconYellow = ContextCompat.getDrawable(this, R.drawable.ic_circle_yellow);
        iconBlue = ContextCompat.getDrawable(this, R.drawable.ic_circle_blue);

        setAdapter();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // TODO 예약 수락, 거절 등 기능 넣기.
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        callListRefreshBtn = (ImageButton) findViewById(R.id.callListRefreshBtn);
        callListRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdapter();
                listView.invalidateViews();
            }
        });

    }

    public void getTable() {

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

        } catch (Exception e) {
            Log.i("getCallTable", e.toString());
        }
    }

    public void setAdapter() {
        getTable();
        adapter.clearItem();

        for (int i = 0; i < callTables.size(); i++) {
            CallTable callTable = callTables.get(i);

            if (MainActivity.memberInfo.getId() == null || MainActivity.memberInfo.getId().isEmpty()) {
                return;
            }

            if (MainActivity.memberInfo.isGuest()) {
                if (!MainActivity.memberInfo.getId().equalsIgnoreCase(callTable.getGuestId()))
                    continue;

            } else {
                if (!MainActivity.memberInfo.getId().equalsIgnoreCase(callTable.getDriverId()))
                    continue;
            }

            Drawable icon;
            String title, str1, str2, str3, str4, str5;
            String name = "", phone = "", carNumber = "", token = "";

            str1 = "[예약시간]\t";
            str2 = "[출발시간]\t";

            if (MainActivity.memberInfo.isGuest())
                str3 = "[기 사 님]\t";
            else
                str3 = "[고 객 님]\t";

            str4 = "[출 발 지]\t";
            str5 = "[도 착 지]\t";

            switch (callTable.getCode()) {
                case 0:
                    if (callTable.isCall())
                        title = "콜 대기";
                    else
                        title = "예약 대기";
                    icon = iconYellow;
                    break;
                case 1:
                    title = "수락";
                    icon = iconGreen;
                    break;
                case 2:
                    title = "거절";
                    icon = iconRed;
                    break;
                case 10:
                case 12:
                    title = "운행 중";
                    icon = iconGreen;
                    break;
                case 11:
                    title = "미 탑승";
                    icon = iconRed;
                    break;
                case 13:
                    title = "도착";
                    icon = iconBlue;
                    break;
                default:
                    title = "에러";
                    icon = iconRed;
                    break;
            }

            str1 += callTable.getGenerateTime();
            str2 += callTable.getStartTime();

            Log.d("callTable", callTable.toString());

            if (MainActivity.memberInfo.isGuest())
                try {
                    String[] driverInfo = new DriverInfoTask().execute(callTable.getDriverId()).get().split("//");
                    name = driverInfo[0];
                    phone = driverInfo[1].substring(0, 3) + "-" + driverInfo[1].substring(3, 7) + "-" + driverInfo[1].substring(7, driverInfo[1].length());
                    carNumber = driverInfo[2];
                    token = driverInfo[3];
                } catch (Exception e) {
                    Log.e("setAdapterD", e.toString());
                }
            else
                try{
                    String[] memberInfo = new MemberInfoTask().execute(callTable.getGuestId()).get().split("//");
                    name = memberInfo[0];
                    phone = memberInfo[1].substring(0, 3) + "-" + memberInfo[1].substring(3, 7) + "-" + memberInfo[1].substring(7, memberInfo[1].length());
                    token = memberInfo[2];
                } catch (Exception e) {
                    Log.e("setAdapterG", e.toString());
                }

            str3 += name + " / " + phone;

            str4 += callTable.getStartPlace().getRoadAddress();
            str5 += callTable.getDestinationPlace().getRoadAddress();

            adapter.addItem(icon, title, str1, str2, str3, str4, str5, callTable.getSerialNumber());
        }
    }
}
